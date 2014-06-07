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
package org.jclouds.abiquo.environment;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.isEmpty;
import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.ContextBuilder;
import org.jclouds.abiquo.AbiquoApiMetadata;
import org.jclouds.abiquo.AbiquoContext;
import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.network.PrivateNetwork;
import org.jclouds.abiquo.features.CloudApi;
import org.jclouds.abiquo.features.services.EventService;

import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

/**
 * Test environment for cloud live tests.
 */
public class CloudTestEnvironment extends InfrastructureTestEnvironment {

   // Environment data made public so tests can use them easily
   public CloudApi cloudApi;

   public EventService eventService;

   public VirtualDatacenter virtualDatacenter;

   public VirtualAppliance virtualAppliance;

   public VirtualMachine virtualMachine;

   public VirtualMachineTemplate template;

   public PrivateNetwork privateNetwork;

   public Enterprise defaultEnterprise;

   public AbiquoContext plainUserContext;

   public AbiquoContext enterpriseAdminContext;

   public CloudTestEnvironment(final AbiquoContext context) {
      super(context);
      this.cloudApi = context.getApiContext().getApi().getCloudApi();
      this.eventService = context.getEventService();
   }

   @Override
   public void setup() throws Exception {
      // Create base infrastructure
      super.setup();

      createUserContext();
      createEnterpriseAdminContext();

      findDefaultEnterprise();
      createVirtualDatacenter();
      createVirtualAppliance();
      refreshTemplateRepository();
      createVirtualMachine();
   }

   @Override
   public void tearDown() throws Exception {
      closeEnterpriseAdminContext();
      closeUserContext();

      deleteVirtualMachine();
      deleteVirtualAppliance();
      deleteVirtualDatacenter();

      // Delete base infrastructure
      super.tearDown();
   }

   // Setup

   private void createUserContext() {
      String endpoint = checkNotNull(System.getProperty("test.abiquo.endpoint"), "test.abiquo.endpoint");

      plainUserContext = ContextBuilder.newBuilder(new AbiquoApiMetadata()) //
            .endpoint(endpoint) //
            .credentials("abiquo", "jclouds") //
            .build(AbiquoContext.class);
   }

   private void createEnterpriseAdminContext() {
      String endpoint = checkNotNull(System.getProperty("test.abiquo.endpoint"), "test.abiquo.endpoint");

      enterpriseAdminContext = ContextBuilder.newBuilder(new AbiquoApiMetadata()) //
            .endpoint(endpoint) //
            .credentials("jclouds-admin", "admin") //
            .build(AbiquoContext.class);
   }

   protected void findDefaultEnterprise() {
      defaultEnterprise = find(context.getAdministrationService().listEnterprises(), new Predicate<Enterprise>() {
         @Override
         public boolean apply(Enterprise input) {
            return input.getName().equals("Abiquo");
         }
      });
   }

   protected void createVirtualDatacenter() {
      privateNetwork = PrivateNetwork.builder(context.getApiContext()).name("DefaultNetwork").gateway("192.168.1.1")
            .address("192.168.1.0").mask(24).build();

      virtualDatacenter = VirtualDatacenter.builder(context.getApiContext(), datacenter, defaultEnterprise)
            .name(PREFIX + "Virtual Aloha").cpuCountLimits(18, 20).hdLimitsInMb(279172872, 279172872)
            .publicIpsLimits(2, 3).ramLimits(19456, 20480).storageLimits(289910292, 322122547).vlansLimits(3, 4)
            .hypervisorType(machine.getType()).network(privateNetwork).build();

      virtualDatacenter.save();
      assertNotNull(virtualDatacenter.getId());

      privateNetwork = find(virtualDatacenter.listPrivateNetworks(), new Predicate<PrivateNetwork>() {
         @Override
         public boolean apply(PrivateNetwork input) {
            return input.getName().equals(privateNetwork.getName());
         }
      });
   }

   protected void createVirtualAppliance() {
      virtualAppliance = VirtualAppliance.builder(context.getApiContext(), virtualDatacenter)
            .name(PREFIX + "Virtual AppAloha").build();

      virtualAppliance.save();
      assertNotNull(virtualAppliance.getId());
   }

   protected void createVirtualMachine() {
      Iterable<VirtualMachineTemplate> templates = virtualDatacenter.listAvailableTemplates();
      assertFalse(isEmpty(templates));

      template = templateBySize().min(templates);

      virtualMachine = VirtualMachine.builder(context.getApiContext(), virtualAppliance, template).cpu(2)
            .nameLabel(PREFIX + "VM Aloha").ram(128).build();

      virtualMachine.save();
      assertNotNull(virtualMachine.getId());

   }

   protected void refreshTemplateRepository() {
      defaultEnterprise.refreshTemplateRepository(datacenter);
   }

   // Tear down

   private void closeUserContext() {
      plainUserContext.close();
   }

   private void closeEnterpriseAdminContext() {
      enterpriseAdminContext.close();
   }

   protected void deleteVirtualDatacenter() {
      if (virtualDatacenter != null && enterprise != null && datacenter != null) {
         Integer idVirtualDatacenter = virtualDatacenter.getId();
         virtualDatacenter.delete();
         assertNull(cloudApi.getVirtualDatacenter(idVirtualDatacenter));
      }
   }

   protected void deleteVirtualAppliance() {
      if (virtualAppliance != null && virtualDatacenter != null) {
         Integer idVirtualAppliance = virtualAppliance.getId();
         virtualAppliance.delete();
         assertNull(cloudApi.getVirtualAppliance(virtualDatacenter.unwrap(), idVirtualAppliance));
      }
   }

   protected void deleteVirtualMachine() {
      if (virtualMachine != null && virtualAppliance != null && virtualDatacenter != null) {
         Integer idVirtualMachine = virtualMachine.getId();
         virtualMachine.delete();
         assertNull(cloudApi.getVirtualMachine(virtualAppliance.unwrap(), idVirtualMachine));
      }
   }

   public static Ordering<VirtualMachineTemplate> templateBySize() {
      return new Ordering<VirtualMachineTemplate>() {
         @Override
         public int compare(final VirtualMachineTemplate left, final VirtualMachineTemplate right) {
            return Longs.compare(left.getDiskFileSize(), right.getDiskFileSize());
         }
      };
   }

}
