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
package org.jclouds.abiquo.domain.network;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.NetworkType;

/**
 * Live integration tests for the {@link Network} domain class.
 */
@Test(groups = "api", testName = "GenericNetworkLiveApiTest")
public class GenericNetworkLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testListDatacenterNetworks() {
      // Make sure all network types are listed
      Iterable<Network<?>> networks = env.datacenter.listNetworks();
      assertNotNull(networks);
      assertEquals(size(networks), 3);
   }

   public void testListPublicNetworks() {
      Iterable<Network<?>> networks = env.datacenter.listNetworks(NetworkType.PUBLIC);
      assertNotNull(networks);
      assertEquals(size(networks), 1);

      // Make sure it can be converted
      get(networks, 0).toPublicNetwork();
   }

   public void testListExternaletworks() {
      Iterable<Network<?>> networks = env.datacenter.listNetworks(NetworkType.EXTERNAL);
      assertNotNull(networks);
      assertEquals(size(networks), 1);

      // Make sure it can be converted
      get(networks, 0).toExternalNetwork();
   }

   public void testListUnmanagedNetworks() {
      Iterable<Network<?>> networks = env.datacenter.listNetworks(NetworkType.UNMANAGED);
      assertNotNull(networks);
      assertEquals(size(networks), 1);

      // Make sure it can be converted
      get(networks, 0).toUnmanagedNetwork();
   }

   public void testListPrivateNetworks() {
      try {
         env.datacenter.listNetworks(NetworkType.INTERNAL);
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.BAD_REQUEST, "QUERY-1");
      }
   }
}
