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
package org.jclouds.azurecompute.compute;

import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = {"integration", "live"}, singleThreaded = true, testName = "AzureComputeServiceLiveTest", alwaysRun = false)
public class AzureComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public AzureComputeServiceLiveTest() {
      super();
      provider = "azurecompute";
      // this is 30 seconds by default, but Azure will take anyway longer because we need to wait for a non-null
      // Deployment object to be returned: see the end of AzureComputeServiceAdapter#createNodeWithGroupEncodedIntoName
      nonBlockDurationSeconds = 600;
   }

   @Override
   protected void checkUserMetadataContains(final NodeMetadata node, final ImmutableMap<String, String> userMetadata) {
      // Azure doe not support user metadata
   }

   @Override
   protected void checkTagsInNodeEquals(final NodeMetadata node, final ImmutableSet<String> tags) {
      // Azure does not support tags
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.imageId(BaseAzureComputeApiLiveTest.IMAGE_NAME)
              .hardwareId("BASIC_A1")
              .locationId(BaseAzureComputeApiLiveTest.LOCATION)
              .build();
   }
}
