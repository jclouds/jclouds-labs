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
package org.jclouds.openstack.heat.v1.features;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jclouds.openstack.heat.v1.HeatApi;
import org.jclouds.openstack.heat.v1.domain.Stack;
import org.jclouds.openstack.heat.v1.domain.StackResource;
import org.jclouds.openstack.heat.v1.internal.BaseHeatApiMockTest;
import org.jclouds.openstack.heat.v1.options.CreateStack;
import org.jclouds.openstack.heat.v1.options.ListStackOptions;
import org.jclouds.openstack.heat.v1.options.UpdateStack;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests annotation parsing of {@code StackApi}
 */
@Test(groups = "unit", testName = "StackApiMockTest")
public class StackApiMockTest extends BaseHeatApiMockTest {

   public static final String TEST_STACK_NAME = "testStack";
   public static final String TEST_STACK_ID = "testStack";
   public static final String RESOURCES_TEST_NAME = "testResources";
   public static final String TEST_STACK_RESOURCE_NAME = "cinder_volume";


   public void testGetAutoStack() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/stack_get_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         Stack stack = api.get("simple_stack", "3095aefc-09fb-4bc7-b1f0-f21a304e864c");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks/simple_stack/3095aefc-09fb-4bc7-b1f0-f21a304e864c");


         assertThat(stack).isNotNull();
      } finally {
         server.shutdown();
      }
   }

   public void testGetStackFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         Stack stack = api.get("Non_Existing_Stack", "Non-Existing-Stack");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks/Non_Existing_Stack/Non-Existing-Stack");

         assertNull(stack);

      } finally {
         server.shutdown();
      }
   }

   public void testGeStackWithIDOnly() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/stack_get_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         Stack stack = api.get("3095aefc-09fb-4bc7-b1f0-f21a304e864c");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks/3095aefc-09fb-4bc7-b1f0-f21a304e864c");


         assertThat(stack).isNotNull();
      } finally {
         server.shutdown();
      }
   }

   public void testGetStackWithIDOnlyFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         Stack stack = api.get("Non-Existing-Stack");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks/Non-Existing-Stack");

         assertNull(stack);

      } finally {
         server.shutdown();
      }
   }

   public void testGetStackResourceFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         StackResource stackResource = api.getStackResource(TEST_STACK_NAME, TEST_STACK_ID, "Non_Existing_Stack_resource");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks/" + TEST_STACK_NAME + "/" + TEST_STACK_ID + "/resources/Non_Existing_Stack_resource");

         assertNull(stackResource);

      } finally {
         server.shutdown();
      }
   }


   public void testList() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/stack_list_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         List<Stack> stacks = api.list();

         /*
          * Check request
          */
         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks");

         /*
          * Check response
          */
         assertThat(stacks).isNotEmpty();
         assertThat(stacks.size()).isEqualTo(1);

      } finally {
         server.shutdown();
      }
   }

   public void testListWithOptions() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/stack_list_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         ListStackOptions options = ListStackOptions.Builder.name("simple_stack").showNested(true).globalTenant(true);

         List<Stack> stacks = api.list(options);

         /*
          * Check request
          */
         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks?name=simple_stack&show_nested=true&global_tenant=true");

         /*
          * Check response
          */
         assertThat(stacks).isNotEmpty();
         assertThat(stacks.size()).isEqualTo(1);

      } finally {
         server.shutdown();
      }
   }

   public void testListResource() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/stack_resources_list_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         List<StackResource> stackResources = api.listStackResources(TEST_STACK_NAME, TEST_STACK_ID);

         /*
          * Check request
          */
         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks/" + TEST_STACK_NAME + "/" + TEST_STACK_ID + "/resources");

         /*
          * Check response
          */
         assertThat(stackResources).isNotEmpty();
         assertThat(stackResources.size()).isEqualTo(1);

      } finally {
         server.shutdown();
      }
   }

   public void testGetResource() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/stack_resources_get_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         StackResource stackResource = api.getStackResource(TEST_STACK_NAME, TEST_STACK_ID, TEST_STACK_RESOURCE_NAME);

         /*
          * Check request
          */
         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks/" + TEST_STACK_NAME + "/" + TEST_STACK_ID + "/resources/" + TEST_STACK_RESOURCE_NAME);

         /*
          * Check response
          */
         assertThat(stackResource).isNotNull();
         assertThat(stackResource.getName()).isNotNull();
         assertThat(stackResource.getName()).isNotEmpty();
         assertThat(stackResource.getLogicalResourceId()).isNotNull();
         assertThat(stackResource.getLogicalResourceId()).isNotEmpty();
         assertThat(stackResource.getPhysicalResourceId()).isNotNull();
         assertThat(stackResource.getPhysicalResourceId()).isNotEmpty();

      } finally {
         server.shutdown();
      }
   }

   public void testListIsEmpty() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         List<Stack> stacks = api.list();

         /*
          * Check request
          */
         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks");

         /*
          * Check response
          */
         assertThat(stacks).isEmpty();
      } finally {
         server.shutdown();
      }
   }

   public void testListWithOptionsIsEmpty() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         ListStackOptions options = ListStackOptions.Builder.name("Stack_dont_exist");
         List<Stack> stacks = api.list(options);

         /*
          * Check request
          */
         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks?name=Stack_dont_exist");

         /*
          * Check response
          */
         assertThat(stacks).isEmpty();
      } finally {
         server.shutdown();
      }
   }

   public void testListStackResourceIsEmpty() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         List<StackResource> stackResources = api.listStackResources("empty_stack", "empty_stack_id");

         /*
          * Check request
          */
         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks/empty_stack/empty_stack_id/resources");

         /*
          * Check response
          */
         assertThat(stackResources).isEmpty();
      } finally {
         server.shutdown();
      }
   }

   public void testCreateWithTemplateUrl() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/create_stack.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         Map<String, String> parameters = new HashMap<String, String>();
         parameters.put("key_name", "myKey");
         parameters.put("image_id", "3b7be1fa-d381-4067-bb81-e835df630564");
         parameters.put("instance_type", "SMALL_1");
         CreateStack createStack = CreateStack.builder().name(TEST_STACK_NAME).templateUrl("http://10.5.5.121/Installs/cPaaS/YAML/simple_stack.yaml").build();
         Stack stack = api.create(createStack);

         /*
          * Check request
          */
         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", BASE_URI + "/stacks");

         /*
          * Check response
          */
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isEqualTo("3095aefc-09fb-4bc7-b1f0-f21a304e864c");

      } finally {
         server.shutdown();
      }
   }

   public void testDeleteStack() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200)));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         boolean result = api.delete(TEST_STACK_NAME, TEST_STACK_ID);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", BASE_URI + "/stacks/" + TEST_STACK_NAME + "/" + TEST_STACK_ID);

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteReturnFalseOn404Stack() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         boolean result = api.delete("Non-Existing-Stack-Name", "Non-Existing-Stack-ID");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", BASE_URI + "/stacks/Non-Existing-Stack-Name/Non-Existing-Stack-ID");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateStack() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(202)));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         UpdateStack updateOptions = UpdateStack.builder().templateUrl("http://10.5.5.121/Installs/cPaaS/YAML/simple_stack.yaml").build();

         boolean result = api.update(TEST_STACK_NAME, TEST_STACK_ID, updateOptions);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", BASE_URI + "/stacks/" + TEST_STACK_NAME + "/" + TEST_STACK_ID,
               "/stack_put_update_request.json");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testResourcesMetadata() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/resources_metadata.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         Map<String, Object> metadata = api.getStackResourceMetadata(TEST_STACK_NAME, TEST_STACK_ID, RESOURCES_TEST_NAME);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks/" + TEST_STACK_NAME + "/" + TEST_STACK_ID + "/resources/" + RESOURCES_TEST_NAME + "/metadata");
         assertThat(metadata).isNotEmpty();


      } finally {
         server.shutdown();
      }
   }

   public void testResourcesMetadataReturnNullOn404() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         StackApi api = heatApi.getStackApi("RegionOne");

         Map<String, Object> metadata = api.getStackResourceMetadata(TEST_STACK_NAME, TEST_STACK_ID, "Stack_Resource_dont_exist");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/stacks/" + TEST_STACK_NAME + "/" + TEST_STACK_ID + "/resources/Stack_Resource_dont_exist/metadata");
         assertThat(metadata).isEmpty();


      } finally {
         server.shutdown();
      }
   }
}

