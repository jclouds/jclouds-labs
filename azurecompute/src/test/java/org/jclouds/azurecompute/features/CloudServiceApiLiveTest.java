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
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.azurecompute.domain.CloudService.Status.UNRECOGNIZED;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import java.util.List;
import java.util.logging.Logger;

import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.CloudService.Status;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

@Test(groups = "live", testName = "CloudServiceApiLiveTest", singleThreaded = true)
public class CloudServiceApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String CLOUD_SERVICE = (System.getProperty("user.name") + "cloudservice").toLowerCase();

   private Predicate<CloudService> cloudServiceCreated;
   private Predicate<CloudService> cloudServiceGone;

   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      cloudServiceCreated = retry(new Predicate<CloudService>() {
         public boolean apply(CloudService input) {
            return api().get(input.name()).status() == Status.CREATED;
         }
      }, 600, 5, 5, SECONDS);
      cloudServiceGone = retry(new Predicate<CloudService>() {
         public boolean apply(CloudService input) {
            return api().get(input.name()) == null;
         }
      }, 600, 5, 5, SECONDS);
   }

   private CloudService cloudService;

   public void testCreate() {

      String requestId = api().createWithLabelInLocation(CLOUD_SERVICE, CLOUD_SERVICE, location);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);

      cloudService = api().get(CLOUD_SERVICE);
      Logger.getAnonymousLogger().info("created cloudService: " + cloudService);

      assertEquals(cloudService.name(), CLOUD_SERVICE);

      checkHostedService(cloudService);

      assertTrue(cloudServiceCreated.apply(cloudService), cloudService.toString());
      cloudService = api().get(cloudService.name());
      Logger.getAnonymousLogger().info("cloudService available: " + cloudService);

   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      CloudService foundCloudService = api().get(cloudService.name());
      assertThat(foundCloudService).isEqualToComparingFieldByField(cloudService);
   }

   @Test(dependsOnMethods = "testGet")
   public void testDelete() {
      String requestId = api().delete(cloudService.name());
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);

      assertTrue(cloudServiceGone.apply(cloudService), cloudService.toString());
      Logger.getAnonymousLogger().info("cloudService deleted: " + cloudService);
   }

   @Override @AfterClass(groups = "live", alwaysRun = true)
   protected void tearDown() {
      String requestId = api().delete(CLOUD_SERVICE);
      if (requestId != null) {
         operationSucceeded.apply(requestId);
      }

      super.tearDown();
   }

   public void testList() {
      List<CloudService> response = api().list();

      for (CloudService cloudService : response) {
         checkHostedService(cloudService);
      }

      if (!response.isEmpty()) {
         CloudService cloudService = response.iterator().next();
         assertEquals(api().get(cloudService.name()), cloudService);
      }
   }

   private void checkHostedService(CloudService cloudService) {
      assertNotNull(cloudService.name(), "ServiceName cannot be null for " + cloudService);
      assertTrue(cloudService.location() != null || cloudService.affinityGroup() != null,
            "Location or AffinityGroup must be present for " + cloudService);
      assertNotNull(cloudService.label(), "Label cannot be null for " + cloudService);
      assertNotNull(cloudService.status(), "Status cannot be null for " + cloudService);
      assertNotEquals(cloudService.status(), UNRECOGNIZED, "Status cannot be UNRECOGNIZED for " + cloudService);
      assertNotNull(cloudService.created(), "Created cannot be null for " + cloudService);
      assertNotNull(cloudService.lastModified(), "LastModified cannot be null for " + cloudService);
      assertNotNull(cloudService.extendedProperties(), "ExtendedProperties cannot be null for " + cloudService);
   }

   private CloudServiceApi api() {
      return api.getCloudServiceApi();
   }
}
