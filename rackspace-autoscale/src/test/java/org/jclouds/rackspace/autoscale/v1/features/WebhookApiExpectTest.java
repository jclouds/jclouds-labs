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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.autoscale.v1.domain.Webhook;
import org.jclouds.rackspace.autoscale.v1.domain.WebhookResponse;
import org.jclouds.rackspace.autoscale.v1.internal.BaseAutoscaleApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests WebhookApi Guice wiring and parsing
 *
 * @author Zack Shoylev
 */
@Test(groups = "unit", testName = "WebhookApiExpectTest")
public class WebhookApiExpectTest extends BaseAutoscaleApiExpectTest {

   public void testCreateWebhook() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/policies/321456/webhooks");
      WebhookApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("POST").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_webhook_create_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(201).payload(payloadFromResource("/autoscale_webhook_create_response.json")).build()
            ).getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");

      FluentIterable<WebhookResponse> webhooks = api.create("PagerDuty", ImmutableMap.<String, Object>of("notes", "PagerDuty will fire this webhook"));
      assertEquals(webhooks.first().get().getName(), "PagerDuty");
      assertEquals(webhooks.first().get().getMetadata().get("notes"), "PagerDuty will fire this webhook");
   }

   public void testCreateWebhookFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/policies/321456/webhooks");
      WebhookApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("POST").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_webhook_create_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/autoscale_webhook_create_response.json")).build()
            ).getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");

      FluentIterable<WebhookResponse> webhooks = api.create("PagerDuty", ImmutableMap.<String, Object>of("notes", "PagerDuty will fire this webhook"));
      assertTrue(webhooks.isEmpty());
   }

   public void testCreateWebhooks() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/policies/321456/webhooks");
      WebhookApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("POST").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_webhooks_create_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(201).payload(payloadFromResource("/autoscale_webhooks_create_response.json")).build()
            ).getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");

      FluentIterable<WebhookResponse> webhooks = api.create(ImmutableList.of(
               Webhook.builder().name("PagerDuty").metadata(ImmutableMap.<String, Object>of("notes", "PagerDuty will fire this webhook")).build(),
               Webhook.builder().name("Nagios").metadata(ImmutableMap.<String, Object>of()).build()
            ));
      assertEquals(webhooks.size(), 2);
      assertEquals(webhooks.first().get().getName(), "PagerDuty");
      assertEquals(webhooks.first().get().getMetadata().get("notes"), "PagerDuty will fire this webhook");
   }

   public void testCreateWebhooksFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/policies/321456/webhooks");
      WebhookApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("POST").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_webhooks_create_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/autoscale_webhooks_create_response.json")).build()
            ).getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");

      FluentIterable<WebhookResponse> webhooks = api.create(ImmutableList.of(
               Webhook.builder().name("PagerDuty").metadata(ImmutableMap.<String, Object>of("notes", "PagerDuty will fire this webhook")).build(),
               Webhook.builder().name("Nagios").metadata(ImmutableMap.<String, Object>of()).build()
            ));
      assertTrue(webhooks.isEmpty());
   }

   public void testListWebhooks() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/policies/321456/webhooks");
      WebhookApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("GET").endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(201).payload(payloadFromResource("/autoscale_webhook_list_response.json")).build()
            ).getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");

      FluentIterable<WebhookResponse> webhooks = api.list();
      assertEquals(webhooks.size(), 2);
      assertEquals(webhooks.first().get().getName(), "PagerDuty");
      assertEquals(webhooks.first().get().getMetadata().get("notes"), "PagerDuty will fire this webhook");
   }

   public void testListWebhooksFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/policies/321456/webhooks");
      WebhookApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("GET").endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/autoscale_webhook_list_response.json")).build()
            ).getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");

      FluentIterable<WebhookResponse> webhooks = api.list();
      assertEquals(webhooks.size(), 0);
   }

   public void testUpdateWebhook() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/policies/321456/webhooks/5555");
      WebhookApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("PUT").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_webhook_update_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(201).build()
            ).getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");

      boolean success = api.update("5555", "alice", ImmutableMap.<String, Object>of("notes", "this is for Alice"));
      assertTrue(success);
   }

   public void testUpdateWebhookFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/policies/321456/webhooks/5555");
      WebhookApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().method("PUT").endpoint(endpoint).payload(payloadFromResourceWithContentType("/autoscale_webhook_update_request.json", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(404).build()
            ).getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");

      boolean success = api.update("5555", "alice", ImmutableMap.<String, Object>of("notes", "this is for Alice"));
      assertFalse(success);
   }

   public void testGetWebhook() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/policies/321456/webhooks/5555");
      WebhookApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(201).payload(payloadFromResource("/autoscale_webhook_get_response.json")).build()
            ).getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");

      WebhookResponse webhook = api.get("5555");
      assertEquals(webhook.getName(), "alice");
      assertEquals(webhook.getLinks().size(), 2);
   }

   public void testGetWebhookFail() {
      URI endpoint = URI.create("https://dfw.autoscale.api.rackspacecloud.com/v1.0/888888/groups/1234567890/policies/321456/webhooks/5555");
      WebhookApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/autoscale_webhook_get_response.json")).build()
            ).getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");

      WebhookResponse webhook = api.get("5555");
      assertNull(webhook);
   }
}
