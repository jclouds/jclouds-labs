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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.rackspace.autoscale.v1.domain.Group;
import org.jclouds.rackspace.autoscale.v1.domain.GroupConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.GroupState;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration.LaunchConfigurationType;
import org.jclouds.rackspace.autoscale.v1.domain.LoadBalancer;
import org.jclouds.rackspace.autoscale.v1.domain.Personality;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy.ScalingPolicyTargetType;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy.ScalingPolicyType;
import org.jclouds.rackspace.autoscale.v1.internal.BaseAutoscaleApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Tests GroupApi Guice wiring and parsing
 *
 * @author Zack Shoylev
 */
@Test(groups = "unit", testName = "GroupApiExpectTest")
public class GroupApiExpectTest extends BaseAutoscaleApiExpectTest {

   public void testCreateGroup() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("POST").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_groups_create_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(201).payload(payloadFromResource("/autoscale_groups_create_response.json")).build()
            ).getGroupApiForZone("DFW");

      GroupConfiguration groupConfiguration = GroupConfiguration.builder()
            .maxEntities(10)
            .cooldown(360)
            .name("testscalinggroup198547")
            .minEntities(0)
            .metadata(ImmutableMap.of("gc_meta_key_2","gc_meta_value_2","gc_meta_key_1","gc_meta_value_1"))
            .build();

      LaunchConfiguration launchConfiguration = LaunchConfiguration.builder()
            .loadBalancers(ImmutableList.of(LoadBalancer.builder().port(8080).id(9099).build()))
            .serverName("autoscale_server")
            .serverImageRef("0d589460-f177-4b0f-81c1-8ab8903ac7d8")
            .serverFlavorRef("2")
            .serverDiskConfig("AUTO")
            .serverMetadata(ImmutableMap.of("build_config","core","meta_key_1","meta_value_1","meta_key_2","meta_value_2"))
            .networks(ImmutableList.of("11111111-1111-1111-1111-111111111111","00000000-0000-0000-0000-000000000000"))
            .personalities(ImmutableList.of(Personality.builder().path("/root/.csivh").contents("VGhpcyBpcyBhIHRlc3QgZmlsZS4=").build()))
            .type(LaunchConfigurationType.LAUNCH_SERVER)
            .build();

      List<ScalingPolicy> scalingPolicies = Lists.newArrayList();

      ScalingPolicy scalingPolicy = ScalingPolicy.builder()
            .cooldown(0)
            .type(ScalingPolicyType.WEBHOOK)
            .name("scale up by 1")
            .targetType(ScalingPolicyTargetType.INCREMENTAL)
            .target("1")
            .build();
      scalingPolicies.add(scalingPolicy);

      Group g = api.create(groupConfiguration, launchConfiguration, scalingPolicies);

      assertNotNull(g);
      assertEquals(g.getId(),"6791761b-821a-4d07-820d-0b2afc7dd7f6");
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
   }

   public void testCreateGroupFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("POST").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_groups_create_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/autoscale_groups_create_response.json")).build()
            ).getGroupApiForZone("DFW");

      GroupConfiguration groupConfiguration = GroupConfiguration.builder()
            .maxEntities(10)
            .cooldown(360)
            .name("testscalinggroup198547")
            .minEntities(0)
            .metadata(ImmutableMap.of("gc_meta_key_2","gc_meta_value_2","gc_meta_key_1","gc_meta_value_1"))
            .build();

      LaunchConfiguration launchConfiguration = LaunchConfiguration.builder()
            .loadBalancers(ImmutableList.of(LoadBalancer.builder().port(8080).id(9099).build()))
            .serverName("autoscale_server")
            .serverImageRef("0d589460-f177-4b0f-81c1-8ab8903ac7d8")
            .serverFlavorRef("2")
            .serverDiskConfig("AUTO")
            .serverMetadata(ImmutableMap.of("build_config","core","meta_key_1","meta_value_1","meta_key_2","meta_value_2"))
            .networks(ImmutableList.of("11111111-1111-1111-1111-111111111111","00000000-0000-0000-0000-000000000000"))
            .personalities(ImmutableList.of(Personality.builder().path("/root/.csivh").contents("VGhpcyBpcyBhIHRlc3QgZmlsZS4=").build()))
            .type(LaunchConfigurationType.LAUNCH_SERVER)
            .build();

      List<ScalingPolicy> scalingPolicies = Lists.newArrayList();

      ScalingPolicy scalingPolicy = ScalingPolicy.builder()
            .cooldown(0)
            .type(ScalingPolicyType.WEBHOOK)
            .name("scale up by 1")
            .targetType(ScalingPolicyTargetType.INCREMENTAL)
            .target("1")
            .build();
      scalingPolicies.add(scalingPolicy);

      Group g = api.create(groupConfiguration, launchConfiguration, scalingPolicies);

      assertNull(g);
   }

   public void testListGroups() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/autoscale_groups_list_response.json")).build()
            ).getGroupApiForZone("DFW");

      FluentIterable<GroupState> groupStates = api.listGroupStates();
      assertEquals(groupStates.size(),2);

      assertEquals(groupStates.get(0).getGroupInstances().size(), 0);
      assertEquals(groupStates.get(0).getActiveCapacity(), 0);
      assertEquals(groupStates.get(0).getDesiredCapacity(), 0);
      assertEquals(groupStates.get(0).getId(), "e41380ae-173c-4b40-848a-25c16d7fa83d");
      assertEquals(groupStates.get(0).getLinks().size(), 1);
      assertEquals(groupStates.get(0).getLinks().get(0).getHref().toString(), "https://dfw.autoscale.api.rackspacecloud.com/v1.0/676873/groups/e41380ae-173c-4b40-848a-25c16d7fa83d/");
      assertEquals(groupStates.get(0).getLinks().get(0).getRelation(), Link.Relation.SELF);
      assertEquals(groupStates.get(0).getPaused(), false);
      assertEquals(groupStates.get(0).getPendingCapacity(), 0);
   }

   @Test(expectedExceptions = org.jclouds.rest.ResourceNotFoundException.class)
   public void testListGroupsFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
            ).getGroupApiForZone("DFW");

      FluentIterable<GroupState> groupStates = api.listGroupStates();
      assertEquals(groupStates.size(), 0);
   }

   public void testGetGroup() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/autoscale_groups_get_response.json")).build()
            ).getGroupApiForZone("DFW");

      Group g = api.get("1234567890");
      assertEquals(g.getId(), "1234567890");
      assertEquals(g.getScalingPolicies().size(), 3);
   }

   public void testGetGroupFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
            ).getGroupApiForZone("DFW");

      Group g = api.get("1234567890");
      assertNull(g);
   }

   public void testDeleteGroup() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(204).build()
            ).getGroupApiForZone("DFW");

      boolean success = api.delete("1234567890");
      assertTrue(success);
   }

   public void testDeleteGroupFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(404).build()
            ).getGroupApiForZone("DFW");

      boolean success = api.delete("1234567890");
      assertFalse(success);
   }

   public void testGetGroupState() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/state");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/autoscale_groups_state_response.json")).build()
            ).getGroupApiForZone("DFW");

      GroupState gs = api.getState("1234567890");
      assertEquals(gs.getId(), "1234567890");
      assertEquals(gs.getGroupInstances().size(), 2);
      assertEquals(gs.getGroupInstances().get(0).getId(), "444444");
   }

   public void testGetGroupStateFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/state");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/autoscale_groups_state_response.json")).build()
            ).getGroupApiForZone("DFW");

      GroupState gs = api.getState("1234567890");
      assertNull(gs);
   }

   public void testPause() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/pause");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("POST").build(),
            HttpResponse.builder().statusCode(204).build()
            ).getGroupApiForZone("DFW");

      boolean success = api.pause("1234567890");
      assertTrue(success);
   }

   public void testPauseFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/pause");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("POST").build(),
            HttpResponse.builder().statusCode(404).build()
            ).getGroupApiForZone("DFW");

      boolean success = api.pause("1234567890");
      assertFalse(success);
   }

   public void testResume() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/resume");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("POST").build(),
            HttpResponse.builder().statusCode(204).build()
            ).getGroupApiForZone("DFW");

      boolean success = api.resume("1234567890");
      assertTrue(success);
   }

   public void testResumeFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/resume");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("POST").build(),
            HttpResponse.builder().statusCode(404).build()
            ).getGroupApiForZone("DFW");

      boolean success = api.resume("1234567890");
      assertFalse(success);
   }

   public void testGetGroupConfiguration() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/config");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/autoscale_groups_configuration_get_response.json")).build())
            .getGroupApiForZone("DFW");

      GroupConfiguration gc = api.getGroupConfiguration("1234567890");
      assertEquals(gc.getCooldown(), 60);
      assertEquals(gc.getMaxEntities(), 100);
   }

   public void testGetGroupConfigurationFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/config");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404)
            .payload(payloadFromResource("/autoscale_groups_configuration_get_response.json")).build())
            .getGroupApiForZone("DFW");

      GroupConfiguration gc = api.getGroupConfiguration("1234567890");
      assertEquals(gc, null);
   }

   public void testGetLaunchConfiguration() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/launch");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/autoscale_groups_launch_configuration_get_response.json")).build())
            .getGroupApiForZone("DFW");

      LaunchConfiguration lc = api.getLaunchConfiguration("1234567890");
      assertEquals(lc.getServerName(), "webhead");
      assertEquals(lc.getType(), LaunchConfigurationType.LAUNCH_SERVER);
   }

   public void testGetLaunchConfigurationFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/launch");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404)
            .payload(payloadFromResource("/autoscale_groups_launch_configuration_get_response.json")).build())
            .getGroupApiForZone("DFW");

      LaunchConfiguration lc = api.getLaunchConfiguration("1234567890");
      assertEquals(lc, null);
   }

   public void testUpdateGroupConfiguration() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/config");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("PUT").endpoint(endpoint)
            .payload(payloadFromResourceWithContentType("/autoscale_groups_update_configuration_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200)
            .build())
            .getGroupApiForZone("DFW");

      GroupConfiguration gc = GroupConfiguration.builder()
            .name("workers")
            .cooldown(60)
            .minEntities(5)
            .maxEntities(100)
            .metadata(ImmutableMap.of("firstkey", "this is a string", "secondkey", "1"))
            .build();

      boolean result = api.updateGroupConfiguration("1234567890", gc);
      assertEquals(result, true);
   }

   public void testUpdateGroupConfigurationFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/config");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("PUT").endpoint(endpoint)
            .payload(payloadFromResourceWithContentType("/autoscale_groups_update_configuration_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(404)
            .build())
            .getGroupApiForZone("DFW");

      GroupConfiguration gc = GroupConfiguration.builder()
            .name("workers")
            .cooldown(60)
            .minEntities(5)
            .maxEntities(100)
            .metadata(ImmutableMap.of("firstkey", "this is a string", "secondkey", "1"))
            .build();

      boolean result = api.updateGroupConfiguration("1234567890", gc);
      assertFalse(result);
   }

   public void testUpdateGroupLaunchConfiguration() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/launch");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("PUT").endpoint(endpoint)
            .payload(payloadFromResourceWithContentType("/autoscale_groups_update_launch_configuration_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200)
            .build())
            .getGroupApiForZone("DFW");

      LaunchConfiguration lc = LaunchConfiguration.builder()
            .loadBalancers(ImmutableList.of(LoadBalancer.builder().port(8080).id(9099).build()))
            .serverName("autoscale_server")
            .serverImageRef("0d589460-f177-4b0f-81c1-8ab8903ac7d8")
            .serverFlavorRef("2")
            .serverDiskConfig("AUTO")
            .serverMetadata(ImmutableMap.of("build_config","core","meta_key_1","meta_value_1","meta_key_2","meta_value_2"))
            .networks(ImmutableList.of("11111111-1111-1111-1111-111111111111","00000000-0000-0000-0000-000000000000"))
            .personalities(ImmutableList.of(Personality.builder().path("/root/.csivh").contents("VGhpcyBpcyBhIHRlc3QgZmlsZS4=").build()))
            .type(LaunchConfigurationType.LAUNCH_SERVER)
            .build();

      boolean result = api.updateLaunchConfiguration("1234567890", lc);
      assertEquals(result, true);
   }

   public void testUpdateGroupLaunchConfigurationFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/launch");
      GroupApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("PUT").endpoint(endpoint)
            .payload(payloadFromResourceWithContentType("/autoscale_groups_update_launch_configuration_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(404)
            .build())
            .getGroupApiForZone("DFW");

      LaunchConfiguration lc = LaunchConfiguration.builder()
            .loadBalancers(ImmutableList.of(LoadBalancer.builder().port(8080).id(9099).build()))
            .serverName("autoscale_server")
            .serverImageRef("0d589460-f177-4b0f-81c1-8ab8903ac7d8")
            .serverFlavorRef("2")
            .serverDiskConfig("AUTO")
            .serverMetadata(ImmutableMap.of("build_config","core","meta_key_1","meta_value_1","meta_key_2","meta_value_2"))
            .networks(ImmutableList.of("11111111-1111-1111-1111-111111111111","00000000-0000-0000-0000-000000000000"))
            .personalities(ImmutableList.of(Personality.builder().path("/root/.csivh").contents("VGhpcyBpcyBhIHRlc3QgZmlsZS4=").build()))
            .type(LaunchConfigurationType.LAUNCH_SERVER)
            .build();

      boolean result = api.updateLaunchConfiguration("1234567890", lc);
      assertFalse(result);
   }
}
