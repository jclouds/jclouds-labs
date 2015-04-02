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

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class VagrantComputeServiceAdapterLiveTest extends BaseComputeServiceLiveTest {

   public VagrantComputeServiceAdapterLiveTest() {
      provider = "vagrant";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   @Test(enabled = true)
   public void testCorrectAuthException() throws Exception {
      // Vagrant doesn't use credential info
   }

   @Override
   protected void checkTagsInNodeEquals(NodeMetadata node, ImmutableSet<String> tags) {
      // Vagrant doesn't support tags
      // TODO Could store it in the json
   }

   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      // Vagrant doesn't support user metadata
      // TODO Could store it in the json
   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testGet")
   public void testOptionToNotBlock() throws Exception {
       // LoginCredentials are available only after the machine starts,
       // so can't return earlier.
   }

   @Override
   @Test(enabled = true, dependsOnMethods = { "testCompareSizes" })
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      // Fails on CentOS 7. Can't ssh back with user foo because SELinux not configured correctly.
      // "foo" is created out of the /home folder, /over/ridden is not white listed with the correct context.
      // Steps needed to configure SELinux before creating the user:
      //
      // semanage fcontext -a -e /home /over/ridden
      // mkdir /over/ridden
      // restorecon /over/ridden
      // useradd -d /over/ridden/foo foo
      //
      // semanage is not available on a default install - needs "yum install policycoreutils-python"

      Template template = buildTemplate(templateBuilder());
      if (template.getImage().getOperatingSystem().getFamily() != OsFamily.CENTOS) {
         super.testAScriptExecutionAfterBootWithBasicTemplate();
      }
   }
}
