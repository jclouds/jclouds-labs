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
package org.apache.jclouds.profitbricks.rest.features;

import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.Image;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ImageApiMockTest", singleThreaded = true)
public class ImageApiMockTest extends BaseProfitBricksApiMockTest {
   
   @Test
   public void testGetList() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/image/list.json"))
      );
      
      List<Image> list = imageApi().getList();
      
      assertNotNull(list);
      assertEquals(list.size(), 13);
      assertEquals(list.get(0).properties().name(), "bacula-client-agent.iso");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images");
   }
   
   @Test
   public void testGetListWithDepth() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/image/list-depth-5.json"))
      );
      
      List<Image> list = imageApi().getList(new DepthOptions().depth(5));
      
      assertNotNull(list);
      assertEquals(list.size(), 5);
      assertEquals(list.get(0).properties().name(), "bacula-client-agent.iso");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images?depth=5");
   }
   
   @Test
   public void testGetListWith404() throws InterruptedException {
      server.enqueue(new MockResponse().setResponseCode(404));
      List<Image> list = imageApi().getList(new DepthOptions().depth(1));
      assertTrue(list.isEmpty());
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images?depth=1");
   }
       
   @Test
   public void testGetImage() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/image/get.json"));
      response.setHeader("Content-Type", "application/vnd.profitbricks.resource+json");
      
      server.enqueue(response);
      
      Image image = imageApi().getImage("some-id");
      
      assertNotNull(image);
      assertEquals(image.properties().name(), "CentOS-6.7-x86_64-netinstall.iso");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images/some-id");
   }
   
   @Test
   public void testGetImageWithDepth() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/image/get-depth-5.json"));
      response.setHeader("Content-Type", "application/vnd.profitbricks.resource+json");
      
      server.enqueue(response);
      
      Image image = imageApi().getImage("some-id", new DepthOptions().depth(5));
      
      assertNotNull(image);
      assertEquals(image.properties().name(), "bacula-client-agent.iso");
      
      assertEquals(this.server.getRequestCount(), 1);
      assertSent(this.server, "GET", "/images/some-id?depth=5");
   }
   
   public void testGetImageWith404() throws InterruptedException {
      server.enqueue(response404());

      Image volume = imageApi().getImage("some-id");
      
      assertEquals(volume, null);

      assertEquals(this.server.getRequestCount(), 1);
      assertSent(this.server, "GET", "/images/some-id");
   }
   
   private ImageApi imageApi() {
      return api.imageApi();
   }
   
}
