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
package org.apache.jclouds.oneandone.rest.compute;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.inject.Module;
import org.apache.jclouds.oneandone.rest.compute.config.OneAndOneRateLimitModule;
import static org.assertj.core.api.Assertions.assertThat;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

@Test(groups = "live", singleThreaded = true, testName = "OneAndOneComputeServiceLiveTest")
public class OneAndOneComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public OneAndOneComputeServiceLiveTest() {
      provider = "oneandone";
   }

   @Override
   protected Iterable<Module> setupModules() {
      ImmutableSet.Builder<Module> modules = ImmutableSet.builder();
      modules.addAll(super.setupModules());
      modules.add(new OneAndOneRateLimitModule());
      return modules.build();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected LoggingModule getLoggingModule() {
      return new SLF4JLoggingModule();
   }

   @Override
   public void testOptionToNotBlock() throws Exception {
      // OneAndOne implementation intentionally blocks until the node is 'AVAILABLE'
   }

   @Override
   protected void checkTagsInNodeEquals(NodeMetadata node, ImmutableSet<String> tags) {
      // OneAndOne doesn't support tags
   }

   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      // OneAndOne doesn't support user metadata
   }

   @Override
   protected void checkResponseEqualsHostname(ExecResponse execResponse, NodeMetadata node1) {
      // OneAndOne doesn't support hostname
   }

   @Override
   @Test
   public void testCreateNodeWithCustomHardware() throws Exception {
      Template template = buildTemplate(templateBuilder()
              .hardwareId("automatic:cores=2;ram=2048;disk=20").osVersionMatches("14.04").osFamily(OsFamily.UBUNTU));
      try {
         NodeMetadata node = getOnlyElement(client.createNodesInGroup(group + "custom", 1, template));
         assertThat(node.getHardware().getRam()).isEqualTo(2048);
         assertThat(node.getHardware().getProcessors().size()).isEqualTo(2);
         assertThat(node.getHardware().getVolumes().get(0).getSize()).isEqualTo(20.0F);
         assertThat(node.getHardware().getId()).isEqualTo("automatic:cores=2.0;ram=2048;disk=20");
      } finally {
         client.destroyNodesMatching(inGroup(group + "custom"));
      }
   }

   @Test
   public void testCreateNodeWithCustomHardwareUsingMins() throws Exception {
      Template template = buildTemplate(templateBuilder().hardwareId("automatic:cores=1;ram=512;disk=20")
              .minCores(1).minRam(512).minDisk(20).osVersionMatches("14.04").osFamily(OsFamily.UBUNTU));
      try {
         NodeMetadata node = getOnlyElement(client.createNodesInGroup(group + "custom", 1, template));
         assertThat(node.getHardware().getRam()).isEqualTo(512);
         assertThat(node.getHardware().getProcessors().size()).isEqualTo(1);
         assertThat(node.getHardware().getVolumes().get(0).getSize()).isEqualTo(20.0F);
         assertThat(node.getHardware().getId()).isEqualTo("automatic:cores=1.0;ram=512;disk=20");
      } finally {
         client.destroyNodesMatching(inGroup(group + "custom"));
      }
   }
}
