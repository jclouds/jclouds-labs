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
package org.jclouds.azurecompute.binders;

import static org.testng.Assert.assertEquals;
import java.net.URI;

import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code DeploymentParamsToXML}
 */
@Test(groups = "unit")
public class DeploymentParamsToXMLTest {
   Injector injector = Guice.createInjector();
   DeploymentParamsToXML binder = injector
         .getInstance(DeploymentParamsToXML.class);

   public void testDeploymentParamsToXmlString() {
      DeploymentParams.Builder paramsBuilder = DeploymentParams.builder()
              .name("name")
              .os(OSImage.Type.LINUX)
              .username("loginUser")
              .password("loginPassword")
              .sourceImageName("sourceImageName")
              .mediaLink(URI.create("http://medialink"))
              .size(RoleSize.Type.BASIC_A0)
              .externalEndpoints(ImmutableSet.of(DeploymentParams.ExternalEndpoint.inboundTcpToLocalPort(22, 22)))
              .virtualNetworkName("virtualNetworkName")
              .reservedIPName("reservedIPName")
              .subnetNames(ImmutableList.of("subnetName"));

      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost")
            .addFormParam("InstanceId", "i-foo").build();
      request = binder.bindToRequest(request, paramsBuilder.build());
      assertEquals(
            request.getPayload().getRawContent(),
            "<Deployment xmlns=\"http://schemas.microsoft.com/windowsazure\"><Name>name</Name><DeploymentSlot>Production</DeploymentSlot><Label>name</Label><RoleList><Role><RoleName>name</RoleName><RoleType>PersistentVMRole</RoleType><ConfigurationSets><ConfigurationSet><ConfigurationSetType>LinuxProvisioningConfiguration</ConfigurationSetType><HostName>name</HostName><UserName>loginUser</UserName><UserPassword>loginPassword</UserPassword><DisableSshPasswordAuthentication>false</DisableSshPasswordAuthentication><SSH><PublicKeys/><KeyPairs/></SSH></ConfigurationSet><ConfigurationSet><ConfigurationSetType>NetworkConfiguration</ConfigurationSetType><InputEndpoints><InputEndpoint><LocalPort>22</LocalPort><Name>tcp_22-22</Name><Port>22</Port><Protocol>tcp</Protocol></InputEndpoint></InputEndpoints><SubnetNames><SubnetName>subnetName</SubnetName></SubnetNames></ConfigurationSet></ConfigurationSets><DataVirtualHardDisks/><OSVirtualHardDisk><HostCaching>ReadWrite</HostCaching><MediaLink>http://medialink</MediaLink><SourceImageName>sourceImageName</SourceImageName><OS>Linux</OS></OSVirtualHardDisk><RoleSize>Basic_A0</RoleSize></Role></RoleList><VirtualNetworkName>virtualNetworkName</VirtualNetworkName><ReservedIPName>reservedIPName</ReservedIPName></Deployment>");
   }

}
