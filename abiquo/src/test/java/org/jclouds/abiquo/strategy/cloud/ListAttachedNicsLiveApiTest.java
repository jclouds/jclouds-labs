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
package org.jclouds.abiquo.strategy.cloud;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.abiquo.domain.network.ExternalIp;
import org.jclouds.abiquo.domain.network.Ip;
import org.jclouds.abiquo.domain.network.PrivateIp;
import org.jclouds.abiquo.domain.network.PublicIp;
import org.jclouds.abiquo.domain.network.UnmanagedNetwork;
import org.jclouds.abiquo.strategy.BaseAbiquoStrategyLiveApiTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * Live tests for the {@link ListAttachedNics} strategy.
 */
@Test(groups = "api", testName = "ListAttachedNicsLiveApiTest")
public class ListAttachedNicsLiveApiTest extends BaseAbiquoStrategyLiveApiTest {
   private ListAttachedNics strategy;

   private PrivateIp privateIp;

   private ExternalIp externalIp;

   private PublicIp publicIp;

   @Override
   @BeforeClass(groups = "api")
   protected void setupStrategy() {
      this.strategy = env.context.utils().injector().getInstance(ListAttachedNics.class);

      privateIp = getLast(env.privateNetwork.listUnusedIps());
      externalIp = getLast(env.externalNetwork.listUnusedIps());

      publicIp = getLast(env.virtualDatacenter.listAvailablePublicIps());
      env.virtualDatacenter.purchasePublicIp(publicIp);
      publicIp = find(env.virtualDatacenter.listPurchasedPublicIps(), new Predicate<PublicIp>() {
         @Override
         public boolean apply(PublicIp input) {
            return input.getIp().equals(publicIp.getIp());
         }
      });

      env.virtualMachine.setNics(Lists.<Ip<?, ?>> newArrayList(privateIp, externalIp, publicIp),
            Lists.<UnmanagedNetwork> newArrayList(env.unmanagedNetwork));
   }

   @AfterClass(groups = "api")
   protected void tearDownStrategy() {
      env.virtualMachine.setNics(Lists.<Ip<?, ?>> newArrayList(privateIp));
      final String address = publicIp.getIp();
      env.virtualDatacenter.releasePublicIp(publicIp);
      assertNull(find(env.virtualDatacenter.listPurchasedPublicIps(), new Predicate<PublicIp>() {
         @Override
         public boolean apply(PublicIp input) {
            return input.getIp().equals(address);
         }
      }, null));
   }

   public void testExecute() {
      Iterable<Ip<?, ?>> vapps = strategy.execute(env.virtualMachine);
      assertNotNull(vapps);
      assertEquals(4, size(vapps));
   }
}
