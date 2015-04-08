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

import static org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest.LOCATION;
import static org.testng.Assert.assertTrue;

import com.google.common.base.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jclouds.azurecompute.domain.ReservedIPAddress;
import org.jclouds.azurecompute.domain.ReservedIPAddress.State;
import org.jclouds.azurecompute.domain.ReservedIPAddressParams;
import org.jclouds.azurecompute.internal.AbstractAzureComputeApiLiveTest;
import org.jclouds.util.Predicates2;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ReservedIPAddressApiLiveTest", singleThreaded = true)
public class ReservedIPAddressApiLiveTest extends AbstractAzureComputeApiLiveTest {

   private static final String RESERVED_IP_NAME = String.format("%s%d-%s",
           System.getProperty("user.name"), RAND, ReservedIPAddressApiLiveTest.class.getSimpleName()).toLowerCase();

   @Test
   public void testCreate() {
      final String requestId = api().create(
              ReservedIPAddressParams.builder().name(RESERVED_IP_NAME).location(LOCATION).build());
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      assertTrue(Predicates2.retry(new Predicate<String>() {

         @Override
         public boolean apply(final String input) {
            final ReservedIPAddress res = api().get(input);
            Assert.assertEquals(res.name(), RESERVED_IP_NAME);
            Assert.assertNull(res.label());
            Assert.assertEquals(res.location(), LOCATION);
            Assert.assertNull(res.serviceName());
            Assert.assertNull(res.deploymentName());
            return res.state().equals(State.CREATED);
         }
      }, 60, 5, 5).apply(RESERVED_IP_NAME));
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      final ReservedIPAddress res = api().list().get(0);
      Assert.assertEquals(res.name(), RESERVED_IP_NAME);
      Assert.assertNull(res.label());
      Assert.assertEquals(res.location(), LOCATION);
      Assert.assertNull(res.serviceName());
      Assert.assertNull(res.deploymentName());
   }

   @AfterClass(alwaysRun = true)
   public void testDelete() {
      final ReservedIPAddress res = api().get(RESERVED_IP_NAME);

      if (res != null) {
         final String requestId = api().delete(RESERVED_IP_NAME);
         assertTrue(operationSucceeded.apply(requestId), requestId);
         Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);
      }

      Assert.assertNull(api().get(RESERVED_IP_NAME));
   }

   private ReservedIPAddressApi api() {
      return api.getReservedIPAddressApi();
   }
}
