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

import org.jclouds.dimensiondata.cloudcontrol.domain.CustomerImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseAccountAwareCloudControlMockTest;
import org.jclouds.http.Uris;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.util.Set;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Mock tests for the {@link ServerImageApi} class.
 */
@Test(groups = "unit", testName = "ServerImageApiMockTest", singleThreaded = true)
public class ServerImageApiMockTest extends BaseAccountAwareCloudControlMockTest {

   public void testGetOsImage() throws Exception {
      server.enqueue(jsonResponse("/osImage.json"));
      OsImage osImage = api.getServerImageApi().getOsImage("id");
      assertNotNull(osImage);
      assertSent(HttpMethod.GET, getOsImageUrl().appendPath("/id").toString());
   }

   public void testGetOsImage_404() throws Exception {
      server.enqueue(response404());
      OsImage osImage = api.getServerImageApi().getOsImage("id");
      assertNull(osImage);
      assertSent(HttpMethod.GET, getOsImageUrl().appendPath("/id").toString());
   }

   public void testListOsImage() throws Exception {
      server.enqueue(jsonResponse("/osImages.json"));
      Iterable<OsImage> osImages = api.getServerImageApi().listOsImages().concat();
      assertEquals(size(osImages), 1);
      assertEquals(server.getRequestCount(), 2);

      assertSent(HttpMethod.GET, getListOsImageUrl().toString());
   }

   public void testListOsImageWithPagination() throws Exception {
      server.enqueue(jsonResponse("/osImages_page1.json"));
      server.enqueue(jsonResponse("/osImages_page2.json"));
      Iterable<OsImage> osImages = api.getServerImageApi().listOsImages().concat();
      assertNotNull(osImages);
      consumeIterableAndAssertAdditionalPagesRequested(osImages, 2, 1);

      Uris.UriBuilder uriBuilder = getListOsImageUrl();
      assertSent(HttpMethod.GET, uriBuilder.toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(uriBuilder, 2, false).toString());
   }

   public void testListOsImage_404() throws Exception {
      server.enqueue(response404());
      assertTrue(api.getServerImageApi().listOsImages().concat().isEmpty());
      assertSent(HttpMethod.GET, getListOsImageUrl().toString());
   }

   private Uris.UriBuilder getListOsImageUrl() {
      Uris.UriBuilder uriBuilder = getOsImageUrl();
      Set<String> zones = ctx.utils().injector().getInstance(ZoneIdsSupplier.class).get();
      for (String zone : zones) {
         uriBuilder.addQuery("datacenterId", zone);
      }
      return uriBuilder;
   }

   public void testGetCustomerImage() throws Exception {
      server.enqueue(jsonResponse("/customerImage.json"));
      CustomerImage customerImage = api.getServerImageApi().getCustomerImage("id");
      assertNotNull(customerImage);
      assertSent(HttpMethod.GET, getCustomerImageUrl().appendPath("/id").toString());
   }

   public void testGetCustomerImage_404() throws Exception {
      server.enqueue(response404());
      CustomerImage customerImage = api.getServerImageApi().getCustomerImage("id");
      assertNull(customerImage);
      assertSent(HttpMethod.GET, getCustomerImageUrl().appendPath("/id").toString());
   }

   public void testListCustomerImage() throws Exception {
      server.enqueue(jsonResponse("/customerImages.json"));
      Iterable<CustomerImage> customerImages = api.getServerImageApi().listCustomerImages().concat();
      assertEquals(size(customerImages), 1);
      assertEquals(server.getRequestCount(), 2);

      assertSent(HttpMethod.GET, getListCustomerImageUrl().toString());
   }

   public void testListCustomerImageWithPagination() throws Exception {
      server.enqueue(jsonResponse("/customerImages_page1.json"));
      server.enqueue(jsonResponse("/customerImages_page2.json"));
      Iterable<CustomerImage> customerImages = api.getServerImageApi().listCustomerImages().concat();
      assertNotNull(customerImages);
      consumeIterableAndAssertAdditionalPagesRequested(customerImages, 10, 1);

      Uris.UriBuilder uriBuilder = getListCustomerImageUrl();

      assertSent(HttpMethod.GET, uriBuilder.toString());
      assertSent(HttpMethod.GET, addZonesToUriBuilder(addPageNumberToUriBuilder(uriBuilder, 2, true)).toString());
   }

   public void testListCustomerImage_404() throws Exception {
      server.enqueue(response404());
      assertTrue(api.getServerImageApi().listCustomerImages().concat().isEmpty());
      assertSent(HttpMethod.GET, getListCustomerImageUrl().toString());
   }

   private Uris.UriBuilder getListCustomerImageUrl() {
      Uris.UriBuilder uriBuilder = getCustomerImageUrl();
      Set<String> zones = ctx.utils().injector().getInstance(ZoneIdsSupplier.class).get();
      for (String zone : zones) {
         uriBuilder.addQuery("datacenterId", zone);
      }
      return uriBuilder;
   }

   private Uris.UriBuilder getOsImageUrl() {
      Uris.UriBuilder uriBuilder = Uris
            .uriBuilder("/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/image/osImage");
      return uriBuilder;
   }

   private Uris.UriBuilder getCustomerImageUrl() {
      Uris.UriBuilder uriBuilder = Uris
            .uriBuilder("/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/image/customerImage");
      return uriBuilder;
   }

}
