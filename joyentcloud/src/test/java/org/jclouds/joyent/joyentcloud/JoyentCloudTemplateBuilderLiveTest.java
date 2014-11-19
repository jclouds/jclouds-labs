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
package org.jclouds.joyent.joyentcloud;

import static org.jclouds.compute.domain.OsFamily.SMARTOS;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "live", singleThreaded = true, testName = "JoyentCloudTemplateBuilderLiveTest")
public class JoyentCloudTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public JoyentCloudTemplateBuilderLiveTest() {
      provider = "joyentcloud";
   }

   @Test
   public void testTemplateBuilderSmartOS() throws IOException {
      Template smartTemplate = view.getComputeService().templateBuilder().osFamily(SMARTOS).build();
      assertEquals(smartTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(smartTemplate.getImage().getOperatingSystem().getVersion(), "1.6.3");
      assertEquals(smartTemplate.getImage().getOperatingSystem().getFamily(), SMARTOS);
      assertEquals(smartTemplate.getImage().getName(), "smartos");
      assertEquals(smartTemplate.getImage().getDefaultCredentials().getUser(), "root");
      assertEquals(smartTemplate.getLocation().getId(), "us-east-1");
      assertEquals(smartTemplate.getImage().getLocation().getId(), "us-east-1");
      assertEquals(smartTemplate.getHardware().getLocation().getId(), "us-east-1");
   }

   @Test
   @Override
   public void testDefaultTemplateBuilder() {
      Template defaultTemplate = this.view.getComputeService().templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "12.04");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), UBUNTU);
      assertEquals(defaultTemplate.getImage().getName(), "ubuntu-12.04");
      assertEquals(defaultTemplate.getImage().getDefaultCredentials().getUser(), "root");
      assertEquals(defaultTemplate.getLocation().getId(), "us-east-1");
      assertEquals(defaultTemplate.getImage().getLocation().getId(), "us-east-1");
      assertEquals(defaultTemplate.getHardware().getLocation().getId(), "us-east-1");
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("US-VA", "US-CA", "US-NV", "NL-NH");
   }
}
