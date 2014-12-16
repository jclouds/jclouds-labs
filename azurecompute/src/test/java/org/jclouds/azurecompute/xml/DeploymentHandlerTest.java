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

import static org.jclouds.azurecompute.xml.DeploymentHandler.parseInstanceStatus;
import static org.testng.Assert.assertEquals;
import java.io.InputStream;
import java.net.URI;

import org.jclouds.azurecompute.domain.DataVirtualHardDisk;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Deployment.InstanceStatus;
import org.jclouds.azurecompute.domain.Deployment.Slot;
import org.jclouds.azurecompute.domain.Deployment.Status;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.domain.Role.ConfigurationSet;
import org.jclouds.azurecompute.domain.Role.ConfigurationSet.InputEndpoint;
import org.jclouds.azurecompute.domain.Role.OSVirtualHardDisk;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "DeploymentHandlerTest")
public class DeploymentHandlerTest extends BaseHandlerTest {

   /**
    * Covers values listed <a href="http://msdn.microsoft.com/en-us/library/azure/ee460804.aspx#RoleInstanceList">here</a>.
    */
   public void parseInstanceStatus_Recognized() {
      assertEquals(parseInstanceStatus("Unknown"), InstanceStatus.UNKNOWN);
      assertEquals(parseInstanceStatus("CreatingVM"), InstanceStatus.CREATING_VM);
      assertEquals(parseInstanceStatus("StartingVM"), InstanceStatus.STARTING_VM);
      assertEquals(parseInstanceStatus("CreatingRole"), InstanceStatus.CREATING_ROLE);
      assertEquals(parseInstanceStatus("StartingRole"), InstanceStatus.STARTING_ROLE);
      assertEquals(parseInstanceStatus("ReadyRole"), InstanceStatus.READY_ROLE);
      assertEquals(parseInstanceStatus("BusyRole"), InstanceStatus.BUSY_ROLE);
      assertEquals(parseInstanceStatus("StoppingRole"), InstanceStatus.STOPPING_ROLE);
      assertEquals(parseInstanceStatus("StoppingVM"), InstanceStatus.STOPPING_VM);
      assertEquals(parseInstanceStatus("DeletingVM"), InstanceStatus.DELETING_VM);
      assertEquals(parseInstanceStatus("StoppedVM"), InstanceStatus.STOPPED_VM);
      assertEquals(parseInstanceStatus("RestartingRole"), InstanceStatus.RESTARTING_ROLE);
      assertEquals(parseInstanceStatus("CyclingRole"), InstanceStatus.CYCLING_ROLE);
      assertEquals(parseInstanceStatus("FailedStartingRole"), InstanceStatus.FAILED_STARTING_ROLE);
      assertEquals(parseInstanceStatus("FailedStartingVM"), InstanceStatus.FAILED_STARTING_VM);
      assertEquals(parseInstanceStatus("UnresponsiveRole"), InstanceStatus.UNRESPONSIVE_ROLE);
      assertEquals(parseInstanceStatus("StoppedDeallocated"), InstanceStatus.STOPPED_DEALLOCATED);
      assertEquals(parseInstanceStatus("Preparing"), InstanceStatus.PREPARING);
   }

   public void parseInstanceStatus_Unrecognized() {
      assertEquals(parseInstanceStatus("FooAddedToday"), InstanceStatus.UNRECOGNIZED);
   }

   public void test() {
      InputStream is = getClass().getResourceAsStream("/deployment.xml");
      Deployment result = factory.create(new DeploymentHandler(
              new VirtualIPHandler(),
              new RoleInstanceHandler(),
              new RoleHandler(
                      new ConfigurationSetHandler(new InputEndpointHandler(), new SubnetNameHandler()),
                      new OSVirtualHardDiskHandler(),
                      new DataVirtualHardDiskHandler(),
                      new ResourceExtensionReferenceHandler(new ResourceExtensionParameterValueHandler()))))
              .parse(is);

      assertEquals(result, expected());
   }

   public static Deployment expected() {
      return Deployment.create( //
              "node1855162607153993262-b26", // name
              Slot.PRODUCTION, // slot
              Status.RUNNING, // status
              "node1855162607153993262-b26", // label
              null, // instanceStateDetails
              null, // instanceErrorCode
              ImmutableList.of(Deployment.VirtualIP.create("191.233.85.49", true, "node1855162607153993262-b26ContractContract")), //virtualIPs
              ImmutableList.of(Deployment.RoleInstance.create(
                      "node1855162607153993262-b26", // roleName
                      "node1855162607153993262-b26", // instanceName
                      InstanceStatus.READY_ROLE, //instanceStatus
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
                                      InputEndpoint.create("tcp_22-22", "tcp", 22, 22, "191.233.85.49", false, null, null, null),
                                      InputEndpoint.create("tcp_2375-2375", "tcp", 2375, 2375, "191.233.85.49", false, null, null, null)
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
                              URI.create("https://test.blob.core.windows.net/clockerblob/container-node1855162607153993262-b26.vhd"),
                              "b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu_DAILY_BUILD-trusty-14_04_1-LTS-amd64-server-20141212-en-us-30GB",
                              OSImage.Type.LINUX),
                      RoleSize.Type.BASIC_A0,
                      null,
                      null
                      )),
              "jclouds" // virtualNetworkName
      );
   }
}
