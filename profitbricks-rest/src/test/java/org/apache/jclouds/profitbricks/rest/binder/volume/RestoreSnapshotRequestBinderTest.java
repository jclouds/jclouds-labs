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

import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.ws.rs.core.MediaType;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.config.GsonModule;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "RestoreSnapshotRequestBinderTest")
public class RestoreSnapshotRequestBinderTest {

   @Test
   public void testRestorePayload() {
      
      Injector injector = Guice.createInjector(new GsonModule());
      RestoreSnapshotRequestBinder binder = injector.getInstance(RestoreSnapshotRequestBinder.class);
            
      Volume.Request.RestoreSnapshotPayload payload = Volume.Request.restoreSnapshotBuilder()
            .dataCenterId("datacenter-id")
            .volumeId("volume-id")
            .snapshotId("snapshot-id")
            .build();

      HttpRequest request = binder.createRequest(
              HttpRequest.builder().method("POST").endpoint("http://test.com").build(), 
              binder.createPayload(payload)
      );
      
      assertEquals(request.getEndpoint().getPath(), "/rest/datacenters/datacenter-id/volumes/volume-id/restore-snapshot");
      assertEquals(request.getPayload().getContentMetadata().getContentType(), MediaType.APPLICATION_FORM_URLENCODED);
      assertEquals(request.getPayload().getRawContent(), "&snapshotId=snapshot-id");

   }

}
