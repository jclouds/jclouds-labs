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
package org.jclouds.openstack.poppy.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jclouds.openstack.poppy.v1.domain.Caching;
import org.jclouds.openstack.poppy.v1.domain.CreateService;
import org.jclouds.openstack.poppy.v1.domain.Domain;
import org.jclouds.openstack.poppy.v1.domain.LogDelivery;
import org.jclouds.openstack.poppy.v1.domain.Origin;
import org.jclouds.openstack.poppy.v1.domain.Restriction;
import org.jclouds.openstack.poppy.v1.domain.RestrictionRule;
import org.jclouds.openstack.poppy.v1.domain.Service;
import org.jclouds.openstack.poppy.v1.domain.ServiceStatus;
import org.jclouds.openstack.poppy.v1.domain.UpdateService;
import org.jclouds.openstack.poppy.v1.internal.BasePoppyApiLiveTest;
import org.jclouds.openstack.poppy.v1.predicates.ServicePredicates;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Uninterruptibles;

/**
 * Live tests, as should be executed against devstack or a provider
 */
@Test(groups = "live", testName = "ServiceApiLiveTest")
public class ServiceApiLiveTest extends BasePoppyApiLiveTest {

   public void testCreateUpdateAndDeleteService() {
      ServiceApi serviceApi = api.getServiceApi();
      FlavorApi flavorApi = api.getFlavorApi();
      String serviceId = null;
      try {
         URI serviceURI = serviceApi.create(
               CreateService.builder()
                     .name("jclouds_test_service")
                     .domains(
                           ImmutableList.of(
                                 Domain.builder().domain("www.jclouds" + UUID.randomUUID() + ".com").build(),
                                 Domain.builder().domain("www.example" + UUID.randomUUID() + ".com").build()))
                     .origins(ImmutableList.of(
                           Origin.builder()
                                 .origin("jclouds123456123456.com")
                                 .port(80)
                                 .sslEnabled(false)
                                 .build()))
                     .restrictions(ImmutableList.of(
                           Restriction.builder()
                                 .name("website only")
                                 .rules(ImmutableList.of(
                                       RestrictionRule.builder()
                                             .name("jclouds123456123456.com")
                                             .httpHost("www.jclouds123456123456.com")
                                             .build())).build()))
                     .caching(ImmutableList.of(Caching.builder().name("default").ttl(3600).build()))
                     .flavorId(flavorApi.list().first().get().getId())
                     .logDelivery(LogDelivery.builder().enabled(false).build())
                     .build()
         );

         assertNotNull(serviceURI);
         String path = serviceURI.getPath();
         serviceId = path.substring(path.lastIndexOf('/') + 1);

         /* List and get tests */
         Service serviceList = api.getServiceApi().list().concat().toSet().iterator().next();
         assertNotNull(serviceList);
         Service serviceGet = api.getServiceApi().get(serviceId);
         assertEquals(serviceList, serviceGet);

         ServicePredicates.awaitDeployed(serviceApi).apply(serviceGet);
         assertEquals(serviceApi.get(serviceId).getStatus(), ServiceStatus.DEPLOYED);

         UpdateService updated = serviceGet.toUpdatableService().name("updated_name").build();
         serviceApi.update(serviceId, serviceGet, updated);
         Uninterruptibles.sleepUninterruptibly(30, TimeUnit.SECONDS);
         assertEquals(serviceApi.get(serviceId).getName(), "updated_name");

         assertTrue(serviceApi.deleteAsset(serviceId, "image/1.jpg"));
         assertTrue(serviceApi.deleteAssets(serviceId));
      }
      finally {
         if (serviceId != null) {
            assertTrue(serviceApi.delete(serviceId));
         }
      }
   }
}
