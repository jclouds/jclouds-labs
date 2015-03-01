/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azurecompute.features;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.azurecompute.domain.Deployment.InstanceStatus.READY_ROLE;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

import com.google.common.collect.Iterables;
import org.jclouds.azurecompute.compute.AzureComputeServiceAdapter;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.VMImage;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.CaptureVMImageParams;
import org.jclouds.azurecompute.domain.VMImageParams;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.azurecompute.util.ConflictManagementPredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

@Test(groups = "live", testName = "VMImageApiLiveTest")
public class VMImageApiLiveTest extends BaseAzureComputeApiLiveTest {

    private static final String CLOUD_SERVICE = String.format("%s%d-%s",
            System.getProperty("user.name"), RAND, VMImageApiLiveTest.class.getSimpleName()).toLowerCase();

    private static final String DEPLOYMENT = String.format("%s%d-%s",
            System.getProperty("user.name"), RAND, VMImageApiLiveTest.class.getSimpleName()).toLowerCase();

    private static final String CAPTURED_IMAGE_NAME = "captured-image";

    private static final String CREATE_IMAGE_NAME = "create-image";

    private String roleName;

    private String diskName;

    private Predicate<String> roleInstanceReady;

    private CloudService cloudService;

    @BeforeClass(groups = {"integration", "live"})
    @Override
    public void setup() {
        super.setup();
        cloudService = getOrCreateCloudService(CLOUD_SERVICE, LOCATION);

        roleInstanceReady = retry(new Predicate<String>() {

            @Override
            public boolean apply(String input) {
                Deployment.RoleInstance roleInstance = getFirstRoleInstanceInDeployment(input);
                return roleInstance != null && roleInstance.instanceStatus() == READY_ROLE;
            }
        }, 600, 5, 5, SECONDS);

        final DeploymentParams params = DeploymentParams.builder()
                .name(DEPLOYMENT)
                .os(OSImage.Type.LINUX)
                .sourceImageName(BaseAzureComputeApiLiveTest.IMAGE_NAME)
                .mediaLink(AzureComputeServiceAdapter.createMediaLink(storageService.serviceName(), DEPLOYMENT))
                .username("test")
                .password("supersecurePassword1!")
                .size(RoleSize.Type.BASIC_A2)
                .subnetName(Iterables.get(virtualNetworkSite.subnets(), 0).name())
                .virtualNetworkName(virtualNetworkSite.name())
                .externalEndpoint(DeploymentParams.ExternalEndpoint.inboundTcpToLocalPort(22, 22))
                .build();
        Deployment deployment = getOrCreateDeployment(cloudService.name(), params);
        Deployment.RoleInstance roleInstance = getFirstRoleInstanceInDeployment(DEPLOYMENT);
        assertTrue(roleInstanceReady.apply(DEPLOYMENT), roleInstance.toString());
        roleName = roleInstance.roleName();
        diskName = deployment.roleList().get(0).osVirtualHardDisk().diskName();
    }

    @Test(dependsOnMethods = "testCaptureVMImage")
    public void testCreate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        VMImage image = api().list().get(5);
        VMImageParams.OSDiskConfigurationParams osParams = VMImageParams.OSDiskConfigurationParams
                .OSDiskConfiguration(CREATE_IMAGE_NAME + "osdisk",
                        VMImageParams.OSDiskConfigurationParams.Caching.READ_ONLY,
                        VMImageParams.OSDiskConfigurationParams.OSState.SPECIALIZED,
                        image.osDiskConfiguration().os(),
                        URI.create(
                                "https://" + storageService.serviceName()
                                        + ".blob.core.windows.net/vhds/" + CAPTURED_IMAGE_NAME + "-os-" + dateFormat.format(date) + ".vhd"),
                        30,
                        "Standard");
        VMImageParams params = VMImageParams.builder().name(CREATE_IMAGE_NAME).label(CREATE_IMAGE_NAME)
                .description(image.description()).recommendedVMSize(image.recommendedVMSize())
                .osDiskConfiguration(osParams).imageFamily(image.imageFamily())
                .build();

        String requestId = api().create(params);
        assertNotNull(requestId);
        operationSucceeded.apply(requestId);
    }

    @Test
    public void testCaptureVMImage() {
        String shutdownRequest = api.getVirtualMachineApiForDeploymentInService(DEPLOYMENT, CLOUD_SERVICE).shutdown(roleName);
        assertTrue(operationSucceeded.apply(shutdownRequest), shutdownRequest);

        CaptureVMImageParams captureParams = CaptureVMImageParams.builder()
                .osState(VMImage.OSDiskConfiguration.OSState.GENERALIZED).name(CAPTURED_IMAGE_NAME)
                .label(CAPTURED_IMAGE_NAME).recommendedVMSize(RoleSize.Type.MEDIUM).build();

        String requestId = api.getVirtualMachineApiForDeploymentInService(DEPLOYMENT, CLOUD_SERVICE)
                .capture(roleName, captureParams);
        assertNotNull(requestId);
        operationSucceeded.apply(requestId);
    }

    @Test(dependsOnMethods = "testCreate")
    public void testUpdateVMImage() {
        VMImage image = api().list().get(5);
        VMImageParams params = VMImageParams.builder()
                .label("UpdatedLabel")
                .description(image.description()).recommendedVMSize(RoleSize.Type.A7)
                .build();

        String requestId = api().update(CAPTURED_IMAGE_NAME, params);
        assertNotNull(requestId);
        operationSucceeded.apply(requestId);
    }

    @Test
    public void testList() {
        List<VMImage> vmImageList = api().list();
        assertTrue(vmImageList.size() > 0);
        for (VMImage VMImage : vmImageList) {
            checkVMImage(VMImage);
        }
    }

    @Test(dependsOnMethods = {"testList", "testUpdateVMImage"})
    public void testDelete() {
        String requestId = api().delete(CAPTURED_IMAGE_NAME);
        assertNotNull(requestId);
        assertTrue(operationSucceeded.apply(requestId), requestId);
    }

    private void checkVMImage(VMImage image) {
        assertNotNull(image.label(), "Label cannot be null for " + image);
        assertNotNull(image.name(), "Name cannot be null for " + image);
        assertNotNull(image.location(), "Location cannot be null for " + image);

        //OSImage
        VMImage.OSDiskConfiguration osDiskConfiguration = image.osDiskConfiguration();
        assertNotNull(osDiskConfiguration);
        assertNotNull(osDiskConfiguration.name());
        assertTrue(osDiskConfiguration.logicalSizeInGB() > 0);

        if (osDiskConfiguration.mediaLink() != null) {
            assertTrue(ImmutableSet.of("http", "https").contains(osDiskConfiguration.mediaLink().getScheme()),
                    "MediaLink should be an http(s) url" + image);
        }

        if (image.category() != null) {
            assertNotEquals("", image.category().trim(), "Invalid Category for " + image);
        }
    }

    @AfterClass
    @Override
    protected void tearDown() {
        assertTrue(new ConflictManagementPredicate(api) {
            @Override
            protected String operation() {
                return api.getDiskApi().delete(diskName);
            }
        }.apply(diskName));
        super.tearDown();
    }

    private VMImageApi api() {
        return api.getVMImageApi();
    }

    private Deployment.RoleInstance getFirstRoleInstanceInDeployment(String deployment) {
        return Iterables.getOnlyElement(api.getDeploymentApiForService(cloudService.name()).get(deployment).
                roleInstanceList());
    }
}
