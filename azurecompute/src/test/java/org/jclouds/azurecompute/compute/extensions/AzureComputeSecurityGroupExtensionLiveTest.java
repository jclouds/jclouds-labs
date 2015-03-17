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
package org.jclouds.azurecompute.compute.extensions;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

import org.jclouds.compute.extensions.internal.BaseSecurityGroupExtensionLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.SecurityGroupExtension;

import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Live test for AzureCompute {@link org.jclouds.compute.extensions.SecurityGroupExtension} implementation.
 */
@Test(groups = "live", singleThreaded = true, testName = "AzureComputeSecurityGroupExtensionLiveTest")
public class AzureComputeSecurityGroupExtensionLiveTest extends BaseSecurityGroupExtensionLiveTest {

   public AzureComputeSecurityGroupExtensionLiveTest() {
      super();
      provider = "azurecompute";
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.of(getLoggingModule(), credentialStoreModule, getSshModule());
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @BeforeClass(groups = {"integration", "live"})
   public void setup() {
      final ComputeService computeService = view.getComputeService();

      final Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      if (securityGroupExtension.isPresent()) {
         final Optional<SecurityGroup> group = Iterables.tryFind(securityGroupExtension.get().listSecurityGroups(),
                 new Predicate<SecurityGroup>() {
                    @Override
                    public boolean apply(final SecurityGroup input) {
                       return input.getId().equals(secGroupName);
                    }
                 });

         if (group.isPresent()) {
            securityGroupExtension.get().removeSecurityGroup(group.get().getId());
         }
      }
   }

   @AfterClass(groups = {"integration", "live"}, alwaysRun = true)
   @Override
   protected void tearDownContext() {
      super.tearDownContext();
   }

}
