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
package org.jclouds.openstack.marconi.v1.features;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.openstack.marconi.v1.MarconiApi;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Everett Toews
 */
@Test
public class QueueApiMockTest extends BaseOpenStackMockTest<MarconiApi> {

   public void createQueue() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZone("DFW");
         boolean success = queueApi.create("jclouds-test");

         assertTrue(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "PUT /v1/123123/queues/jclouds-test HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }
}
