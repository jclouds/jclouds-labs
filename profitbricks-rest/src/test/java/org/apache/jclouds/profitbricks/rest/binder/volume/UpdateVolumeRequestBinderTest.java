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

import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.Map;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "UpdateVolumeRequestBinderTest")
public class UpdateVolumeRequestBinderTest {

   @Test
   public void testCreatePayload() {
      
      Injector injector = Guice.createInjector(new GsonModule());
      UpdateVolumeRequestBinder binder = injector.getInstance(UpdateVolumeRequestBinder.class);
            
      Volume.Request.UpdatePayload payload = Volume.Request.updatingBuilder()
              .dataCenterId("datacenter-id")
              .id("volume-id")
              .name("apache-volume")
              .build();

      String actual = binder.createPayload(payload);
      
      HttpRequest request = binder.createRequest(
              HttpRequest.builder().method("PATCH").endpoint("http://test.com").build(), 
              actual
      );
      
      assertEquals(request.getEndpoint().getPath(), "/rest/datacenters/datacenter-id/volumes/volume-id");
      assertNotNull(actual, "Binder returned null payload");

      Json json = injector.getInstance(Json.class);
      String expectedJson = json.toJson(json.fromJson(expectedPayload, new TypeToken<Map<String, Object>>(){}.getType()));
      
      assertEquals(actual, expectedJson);
   }

   private final String expectedPayload = "{\"name\":\"apache-volume\"}";
}
