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
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import org.jclouds.openstack.heat.v1.HeatApi;
import org.jclouds.openstack.heat.v1.domain.Template;
import org.jclouds.openstack.heat.v1.internal.BaseHeatApiMockTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests annotation parsing of {@code StackApi}
 */
@Test(groups = "unit", testName = "StackApiMockTest")
public class TemplateApiMockTest extends BaseHeatApiMockTest {

   public void testGetTemplate() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/template_get_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         TemplateApi api = heatApi.getTemplateApi("RegionOne");

         Template template = api.get("simple_stack", "3095aefc-09fb-4bc7-b1f0-f21a304e864c");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET",
               BASE_URI + "/stacks/simple_stack/3095aefc-09fb-4bc7-b1f0-f21a304e864c/template");

         assertThat(template).isNotNull();
         assertEquals(template.getDescription(), "Heat Orchestration Template that spins up a single server");
      } finally {
         server.shutdown();
      }
   }

   public void testGetTemplateFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404).setBody(stringFromResource("/template_get_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         TemplateApi api = heatApi.getTemplateApi("RegionOne");

         Template template = api.get("simple_stack", "3095aefc-09fb-4bc7-b1f0-f21a304e864c");

         assertNull(template);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET",
               BASE_URI + "/stacks/simple_stack/3095aefc-09fb-4bc7-b1f0-f21a304e864c/template");
      } finally {
         server.shutdown();
      }
   }

   public void testValidateTemplate() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/template_validate_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         TemplateApi api = heatApi.getTemplateApi("RegionOne");

         Template template = api.validate("https://examplevalidateurl.com/exampletemplate.json");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST",
               BASE_URI + "/validate", "/template_validate_request.json");

         assertThat(template).isNotNull();
         assertEquals("A template implementation of a resource that provides a single MongoDB server instance",
               template.getDescription());
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testValidateTemplateFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404).setBody(stringFromResource("/template_validate_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         TemplateApi api = heatApi.getTemplateApi("RegionOne");

         Template template = api.validate("https://examplevalidateurl.com/exampletemplate.json");

      } finally {
         server.shutdown();
      }
   }
}

