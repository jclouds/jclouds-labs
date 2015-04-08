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

import com.google.common.collect.ImmutableList;
import java.util.List;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jclouds.azurecompute.domain.CreateProfileParams;
import org.jclouds.azurecompute.domain.Profile;
import org.jclouds.azurecompute.domain.ProfileDefinition;
import org.jclouds.azurecompute.domain.ProfileDefinitionEndpoint;
import org.jclouds.azurecompute.domain.ProfileDefinitionEndpointParams;
import org.jclouds.azurecompute.domain.ProfileDefinitionParams;
import org.jclouds.azurecompute.domain.UpdateProfileParams;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import static org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest.LOCATION;
import org.jclouds.azurecompute.util.ConflictManagementPredicate;
import static org.jclouds.util.Predicates2.retry;
import org.testng.Assert;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ServiceCertificatesApiLivTest", singleThreaded = true)
public class TrafficManagerApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String CLOUD1 = String.format("%s%d-%s1",
           System.getProperty("user.name"), RAND, TrafficManagerApiLiveTest.class.getSimpleName()).toLowerCase();

   private static final String CLOUD2 = String.format("%s%d-%s2",
           System.getProperty("user.name"), RAND, TrafficManagerApiLiveTest.class.getSimpleName()).toLowerCase();

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      String requestId = api.getCloudServiceApi().createWithLabelInLocation(CLOUD1, CLOUD1, LOCATION);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);

      requestId = api.getCloudServiceApi().createWithLabelInLocation(CLOUD2, CLOUD2, LOCATION);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);
   }

   @Test
   public void createProfile() throws Exception {
      final CreateProfileParams params = CreateProfileParams.builder().
              domain(String.format("%s.trafficmanager.net", CLOUD1)).name(CLOUD1).build();

      final String requestId = api().createProfile(params);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);
   }

   @Test(dependsOnMethods = "createProfile")
   public void createDefinition() throws Exception {
      final ImmutableList.Builder<ProfileDefinitionEndpointParams> endpoints
              = ImmutableList.<ProfileDefinitionEndpointParams>builder();

      endpoints.add(ProfileDefinitionEndpointParams.builder()
              .domain(String.format("%s.cloudapp.net", CLOUD1))
              .status(ProfileDefinition.Status.ENABLED)
              .type(ProfileDefinitionEndpoint.Type.CLOUDSERVICE)
              .weight(1).build());

      endpoints.add(ProfileDefinitionEndpointParams.builder()
              .domain(String.format("%s.cloudapp.net", CLOUD2))
              .status(ProfileDefinition.Status.ENABLED)
              .type(ProfileDefinitionEndpoint.Type.CLOUDSERVICE)
              .weight(1).build());

      final ProfileDefinitionParams params = ProfileDefinitionParams.builder()
              .ttl(300)
              .lb(ProfileDefinition.LBMethod.ROUNDROBIN)
              .path("/")
              .port(80)
              .protocol(ProfileDefinition.Protocol.HTTP)
              .endpoints(endpoints.build())
              .build();

      final String requestId = api().createDefinition(CLOUD1, params);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);
   }

   @Test(dependsOnMethods = "createDefinition")
   public void updateProfile() throws Exception {
      final UpdateProfileParams params = UpdateProfileParams.builder().
              status(ProfileDefinition.Status.DISABLED).build();

      final String requestId = api().updateProfile(CLOUD1, params);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);
   }

   @Test(dependsOnMethods = "createDefinition")
   public void listDefinitions() throws Exception {
      final List<ProfileDefinition> defs = api().listDefinitions(CLOUD1);
      Assert.assertEquals(defs.size(), 1);
      Assert.assertEquals(defs.get(0).endpoints().size(), 2);
      Assert.assertEquals(defs.get(0).monitors().size(), 1);
      Assert.assertEquals(defs.get(0).lb(), ProfileDefinition.LBMethod.ROUNDROBIN);
      Assert.assertEquals(defs.get(0).ttl(), 300, 0);
      Assert.assertEquals(defs.get(0).status(), ProfileDefinition.Status.ENABLED);
      Assert.assertEquals(defs.get(0).monitors().get(0).port(), 80, 0);
      Assert.assertEquals(defs.get(0).monitors().get(0).path(), "/");
      Assert.assertEquals(defs.get(0).endpoints().get(0).type(), ProfileDefinitionEndpoint.Type.CLOUDSERVICE);
      Assert.assertNull(defs.get(0).endpoints().get(0).location());
   }

   @Test(dependsOnMethods = "createDefinition")
   public void getDefinitions() throws Exception {
      final ProfileDefinition def = api().getDefinition(CLOUD1);
      Assert.assertEquals(def.endpoints().size(), 2);
      Assert.assertEquals(def.monitors().size(), 1);
      Assert.assertEquals(def.lb(), ProfileDefinition.LBMethod.ROUNDROBIN);
      Assert.assertEquals(def.ttl(), 300, 0);
      Assert.assertEquals(def.status(), ProfileDefinition.Status.ENABLED);
      Assert.assertEquals(def.monitors().get(0).port(), 80, 0);
      Assert.assertEquals(def.monitors().get(0).path(), "/");
      Assert.assertEquals(def.endpoints().get(0).type(), ProfileDefinitionEndpoint.Type.CLOUDSERVICE);
      Assert.assertNull(def.endpoints().get(0).location());
   }

   @Test(dependsOnMethods = "createDefinition")
   public void listProfile() throws Exception {
      final List<Profile> profs = api().listProfiles();
      Assert.assertFalse(profs.isEmpty());

      final Profile prof = api().getProfile(CLOUD1);
      Assert.assertEquals(prof.domain(), String.format("%s.trafficmanager.net", CLOUD1));
      Assert.assertEquals(prof.name(), CLOUD1);
      Assert.assertEquals(prof.status(), ProfileDefinition.Status.ENABLED);
      Assert.assertEquals(prof.version(), "1");
      Assert.assertFalse(prof.definitions().isEmpty());
   }

   @Override
   @AfterClass(alwaysRun = true)
   protected void tearDown() {
      final String requestId = api().delete(CLOUD1);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);

      retry(new ConflictManagementPredicate(operationSucceeded) {
         @Override
         protected String operation() {
            return api.getCloudServiceApi().delete(CLOUD1);
         }
      }, 600, 30, 30, SECONDS).apply(CLOUD1);

      retry(new ConflictManagementPredicate(operationSucceeded) {
         @Override
         protected String operation() {
            return api.getCloudServiceApi().delete(CLOUD2);
         }
      }, 600, 30, 30, SECONDS).apply(CLOUD2);

      super.tearDown();
   }

   private TrafficManagerApi api() {
      return api.getTrafficManaerApi();
   }
}
