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

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jclouds.azurecompute.compute.options.AzureComputeTemplateOptions;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Module;

@Test(groups = "live", testName = "AzureComputeServiceContextLiveTest")
public class AzureComputeServiceContextLiveTest extends BaseComputeServiceContextLiveTest {

   private static final int COUNT = 2;

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   public AzureComputeServiceContextLiveTest() {
      provider = "azurecompute";
   }


   @Test
   public void testWindowsNode() throws RunNodesException {
      final String groupName = String.format("win-%s", System.getProperty("user.name"));

      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      //templateBuilder.imageId("3a50f22b388a4ff7ab41029918570fa6__Windows-Server-2012-Essentials-20141204-enus");
      templateBuilder.imageId("a699494373c04fc0bc8f2bb1389d6106__Nano_TP4_Azure_20151118_8GB.vhd");
      templateBuilder.hardwareId("BASIC_A2");
      templateBuilder.locationId(BaseAzureComputeApiLiveTest.LOCATION);
      final Template template = templateBuilder.build();

      // test passing custom options
      final AzureComputeTemplateOptions options = template.getOptions().as(AzureComputeTemplateOptions.class);
      options.inboundPorts(5985);

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 1, template);
         assertThat(nodes).hasSize(1);
      } finally {
         view.getComputeService().destroyNodesMatching(inGroup(groupName));
      }
   }

   @Test
   public void testLaunchNodes() throws RunNodesException {
      final int rand = new Random().nextInt(999);
      final String groupName = String.format("%s%d-group-acsclt", System.getProperty("user.name"), rand);

      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      templateBuilder.imageId(BaseAzureComputeApiLiveTest.IMAGE_NAME);
      templateBuilder.hardwareId("BASIC_A0");
      templateBuilder.locationId(BaseAzureComputeApiLiveTest.LOCATION);
      final Template template = templateBuilder.build();

      // test passing custom options
      final AzureComputeTemplateOptions options = template.getOptions().as(AzureComputeTemplateOptions.class);
      options.inboundPorts(22);

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, COUNT, template);
         assertThat(nodes).hasSize(COUNT);

         Map<? extends NodeMetadata, ExecResponse> responses = view.getComputeService().runScriptOnNodesMatching(runningInGroup(groupName), "echo hello");
         assertThat(responses).hasSize(COUNT);

         for (ExecResponse execResponse : responses.values()) {
            assertThat(execResponse.getOutput().trim()).isEqualTo("hello");
         }
      } catch (RunScriptOnNodesException e) {
         Assert.fail();
      } finally {
         view.getComputeService().destroyNodesMatching(inGroup(groupName));
      }
   }

   @Test(expectedExceptions = { IllegalStateException.class })
   public void testNotExistingStorageAccount() throws RunNodesException {
      final int rand = new Random().nextInt(999);
      final String groupName = String.format("%s%d-group-acsclt", System.getProperty("user.name"), rand);

      final String storageServiceName = "not3x1st1ng";

      final Template template = view.getComputeService().templateBuilder().build();

      // test passing custom options
      final AzureComputeTemplateOptions options = template.getOptions().as(AzureComputeTemplateOptions.class);
      options.storageAccountName(storageServiceName);

      Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 1, template);
   }

}
