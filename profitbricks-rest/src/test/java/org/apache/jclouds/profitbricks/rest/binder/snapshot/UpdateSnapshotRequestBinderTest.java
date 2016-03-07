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
package org.apache.jclouds.profitbricks.rest.binder.snapshot;

import java.util.HashMap;
import org.apache.jclouds.profitbricks.rest.binder.BinderTestBase;
import org.apache.jclouds.profitbricks.rest.domain.Snapshot;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "UpdateSnapshotRequestBinderTest")
public class UpdateSnapshotRequestBinderTest extends BinderTestBase {

   @Test
   public void testUpdatePayload() {
      
      UpdateSnapshotRequestBinder binder = injector.getInstance(UpdateSnapshotRequestBinder.class);
      
      Snapshot.Request.UpdatePayload payload = Snapshot.Request.updatingBuilder()
              .id("some-id")
              .name("new-snapshot-name")
              .description("description...")
              .build();
      
      String actual = binder.createPayload(payload);

      HttpRequest request = binder.createRequest(
              HttpRequest.builder().method("POST").endpoint("http://test.com").build(), 
              actual
      );
      
      assertEquals(request.getEndpoint().getPath(), "/rest/snapshots/some-id");
      assertNotNull(actual, "Binder returned null payload");
      
      Json json = injector.getInstance(Json.class);
      
      HashMap<String, Object> expectedPayload = new HashMap<String, Object>();
      
      expectedPayload.put("name", "new-snapshot-name");
      expectedPayload.put("description", "description...");
      
      assertEquals(actual, json.toJson(expectedPayload));
   }

}
