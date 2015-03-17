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
package org.jclouds.azurecompute.xml;

import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.CloudServiceProperties;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.domain.DataVirtualHardDisk;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.Deployment.InstanceStatus;
import org.jclouds.azurecompute.domain.Deployment.Slot;
import org.jclouds.azurecompute.domain.Deployment.Status;
import org.jclouds.azurecompute.domain.Role.ConfigurationSet;
import org.jclouds.azurecompute.domain.Role.ConfigurationSet.InputEndpoint;
import org.jclouds.azurecompute.domain.Role.OSVirtualHardDisk;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Test(groups = "unit", testName = "CloudServicePropertiesHandlerTest")
public class CloudServicePropertiesHandlerTest extends BaseHandlerTest {

    private static final DateService DATE_SERVICE = new SimpleDateFormatDateService();

    public void test() {
        InputStream is = getClass().getResourceAsStream("/cloudserviceproperties.xml");
        CloudServiceProperties result = factory.create(
                new CloudServicePropertiesHandler(DATE_SERVICE, new DeploymentHandler(
                        new VirtualIPHandler(),
                        new RoleInstanceHandler(),
                        new RoleHandler(
                                new ConfigurationSetHandler(new InputEndpointHandler(), new SubnetNameHandler()),
                                new OSVirtualHardDiskHandler(),
                                new DataVirtualHardDiskHandler(),
                                new ResourceExtensionReferenceHandler(new ResourceExtensionParameterValueHandler())))))
                .parse(is);
        assertEquals(result, expected());
    }

    public static CloudServiceProperties expected() {
        return CloudServiceProperties.create("neotys",
                URI.create("https://api/services/hostedservices/neotys"),
                "West Europe",
                null,
                "bmVvdHlz",
                "Implicitly created cloud service2012-08-06 14:55",
                CloudServiceProperties.Status.CREATED,
                DATE_SERVICE.iso8601SecondsDateParse("2012-08-06T14:55:17Z"), // created
                DATE_SERVICE.iso8601SecondsDateParse("2012-08-06T15:50:34Z"),
                ImmutableMap.<String, String>builder().build(),
                deploymentList()
        );
    }

    private static List<Deployment> deploymentList() {
        return ImmutableList.of(
                Deployment.create( //
                        "node1855162607153993262-b26", // name
                        Slot.PRODUCTION, // slot
                        Status.RUNNING, // status
                        "node1855162607153993262-b26", // label
                        null, // instanceStateDetails
                        null, // instanceErrorCode
                        ImmutableList.of(Deployment.VirtualIP.create("191.233.85.49", true,
                                "node1855162607153993262-b26ContractContract")), //virtualIPs
                        ImmutableList.of(Deployment.RoleInstance.create(
                                "node1855162607153993262-b26", // roleName
                                "node1855162607153993262-b26", // instanceName
                                InstanceStatus.READY_ROLE, //instanceStatus
                                Deployment.PowerState.STARTED,
                                0,
                                0,
                                RoleSize.Type.BASIC_A0,
                                "10.0.2.6",
                                "node1855162607153993262-b26", // hostname
                                ImmutableList.of(
                                        Deployment.InstanceEndpoint.create(
                                                "tcp_22-22", // name
                                                "191.233.85.49", // vip
                                                22, // publicPort
                                                22, // localPort
                                                "tcp" // protocol
                                        )
                                )
                        )),
                        ImmutableList.of(Role.create(
                                "node1855162607153993262-b26",
                                "PersistentVMRole",
                                null,
                                null,
                                ImmutableList.of(ConfigurationSet.create(
                                        "NetworkConfiguration",
                                        ImmutableList.of(
                                                InputEndpoint.create("tcp_22-22", "tcp", 22, 22, "191.233.85.49",
                                                        false, null, null, null),
                                                InputEndpoint.create("tcp_2375-2375", "tcp", 2375, 2375,
                                                        "191.233.85.49", false, null, null, null)
                                        ),
                                        ImmutableList.of(ConfigurationSet.SubnetName.create("Subnet-1")),
                                        null,
                                        ImmutableList.<ConfigurationSet.PublicIP>of(),
                                        null)),
                                ImmutableList.<Role.ResourceExtensionReference>of(),
                                null,
                                ImmutableList.<DataVirtualHardDisk>of(),
                                OSVirtualHardDisk.create(
                                        "ReadWrite",
                                        "node1855162607153993262-b26-node1855162607153993262-b26-0-201412221704390597",
                                        null,
                                        null,
                                        URI.create(
                                                "https://test.blob.core.windows.net/clockerblob/container-node1855162607153993262-b26.vhd"),
                                        "b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu_DAILY_BUILD-trusty-14_04_1-LTS-amd64-server-20141212-en-us-30GB",
                                        OSImage.Type.LINUX),
                                RoleSize.Type.BASIC_A0,
                                null,
                                null
                        )),
                        "jclouds" // virtualNetworkName
                )
        );
    }
}
