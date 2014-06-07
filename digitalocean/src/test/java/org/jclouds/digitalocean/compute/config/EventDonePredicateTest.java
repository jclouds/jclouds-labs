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
package org.jclouds.digitalocean.compute.config;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.easymock.EasyMock;
import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.compute.config.DigitalOceanComputeServiceContextModule.EventDonePredicate;
import org.jclouds.digitalocean.domain.Event;
import org.jclouds.digitalocean.domain.Event.Status;
import org.jclouds.digitalocean.features.EventApi;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link EventDonePredicate} class.
 */
@Test(groups = "unit", testName = "EventDonePredicateTest")
public class EventDonePredicateTest {

   public void testEventProgress() {
      EventApi eventApi = EasyMock.createMock(EventApi.class);
      DigitalOceanApi api = EasyMock.createMock(DigitalOceanApi.class);

      expect(eventApi.get(1)).andReturn(event(Status.DONE));
      expect(eventApi.get(2)).andReturn(event(Status.PENDING));
      expect(api.getEventApi()).andReturn(eventApi).times(2);
      replay(eventApi, api);

      EventDonePredicate predicate = new EventDonePredicate(api);
      assertTrue(predicate.apply(1));
      assertFalse(predicate.apply(2));
   }

   public void testEventFailed() {
      EventApi eventApi = EasyMock.createMock(EventApi.class);
      DigitalOceanApi api = EasyMock.createMock(DigitalOceanApi.class);

      expect(eventApi.get(anyInt())).andReturn(event(Status.ERROR));
      expect(api.getEventApi()).andReturn(eventApi);
      replay(eventApi, api);

      EventDonePredicate predicate = new EventDonePredicate(api);

      try {
         predicate.apply(1);
         fail("Method should have thrown an IllegalStateException");
      } catch (IllegalStateException ex) {
         assertEquals(ex.getMessage(), "Resource is in invalid status: ERROR");
      }
   }

   private static Event event(Status status) {
      return new Event(0, status, 0, "0", 0);
   }
}
