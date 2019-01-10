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
package org.jclouds.dimensiondata.cloudcontrol.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.dimensiondata.cloudcontrol.domain.NIC;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.domain.internal.ServerWithExternalIp;
import org.jclouds.domain.Location;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;

@Singleton
class ServerWithNatRuleToNodeMetadata implements Function<ServerWithExternalIp, NodeMetadata> {

   private static final Map<State, NodeMetadata.Status> serverStateToNodeStatus = ImmutableMap.<State, NodeMetadata.Status>builder()
         .put(State.PENDING_DELETE, NodeMetadata.Status.PENDING).put(State.PENDING_CHANGE, NodeMetadata.Status.PENDING)
         .put(State.PENDING_ADD, NodeMetadata.Status.PENDING).put(State.FAILED_ADD, NodeMetadata.Status.ERROR)
         .put(State.FAILED_CHANGE, NodeMetadata.Status.ERROR).put(State.FAILED_DELETE, NodeMetadata.Status.ERROR)
         .put(State.DELETED, NodeMetadata.Status.TERMINATED).put(State.NORMAL, NodeMetadata.Status.RUNNING)
         .put(State.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED).build();

   private final Supplier<Set<? extends Location>> locations;
   private final GroupNamingConvention nodeNamingConvention;
   private final ServerToHardware serverToHardware;
   private final OperatingSystemToOperatingSystem operatingSystemToOperatingSystem;

   @Inject
   ServerWithNatRuleToNodeMetadata(@Memoized final Supplier<Set<? extends Location>> locations,
         final GroupNamingConvention.Factory namingConvention, final ServerToHardware serverToHardware,
         final OperatingSystemToOperatingSystem operatingSystemToOperatingSystem) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.locations = checkNotNull(locations, "locations");
      this.serverToHardware = checkNotNull(serverToHardware, "serverToHardware");
      this.operatingSystemToOperatingSystem = checkNotNull(operatingSystemToOperatingSystem,
            "operatingSystemToOperatingSystem");
   }

   @Override
   public NodeMetadata apply(final ServerWithExternalIp serverWithExternalIp) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      Server server = serverWithExternalIp.server();
      builder.ids(server.id());
      builder.name(server.name());
      builder.location(find(locations.get(), idEquals(nullToEmpty(server.datacenterId()))));
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(server.name()));
      builder.hardware(serverToHardware.apply(serverWithExternalIp.server()));
      builder.imageId(server.sourceImageId());
      builder.operatingSystem(operatingSystemToOperatingSystem.apply(server.guest().operatingSystem()));
      builder.status(server.started() ?
            serverStateToNodeStatus.get(server.state()) :
            NodeMetadata.Status.SUSPENDED);

      Set<String> privateAddresses = new HashSet<String>();
      if (server.networkInfo() != null) {
         if (server.networkInfo().primaryNic() != null && server.networkInfo().primaryNic().privateIpv4() != null) {
            privateAddresses.add(server.networkInfo().primaryNic().privateIpv4());
         }
         if (!server.networkInfo().additionalNic().isEmpty()) {
            privateAddresses.addAll(Sets.newHashSet(
                  Iterables.transform(server.networkInfo().additionalNic(), new Function<NIC, String>() {
                     @Override
                     public String apply(NIC nic) {
                        return nic.privateIpv4();
                     }
                  })));
         }
      }
      builder.privateAddresses(privateAddresses);
      if (serverWithExternalIp.externalIp() != null) {
         builder.publicAddresses(ImmutableSet.of(serverWithExternalIp.externalIp()));
      }
      return builder.build();
   }
}
