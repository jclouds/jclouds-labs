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
package org.apache.jclouds.oneandone.rest.compute.function;

import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "HddToVolumeTest", singleThreaded = true)
public class HddToVolumeTest {

   private HddToVolume fnVolume;

   @BeforeTest
   public void setup() {
      this.fnVolume = new HddToVolume();
   }

   @Test
   public void testHddToVolume() {


      org.apache.jclouds.oneandone.rest.domain.Hdd hdd = org.apache.jclouds.oneandone.rest.domain.Hdd.create("some-id", 40, true);

      Volume actual = fnVolume.apply(hdd);

      Volume expected = new VolumeBuilder()
              .id(hdd.id())
              .size((float) hdd.size())
              .bootDevice(hdd.isMain())
              .durable(true)
              .type(Volume.Type.LOCAL).build();

      assertEquals(actual, expected);
   }
}
