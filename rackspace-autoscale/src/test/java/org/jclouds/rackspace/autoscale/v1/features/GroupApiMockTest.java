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
package org.jclouds.rackspace.autoscale.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.rackspace.autoscale.v1.AutoscaleApi;
import org.jclouds.rackspace.autoscale.v1.domain.Group;
import org.jclouds.rackspace.autoscale.v1.domain.GroupConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.GroupState;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration.LaunchConfigurationType;
import org.jclouds.rackspace.autoscale.v1.domain.LoadBalancer;
import org.jclouds.rackspace.autoscale.v1.domain.Personality;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy.ScalingPolicyTargetType;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy.ScalingPolicyType;
import org.jclouds.rackspace.autoscale.v1.internal.BaseAutoscaleApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests GroupApi Guice wiring and parsing
 *
 * @author Zack Shoylev
 */
@Test//(groups = "unit", testName = "GroupApiMockTest")
public class GroupApiMockTest extends BaseAutoscaleApiMockTest {

   public void testCreateGroup() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/autoscale_groups_create_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         GroupConfiguration groupConfiguration = GroupConfiguration.builder()
               .maxEntities(10)
               .cooldown(360)
               .name("testscalinggroup198547")
               .minEntities(0)
               .metadata(ImmutableMap.of("gc_meta_key_2", "gc_meta_value_2", "gc_meta_key_1", "gc_meta_value_1"))
               .build();

         LaunchConfiguration launchConfiguration = LaunchConfiguration.builder()
               .loadBalancers(ImmutableList.of(LoadBalancer.builder().port(8080).id(9099).build()))
               .serverName("autoscale_server")
               .serverImageRef("0d589460-f177-4b0f-81c1-8ab8903ac7d8")
               .serverFlavorRef("2")
               .serverDiskConfig("AUTO")
               .serverMetadata(ImmutableMap.of("build_config", "core", "meta_key_1", "meta_value_1", "meta_key_2", "meta_value_2"))
               .networks(ImmutableList.of("11111111-1111-1111-1111-111111111111", "00000000-0000-0000-0000-000000000000"))
               .personalities(ImmutableList.of(Personality.builder().path("/root/.csivh").contents("VGhpcyBpcyBhIHRlc3QgZmlsZS4=").build()))
               .type(LaunchConfigurationType.LAUNCH_SERVER)
               .build();

         List<CreateScalingPolicy> scalingPolicies = Lists.newArrayList();

         CreateScalingPolicy scalingPolicy = CreateScalingPolicy.builder()
               .cooldown(0)
               .type(ScalingPolicyType.WEBHOOK)
               .name("scale up by 1")
               .targetType(ScalingPolicyTargetType.INCREMENTAL)
               .target("1")
               .build();
         scalingPolicies.add(scalingPolicy);

         Group g = api.create(groupConfiguration, launchConfiguration, scalingPolicies);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups", "/autoscale_groups_create_request.json");

         /*
          * Check response
          */
         assertNotNull(g);
         assertEquals(g.getId(), "6791761b-821a-4d07-820d-0b2afc7dd7f6");
         assertEquals(g.getLinks().size(), 1);
         assertEquals(g.getLinks().get(0).getHref().toString(), "https://ord.autoscale.api.rackspacecloud.com/v1.0/829409/groups/6791761b-821a-4d07-820d-0b2afc7dd7f6/");
         assertEquals(g.getLinks().get(0).getRelation(), Link.Relation.SELF);

         assertEquals(g.getScalingPolicies().get(0).getId(), "dceb14ac-b2b3-4f06-aac9-a5b6cd5d40e1");
         assertEquals(g.getScalingPolicies().get(0).getLinks().size(), 1);
         assertEquals(g.getScalingPolicies().get(0).getLinks().get(0).getHref().toString(), "https://ord.autoscale.api.rackspacecloud.com/v1.0/829409/groups/6791761b-821a-4d07-820d-0b2afc7dd7f6/policies/dceb14ac-b2b3-4f06-aac9-a5b6cd5d40e1/");
         assertEquals(g.getScalingPolicies().get(0).getLinks().get(0).getRelation(), Link.Relation.SELF);
         assertEquals(g.getScalingPolicies().get(0).getCooldown(), 0);
         assertEquals(g.getScalingPolicies().get(0).getTarget(), "1");
         assertEquals(g.getScalingPolicies().get(0).getTargetType(), ScalingPolicyTargetType.INCREMENTAL);
         assertEquals(g.getScalingPolicies().get(0).getType(), ScalingPolicyType.WEBHOOK);
         assertEquals(g.getScalingPolicies().get(0).getName(), "scale up by 1");

         assertEquals(g.getLaunchConfiguration().getLoadBalancers().size(), 1);
         assertEquals(g.getLaunchConfiguration().getLoadBalancers().get(0).getId(), 9099);
         assertEquals(g.getLaunchConfiguration().getLoadBalancers().get(0).getPort(), 8080);
         assertEquals(g.getLaunchConfiguration().getServerName(), "autoscale_server");
         assertEquals(g.getLaunchConfiguration().getServerImageRef(), "0d589460-f177-4b0f-81c1-8ab8903ac7d8");
         assertEquals(g.getLaunchConfiguration().getServerFlavorRef(), "2");
         assertEquals(g.getLaunchConfiguration().getServerDiskConfig(), "AUTO");
         assertEquals(g.getLaunchConfiguration().getPersonalities().size(), 1);
         assertEquals(g.getLaunchConfiguration().getPersonalities().get(0).getPath(), "/root/.csivh");
         assertEquals(g.getLaunchConfiguration().getPersonalities().get(0).getContents(), "VGhpcyBpcyBhIHRlc3QgZmlsZS4=");
         assertEquals(g.getLaunchConfiguration().getNetworks().size(), 2);
         assertEquals(g.getLaunchConfiguration().getNetworks().get(0), "11111111-1111-1111-1111-111111111111");
         assertEquals(g.getLaunchConfiguration().getNetworks().get(1), "00000000-0000-0000-0000-000000000000");
         assertEquals(g.getLaunchConfiguration().getServerMetadata().size(), 3);
         assertTrue(g.getLaunchConfiguration().getServerMetadata().containsKey("build_config"));
         assertTrue(g.getLaunchConfiguration().getServerMetadata().containsValue("core"));
         assertEquals(g.getLaunchConfiguration().getType(), LaunchConfigurationType.LAUNCH_SERVER);

         assertEquals(g.getGroupConfiguration().getMaxEntities(), 10);
         assertEquals(g.getGroupConfiguration().getCooldown(), 360);
         assertEquals(g.getGroupConfiguration().getName(), "testscalinggroup198547");
         assertEquals(g.getGroupConfiguration().getMinEntities(), 0);
         assertEquals(g.getGroupConfiguration().getMetadata().size(), 2);
         assertTrue(g.getGroupConfiguration().getMetadata().containsKey("gc_meta_key_2"));
         assertTrue(g.getGroupConfiguration().getMetadata().containsValue("gc_meta_value_2"));
      } finally {
         server.shutdown();
      }
   }

   public void testCreateGroupFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_groups_create_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         GroupConfiguration groupConfiguration = GroupConfiguration.builder()
               .maxEntities(10)
               .cooldown(360)
               .name("testscalinggroup198547")
               .minEntities(0)
               .metadata(ImmutableMap.of("gc_meta_key_2", "gc_meta_value_2", "gc_meta_key_1", "gc_meta_value_1"))
               .build();

         LaunchConfiguration launchConfiguration = LaunchConfiguration.builder()
               .loadBalancers(ImmutableList.of(LoadBalancer.builder().port(8080).id(9099).build()))
               .serverName("autoscale_server")
               .serverImageRef("0d589460-f177-4b0f-81c1-8ab8903ac7d8")
               .serverFlavorRef("2")
               .serverDiskConfig("AUTO")
               .serverMetadata(ImmutableMap.of("build_config", "core", "meta_key_1", "meta_value_1", "meta_key_2", "meta_value_2"))
               .networks(ImmutableList.of("11111111-1111-1111-1111-111111111111", "00000000-0000-0000-0000-000000000000"))
               .personalities(ImmutableList.of(Personality.builder().path("/root/.csivh").contents("VGhpcyBpcyBhIHRlc3QgZmlsZS4=").build()))
               .type(LaunchConfigurationType.LAUNCH_SERVER)
               .build();

         List<CreateScalingPolicy> scalingPolicies = Lists.newArrayList();

         CreateScalingPolicy scalingPolicy = CreateScalingPolicy.builder()
               .cooldown(0)
               .type(ScalingPolicyType.WEBHOOK)
               .name("scale up by 1")
               .targetType(ScalingPolicyTargetType.INCREMENTAL)
               .target("1")
               .build();
         scalingPolicies.add(scalingPolicy);

         Group g = api.create(groupConfiguration, launchConfiguration, scalingPolicies);
         assertNull(g);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups", "/autoscale_groups_create_request.json");

      } finally {
         server.shutdown();
      }
   }

   public void testListGroups() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/autoscale_groups_list_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         FluentIterable<GroupState> groupStates = api.listGroupStates();

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups");

         /*
          * Check response
          */
         assertEquals(groupStates.size(), 2);

         assertEquals(groupStates.get(0).getGroupInstances().size(), 0);
         assertEquals(groupStates.get(0).getActiveCapacity(), 0);
         assertEquals(groupStates.get(0).getDesiredCapacity(), 0);
         assertEquals(groupStates.get(0).getId(), "e41380ae-173c-4b40-848a-25c16d7fa83d");
         assertEquals(groupStates.get(0).getLinks().size(), 1);
         assertEquals(groupStates.get(0).getLinks().get(0).getHref().toString(), "https://dfw.autoscale.api.rackspacecloud.com/v1.0/676873/groups/e41380ae-173c-4b40-848a-25c16d7fa83d/");
         assertEquals(groupStates.get(0).getLinks().get(0).getRelation(), Link.Relation.SELF);
         assertEquals(groupStates.get(0).getPaused(), false);
         assertEquals(groupStates.get(0).getPendingCapacity(), 0);
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = org.jclouds.rest.ResourceNotFoundException.class)
   public void testListGroupsFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_groups_list_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         FluentIterable<GroupState> groupStates = api.listGroupStates();

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups");

         /*
          * Check response
          */
         assertEquals(groupStates.size(), 0);
      } finally {
         server.shutdown();
      }
   }

   public void testGetGroup() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/autoscale_groups_get_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         Group g = api.get("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890");

         /*
          * Check response
          */
         assertEquals(g.getId(), "1234567890");
         assertEquals(g.getScalingPolicies().size(), 3);
      } finally {
         server.shutdown();
      }
   }

   public void testGetGroupFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_groups_get_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         Group g = api.get("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890");

         /*
          * Check response
          */
         assertNull(g);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteGroup() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         boolean success = api.delete("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v1.0/888888/groups/1234567890");

         /*
          * Check response
          */
         assertTrue(success);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteGroupFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         boolean success = api.delete("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v1.0/888888/groups/1234567890");

         /*
          * Check response
          */
         assertFalse(success);
      } finally {
         server.shutdown();
      }
   }

   public void testGetGroupState() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/autoscale_groups_state_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         GroupState gs = api.getState("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890/state");

         /*
          * Check response
          */
         assertEquals(gs.getId(), "1234567890");
         assertEquals(gs.getGroupInstances().size(), 2);
         assertEquals(gs.getGroupInstances().get(0).getId(), "444444");
      } finally {
         server.shutdown();
      }
   }

   public void testGetGroupStateFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_groups_state_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         GroupState gs = api.getState("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890/state");

         /*
          * Check response
          */
         assertNull(gs);
      } finally {
         server.shutdown();
      }
   }

   public void testPause() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         boolean success = api.pause("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/1234567890/pause");

         /*
          * Check response
          */
         assertTrue(success);
      } finally {
         server.shutdown();
      }
   }

   public void testPauseFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         boolean success = api.pause("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/1234567890/pause");

         /*
          * Check response
          */
         assertFalse(success);
      } finally {
         server.shutdown();
      }
   }

   public void testResume() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         boolean success = api.resume("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/1234567890/resume");

         /*
          * Check response
          */
         assertTrue(success);
      } finally {
         server.shutdown();
      }
   }

   public void testResumeFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         boolean success = api.resume("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/1234567890/resume");

         /*
          * Check response
          */
         assertFalse(success);
      } finally {
         server.shutdown();
      }
   }

   public void testGetGroupConfiguration() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/autoscale_groups_configuration_get_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         GroupConfiguration gc = api.getGroupConfiguration("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890/config");

         /*
          * Check response
          */
         assertEquals(gc.getCooldown(), 60);
         assertEquals(gc.getMaxEntities(), 100);
      } finally {
         server.shutdown();
      }
   }

   public void testGetGroupConfigurationFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_groups_configuration_get_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         GroupConfiguration gc = api.getGroupConfiguration("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890/config");

         /*
          * Check response
          */
         assertNull(gc);
      } finally {
         server.shutdown();
      }
   }

   public void testGetLaunchConfiguration() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/autoscale_groups_launch_configuration_get_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         LaunchConfiguration lc = api.getLaunchConfiguration("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890/launch");

         /*
          * Check response
          */
         assertEquals(lc.getServerName(), "webhead");
         assertEquals(lc.getType(), LaunchConfigurationType.LAUNCH_SERVER);
      } finally {
         server.shutdown();
      }
   }

   public void testGetLaunchConfigurationFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_groups_launch_configuration_get_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         LaunchConfiguration lc = api.getLaunchConfiguration("1234567890");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890/launch");

         /*
          * Check response
          */
         assertNull(lc);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateGroupConfiguration() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         GroupConfiguration gc = GroupConfiguration.builder()
               .name("workers")
               .cooldown(60)
               .minEntities(5)
               .maxEntities(100)
               .metadata(ImmutableMap.of("firstkey", "this is a string", "secondkey", "1"))
               .build();

         boolean result = api.updateGroupConfiguration("1234567890", gc);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1.0/888888/groups/1234567890/config", "/autoscale_groups_update_configuration_request.json");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateGroupConfigurationFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         GroupConfiguration gc = GroupConfiguration.builder()
               .name("workers")
               .cooldown(60)
               .minEntities(5)
               .maxEntities(100)
               .metadata(ImmutableMap.of("firstkey", "this is a string", "secondkey", "1"))
               .build();

         boolean result = api.updateGroupConfiguration("1234567890", gc);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1.0/888888/groups/1234567890/config", "/autoscale_groups_update_configuration_request.json");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateGroupLaunchConfiguration() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         LaunchConfiguration lc = LaunchConfiguration.builder()
               .loadBalancers(ImmutableList.of(LoadBalancer.builder().port(8080).id(9099).build()))
               .serverName("autoscale_server")
               .serverImageRef("0d589460-f177-4b0f-81c1-8ab8903ac7d8")
               .serverFlavorRef("2")
               .serverDiskConfig("AUTO")
               .serverMetadata(ImmutableMap.of("build_config", "core", "meta_key_1", "meta_value_1", "meta_key_2", "meta_value_2"))
               .networks(ImmutableList.of("11111111-1111-1111-1111-111111111111", "00000000-0000-0000-0000-000000000000"))
               .personalities(ImmutableList.of(Personality.builder().path("/root/.csivh").contents("VGhpcyBpcyBhIHRlc3QgZmlsZS4=").build()))
               .type(LaunchConfigurationType.LAUNCH_SERVER)
               .build();

         boolean result = api.updateLaunchConfiguration("1234567890", lc);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1.0/888888/groups/1234567890/launch", "/autoscale_groups_update_launch_configuration_request.json");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateGroupLaunchConfigurationFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         GroupApi api = autoscaleApi.getGroupApiForZone("DFW");

         LaunchConfiguration lc = LaunchConfiguration.builder()
               .loadBalancers(ImmutableList.of(LoadBalancer.builder().port(8080).id(9099).build()))
               .serverName("autoscale_server")
               .serverImageRef("0d589460-f177-4b0f-81c1-8ab8903ac7d8")
               .serverFlavorRef("2")
               .serverDiskConfig("AUTO")
               .serverMetadata(ImmutableMap.of("build_config", "core", "meta_key_1", "meta_value_1", "meta_key_2", "meta_value_2"))
               .networks(ImmutableList.of("11111111-1111-1111-1111-111111111111", "00000000-0000-0000-0000-000000000000"))
               .personalities(ImmutableList.of(Personality.builder().path("/root/.csivh").contents("VGhpcyBpcyBhIHRlc3QgZmlsZS4=").build()))
               .type(LaunchConfigurationType.LAUNCH_SERVER)
               .build();

         boolean result = api.updateLaunchConfiguration("1234567890", lc);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1.0/888888/groups/1234567890/launch", "/autoscale_groups_update_launch_configuration_request.json");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }
}
