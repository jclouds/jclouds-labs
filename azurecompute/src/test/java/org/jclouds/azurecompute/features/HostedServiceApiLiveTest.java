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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.azurecompute.domain.HostedService.Status.UNRECOGNIZED;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.jclouds.azurecompute.domain.HostedService;
import org.jclouds.azurecompute.domain.HostedService.Status;
import org.jclouds.azurecompute.domain.Operation;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "HostedServiceApiLiveTest")
public class HostedServiceApiLiveTest extends BaseAzureComputeApiLiveTest {

   public static final String HOSTED_SERVICE = (System.getProperty("user.name") + "-jclouds-hostedService")
         .toLowerCase();

   private Predicate<String> operationSucceeded;
   private Predicate<HostedService> hostedServiceCreated;
   private Predicate<HostedService> hostedServiceGone;

   private String location;

   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      // TODO: filter locations on those who have compute
      location = Iterables.get(api.getLocationApi().list(), 0).name();
      operationSucceeded = retry(new Predicate<String>() {
         public boolean apply(String input) {
            return api.getOperationApi().get(input).getStatus() == Operation.Status.SUCCEEDED;
         }
      }, 600, 5, 5, SECONDS);
      hostedServiceCreated = retry(new Predicate<HostedService>() {
         public boolean apply(HostedService input) {
            return api().get(input.name()).status() == Status.CREATED;
         }
      }, 600, 5, 5, SECONDS);
      hostedServiceGone = retry(new Predicate<HostedService>() {
         public boolean apply(HostedService input) {
            return api().get(input.name()) == null;
         }
      }, 600, 5, 5, SECONDS);
   }

   private HostedService hostedService;

   public void testCreateHostedService() {

      String requestId = api().createServiceWithLabelInLocation(HOSTED_SERVICE, HOSTED_SERVICE, location);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);

      hostedService = api().get(HOSTED_SERVICE);
      Logger.getAnonymousLogger().info("created hostedService: " + hostedService);

      assertEquals(hostedService.name(), HOSTED_SERVICE);

      checkHostedService(hostedService);

      assertTrue(hostedServiceCreated.apply(hostedService), hostedService.toString());
      hostedService = api().get(hostedService.name());
      Logger.getAnonymousLogger().info("hostedService available: " + hostedService);

   }

   @Test(dependsOnMethods = "testCreateHostedService")
   public void testDeleteHostedService() {
      String requestId = api().delete(hostedService.name());
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);

      assertTrue(hostedServiceGone.apply(hostedService), hostedService.toString());
      Logger.getAnonymousLogger().info("hostedService deleted: " + hostedService);
   }

   @Override @AfterClass(groups = "live")
   protected void tearDown() {
      String requestId = api().delete(HOSTED_SERVICE);
      if (requestId != null) {
         operationSucceeded.apply(requestId);
      }

      super.tearDown();
   }

   @Test
   protected void testList() {
      List<HostedService> response = api().list();

      for (HostedService hostedService : api().list()) {
         checkHostedService(hostedService);
      }

      if (response.size() > 0) {
         HostedService hostedService = response.iterator().next();
         assertEquals(api().get(hostedService.name()), hostedService);
      }
   }

   private void checkHostedService(HostedService hostedService) {
      assertNotNull(hostedService.name(), "ServiceName cannot be null for " + hostedService);
      assertTrue(hostedService.location() != null || hostedService.affinityGroup() != null,
            "Location or AffinityGroup must be present for " + hostedService);
      assertNotNull(hostedService.label(), "Label cannot be null for " + hostedService);
      assertNotNull(hostedService.status(), "Status cannot be null for " + hostedService);
      assertNotEquals(hostedService.status(), UNRECOGNIZED, "Status cannot be UNRECOGNIZED for " + hostedService);
      assertNotNull(hostedService.created(), "Created cannot be null for " + hostedService);
      assertNotNull(hostedService.lastModified(), "LastModified cannot be null for " + hostedService);
      assertNotNull(hostedService.extendedProperties(), "ExtendedProperties cannot be null for " + hostedService);
   }

   private HostedServiceApi api() {
      return api.getHostedServiceApi();
   }
}
