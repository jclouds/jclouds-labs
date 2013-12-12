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
package org.jclouds.management.config;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.apis.Compute;
import org.jclouds.management.ComputeManagement;
import org.jclouds.management.ManagementContext;
import org.jclouds.providers.JcloudsTestComputeProviderMetadata;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

@Test(groups = "unit", testName = "ManagementLifecycleTest")
public class ManagementLifecycleTest {


   @Test
   void testManagementLifecycle()  {
      //Test that the ManagementLifeCycle module properly listens for view creation events and context destruction.
      ManagementContext managementContext = createMock(ManagementContext.class);

      managementContext.register(anyObject(Compute.class));
      expectLastCall().once();
      managementContext.manage(anyObject(ComputeManagement.class), eq("testname"));
      expectLastCall().once();
      managementContext.unmanage(anyObject(ComputeManagement.class), eq("testname"));
      expectLastCall().once();
      managementContext.unregister(anyObject(Compute.class));
      expectLastCall().once();
      replay(managementContext);

      Compute compute = ContextBuilder.newBuilder(new JcloudsTestComputeProviderMetadata()).name("testname")
              .credentials("user", "password")
              .modules(ImmutableSet.<Module>builder().add(new ManagementLifecycle(managementContext)).build()).build(Compute.class);
      compute.unwrap().close();
      verify(managementContext);
   }

}
