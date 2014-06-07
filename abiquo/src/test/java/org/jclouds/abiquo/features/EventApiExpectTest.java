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
package org.jclouds.abiquo.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.event.options.EventOptions;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.abiquo.server.core.event.EventDto;
import com.abiquo.server.core.event.EventsDto;

/**
 * Expect tests for the {@link EventApi}.
 */
@Test(groups = "unit", testName = "EventApiExpectTest")
public class EventApiExpectTest extends BaseAbiquoApiExpectTest<EventApi> {

   public void testListEventsWithPagination() {
      EventApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost/api/events")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(EventsDto.MEDIA_TYPE)) //
                  .addQueryParam("limit", "1") //
                  .addQueryParam("has", "text") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/events-page.xml", normalize(EventsDto.MEDIA_TYPE))) //
                  .build());

      EventOptions options = EventOptions.builder().limit(1).has("text").build();
      PaginatedCollection<EventDto, EventsDto> result = api.listEvents(options);

      assertEquals(result.size(), 2);
      assertEquals(result.getTotalSize().intValue(), 4);
      assertEquals(result.get(0).getId().intValue(), 109);
      assertNotNull(result.searchLink("first"));
      assertNotNull(result.searchLink("last"));
   }

   public void testListEventsReturns2xx() {
      EventApi api = requestsSendResponses(
            HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost/api/events")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(EventsDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/events-page.xml", normalize(EventsDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost/api/events")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(EventsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "1") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/events-lastpage.xml",
                              normalize(EventsDto.MEDIA_TYPE))) //
                  .build());

      PagedIterable<EventDto> result = api.listEvents();
      List<EventDto> all = result.concat().toList();

      assertEquals(all.size(), 4);
      assertEquals(all.get(0).getId().intValue(), 109);
      assertEquals(all.get(1).getId().intValue(), 108);
      assertEquals(all.get(2).getId().intValue(), 110);
      assertEquals(all.get(3).getId().intValue(), 111);
   }

   @Override
   protected EventApi clientFrom(AbiquoApi api) {
      return api.getEventApi();
   }
}
