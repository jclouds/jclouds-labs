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

import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Deployment.InstanceStatus;
import org.jclouds.azurecompute.domain.Deployment.Slot;
import org.jclouds.azurecompute.domain.Deployment.Status;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

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
      Deployment result = factory.create(new DeploymentHandler()).parse(is);

      assertEquals(result, expected());
   }

   public static Deployment expected() {
      return Deployment.create( //
            "deployment_name", // name
            Slot.PRODUCTION, // slot
            Status.RUNNING, // status
            "neotysss", // label
            "role_name_from_role_list", // virtualMachineName
            "instance_name", // instanceName
            InstanceStatus.READY_ROLE, // instanceStatus
            null, // instanceStateDetails
            null, // instanceErrorCode
            RoleSize.MEDIUM, // instanceSize
            "10.59.244.162", // privateIpAddress
            "168.63.27.148" // publicIpAddress
      );
   }
}
