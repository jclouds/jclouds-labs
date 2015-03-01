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

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.domain.VMImageParams;
import org.jclouds.azurecompute.domain.CaptureVMImageParams;
import org.jclouds.azurecompute.domain.VMImage;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecompute.xml.ListVMImagesHandlerTest;
import org.testng.annotations.Test;

import java.net.URI;

@Test(groups = "unit", testName = "VMImageApiMockTest")
public class VMImageApiMockTest extends BaseAzureComputeApiMockTest {

    public void listWhenFound() throws Exception {
        MockWebServer server = mockAzureManagementServer();
        server.enqueue(xmlResponse("/vmimages.xml"));

        try {
            VMImageApi api = api(server.getUrl("/")).getVMImageApi();

            assertEquals(api.list(), ListVMImagesHandlerTest.expected());

            assertSent(server, "GET", "/services/vmimages");
        } finally {
            server.shutdown();
        }
    }

    public void listWhenNotFound() throws Exception {
        MockWebServer server = mockAzureManagementServer();
        server.enqueue(new MockResponse().setResponseCode(404));

        try {
            VMImageApi api = api(server.getUrl("/")).getVMImageApi();

            assertTrue(api.list().isEmpty());

            assertSent(server, "GET", "/services/vmimages");
        } finally {
            server.shutdown();
        }
    }

    public void create() throws Exception {
        MockWebServer server = mockAzureManagementServer();
        server.enqueue(requestIdResponse("request-1"));

        try {
            VMImageApi api = api(server.getUrl("/")).getVMImageApi();

            VMImageParams.OSDiskConfigurationParams osParams = VMImageParams.OSDiskConfigurationParams
                    .OSDiskConfiguration("ClouderaGolden-os_disk",
                            VMImageParams.OSDiskConfigurationParams.Caching.READ_ONLY,
                            VMImageParams.OSDiskConfigurationParams.OSState.SPECIALIZED,
                            OSImage.Type.LINUX,
                            URI.create("http://blobs/disks/neotysss/MSFT__Win2K8R2SP1-ABCD-en-us-30GB.vhd"),
                            30,
                            "Standard");
            VMImageParams params = VMImageParams.builder()
                    .name("ClouderaGolden")
                    .label("CDH 5.1 Evaluation")
                    .description("Single click deployment")
                    .recommendedVMSize(RoleSize.Type.LARGE)
                    .osDiskConfiguration(osParams)
                    .imageFamily("Ubuntu")
                    .language("en")
                    .eula("http://www.gnu.org/copyleft/gpl.html")
                    .iconUri(URI.create("http://www.cloudera.com/content/cloudera/en/privacy-policy.html"))
                    .smallIconUri(URI.create("http://www.cloudera.com/content/cloudera/en/privacy-policy.html"))
                    .privacyUri(URI.create("http://www.cloudera.com/content/cloudera/en/privacy-policy.html"))
                    .showGui(Boolean.TRUE)
                    .build();

            assertEquals(api.create(params), "request-1");

            assertSent(server, "POST", "/services/vmimages", "/vmimageparams.xml");
        } finally {
            server.shutdown();
        }
    }

    public void testUpdate() throws Exception {
        MockWebServer server = mockAzureManagementServer();
        server.enqueue(requestIdResponse("request-1"));

        try {
            VMImageApi api = api(server.getUrl("/")).getVMImageApi();

            VMImageParams.OSDiskConfigurationParams osParams = VMImageParams.OSDiskConfigurationParams
                    .OSDiskConfiguration("ClouderaGolden-os_disk",
                            VMImageParams.OSDiskConfigurationParams.Caching.READ_ONLY,
                            VMImageParams.OSDiskConfigurationParams.OSState.SPECIALIZED,
                            OSImage.Type.LINUX,
                            URI.create("http://blobs/disks/neotysss/MSFT__Win2K8R2SP1-ABCD-en-us-30GB.vhd"),
                            30,
                            "Standard");
            VMImageParams params = VMImageParams.builder()
                    .name("ClouderaGolden")
                    .label("CDH 5.1 Evaluation")
                    .description("Single click deployment")
                    .recommendedVMSize(RoleSize.Type.LARGE)
                    .osDiskConfiguration(osParams)
                    .imageFamily("Ubuntu")
                    .language("en")
                    .eula("http://www.gnu.org/copyleft/gpl.html")
                    .iconUri(URI.create("http://www.cloudera.com/content/cloudera/en/privacy-policy.html"))
                    .smallIconUri(URI.create("http://www.cloudera.com/content/cloudera/en/privacy-policy.html"))
                    .privacyUri(URI.create("http://www.cloudera.com/content/cloudera/en/privacy-policy.html"))
                    .showGui(Boolean.TRUE)
                    .build();

            assertEquals(api.update("myvmimage", params), "request-1");

            assertSent(server, "PUT", "/services/vmimages/myvmimage", "/vmimageparams.xml");
        } finally {
            server.shutdown();
        }
    }

    public void deleteWhenFound() throws Exception {
        MockWebServer server = mockAzureManagementServer();
        server.enqueue(requestIdResponse("request-1"));

        try {
            VMImageApi api = api(server.getUrl("/")).getVMImageApi();

            assertEquals(api.delete("myvmimage"), "request-1");

            assertSent(server, "DELETE", "/services/vmimages/myvmimage");
        } finally {
            server.shutdown();
        }
    }

    public void deleteWhenNotFound() throws Exception {
        MockWebServer server = mockAzureManagementServer();
        server.enqueue(new MockResponse().setResponseCode(404));

        try {
            VMImageApi api = api(server.getUrl("/")).getVMImageApi();

            assertNull(api.delete("myvmimage"));

            assertSent(server, "DELETE", "/services/vmimages/myvmimage");
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void testCaptureVMImage() throws Exception {

        MockWebServer server = mockAzureManagementServer();
        server.enqueue(requestIdResponse("request-1"));

        try {
            CaptureVMImageParams captureParams = CaptureVMImageParams.builder()
                    .osState(VMImage.OSDiskConfiguration.OSState.GENERALIZED).name("capturedimage")
                    .label("CapturedImage").recommendedVMSize(RoleSize.Type.MEDIUM).build();

            VirtualMachineApi api = api(server.getUrl("/")).
                    getVirtualMachineApiForDeploymentInService("mydeployment", "myservice");

            assertEquals(api.capture("myvirtualmachine", captureParams), "request-1");
            assertSent(server, "POST",
                    "/services/hostedservices/myservice/deployments/mydeployment/roleinstances/" +
                            "myvirtualmachine/Operations",
                    "/vmimageparams_mock.xml");
        } finally {
            server.shutdown();
        }
    }
}
