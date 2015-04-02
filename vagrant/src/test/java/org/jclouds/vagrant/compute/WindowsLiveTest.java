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

import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.vagrant.internal.BoxConfig;
import org.jclouds.vagrant.reference.VagrantConstants;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Building the image:
 *   $ git clone https://github.com/boxcutter/windows.git boxcutter-windows
 *   $ cd boxcutter-windows
 *   $ make virtualbox/eval-win7x86-enterprise
 *   $ vagrant box add boxcutter/eval-win7x86-enterprise box/virtualbox/eval-win7x86-enterprise-nocm-1.0.4.box
 */
@Test(groups = "live", singleThreaded = true, enabled = true, testName = "WindowsLiveTest")
public class WindowsLiveTest extends BaseComputeServiceContextLiveTest {

   protected ComputeService client;

   public WindowsLiveTest() {
      provider = "vagrant";
   }

   @Override
   protected void initializeContext() {
      super.initializeContext();
      client = view.getComputeService();
   }

   protected TemplateBuilder templateBuilder() {
      TemplateBuilder templateBuilder = client.templateBuilder();
      if (templateBuilderSpec != null) {
         templateBuilder = templateBuilder.from(templateBuilderSpec);
      }
      templateBuilder.imageId(getImageId());
      return templateBuilder;
   }

   private String getImageId() {
      return "boxcutter/eval-win7x86-enterprise";
   }

   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.build();
   }

   @Test
   public void testGet() throws Exception {
      Set<? extends NodeMetadata> nodes = client.createNodesInGroup("vagrant-win", 1, buildTemplate(templateBuilder()));
      NodeMetadata node = Iterables.getOnlyElement(nodes);
      OperatingSystem os = node.getOperatingSystem();
      LoginCredentials creds = node.getCredentials();
      assertEquals(os.getFamily(), OsFamily.WINDOWS);
      assertEquals(creds.getUser(), "vagrant");
      assertTrue(creds.getOptionalPassword().isPresent(), "password expected");
      assertEquals(creds.getOptionalPassword().get(), "vagrant");
      assertFalse(creds.getOptionalPrivateKey().isPresent(), "no private key expected for windows");
      assertEquals(node.getLoginPort(), 5985);
      client.destroyNode(node.getId());
   }


   @Test
   public void testBoxConfig() {
      Image image = view.getComputeService().getImage(getImageId());

      BoxConfig.Factory boxConfigFactory = new BoxConfig.Factory();
      BoxConfig boxConfig = boxConfigFactory.newInstance(image);

      assertEquals(boxConfig.getStringKey(".vm.communicator"), Optional.of("winrm"));
      assertEquals(boxConfig.getKey(VagrantConstants.KEY_VM_GUEST), Optional.of(VagrantConstants.VM_GUEST_WINDOWS));
   }
}
