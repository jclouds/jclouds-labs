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
package org.jclouds.dimensiondata.cloudcontrol.features;

import com.squareup.okhttp.mockwebserver.MockResponse;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseAccountAwareCloudControlMockTest;
import org.testng.annotations.Test;

import static javax.ws.rs.HttpMethod.POST;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

@Test(groups = "unit", testName = "CustomerImageApiMockTest", singleThreaded = true)
public class CustomerImageApiMockTest extends BaseAccountAwareCloudControlMockTest {

   public void testDeleteCustomerImage() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"DELETE_CUSTOMER_IMAGE\",\n" + "\"responseCode\": \"IN_PROGRESS\",\n"
                  + "\"message\": \"Request to Delete Customer Image has been accepted. Please use appropriate Get or List API for status.\"\n"
                  + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      boolean deleted = api.getCustomerImageApi().deleteCustomerImage("id");
      assertSent(POST, "/caas/2.7/6ac1e746-b1ea-4da5-a24e-caf1a978789d/image/deleteCustomerImage");
      assertTrue(deleted);
   }

   public void testDeleteCustomerImage_404() throws Exception {
      server.enqueue(response404());
      boolean deleted = api.getCustomerImageApi().deleteCustomerImage("networkDomainId");
      assertSent(POST, "/caas/2.7/6ac1e746-b1ea-4da5-a24e-caf1a978789d/image/deleteCustomerImage");
      assertFalse(deleted);
   }
}
