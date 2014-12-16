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
import java.util.Properties;
import java.util.Random;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.functions.DefaultCredentialsFromImageOrOverridingCredentials;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.inject.Injector;
import com.google.inject.Module;

@Test(groups = "live", singleThreaded = true, testName = "AzureComputeServiceAdapterLiveTest")
public class AzureComputeServiceAdapterLiveTest extends BaseAzureComputeApiLiveTest {

   private AzureComputeServiceAdapter adapter;
   private TemplateBuilder templateBuilder;
   private Factory sshFactory;
   private NodeAndInitialCredentials<Deployment> deployment;

   @Override
   protected AzureComputeApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      adapter = injector.getInstance(AzureComputeServiceAdapter.class);
      templateBuilder = injector.getInstance(TemplateBuilder.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      return injector.getInstance(AzureComputeApi.class);
   }

   @Test
   public void testListLocations() {
      Iterable<Location> locations = adapter.listLocations();
      assertFalse(Iterables.isEmpty(locations), "locations must not be empty");
   }

   @Test
   public void testListImages() {
      assertFalse(Iterables.isEmpty(adapter.listImages()), "images must not be empty");
   }

   private static final PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate = new PrioritizeCredentialsFromTemplate(
         new DefaultCredentialsFromImageOrOverridingCredentials());

   @Test
   public void testCreateNodeWithGroupEncodedIntoNameThenStoreCredentials() {
      String group = "foo";
      String name = "node" + new Random().nextInt();

      Template template = templateBuilder.build();

      deployment = adapter.createNodeWithGroupEncodedIntoName(group, name, template);
      assertEquals(deployment.getNode().name(), name);
      assertEquals(deployment.getNodeId(), deployment.getNode().name());
      assert InetAddresses.isInetAddress(deployment.getNode().virtualIPs().get(0).address()) : deployment;
      doConnectViaSsh(deployment.getNode(), prioritizeCredentialsFromTemplate.apply(template, deployment.getCredentials()));
   }

   protected void doConnectViaSsh(Deployment deployment, LoginCredentials creds) {
      SshClient ssh = sshFactory.create(HostAndPort.fromParts(deployment.virtualIPs().get(0).address(), 22), creds);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @Test
   public void testListHardwareProfiles() {
      Iterable<RoleSize> roleSizes = adapter.listHardwareProfiles();
      assertFalse(Iterables.isEmpty(roleSizes));

      for (RoleSize roleSize : roleSizes) {
         assertNotNull(roleSize);
      }
   }

   @AfterGroups(groups = "live", alwaysRun = true)
   protected void tearDown() {
      if (deployment != null) {
         adapter.destroyNode(deployment.getNodeId());
      }
      super.tearDown();
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(getLoggingModule(), new SshjSshClientModule());
   }

}
