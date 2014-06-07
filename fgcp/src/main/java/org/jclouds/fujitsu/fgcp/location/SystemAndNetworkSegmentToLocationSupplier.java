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
package org.jclouds.fujitsu.fgcp.location;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.domain.VNet;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.fujitsu.fgcp.domain.VSystemWithDetails;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.all.RegionToProviderOrJustProvider;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;

/**
 * Builds location hierarchy by querying the back-end for the networks of all
 * virtual systems.
 * <p>
 * Example:
 * <p>
 * 
 * <pre>
 * >location provider fgcp(-au)
 * -> location region au/nsw
 * --> location system vsys
 * ---> location network DMZ/SECURE1/SECURE2
 * </pre>
 * 
 * Todo: caching - provider/region won't change. if vsys still exists, network
 * won't change
 */
@Singleton
public class SystemAndNetworkSegmentToLocationSupplier implements LocationsSupplier {

   private final RegionToProviderOrJustProvider regionProvider;
   private FGCPApi api;

   @Inject
   SystemAndNetworkSegmentToLocationSupplier(RegionToProviderOrJustProvider regionProvider, FGCPApi api) {
      this.regionProvider = checkNotNull(regionProvider, "regionToProviderOrJustProvider");
      this.api = checkNotNull(api, "api");
   }

   @Override
   public Set<Location> get() {
      Builder<Location> locations = ImmutableSet.builder();

      for (VSystem system : api.getVirtualDCApi().listVirtualSystems()) {

         VSystemWithDetails systemWithDetails = api.getVirtualSystemApi().getDetails(system.getId());

         Location systemLocation = new LocationBuilder().scope(LocationScope.SYSTEM)
               .parent(Iterables.getOnlyElement(regionProvider.get())).description(system.getName()).id(system.getId())
               .build();

         for (VNet net : systemWithDetails.getNetworks()) {

            locations.add(new LocationBuilder().scope(LocationScope.NETWORK).parent(systemLocation)
                  .description(net.getNetworkId().replaceFirst(".+-N-", "")).id(net.getNetworkId())
                  .build());
         }
      }

      return locations.build();
   }
}
