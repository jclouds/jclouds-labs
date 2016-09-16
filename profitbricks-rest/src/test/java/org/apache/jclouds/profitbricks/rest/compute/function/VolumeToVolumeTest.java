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
package org.apache.jclouds.profitbricks.rest.compute.function;

import com.squareup.okhttp.mockwebserver.MockResponse;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "VolumeToVolumeTest", singleThreaded = true)
public class VolumeToVolumeTest extends BaseProfitBricksApiMockTest {

   private VolumeToVolume fnVolume;

   @BeforeTest
   public void setup() {
      this.fnVolume = new VolumeToVolume();
   }

   @Test
   public void testVolumeToVolume() {

      server.enqueue(
              new MockResponse().setBody(stringFromResource("/compute/volume.json"))
      );

      org.apache.jclouds.profitbricks.rest.domain.Volume volume = api.volumeApi().getVolume("datacenter-id", "some-id");

      Volume actual = fnVolume.apply(volume);

      Volume expected = new VolumeBuilder()
              .id(volume.id())
              .size(40f)
              .device("1")
              .durable(true)
              .type(Volume.Type.LOCAL)
              .build();

      assertEquals(actual, expected);
   }
}
