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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

import static org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest.LOCATION;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.List;

import org.jclouds.azurecompute.domain.AffinityGroup;
import org.jclouds.azurecompute.domain.CreateAffinityGroupParams;
import org.jclouds.azurecompute.domain.UpdateAffinityGroupParams;
import org.jclouds.azurecompute.internal.AbstractAzureComputeApiLiveTest;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "AffinityGroupApiLiveTest")
public class AffinityGroupApiLiveTest extends AbstractAzureComputeApiLiveTest {

   private static final String GROUP_NAME = String.format("%3.24s",
           System.getProperty("user.name") + RAND + "-securityGroup");

   private AffinityGroupApi api() {
      return api.getAffinityGroupApi();
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      final List<AffinityGroup> groups = api().list();
      assertFalse(groups.isEmpty());

      final AffinityGroup matching = Iterables.find(groups, new Predicate<AffinityGroup>() {

         @Override
         public boolean apply(final AffinityGroup group) {
            return GROUP_NAME.equals(group.name());
         }
      });
      assertNotNull(matching);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testRead() {
      final AffinityGroup group = api().get(GROUP_NAME);
      assertNotNull(group);
      assertEquals(group.name(), GROUP_NAME);
   }

   public void testCreate() {
      final CreateAffinityGroupParams params = CreateAffinityGroupParams.builder().
              name(GROUP_NAME).
              label(GROUP_NAME).
              location(LOCATION).
              build();

      final String requestId = api().add(params);
      assertTrue(operationSucceeded.apply(requestId), requestId);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testUpdate() {
      final UpdateAffinityGroupParams params = UpdateAffinityGroupParams.builder().
              description(GROUP_NAME + " description").
              build();

      final String requestId = api().update(GROUP_NAME, params);
      assertTrue(operationSucceeded.apply(requestId), requestId);
   }

   @AfterClass(alwaysRun = true)
   public void testDelete() throws Exception {
      final String requestId = api().delete(GROUP_NAME);
      assertTrue(operationSucceeded.apply(requestId), requestId);
   }
}
