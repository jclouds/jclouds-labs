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

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy.ScalingPolicyTargetType;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy.ScalingPolicyType;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicyResponse;
import org.jclouds.rackspace.autoscale.v1.internal.BaseAutoscaleApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

/**
 * Tests Scaling Policy Api Guice wiring and parsing
 *
 * @author Zack Shoylev
 */
@Test(groups = "unit", testName = "GroupApiExpectTest")
public class ScalingPolicyApiExpectTest extends BaseAutoscaleApiExpectTest {

   public void testCreateScalingPolicy() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("POST").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_policy_create_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(201).payload(payloadFromResource("/autoscale_policy_create_response.json")).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");      

      List<ScalingPolicy> scalingPolicies = Lists.newArrayList();

      ScalingPolicy scalingPolicy = ScalingPolicy.builder()
            .cooldown(1800)
            .type(ScalingPolicyType.WEBHOOK)
            .name("scale up by one server")
            .targetType(ScalingPolicyTargetType.INCREMENTAL)
            .target("1")
            .build();
      scalingPolicies.add(scalingPolicy);

      FluentIterable<ScalingPolicyResponse> scalingPolicyResponse = api.create(scalingPolicies);

      assertNotNull(scalingPolicyResponse);
      assertEquals(scalingPolicyResponse.size(), 1);
      assertEquals(scalingPolicyResponse.get(0).getCooldown(), 1800);
      assertEquals(scalingPolicyResponse.get(0).getId(), "dceb14ac-b2b3-4f06-aac9-a5b6cd5d40e1");
      assertEquals(scalingPolicyResponse.get(0).getName(), "scale up by one server");
      assertEquals(scalingPolicyResponse.get(0).getTarget(), "1");
      assertEquals(scalingPolicyResponse.get(0).getLinks().size(), 1);
   }

   public void testCreateScalingPolicyFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("POST").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_policy_create_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/autoscale_policy_create_response.json")).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");      

      List<ScalingPolicy> scalingPolicies = Lists.newArrayList();

      ScalingPolicy scalingPolicy = ScalingPolicy.builder()
            .cooldown(1800)
            .type(ScalingPolicyType.WEBHOOK)
            .name("scale up by one server")
            .targetType(ScalingPolicyTargetType.INCREMENTAL)
            .target("1")
            .build();
      scalingPolicies.add(scalingPolicy);

      FluentIterable<ScalingPolicyResponse> scalingPolicyResponse = api.create(scalingPolicies);

      assertTrue(scalingPolicyResponse.size() == 0);
   }

   public void testListScalingPolicies() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("GET").endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(201).payload(payloadFromResource("/autoscale_policy_list_response.json")).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");

      FluentIterable<ScalingPolicyResponse> scalingPolicyResponse = api.list();

      assertNotNull(scalingPolicyResponse);
      assertEquals(scalingPolicyResponse.size(), 2);
      assertEquals(scalingPolicyResponse.get(0).getCooldown(), 150);
      assertEquals(scalingPolicyResponse.get(0).getId(), "policyId1");
      assertEquals(scalingPolicyResponse.get(0).getName(), "scale up by one server");
      assertEquals(scalingPolicyResponse.get(0).getTarget(), "1");
      assertEquals(scalingPolicyResponse.get(0).getLinks().size(), 1);
   }

   public void testListScalingPoliciesFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("GET").endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/autoscale_policy_list_response.json")).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");

      FluentIterable<ScalingPolicyResponse> scalingPolicyResponse = api.list();

      assertEquals(scalingPolicyResponse.size(), 0);
   }

   public void testGetScalingPolicies() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies/policyId");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("GET").endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(201).payload(payloadFromResource("/autoscale_policy_get_response.json")).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");

      ScalingPolicyResponse scalingPolicyResponse = api.get("policyId");

      assertNotNull(scalingPolicyResponse);
      assertEquals(scalingPolicyResponse.getCooldown(), 150);
      assertEquals(scalingPolicyResponse.getId(), "policyId");
      assertEquals(scalingPolicyResponse.getName(), "scale up by one server");
      assertEquals(scalingPolicyResponse.getTarget(), "1");
      assertEquals(scalingPolicyResponse.getLinks().size(), 1);
   }

   public void testGetScalingPoliciesFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies/policyId");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("GET").endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/autoscale_policy_get_response.json")).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");

      ScalingPolicyResponse scalingPolicyResponse = api.get("policyId");

      assertNull(scalingPolicyResponse);
   }

   public void testUpdateScalingPolicy() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies/policyId");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("PUT").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_policy_update_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(201).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");

      ScalingPolicy scalingPolicy = ScalingPolicy.builder()
            .cooldown(6)
            .type(ScalingPolicyType.WEBHOOK)
            .name("scale down by 5 percent")
            .targetType(ScalingPolicyTargetType.PERCENT_CHANGE)
            .target("-5")
            .build();

      boolean result = api.update("policyId", scalingPolicy);
      assertTrue(result);
   }

   public void testUpdateScalingPolicyFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies/policyId");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("PUT").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_policy_update_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(404).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");      

      ScalingPolicy scalingPolicy = ScalingPolicy.builder()
            .cooldown(6)
            .type(ScalingPolicyType.WEBHOOK)
            .name("scale down by 5 percent")
            .targetType(ScalingPolicyTargetType.PERCENT_CHANGE)
            .target("-5")
            .build();

      boolean result = api.update("policyId", scalingPolicy);
      assertFalse(result);
   }

   public void testDeleteScalingPolicy() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies/policyId");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("DELETE").endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(201).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");      

      boolean result = api.delete("policyId");
      assertTrue(result);
   }

   public void testDeleteScalingPolicyFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies/policyId");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("DELETE").endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");      

      boolean result = api.delete("policyId");
      assertFalse(result);
   }

   public void testExecuteScalingPolicy() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies/policyId/execute");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("POST").endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(201).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");      

      boolean result = api.execute("policyId");
      assertTrue(result);
   }

   public void testExecuteScalingPolicyFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/groupId1/policies/policyId/execute");
      PolicyApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("POST").endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
            ).getPolicyApiForGroupInZone("groupId1", "DFW");      

      boolean result = api.execute("policyId");
      assertFalse(result);
   }
}
