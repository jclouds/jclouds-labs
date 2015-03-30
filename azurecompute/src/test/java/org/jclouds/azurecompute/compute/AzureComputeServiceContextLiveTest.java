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
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import org.jclouds.azurecompute.options.AzureComputeTemplateOptions;
import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.azurecompute.util.ConflictManagementPredicate;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "AzureComputeServiceContextLiveTest")
public class AzureComputeServiceContextLiveTest extends BaseComputeServiceContextLiveTest {

   private static final int RAND = new Random().nextInt(999);

   private AzureComputeApi api;

   private String storageServiceName = null;

   protected String getStorageServiceName() {
      if (storageServiceName == null) {
         storageServiceName = String.format("%3.20sacsc", System.getProperty("user.name") + RAND).toLowerCase();
      }
      return storageServiceName;
   }

   @BeforeClass
   public void setup() {
      api = newBuilder().
              modules(setupModules()).
              overrides(setupProperties()).
              buildApi(new TypeToken<AzureComputeApi>(getClass()) {

                 private static final long serialVersionUID = 309104475566522958L;

              });
   }

   @AfterClass(alwaysRun = true)
   public void tearDown() {
      assertTrue(new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api.getStorageAccountApi().delete(getStorageServiceName());
         }
      }.apply(getStorageServiceName()));
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   public AzureComputeServiceContextLiveTest() {
      super();

      provider = "azurecompute";
   }

   /**
    * Functionally equivalent to
    * {@link AzureComputeServiceAdapterLiveTest#testCreateNodeWithGroupEncodedIntoNameThenStoreCredentials}.
    *
    * @throws RunNodesException
    */
   @Test
   public void testLaunchNode() throws RunNodesException {
      final String groupName = String.format("%s%d-group-acsclt",
              System.getProperty("user.name"),
              new Random(999).nextInt());

      final String name = String.format("%1.5s%dacsclt", System.getProperty("user.name"), new Random(999).nextInt());

      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      templateBuilder.imageId(BaseAzureComputeApiLiveTest.IMAGE_NAME);
      templateBuilder.hardwareId("BASIC_A0");
      templateBuilder.locationId(BaseAzureComputeApiLiveTest.LOCATION);
      final Template tmp = templateBuilder.build();

      // test passing custom options
      final AzureComputeTemplateOptions options = tmp.getOptions().as(AzureComputeTemplateOptions.class);
      options.inboundPorts(22);
      options.storageAccountName(getStorageServiceName());
      options.virtualNetworkName(BaseAzureComputeApiLiveTest.VIRTUAL_NETWORK_NAME);
      options.subnetName(BaseAzureComputeApiLiveTest.DEFAULT_SUBNET_NAME);
      options.addressSpaceAddressPrefix(BaseAzureComputeApiLiveTest.DEFAULT_ADDRESS_SPACE);
      options.subnetAddressPrefix(BaseAzureComputeApiLiveTest.DEFAULT_SUBNET_ADDRESS_SPACE);
      options.nodeNames(Arrays.asList(name));

      NodeMetadata node = null;
      try {
         final Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 1, tmp);
         node = Iterables.getOnlyElement(nodes);

         final SshClient client = view.utils().sshForNode().apply(node);
         client.connect();
         final ExecResponse hello = client.exec("echo hello");
         assertThat(hello.getOutput().trim()).isEqualTo("hello");
      } finally {
         if (node != null) {
            view.getComputeService().destroyNode(node.getId());
         }
      }
   }

}
