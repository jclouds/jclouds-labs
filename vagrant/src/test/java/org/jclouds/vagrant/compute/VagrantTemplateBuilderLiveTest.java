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
package org.jclouds.vagrant.compute;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;
import static org.jclouds.compute.domain.OsFamily.CENTOS;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "live", testName = "VagrantTemplateBuilderLiveTest")
public class VagrantTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public VagrantTemplateBuilderLiveTest() {
      provider = "vagrant";
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.of();
   }

   @Test
   @Override
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      String imageId = defaultTemplate.getImage().getId();
      if (imageId.startsWith("ubuntu")) {
         assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), UBUNTU);
      } else if (imageId.startsWith("centos")) {
         assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), CENTOS);
      }
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getHardware().getName(), "micro");
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

}
