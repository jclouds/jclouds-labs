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
package org.apache.jclouds.profitbricks.rest.compute;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.inject.Module;
import org.apache.jclouds.profitbricks.rest.ProfitBricksApi;
import org.apache.jclouds.profitbricks.rest.config.ProfitBricksRateLimitModule;
import org.apache.jclouds.profitbricks.rest.domain.FirewallRule;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.domain.zonescoped.DataCenterAndId;
import static org.assertj.core.api.Assertions.assertThat;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = "live", singleThreaded = true, testName = "ProfitBricksComputeServiceLiveTest")
public class ProfitBricksComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   static ProfitBricksApi pbApi;

   public ProfitBricksComputeServiceLiveTest() {
      provider = "profitbricks-rest";
   }

   @Override
   protected TemplateBuilder templateBuilder() {
      TemplateOptions authOptions = TemplateOptions.Builder
              .authorizePublicKey(keyPair.get("public"))
              .overrideLoginPrivateKey(keyPair.get("private"));
      return super.templateBuilder().options(authOptions);
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
   protected Iterable<Module> setupModules() {
      ImmutableSet.Builder<Module> modules = ImmutableSet.builder();
      modules.addAll(super.setupModules());
      modules.add(new ProfitBricksRateLimitModule());
      return modules.build();
   }

   @Override
   public void testOptionToNotBlock() throws Exception {
      // ProfitBricks implementation intentionally blocks until the node is 'AVAILABLE'
   }

   @Override
   protected void checkTagsInNodeEquals(NodeMetadata node, ImmutableSet<String> tags) {
      // ProfitBricks doesn't support tags
   }

   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      // ProfitBricks doesn't support user metadata
   }

   @Override
   protected void checkResponseEqualsHostname(ExecResponse execResponse, NodeMetadata node1) {
      // ProfitBricks doesn't support hostname
   }

   @Override
   protected void checkOsMatchesTemplate(NodeMetadata node) {
      // Not enough description from API to match template
   }

   @Override
   protected void createAndRunAServiceInGroup(String group) throws RunNodesException {
      super.createAndRunAServiceInGroup(group);
      pbApi = client.getContext().unwrapApi(ProfitBricksApi.class);

      int matches = 0;
      NodeMetadata node = Iterables.getOnlyElement(client.listNodesDetailsMatching(NodePredicates.inGroup(group)));
      DataCenterAndId datacenterAndId = DataCenterAndId.fromSlashEncoded(node.getId());
      Server server = pbApi.serverApi().getServer(datacenterAndId.getDataCenter(), datacenterAndId.getId(), new DepthOptions().depth(5));
      for (FirewallRule rule : server.entities().nics().items().get(0).entities().firewallrules().items()) {
         if (rule.properties().portRangeStart() == 22 || rule.properties().portRangeStart() == 8080) {
            matches++;
         }
      }
      Assert.assertEquals(2, matches);
   }

   @Override
   @Test
   public void testCreateNodeWithCustomHardware() throws Exception {
      Template template = buildTemplate(templateBuilder().imageNameMatches("Ubuntu").osVersionMatches("1[467]\\.04")
              .hardwareId("automatic:cores=2;ram=2048;disk=20"));
      try {
         NodeMetadata node = getOnlyElement(client.createNodesInGroup(group + "custom", 1, template));
         assertThat(node.getHardware().getRam()).isEqualTo(2048);
         assertThat(node.getHardware().getProcessors().get(0).getCores()).isEqualTo(2);
         assertThat(node.getHardware().getVolumes().get(0).getSize()).isEqualTo(20);
         assertThat(node.getHardware().getId()).isEqualTo("cpu=2,ram=2048,disk=20");
      } finally {
         client.destroyNodesMatching(inGroup(group + "custom"));
      }
   }

   @Test
   public void testCreateNodeWithCustomHardwareUsingMins() throws Exception {
      Template template = buildTemplate(templateBuilder().imageNameMatches("Ubuntu").osVersionMatches("1[467]\\.04")
              .minCores(2).minRam(2048).minDisk(20));
      try {
         NodeMetadata node = getOnlyElement(client.createNodesInGroup(group + "custom", 1, template));
         assertThat(node.getHardware().getRam()).isEqualTo(2048);
         assertThat(node.getHardware().getProcessors().get(0).getCores()).isEqualTo(2);
         assertThat(node.getHardware().getVolumes().get(0).getSize()).isEqualTo(20);
         assertThat(node.getHardware().getId()).isEqualTo("cpu=2,ram=2048,disk=20");
      } finally {
         client.destroyNodesMatching(inGroup(group + "custom"));
      }
   }

}
