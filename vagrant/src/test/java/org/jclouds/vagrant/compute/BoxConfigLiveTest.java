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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.vagrant.internal.BoxConfig;
import org.jclouds.vagrant.reference.VagrantConstants;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

@Test(groups = "live", testName = "BoxConfigLiveTest")
public class BoxConfigLiveTest extends BaseComputeServiceContextLiveTest {

   public BoxConfigLiveTest() {
      provider = "vagrant";
   }

   @Test
   public void testDefaultCredentials() {
      Image image = view.getComputeService().getImage("ubuntu/trusty64");

      BoxConfig.Factory boxConfigFactory = new BoxConfig.Factory();
      BoxConfig boxConfig = boxConfigFactory.newInstance(image);

      assertFalse(boxConfig.getStringKey(VagrantConstants.CONFIG_USERNAME).isPresent());
      assertFalse(boxConfig.getStringKey(VagrantConstants.CONFIG_PASSWORD).isPresent());
   }

   @Test
   public void testCustomUsernameAndPassword() {
      Image image = view.getComputeService().getImage("ubuntu/xenial64");

      BoxConfig.Factory boxConfigFactory = new BoxConfig.Factory();
      BoxConfig boxConfig = boxConfigFactory.newInstance(image);

      assertEquals(boxConfig.getStringKey(VagrantConstants.CONFIG_USERNAME), Optional.of("ubuntu"));
      // Password changes on each box update
      assertTrue(boxConfig.getStringKey(VagrantConstants.CONFIG_PASSWORD).isPresent());
   }
}
