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
package org.apache.jclouds.oneandone.rest.compute.function;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.find;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jclouds.oneandone.rest.OneAndOneApi;
import org.apache.jclouds.oneandone.rest.domain.DataCenter;
import org.apache.jclouds.oneandone.rest.domain.Hdd;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.ServerIp;
import org.apache.jclouds.oneandone.rest.domain.Types.ServerState;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.util.AutomaticHardwareIdSpec;
import org.jclouds.domain.Location;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;
import org.jclouds.util.InetAddresses2;

public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

   private final Supplier<Set<? extends Location>> locations;
   private final Supplier<Map<String, ? extends Hardware>> hardwareFlavors;
   private final Supplier<Map<String, ? extends Image>> images;
   private final Function<Hdd, Volume> fnVolume;
   private final OneAndOneApi api;

   private final GroupNamingConvention groupNamingConvention;

   @Inject
   public ServerToNodeMetadata(Function<Hdd, Volume> fnVolume,
           @Memoized Supplier<Set<? extends Location>> locations,
           Supplier<Map<String, ? extends Hardware>> hardwareFlavors,
           Supplier<Map<String, ? extends Image>> images,
           OneAndOneApi api,
           GroupNamingConvention.Factory groupNamingConvention) {
      this.hardwareFlavors = hardwareFlavors;
      this.images = images;
      this.locations = locations;
      this.api = api;
      this.fnVolume = fnVolume;
      this.groupNamingConvention = groupNamingConvention.createWithoutPrefix();
   }

   @Override
   public NodeMetadata apply(final Server server) {
      checkNotNull(server, "Null server");

      DataCenter dataCenter = api.dataCenterApi().get(server.datacenter().id());
      Location location = find(locations.get(), idEquals(dataCenter.id()));
      Hardware hardware = null;
      //check if the server was built on a hardware flavour(Fixed instance)
      if (server.hardware().fixedInstanceSizeId() != null && !"0".equals(server.hardware().fixedInstanceSizeId())) {
         hardware = hardwareFlavors.get().get(server.hardware().fixedInstanceSizeId());

      } else {
         List<Volume> volumes = Lists.newArrayList();
         //customer hardware
         double size = 0d;
         double minRam = server.hardware().ram();
         List<Hdd> hdds = server.hardware().hdds();

         if (server.hardware().hdds().isEmpty()) {
            hdds = api.serverApi().getHardware(server.id()).hdds();
         }

         size = getHddSize(hdds);
         volumes = convertHddToVolume(hdds);

         if (minRam < 1) {
            minRam = 512;
         } else {
            minRam = minRam * 1024;
         }

         List<Processor> processors = new ArrayList<Processor>();
         for (int i = 0; i < server.hardware().vcore(); i++) {
            Processor proc = new Processor(server.hardware().coresPerProcessor(), 1d);
            processors.add(proc);
         }
         AutomaticHardwareIdSpec id = AutomaticHardwareIdSpec.automaticHardwareIdSpecBuilder(server.hardware().vcore(), (int) minRam, Optional.of((float) size));
         hardware = new HardwareBuilder()
                 .ids(id.toString())
                 .ram((int) minRam)
                 .processors(ImmutableList.copyOf(processors))
                 .hypervisor("kvm")
                 .volumes(volumes)
                 .location(location)
                 .build();

      }

      // Collect ips
      List<String> addresses = Lists.transform(server.ips(), new Function<ServerIp, String>() {
         @Override
         public String apply(ServerIp in) {
            return in.ip();
         }
      });

      Image image = images.get().get(server.image().id());
      // Build node
      NodeMetadataBuilder nodeBuilder = new NodeMetadataBuilder();
      nodeBuilder.ids(server.id())
              .group(groupNamingConvention.extractGroup(server.name()))
              .name(server.name())
              .backendStatus(server.status().state().toString())
              .status(mapStatus(server.status().state()))
              .hardware(hardware)
              .operatingSystem(image.getOperatingSystem())
              .location(location)
              .privateAddresses(Iterables.filter(addresses, InetAddresses2.IsPrivateIPAddress.INSTANCE))
              .publicAddresses(Iterables.filter(addresses, not(InetAddresses2.IsPrivateIPAddress.INSTANCE)));

      return nodeBuilder.build();
   }

   private double getHddSize(List<Hdd> hdds) {
      double size = 0d;
      for (Hdd storage : hdds) {
         size += storage.size();
      }
      return size;
   }

   private List<Volume> convertHddToVolume(List<Hdd> hdds) {
      List<Volume> volumes = Lists.newArrayList();
      for (Hdd storage : hdds) {
         volumes.add(fnVolume.apply(storage));
      }
      return volumes;
   }

   static NodeMetadata.Status mapStatus(ServerState status) {
      if (status == null) {
         return NodeMetadata.Status.UNRECOGNIZED;
      }
      switch (status) {
         case CONFIGURING:
         case DEPLOYING:
         case POWERING_OFF:
         case POWERING_ON:
         case REBOOTING:
         case REMOVING:
            return NodeMetadata.Status.PENDING;
         case POWERED_OFF:
            return NodeMetadata.Status.SUSPENDED;
         case POWERED_ON:
            return NodeMetadata.Status.RUNNING;
         default:
            return NodeMetadata.Status.UNRECOGNIZED;
      }
   }

}
