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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.rackspace.autoscale.v1.domain.Group;
import org.jclouds.rackspace.autoscale.v1.domain.GroupConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration.LaunchConfigurationType;
import org.jclouds.rackspace.autoscale.v1.domain.LoadBalancer;
import org.jclouds.rackspace.autoscale.v1.domain.Personality;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy.ScalingPolicyTargetType;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy.ScalingPolicyType;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy;
import org.jclouds.rackspace.autoscale.v1.domain.CreateWebhook;
import org.jclouds.rackspace.autoscale.v1.domain.Webhook;
import org.jclouds.rackspace.autoscale.v1.internal.BaseAutoscaleApiLiveTest;
import org.jclouds.rackspace.autoscale.v1.utils.AutoscaleUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;

/**
 * Webhook live test
 */
@Test(groups = "live", testName = "WebhookApiLiveTest", singleThreaded = true)
public class WebhookApiLiveTest extends BaseAutoscaleApiLiveTest {

   private static Map<String, List<Group>> created = Maps.newHashMap();

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      for (String region : api.getConfiguredRegions()) {
         List<Group> createdGroupList = Lists.newArrayList();
         created.put(region, createdGroupList);
         GroupApi groupApi = api.getGroupApi(region);

         GroupConfiguration groupConfiguration = GroupConfiguration.builder().maxEntities(10).cooldown(3)
               .name("testscalinggroup198547").minEntities(0)
               .metadata(ImmutableMap.of("gc_meta_key_2", "gc_meta_value_2", "gc_meta_key_1", "gc_meta_value_1"))
               .build();

         LaunchConfiguration launchConfiguration = LaunchConfiguration
               .builder()
               .loadBalancers(ImmutableList.of(LoadBalancer.builder().port(8080).id(9099).build()))
               .serverName("autoscale_server")
               .serverImageRef("5cc098a5-7286-4b96-b3a2-49f4c4f82537")
               .serverFlavorRef("2")
               .serverDiskConfig("AUTO")
               .serverMetadata(
                     ImmutableMap
                     .of("build_config", "core", "meta_key_1", "meta_value_1", "meta_key_2", "meta_value_2"))
                     .networks(
                           ImmutableList.of("11111111-1111-1111-1111-111111111111", "00000000-0000-0000-0000-000000000000"))
                           .personalities(
                                 ImmutableList.of(Personality.builder().path("/root/.csivh")
                                       .contents("VGhpcyBpcyBhIHRlc3QgZmlsZS4=").build()))
                                       .type(LaunchConfigurationType.LAUNCH_SERVER).build();

         List<CreateScalingPolicy> scalingPolicies = Lists.newArrayList();

         CreateScalingPolicy scalingPolicy = CreateScalingPolicy.builder().cooldown(3).type(ScalingPolicyType.WEBHOOK)
               .name("scale up by 1").targetType(ScalingPolicyTargetType.INCREMENTAL).target("1").build();
         scalingPolicies.add(scalingPolicy);

         Group g = groupApi.create(groupConfiguration, launchConfiguration, scalingPolicies);
         createdGroupList.add(g);

         WebhookApi webhookApi = api.getWebhookApi(region, g.getId(), g.getScalingPolicies().iterator().next().getId());
         assertFalse( webhookApi.create("test_webhook", ImmutableMap.<String, Object>of()).isEmpty() );

         assertNotNull(g);
         assertNotNull(g.getId());
         assertEquals(g.getLinks().size(), 1);
         assertEquals(g.getLinks().get(0).getHref().toString(),
               "https://" + region.toLowerCase() + ".autoscale.api.rackspacecloud.com/v1.0/" + api.getCurrentTenantId().get().getId() + "/groups/" + g.getId() + "/");
         assertEquals(g.getLinks().get(0).getRelation(), Link.Relation.SELF);

         assertNotNull(g.getScalingPolicies().get(0).getId());
         assertEquals(g.getScalingPolicies().get(0).getLinks().size(), 1);
         assertEquals(
               g.getScalingPolicies().get(0).getLinks().get(0).getHref().toString(),
               "https://" + region.toLowerCase() + ".autoscale.api.rackspacecloud.com/v1.0/" + api.getCurrentTenantId().get().getId() + "/groups/" + g.getId() + "/policies/" + g.getScalingPolicies().get(0).getId() + "/");
         assertEquals(g.getScalingPolicies().get(0).getLinks().get(0).getRelation(), Link.Relation.SELF);
         assertEquals(g.getScalingPolicies().get(0).getCooldown(), 3);
         assertEquals(g.getScalingPolicies().get(0).getTarget(), "1");
         assertEquals(g.getScalingPolicies().get(0).getTargetType(), ScalingPolicyTargetType.INCREMENTAL);
         assertEquals(g.getScalingPolicies().get(0).getType(), ScalingPolicyType.WEBHOOK);
         assertEquals(g.getScalingPolicies().get(0).getName(), "scale up by 1");

         assertEquals(g.getLaunchConfiguration().getLoadBalancers().size(), 1);
         assertEquals(g.getLaunchConfiguration().getLoadBalancers().get(0).getId(), 9099);
         assertEquals(g.getLaunchConfiguration().getLoadBalancers().get(0).getPort(), 8080);
         assertEquals(g.getLaunchConfiguration().getServerName(), "autoscale_server");
         assertNotNull(g.getLaunchConfiguration().getServerImageRef());
         assertEquals(g.getLaunchConfiguration().getServerFlavorRef(), "2");
         assertEquals(g.getLaunchConfiguration().getServerDiskConfig(), "AUTO");
         assertEquals(g.getLaunchConfiguration().getPersonalities().size(), 1);
         assertEquals(g.getLaunchConfiguration().getPersonalities().get(0).getPath(), "/root/.csivh");
         assertEquals(g.getLaunchConfiguration().getPersonalities().get(0).getContents(),
               "VGhpcyBpcyBhIHRlc3QgZmlsZS4=");
         assertEquals(g.getLaunchConfiguration().getNetworks().size(), 2);
         assertEquals(g.getLaunchConfiguration().getNetworks().get(0), "11111111-1111-1111-1111-111111111111");
         assertEquals(g.getLaunchConfiguration().getNetworks().get(1), "00000000-0000-0000-0000-000000000000");
         assertEquals(g.getLaunchConfiguration().getServerMetadata().size(), 3);
         assertTrue(g.getLaunchConfiguration().getServerMetadata().containsKey("build_config"));
         assertTrue(g.getLaunchConfiguration().getServerMetadata().containsValue("core"));
         assertEquals(g.getLaunchConfiguration().getType(), LaunchConfigurationType.LAUNCH_SERVER);

         assertEquals(g.getGroupConfiguration().getMaxEntities(), 10);
         assertEquals(g.getGroupConfiguration().getCooldown(), 3);
         assertEquals(g.getGroupConfiguration().getName(), "testscalinggroup198547");
         assertEquals(g.getGroupConfiguration().getMinEntities(), 0);
         assertEquals(g.getGroupConfiguration().getMetadata().size(), 2);
         assertTrue(g.getGroupConfiguration().getMetadata().containsKey("gc_meta_key_2"));
         assertTrue(g.getGroupConfiguration().getMetadata().containsValue("gc_meta_value_2"));
      }
   }

   @Test
   public void testCreateWebhook() {
      for (String region : api.getConfiguredRegions()) {
         Group g = created.get(region).get(0);
         WebhookApi webhookApi = api.getWebhookApi(region, g.getId(), g.getScalingPolicies().iterator().next().getId());
         Webhook webhook = webhookApi.create("test1", ImmutableMap.<String, Object>of("notes", "test metadata")).first().get();

         assertEquals(webhook.getName(), "test1");
         assertEquals(webhook.getMetadata().get("notes"), "test metadata");
      }
   }

   @Test
   public void testCreateWebhooks() {
      for (String region : api.getConfiguredRegions()) {
         Group g = created.get(region).get(0);
         WebhookApi webhookApi = api.getWebhookApi(region, g.getId(), g.getScalingPolicies().iterator().next().getId());
         FluentIterable<Webhook> webhookResponse = webhookApi.create(
               ImmutableList.of(
                     CreateWebhook.builder().name("test5").metadata(null).build(),
                     CreateWebhook.builder().name("test6").metadata(ImmutableMap.<String, Object>of("notes2", "different test")).build()
                     ));

         assertEquals(webhookResponse.get(0).getName(), "test5");
         assertNull(webhookResponse.get(0).getMetadata().get("notes"));
         assertEquals(webhookResponse.get(1).getName(), "test6");
         assertEquals(webhookResponse.get(1).getMetadata().get("notes2"), "different test");
      }
   }

   @Test
   public void testUpdateWebhook() {
      for (String region : api.getConfiguredRegions()) {
         Group g = created.get(region).get(0);
         WebhookApi webhookApi = api.getWebhookApi(region, g.getId(), g.getScalingPolicies().iterator().next().getId());
         String webhookId = webhookApi.list().first().get().getId();
         assertTrue( webhookApi.update(webhookId, "updated_name", ImmutableMap.<String, Object>of()) );

         Webhook webhook = webhookApi.get(webhookId);
         assertEquals(webhook.getName(), "updated_name");
         assertTrue( webhook.getMetadata().isEmpty() );
      }
   }

   @Test
   public void testGetWebhook() {
      for (String region : api.getConfiguredRegions()) {
         Group g = created.get(region).get(0);
         WebhookApi webhookApi;
         boolean foundWebhook = false;
         for (ScalingPolicy sp :  g.getScalingPolicies()) {
            webhookApi = api.getWebhookApi(region, g.getId(), sp.getId());
            Webhook webhookResponse = webhookApi.list().first().get();
            if (webhookResponse != null) {
               Webhook webhookGet = webhookApi.get(webhookResponse.getId());
               assertEquals(webhookResponse, webhookGet);
               foundWebhook = true;
            }
         }
         assertTrue(foundWebhook, "No webhooks were found, and some were expected");
      }
   }

   @Test
   public void testListWebhook() {
      for (String region : api.getConfiguredRegions()) {
         Group g = created.get(region).get(0);
         WebhookApi webhookApi = api.getWebhookApi(region, g.getId(), g.getScalingPolicies().iterator().next().getId());
         assertFalse( webhookApi.list().isEmpty() );
      }
   }

   @Test
   public void testDeleteWebhook() {
      for (String region : api.getConfiguredRegions()) {
         Group g = created.get(region).get(0);
         WebhookApi webhookApi = api.getWebhookApi(region, g.getId(), g.getScalingPolicies().iterator().next().getId());
         Webhook webhook = webhookApi.create("test1", ImmutableMap.<String, Object>of("notes", "test metadata")).first().get();

         assertEquals(webhook.getName(), "test1");
         assertEquals(webhook.getMetadata().get("notes"), "test metadata");

         assertTrue( webhookApi.delete(webhook.getId()) );
         assertNull( webhookApi.get(webhook.getId()) );
      }
   }

   @Test
   public void testExecuteWebhook() throws IOException {
      for (String region : api.getConfiguredRegions()) {
         Group g = created.get(region).get(0);
         WebhookApi webhookApi = api.getWebhookApi(region, g.getId(), g.getScalingPolicies().iterator().next().getId());
         Webhook webhook = webhookApi.create("test_execute", ImmutableMap.<String, Object>of("notes", "test metadata")).first().get();

         assertTrue( AutoscaleUtils.execute(webhook.getAnonymousExecutionURI().get()) , " for " + webhook + " in " + region);
      }
   }

   @Test
   public void testExecuteWebhookFail() throws IOException, URISyntaxException {
      for (String region : api.getConfiguredRegions()) {
         Group g = created.get(region).get(0);
         WebhookApi webhookApi = api.getWebhookApi(region, g.getId(), g.getScalingPolicies().iterator().next().getId());
         Webhook webhook = webhookApi.create("test_execute_fail", ImmutableMap.<String, Object>of("notes", "test metadata")).first().get();

         URI uri = new URI(webhook.getAnonymousExecutionURI().get().toString() + "123");
         assertFalse( AutoscaleUtils.execute(uri) );
      }
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   public void tearDown() {
      for (String region : api.getConfiguredRegions()) {
         GroupApi groupApi = api.getGroupApi(region);
         for (Group group : created.get(region)) {
            PolicyApi policyApi = api.getPolicyApi(region, group.getId());
            if (policyApi == null)
                continue;
            for (ScalingPolicy sgr : policyApi.list()) {
               if (!policyApi.delete(sgr.getId())) {
                  System.out.println("Could not delete an autoscale policy after tests!");
               }
            }

            List<CreateScalingPolicy> scalingPolicies = Lists.newArrayList();

            CreateScalingPolicy scalingPolicy = CreateScalingPolicy.builder()
                  .cooldown(2)
                  .type(ScalingPolicyType.WEBHOOK)
                  .name("0 machines")
                  .targetType(ScalingPolicyTargetType.DESIRED_CAPACITY)
                  .target("0")
                  .build();
            scalingPolicies.add(scalingPolicy);

            FluentIterable<ScalingPolicy> scalingPolicyResponse = policyApi.create(scalingPolicies);
            String policyId = scalingPolicyResponse.iterator().next().getId();

            Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);

            policyApi.execute(policyId);

            Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
            policyApi.delete(policyId);

            if (!groupApi.delete(group.getId()))
               System.out.println("Could not delete an autoscale group after tests!");
         }
      }
      super.tearDown();
   }
}
