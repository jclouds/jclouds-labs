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
import java.io.InputStream;
import java.net.URI;

import org.jclouds.azurecompute.domain.DataVirtualHardDisk;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "RoleHandlerTest")
public class RoleHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/role.xml");
      Role result = factory.create(new RoleHandler(new ConfigurationSetHandler(new InputEndpointHandler(), new SubnetNameHandler()),
              new OSVirtualHardDiskHandler(),
              new DataVirtualHardDiskHandler(),
              new ResourceExtensionReferenceHandler(new ResourceExtensionParameterValueHandler()))).parse(is);

      assertEquals(result, expected());
   }

   public static Role expected() {
      return Role.create(
              "testvnetsg02",
              "PersistentVMRole",
              null,
              null,
              ImmutableList.of(Role.ConfigurationSet.create(
                              "NetworkConfiguration",
                              ImmutableList.of(
                                      Role.ConfigurationSet.InputEndpoint.create("PowerShell", "tcp", 5986, 5986, "23.101.193.92", false, null, null, null),
                                      Role.ConfigurationSet.InputEndpoint.create("Remote Desktop", "tcp", 3389, 59440, "23.101.193.92", false, null, null, null)
                              ),
                              ImmutableList.of(Role.ConfigurationSet.SubnetName.create("Subnet-1")),
                              null,
                              ImmutableList.<Role.ConfigurationSet.PublicIP>of(),
                              "vnetnsgsg01")
              ),
              ImmutableList.of(
                      Role.ResourceExtensionReference.create("BGInfo", "Microsoft.Compute", "BGInfo", "1.*", ImmutableList.<Role.ResourceExtensionReference.ResourceExtensionParameterValue>of(), "Enable")
              ),
              null,
              ImmutableList.<DataVirtualHardDisk>of(),
              Role.OSVirtualHardDisk.create(
                      "ReadWrite",
                      "testvnetsg02-testvnetsg02-0-201502180825130518",
                      null,
                      null,
                      URI.create("https://portalvhdsxz8nc6chc32j1.blob.core.windows.net/vhds/testvnetsg02-testvnetsg02-2015-02-18.vhd"),
                      "a699494373c04fc0bc8f2bb1389d6106__Windows-Server-2012-R2-201412.01-en.us-127GB.vhd",
                      OSImage.Type.WINDOWS),
              RoleSize.Type.SMALL,
              true,
              null
      );
   }
}
