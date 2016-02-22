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
package org.apache.jclouds.profitbricks.rest.binder.volume;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.ws.rs.core.MediaType;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.payloads.UrlEncodedFormPayload;
import org.jclouds.json.config.GsonModule;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "CreateSnapshotRequestBinderTest")
public class CreateSnapshotRequestBinderTest {

   @Test
   public void testCreatePayload() {
      
      Injector injector = Guice.createInjector(new GsonModule());
      CreateSnapshotRequestBinder binder = injector.getInstance(CreateSnapshotRequestBinder.class);
            
      Volume.Request.CreateSnapshotPayload payload = Volume.Request.createSnapshotBuilder()
            .dataCenterId("datacenter-id")
            .volumeId("volume-id")
            .name("test-snapshot")
            .description("snapshot desc...")
            .build();

      HttpRequest request = binder.createRequest(
              HttpRequest.builder().method("POST").endpoint("http://test.com").build(), 
              binder.createPayload(payload)
      );
      
      Multimap<String, String> expectedPayload = HashMultimap.create();
      
      expectedPayload.put("name", "test-snapshot");
      expectedPayload.put("description", "snapshot desc...");
            
      assertEquals(request.getEndpoint().getPath(), "/rest/datacenters/datacenter-id/volumes/volume-id/create-snapshot");
      assertEquals(request.getPayload().getContentMetadata().getContentType(), MediaType.APPLICATION_FORM_URLENCODED);
      assertEquals(request.getPayload().getRawContent(), "&" + (new UrlEncodedFormPayload(expectedPayload)).getRawContent());

   }

}
