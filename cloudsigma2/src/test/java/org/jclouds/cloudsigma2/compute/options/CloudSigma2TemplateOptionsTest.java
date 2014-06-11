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
package org.jclouds.cloudsigma2.compute.options;

import org.jclouds.cloudsigma2.domain.DeviceEmulationType;
import org.jclouds.cloudsigma2.domain.Model;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "CloudSigma2TemplateOptionsTest")
public class CloudSigma2TemplateOptionsTest {

   public void testDefaultsToVirtIO() {
      CloudSigma2TemplateOptions options = new CloudSigma2TemplateOptions();
      assertEquals(options.getDeviceEmulationType(), DeviceEmulationType.VIRTIO);
      assertEquals(options.getNicModel(), Model.VIRTIO);
   }

   public void testDeviceEmulationType() {
      CloudSigma2TemplateOptions options = new CloudSigma2TemplateOptions.Builder()
            .deviceEmulationType(DeviceEmulationType.IDE);
      assertEquals(options.getDeviceEmulationType(), DeviceEmulationType.IDE);
   }

   public void testNicModel() {
      CloudSigma2TemplateOptions options = new CloudSigma2TemplateOptions.Builder().nicModel(Model.E1000);
      assertEquals(options.getNicModel(), Model.E1000);
   }

   public void testVncPassword() {
      CloudSigma2TemplateOptions options = new CloudSigma2TemplateOptions.Builder().vncPassword("foo");
      assertEquals(options.getVncPassword(), "foo");
   }
}
