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

import org.jclouds.rackspace.autoscale.v1.AutoscaleApi;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy.ScalingPolicyScheduleType;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy.ScalingPolicyTargetType;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy.ScalingPolicyType;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy;
import org.jclouds.rackspace.autoscale.v1.internal.BaseAutoscaleApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests Scaling Policy Api Guice wiring and parsing
 *
 * @author Zack Shoylev
 */
@Test(groups = "unit", testName = "ScalingPolicyApiMockTest")
public class ScalingPolicyApiMockTest extends BaseAutoscaleApiMockTest {

   public void testCreateScalingPolicy() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/autoscale_policy_create_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");         

         List<CreateScalingPolicy> scalingPolicies = Lists.newArrayList();

         CreateScalingPolicy scalingPolicy = CreateScalingPolicy.builder()
               .cooldown(1800)
               .type(ScalingPolicyType.WEBHOOK)
               .name("scale up by one server")
               .targetType(ScalingPolicyTargetType.INCREMENTAL)
               .target("1")
               .build();
         scalingPolicies.add(scalingPolicy);

         FluentIterable<ScalingPolicy> scalingPolicyResponse = api.create(scalingPolicies);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/groupId1/policies", "/autoscale_policy_create_request.json");

         /*
          * Check response
          */
         assertNotNull(scalingPolicyResponse);
         assertEquals(scalingPolicyResponse.size(), 1);
         assertEquals(scalingPolicyResponse.get(0).getCooldown(), 1800);
         assertEquals(scalingPolicyResponse.get(0).getId(), "dceb14ac-b2b3-4f06-aac9-a5b6cd5d40e1");
         assertEquals(scalingPolicyResponse.get(0).getName(), "scale up by one server");
         assertEquals(scalingPolicyResponse.get(0).getTarget(), "1");
         assertEquals(scalingPolicyResponse.get(0).getLinks().size(), 1);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateScalingPolicyFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_policy_create_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");         

         List<CreateScalingPolicy> scalingPolicies = Lists.newArrayList();

         CreateScalingPolicy scalingPolicy = CreateScalingPolicy.builder()
               .cooldown(1800)
               .type(ScalingPolicyType.WEBHOOK)
               .name("scale up by one server")
               .targetType(ScalingPolicyTargetType.INCREMENTAL)
               .target("1")
               .build();
         scalingPolicies.add(scalingPolicy);

         FluentIterable<ScalingPolicy> scalingPolicyResponse = api.create(scalingPolicies);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/groupId1/policies", "/autoscale_policy_create_request.json");

         /*
          * Check response
          */
         assertTrue(scalingPolicyResponse.isEmpty(), "Expected no policy response, but was: " + scalingPolicyResponse);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateCronScheduleScalingPolicy() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/autoscale_policy_schedule_cron_create_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");         

         List<CreateScalingPolicy> scalingPolicies = Lists.newArrayList();

         CreateScalingPolicy scalingPolicy = CreateScalingPolicy.builder()
               .cooldown(2)
               .type(ScalingPolicyType.SCHEDULE)
               .name("scale down by 5.5 percent at 11pm")
               .targetType(ScalingPolicyTargetType.PERCENT_CHANGE)
               .target("-5.5")
               .cronSchedule("23 * * * *")
               .build();
         scalingPolicies.add(scalingPolicy);

         FluentIterable<ScalingPolicy> scalingPolicyResponse = api.create(scalingPolicies);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/groupId1/policies", "/autoscale_policy_schedule_cron_create_request.json");

         /*
          * Check response
          */
         assertNotNull(scalingPolicyResponse);
         assertEquals(scalingPolicyResponse.size(), 1);
         assertEquals(scalingPolicyResponse.get(0).getCooldown(), 2);
         assertEquals(scalingPolicyResponse.get(0).getId(), "30707675-8e7c-4ea5-9358-c21648afcf29");
         assertEquals(scalingPolicyResponse.get(0).getName(), "scale down by 5.5 percent at 11pm");
         assertEquals(scalingPolicyResponse.get(0).getTarget(), "-5.5");
         assertEquals(scalingPolicyResponse.get(0).getType(), ScalingPolicyType.SCHEDULE);
         assertEquals(scalingPolicyResponse.get(0).getSchedulingType(), ScalingPolicyScheduleType.CRON);
         assertEquals(scalingPolicyResponse.get(0).getSchedulingString(), "23 * * * *");
         assertEquals(scalingPolicyResponse.get(0).getLinks().size(), 1);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateAtScheduleScalingPolicy() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/autoscale_policy_schedule_at_create_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");         

         List<CreateScalingPolicy> scalingPolicies = Lists.newArrayList();

         CreateScalingPolicy scalingPolicy = CreateScalingPolicy.builder()
               .cooldown(2)
               .type(ScalingPolicyType.SCHEDULE)
               .name("scale down by 5.5 percent on the 5th")
               .targetType(ScalingPolicyTargetType.PERCENT_CHANGE)
               .target("-5.5")
               .atSchedule("2013-06-05T03:12Z")
               .build();
         scalingPolicies.add(scalingPolicy);

         FluentIterable<ScalingPolicy> scalingPolicyResponse = api.create(scalingPolicies);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/groupId1/policies", "/autoscale_policy_schedule_at_create_request.json");

         /*
          * Check response
          */
         assertNotNull(scalingPolicyResponse);
         assertEquals(scalingPolicyResponse.size(), 1);
         assertEquals(scalingPolicyResponse.get(0).getCooldown(), 2);
         assertEquals(scalingPolicyResponse.get(0).getId(), "9f7c5801-6b25-4f5a-af07-4bb752e23d53");
         assertEquals(scalingPolicyResponse.get(0).getName(), "scale down by 5.5 percent on the 5th");
         assertEquals(scalingPolicyResponse.get(0).getTarget(), "-5.5");
         assertEquals(scalingPolicyResponse.get(0).getType(), ScalingPolicyType.SCHEDULE);
         assertEquals(scalingPolicyResponse.get(0).getSchedulingType(), ScalingPolicyScheduleType.AT);
         assertEquals(scalingPolicyResponse.get(0).getSchedulingString(), "2013-06-05T03:12Z");
         assertEquals(scalingPolicyResponse.get(0).getLinks().size(), 1);
      } finally {
         server.shutdown();
      }
   }

   public void testListScalingPolicies() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/autoscale_policy_list_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");         

         FluentIterable<ScalingPolicy> scalingPolicyResponse = api.list();

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/groupId1/policies");

         /*
          * Check response
          */
         assertNotNull(scalingPolicyResponse);
         assertEquals(scalingPolicyResponse.size(), 2);
         assertEquals(scalingPolicyResponse.get(0).getCooldown(), 150);
         assertEquals(scalingPolicyResponse.get(0).getId(), "policyId1");
         assertEquals(scalingPolicyResponse.get(0).getName(), "scale up by one server");
         assertEquals(scalingPolicyResponse.get(0).getTarget(), "1");
         assertEquals(scalingPolicyResponse.get(0).getLinks().size(), 1);
      } finally {
         server.shutdown();
      }
   }

   public void testListScalingPoliciesFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_policy_list_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");         

         FluentIterable<ScalingPolicy> scalingPolicyResponse = api.list();

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/groupId1/policies");

         /*
          * Check response
          */
         assertTrue(scalingPolicyResponse.isEmpty(), "Expected no policy response, but was: " + scalingPolicyResponse);
      } finally {
         server.shutdown();
      }
   }

   public void testGetScalingPolicies() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/autoscale_policy_get_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");         

         ScalingPolicy scalingPolicyResponse = api.get("policyId");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/groupId1/policies/policyId");

         /*
          * Check response
          */
         assertNotNull(scalingPolicyResponse);
         assertEquals(scalingPolicyResponse.getCooldown(), 150);
         assertEquals(scalingPolicyResponse.getId(), "policyId");
         assertEquals(scalingPolicyResponse.getName(), "scale up by one server");
         assertEquals(scalingPolicyResponse.getTarget(), "1");
         assertEquals(scalingPolicyResponse.getLinks().size(), 1);
      } finally {
         server.shutdown();
      }
   }

   public void testGetScalingPoliciesFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_policy_get_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");         

         ScalingPolicy scalingPolicyResponse = api.get("policyId");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/groupId1/policies/policyId");

         /*
          * Check response
          */
         assertNull(scalingPolicyResponse);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateScalingPolicy() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");

         CreateScalingPolicy scalingPolicy = CreateScalingPolicy.builder()
               .cooldown(6)
               .type(ScalingPolicyType.WEBHOOK)
               .name("scale down by 5 percent")
               .targetType(ScalingPolicyTargetType.PERCENT_CHANGE)
               .target("-5")
               .build();

         boolean result = api.update("policyId", scalingPolicy);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1.0/888888/groups/groupId1/policies/policyId", "/autoscale_policy_update_request.json");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateScalingPolicyFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");

         CreateScalingPolicy scalingPolicy = CreateScalingPolicy.builder()
               .cooldown(6)
               .type(ScalingPolicyType.WEBHOOK)
               .name("scale down by 5 percent")
               .targetType(ScalingPolicyTargetType.PERCENT_CHANGE)
               .target("-5")
               .build();

         boolean result = api.update("policyId", scalingPolicy);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1.0/888888/groups/groupId1/policies/policyId", "/autoscale_policy_update_request.json");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteScalingPolicy() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");

         boolean result = api.delete("policyId");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v1.0/888888/groups/groupId1/policies/policyId");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteScalingPolicyFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");

         boolean result = api.delete("policyId");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v1.0/888888/groups/groupId1/policies/policyId");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testExecuteScalingPolicy() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");

         boolean result = api.execute("policyId");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/groupId1/policies/policyId/execute");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testExecuteScalingPolicyFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         PolicyApi api = autoscaleApi.getPolicyApiForZoneAndGroup("DFW", "groupId1");

         boolean result = api.execute("policyId");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/groupId1/policies/policyId/execute");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }
}
