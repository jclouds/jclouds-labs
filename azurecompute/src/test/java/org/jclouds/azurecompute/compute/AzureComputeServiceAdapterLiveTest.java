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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.azurecompute.options.AzureComputeTemplateOptions;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.sshj.config.SshjSshClientModule;

import org.testng.annotations.Test;
import org.testng.annotations.AfterGroups;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.Arrays;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

@Test(groups = "live", singleThreaded = true, testName = "AzureComputeServiceAdapterLiveTest")
public class AzureComputeServiceAdapterLiveTest extends BaseAzureComputeApiLiveTest {

   private AzureComputeServiceAdapter adapter;

   private TemplateBuilder templateBuilder;

   private Factory sshFactory;

   private String storageServiceName = null;

   @Override
   protected String getStorageServiceName() {
      if (storageServiceName == null) {
         storageServiceName = String.format("%3.20sacsa", System.getProperty("user.name") + RAND).toLowerCase();
      }
      return storageServiceName;
   }

   @Override
   protected AzureComputeApi create(final Properties props, final Iterable<Module> modules) {
      final Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      adapter = injector.getInstance(AzureComputeServiceAdapter.class);
      templateBuilder = injector.getInstance(TemplateBuilder.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      return injector.getInstance(AzureComputeApi.class);
   }

   @Test
   public void testListLocations() {
      assertFalse(Iterables.isEmpty(adapter.listLocations()), "locations must not be empty");
   }

   @Test
   public void testListImages() {
      assertFalse(Iterables.isEmpty(adapter.listImages()), "images must not be empty");
   }

   /**
    * Functionally equivalent to {@link AzureComputeServiceContextLiveTest#testLaunchNode()}.
    */
   @Test
   public void testCreateNodeWithGroupEncodedIntoNameThenStoreCredentials() {
      final String groupName = String.format("%s%d-group-acsalt",
              System.getProperty("user.name"),
              new Random(999).nextLong());

      final String name = String.format("%1.5s%dacsalt", System.getProperty("user.name"), new Random(999).nextInt());

      templateBuilder.imageId(BaseAzureComputeApiLiveTest.IMAGE_NAME);
      templateBuilder.hardwareId("BASIC_A0");
      templateBuilder.locationId(BaseAzureComputeApiLiveTest.LOCATION);
      final Template template = templateBuilder.build();

      // test passing custom options
      final AzureComputeTemplateOptions options = template.getOptions().as(AzureComputeTemplateOptions.class);
      options.inboundPorts(22);
      options.storageAccountName(getStorageServiceName());
      options.virtualNetworkName(VIRTUAL_NETWORK_NAME);
      options.subnetName(DEFAULT_SUBNET_NAME);
      options.addressSpaceAddressPrefix(DEFAULT_ADDRESS_SPACE);
      options.subnetAddressPrefix(DEFAULT_SUBNET_ADDRESS_SPACE);
      options.nodeNames(Arrays.asList(name));

      NodeAndInitialCredentials<Deployment> deployment = null;
      try {
         deployment = adapter.createNodeWithGroupEncodedIntoName(groupName, name, template);
         assertEquals(deployment.getNode().name(), name);
         assertEquals(deployment.getNodeId(), deployment.getNode().name());

         // wait for node to start...
         final Set<Deployment> nodes = Sets.newHashSet();
         retry(new Predicate<String>() {

            @Override
            public boolean apply(final String input) {
               final Deployment node = adapter.getNode(input);
               if (node != null) {
                  nodes.add(node);
               }
               return !nodes.isEmpty();
            }
         }, 600, 30, 30, SECONDS).apply(name);

         assertFalse(nodes.isEmpty());
         final Deployment node = nodes.iterator().next();
         assert InetAddresses.isInetAddress(node.virtualIPs().get(0).address()) : deployment;

         final SshClient client = sshFactory.create(
                 HostAndPort.fromParts(node.virtualIPs().get(0).address(), 22),
                 deployment.getCredentials());
         client.connect();
         final ExecResponse hello = client.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
      } finally {
         if (deployment != null) {
            adapter.destroyNode(deployment.getNodeId());
         }
      }
   }

   @Test
   public void testListHardwareProfiles() {
      final Iterable<RoleSize> roleSizes = adapter.listHardwareProfiles();
      assertFalse(Iterables.isEmpty(roleSizes));

      for (RoleSize roleSize : roleSizes) {
         assertNotNull(roleSize);
      }
   }

   @AfterGroups(groups = "live", alwaysRun = true)
   @Override
   protected void tearDown() {
      super.tearDown();
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module>of(getLoggingModule(), new SshjSshClientModule());
   }

}
