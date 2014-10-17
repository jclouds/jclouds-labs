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
package org.jclouds.digitalocean.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.Event;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.domain.Size;
import org.jclouds.util.Predicates2;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * Base class for the DigitalOcean live tests.
 */
public class BaseDigitalOceanLiveTest extends BaseApiLiveTest<DigitalOceanApi> {

   protected static final int DEFAULT_TIMEOUT_SECONDS = 600;
   protected static final int DEFAULT_POLL_SECONDS = 1;
   protected static final String DEFAULT_IMAGE = "ubuntu-14-04-x64";

   protected List<Size> sizes;
   protected List<Region> regions;

   protected Size defaultSize;
   protected Image defaultImage;
   protected Region defaultRegion;

   public BaseDigitalOceanLiveTest() {
      provider = "digitalocean";
   }

   protected void initializeImageSizeAndRegion() {
      sizes = sortedSizes().sortedCopy(api.getSizesApi().list());
      regions = api.getRegionApi().list();

      assertTrue(sizes.size() > 1, "There must be at least two sizes");
      assertTrue(regions.size() > 1, "There must be at least two regions");

      defaultImage = api.getImageApi().get(DEFAULT_IMAGE);
      checkNotNull(defaultImage, "Image %s not found", DEFAULT_IMAGE);

      defaultSize = sizes.get(0);
      defaultRegion = regions.get(0);
   }

   protected void waitForEvent(Integer eventId) {
      Predicates2.retry(new Predicate<Integer>() {
         @Override
         public boolean apply(Integer input) {
            Event event = api.getEventApi().get(input);
            return Event.Status.DONE == event.getStatus();
         }
      }, DEFAULT_TIMEOUT_SECONDS, DEFAULT_POLL_SECONDS, TimeUnit.SECONDS).apply(eventId);
   }

   protected static Ordering<Size> sortedSizes() {
      return new Ordering<Size>() {
         @Override
         public int compare(Size left, Size right) {
            return ComparisonChain.start().compare(left.getCpu(), right.getCpu())
                  .compare(left.getMemory(), right.getMemory()).compare(left.getDisk(), right.getDisk()).result();
         }
      };
   }
}
