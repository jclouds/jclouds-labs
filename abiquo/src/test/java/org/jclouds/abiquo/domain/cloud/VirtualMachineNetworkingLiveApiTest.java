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
package org.jclouds.abiquo.domain.cloud;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.domain.network.ExternalIp;
import org.jclouds.abiquo.domain.network.Ip;
import org.jclouds.abiquo.domain.network.PrivateIp;
import org.jclouds.abiquo.domain.network.PublicIp;
import org.jclouds.abiquo.domain.network.PublicNetwork;
import org.jclouds.abiquo.domain.network.UnmanagedIp;
import org.jclouds.abiquo.domain.network.UnmanagedNetwork;
import org.jclouds.abiquo.domain.task.VirtualMachineTask;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * Live integration tests for the {@link VirtualMachine} networking operations.
 */
@Test(groups = "api", testName = "VirtualMachineNetworkingLiveApiTest")
public class VirtualMachineNetworkingLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private PrivateIp privateIp;

   private ExternalIp externalIp;

   private PublicIp publicIpInfrastructure;

   private PublicIp publicIpCloud;

   private UnmanagedIp unmanagedIp1;

   private UnmanagedIp unmanagedIp2;

   @BeforeClass
   public void setupIps() {
      privateIp = getLast(env.privateNetwork.listUnusedIps());
      externalIp = getLast(env.externalNetwork.listUnusedIps());

      publicIpInfrastructure = getLast(env.virtualDatacenter.listAvailablePublicIps());
      env.virtualDatacenter.purchasePublicIp(publicIpInfrastructure);

      publicIpCloud = find(env.virtualDatacenter.listPurchasedPublicIps(), new Predicate<PublicIp>() {
         @Override
         public boolean apply(PublicIp input) {
            return input.getIp().equals(publicIpInfrastructure.getIp());
         }
      });
   }

   @AfterClass
   public void restorePrivateIp() {
      VirtualMachineTask task = env.virtualMachine.setNics(Lists.<Ip<?, ?>> newArrayList(privateIp));
      assertNull(task);

      Iterable<Ip<?, ?>> nics = env.virtualMachine.listAttachedNics();
      assertEquals(size(nics), 1);
      assertEquals(get(nics, 0).getId(), privateIp.getId());

      final String address = publicIpCloud.getIp();
      env.virtualDatacenter.releasePublicIp(publicIpCloud);

      assertNull(find(env.virtualDatacenter.listPurchasedPublicIps(), new Predicate<PublicIp>() {
         @Override
         public boolean apply(PublicIp input) {
            return input.getIp().equals(address);
         }
      }, null));
   }

   // TODO: Infrastructure edit link for public ips can not be used to attach
   @Test(enabled = false)
   public void testAttachInfrastructurePublicIp() {
      VirtualMachineTask task = env.virtualMachine.setNics(Lists.<Ip<?, ?>> newArrayList(publicIpInfrastructure));
      assertNull(task);

      Iterable<Ip<?, ?>> nics = env.virtualMachine.listAttachedNics();
      assertEquals(size(nics), 1);
      assertEquals(get(nics, 0).getId(), publicIpInfrastructure.getId());
   }

   public void testAttachPublicIp() {
      VirtualMachineTask task = env.virtualMachine.setNics(Lists.<Ip<?, ?>> newArrayList(publicIpCloud));
      assertNull(task);

      Iterable<Ip<?, ?>> nics = env.virtualMachine.listAttachedNics();
      assertEquals(size(nics), 1);
      assertEquals(get(nics, 0).getId(), publicIpCloud.getId());
   }

   @Test(dependsOnMethods = "testAttachPublicIp")
   public void testAttachPrivateIp() {
      List<Ip<?, ?>> nics = Lists.newArrayList(env.virtualMachine.listAttachedNics());
      nics.add(privateIp);

      VirtualMachineTask task = env.virtualMachine.setNics(nics);
      assertNull(task);

      nics = Lists.newArrayList(env.virtualMachine.listAttachedNics());
      assertEquals(nics.size(), 2);
      assertEquals(nics.get(0).getId(), publicIpCloud.getId());
      assertEquals(nics.get(1).getId(), privateIp.getId());
   }

   @Test(dependsOnMethods = "testAttachPrivateIp")
   public void testAttachExternalIp() {
      List<Ip<?, ?>> nics = Lists.newArrayList(env.virtualMachine.listAttachedNics());
      nics.add(externalIp);

      VirtualMachineTask task = env.virtualMachine.setNics(nics);
      assertNull(task);

      nics = Lists.newArrayList(env.virtualMachine.listAttachedNics());
      assertEquals(nics.size(), 3);
      assertEquals(nics.get(0).getId(), publicIpCloud.getId());
      assertEquals(nics.get(1).getId(), privateIp.getId());
      assertEquals(nics.get(2).getId(), externalIp.getId());
   }

   @Test(dependsOnMethods = "testAttachExternalIp")
   public void testAddUnmanagedNics() {
      Iterable<Ip<?, ?>> nics = env.virtualMachine.listAttachedNics();

      VirtualMachineTask task = env.virtualMachine.setNics(Lists.newArrayList(nics),
            Lists.<UnmanagedNetwork> newArrayList(env.unmanagedNetwork, env.unmanagedNetwork));
      assertNull(task);

      nics = env.virtualMachine.listAttachedNics();
      assertEquals(size(nics), 5);
      assertEquals(get(nics, 0).getId(), publicIpCloud.getId());
      assertEquals(get(nics, 1).getId(), privateIp.getId());
      assertEquals(get(nics, 2).getId(), externalIp.getId());
      // Unmanaged ips are created during the attach.
      assertEquals(get(nics, 3).getNetworkName(), env.unmanagedNetwork.getName());
      assertEquals(get(nics, 4).getNetworkName(), env.unmanagedNetwork.getName());

      unmanagedIp1 = (UnmanagedIp) get(nics, 3);
      unmanagedIp2 = (UnmanagedIp) get(nics, 4);
   }

   @Test(dependsOnMethods = "testAddUnmanagedNics")
   public void testReorderNics() {
      List<Ip<?, ?>> nics = Lists.newArrayList(env.virtualMachine.listAttachedNics());

      VirtualMachineTask task = env.virtualMachine.setNics(Lists.<Ip<?, ?>> newArrayList(nics.get(2), nics.get(1),
            nics.get(0), nics.get(4), nics.get(3)));
      assertNull(task);

      nics = Lists.newArrayList(env.virtualMachine.listAttachedNics());
      assertEquals(nics.size(), 5);
      assertEquals(nics.get(0).getId(), externalIp.getId());
      assertEquals(nics.get(1).getId(), privateIp.getId());
      assertEquals(nics.get(2).getId(), publicIpCloud.getId());
      assertEquals(nics.get(3).getId(), unmanagedIp2.getId());
      assertEquals(nics.get(4).getId(), unmanagedIp1.getId());
   }

   @Test(dependsOnMethods = "testReorderNics")
   public void testDetachNics() {
      List<Ip<?, ?>> nics = Lists.newArrayList(env.virtualMachine.listAttachedNics());

      VirtualMachineTask task = env.virtualMachine.setNics(Lists.<Ip<?, ?>> newArrayList(nics.get(1), nics.get(2)));
      assertNull(task);

      nics = Lists.newArrayList(env.virtualMachine.listAttachedNics());
      assertEquals(nics.size(), 2);
      assertEquals(nics.get(0).getId(), privateIp.getId());
      assertEquals(nics.get(1).getId(), publicIpCloud.getId());
   }

   @Test(dependsOnMethods = "testDetachNics")
   public void testSetDefaultGateway() {
      PublicNetwork network = publicIpCloud.getNetwork();
      env.virtualMachine.setGatewayNetwork(network);

      Integer configId = env.virtualMachine.unwrap().getIdFromLink("network_configuration");
      assertEquals(configId, network.getId());
   }

   // TODO: Review this functionality
   @Test(dependsOnMethods = "testSetDefaultGateway", enabled = false)
   public void testDetachAllNics() {
      try {
         env.virtualMachine.setNics(null);

         fail("It should not be allowed to remove all nics from a vm");
      } catch (AbiquoException ex) {
         // At least one nic must be configured
         assertHasError(ex, Status.BAD_REQUEST, "VM-46");
      }
   }
}
