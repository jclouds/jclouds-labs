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
package org.jclouds.digitalocean.features;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.digitalocean.domain.DropletCreation;
import org.jclouds.digitalocean.domain.Event;
import org.jclouds.digitalocean.internal.BaseDigitalOceanLiveTest;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link EventApi} class.
 */
@Test(groups = "live", testName = "EventApiLiveTest")
public class EventApiLiveTest extends BaseDigitalOceanLiveTest {

   @Override
   protected void initialize() {
      super.initialize();
      initializeImageSizeAndRegion();
   }

   public void testGetEvent() {
      DropletCreation droplet = null;

      try {
         droplet = api.getDropletApi().create("eventtest", defaultImage.getId(), defaultSize.getId(),
               defaultRegion.getId());
         Event event = api.getEventApi().get(droplet.getEventId());
         assertNotNull(event, "Droplet creation event should not be null");
         assertTrue(event.getId() > 0, "Event id should be > 0");
      } finally {
         if (droplet != null) {
            waitForEvent(droplet.getEventId());
            api.getDropletApi().destroy(droplet.getId(), true);
         }
      }
   }

}
