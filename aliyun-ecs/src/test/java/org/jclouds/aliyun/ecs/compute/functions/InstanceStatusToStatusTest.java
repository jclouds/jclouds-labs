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
package org.jclouds.aliyun.ecs.compute.functions;

import org.jclouds.aliyun.ecs.domain.Instance;
import org.jclouds.compute.domain.NodeMetadata;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for the {@link InstanceStatusToStatus} class.
 */
@Test(groups = "unit", testName = "InstanceStatusToStatusTest")
public class InstanceStatusToStatusTest {
   private InstanceStatusToStatus function;

   @BeforeMethod
   public void setup() {
      function = new InstanceStatusToStatus();
   }

   public void testStatusRunningToStatusRunning() {
      NodeMetadata.Status status = function.apply(Instance.Status.RUNNING);
      assertEquals(status, NodeMetadata.Status.RUNNING);
   }

   public void testStatusStartingToStatusPending() {
      NodeMetadata.Status status = function.apply(Instance.Status.STARTING);
      assertEquals(status, NodeMetadata.Status.PENDING);
   }

   public void testStatusStoppingToStatusPending() {
      NodeMetadata.Status status = function.apply(Instance.Status.STOPPING);
      assertEquals(status, NodeMetadata.Status.PENDING);
   }

   public void testStatusStoppedToStatusSuspended() {
      NodeMetadata.Status status = function.apply(Instance.Status.STOPPED);
      assertEquals(status, NodeMetadata.Status.SUSPENDED);
   }

}
