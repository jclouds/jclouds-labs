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

import java.io.IOException;

import org.jclouds.rackspace.autoscale.v1.AutoscaleApi;
import org.jclouds.rackspace.autoscale.v1.domain.Webhook;
import org.jclouds.rackspace.autoscale.v1.domain.WebhookResponse;
import org.jclouds.rackspace.autoscale.v1.internal.BaseAutoscaleApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests WebhookApi Guice wiring and parsing
 *
 * @author Zack Shoylev
 */
@Test(groups = "unit", testName = "WebhookApiMockTest")
public class WebhookApiMockTest extends BaseAutoscaleApiMockTest {

   public void testCreateWebhook() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/autoscale_webhook_create_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         WebhookApi api = autoscaleApi.getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");         

         FluentIterable<WebhookResponse> webhooks = api.create("PagerDuty", ImmutableMap.<String, Object>of("notes", "PagerDuty will fire this webhook"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/1234567890/policies/321456/webhooks", "/autoscale_webhook_create_request.json");

         /*
          * Check response
          */
         assertEquals(webhooks.first().get().getName(), "PagerDuty");
         assertEquals(webhooks.first().get().getMetadata().get("notes"), "PagerDuty will fire this webhook");
      } finally {
         server.shutdown();
      }
   }

   public void testCreateWebhookFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_webhook_create_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         WebhookApi api = autoscaleApi.getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");         

         FluentIterable<WebhookResponse> webhooks = api.create("PagerDuty", ImmutableMap.<String, Object>of("notes", "PagerDuty will fire this webhook"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/1234567890/policies/321456/webhooks", "/autoscale_webhook_create_request.json");

         /*
          * Check response
          */
         assertTrue(webhooks.isEmpty(), "Expected no webhooks, but was: " + webhooks);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateWebhooks() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/autoscale_webhooks_create_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         WebhookApi api = autoscaleApi.getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");         

         FluentIterable<WebhookResponse> webhooks = api.create(ImmutableList.of(
               Webhook.builder().name("PagerDuty").metadata(ImmutableMap.<String, Object>of("notes", "PagerDuty will fire this webhook")).build(),
               Webhook.builder().name("Nagios").metadata(ImmutableMap.<String, Object>of()).build()
               ));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/1234567890/policies/321456/webhooks", "/autoscale_webhooks_create_request.json");

         /*
          * Check response
          */
         assertEquals(webhooks.size(), 2);
         assertEquals(webhooks.first().get().getName(), "PagerDuty");
         assertEquals(webhooks.first().get().getMetadata().get("notes"), "PagerDuty will fire this webhook");
      } finally {
         server.shutdown();
      }
   }

   public void testCreateWebhooksFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_webhooks_create_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         WebhookApi api = autoscaleApi.getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");         

         FluentIterable<WebhookResponse> webhooks = api.create(ImmutableList.of(
               Webhook.builder().name("PagerDuty").metadata(ImmutableMap.<String, Object>of("notes", "PagerDuty will fire this webhook")).build(),
               Webhook.builder().name("Nagios").metadata(ImmutableMap.<String, Object>of()).build()
               ));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/groups/1234567890/policies/321456/webhooks", "/autoscale_webhooks_create_request.json");

         /*
          * Check response
          */
         assertTrue(webhooks.isEmpty(), "Expected no webhooks, but was: " + webhooks);
      } finally {
         server.shutdown();
      }
   }

   public void testListWebhooks() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/autoscale_webhook_list_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         WebhookApi api = autoscaleApi.getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");         

         FluentIterable<WebhookResponse> webhooks = api.list();

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890/policies/321456/webhooks");

         /*
          * Check response
          */
         assertEquals(webhooks.size(), 2);
         assertEquals(webhooks.first().get().getName(), "PagerDuty");
         assertEquals(webhooks.first().get().getMetadata().get("notes"), "PagerDuty will fire this webhook");
      } finally {
         server.shutdown();
      }
   }

   public void testListWebhooksFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_webhook_list_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         WebhookApi api = autoscaleApi.getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");         

         FluentIterable<WebhookResponse> webhooks = api.list();

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890/policies/321456/webhooks");

         /*
          * Check response
          */
         assertTrue(webhooks.isEmpty(), "Expected no webhooks, but was: " + webhooks);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateWebhook() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         WebhookApi api = autoscaleApi.getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");         

         boolean success = api.update("5555", "alice", ImmutableMap.<String, Object>of("notes", "this is for Alice"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1.0/888888/groups/1234567890/policies/321456/webhooks/5555", "/autoscale_webhook_update_request.json");

         /*
          * Check response
          */
         assertTrue(success);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateWebhookFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         WebhookApi api = autoscaleApi.getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");         

         boolean success = api.update("5555", "alice", ImmutableMap.<String, Object>of("notes", "this is for Alice"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1.0/888888/groups/1234567890/policies/321456/webhooks/5555", "/autoscale_webhook_update_request.json");

         /*
          * Check response
          */
         assertFalse(success);
      } finally {
         server.shutdown();
      }
   }

   public void testGetWebhook() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/autoscale_webhook_get_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         WebhookApi api = autoscaleApi.getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");         

         WebhookResponse webhook = api.get("5555");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890/policies/321456/webhooks/5555");

         /*
          * Check response
          */
         assertEquals(webhook.getName(), "alice");
         assertEquals(webhook.getLinks().size(), 2);
      } finally {
         server.shutdown();
      }
   }

   public void testGetWebhookFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/autoscale_webhook_get_response.json"))));

      try {
         AutoscaleApi autoscaleApi = api(server.getUrl("/").toString(), "rackspace-autoscale", overrides);
         WebhookApi api = autoscaleApi.getWebhookApiForZoneAndGroupAndPolicy("DFW", "1234567890", "321456");         

         WebhookResponse webhook = api.get("5555");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/groups/1234567890/policies/321456/webhooks/5555");

         /*
          * Check response
          */
         assertNull(webhook);
      } finally {
         server.shutdown();
      }
   }
}
