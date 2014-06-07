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

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.ApiContext;

import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

/**
 * Adds high level functionality to private {@link VLANNetworkDto}.
 * 
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/Private+Network+Resource"
 *      > http://community.abiquo.com/display/ABI20/Private+Network+Resource</a>
 */
public class PrivateNetwork extends Network<PrivateIp> {
   /** The virtual datacenter where the network belongs. */
   private VirtualDatacenter virtualDatacenter;

   /**
    * Constructor to be used only by the builder.
    */
   protected PrivateNetwork(final ApiContext<AbiquoApi> context, final VLANNetworkDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Private+Network+Resource#PrivateNetworkResource-DeleteaPrivateNetwork"
    *      > http://community.abiquo.com/display/ABI20/Private+Network+Resource#
    *      PrivateNetworkResource -DeleteaPrivateNetwork</a>
    */
   @Override
   public void delete() {
      context.getApi().getCloudApi().deletePrivateNetwork(target);
      target = null;
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Private+Network+Resource#PrivateNetworkResource-CreateaPrivateNetwork"
    *      > http://community.abiquo.com/display/ABI20/Private+Network+Resource#
    *      PrivateNetworkResource -CreateaPrivateNetwork</a>
    */
   @Override
   public void save() {
      target = context.getApi().getCloudApi().createPrivateNetwork(virtualDatacenter.unwrap(), target);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Private+Network+Resource#PrivateNetworkResource-UpdateaPrivateNetwork"
    *      > http://community.abiquo.com/display/ABI20/Private+Network+Resource#
    *      PrivateNetworkResource -UpdateaPrivateNetwork</a>
    */
   @Override
   public void update() {
      target = context.getApi().getCloudApi().updatePrivateNetwork(target);
   }

   @Override
   public Iterable<PrivateIp> listIps() {
      PagedIterable<PrivateIpDto> ips = context.getApi().getCloudApi().listPrivateNetworkIps(target);
      return wrap(context, PrivateIp.class, ips.concat());
   }

   @Override
   public Iterable<PrivateIp> listIps(final IpOptions options) {
      PaginatedCollection<PrivateIpDto, PrivateIpsDto> ips = context.getApi().getCloudApi()
            .listPrivateNetworkIps(target, options);
      return wrap(context, PrivateIp.class, ips.toPagedIterable().concat());
   }

   // Override to apply the filter in the server side
   @Override
   public Iterable<PrivateIp> listUnusedIps() {
      IpOptions options = IpOptions.builder().free(true).build();
      PaginatedCollection<PrivateIpDto, PrivateIpsDto> ips = context.getApi().getCloudApi()
            .listPrivateNetworkIps(target, options);
      return wrap(context, PrivateIp.class, ips.toPagedIterable().concat());
   }

   @Override
   public PrivateIp getIp(final Integer id) {
      PrivateIpDto ip = context.getApi().getCloudApi().getPrivateNetworkIp(target, id);
      return wrap(context, PrivateIp.class, ip);
   }

   // Builder

   public static Builder builder(final ApiContext<AbiquoApi> context) {
      return new Builder(context);
   }

   public static class Builder extends NetworkBuilder<Builder> {
      private VirtualDatacenter virtualDatacenter;

      public Builder(final ApiContext<AbiquoApi> context) {
         super(context);
         this.context = context;
      }

      public Builder virtualDatacenter(final VirtualDatacenter virtualDatacenter) {
         this.virtualDatacenter = virtualDatacenter;
         return this;
      }

      public PrivateNetwork build() {
         VLANNetworkDto dto = new VLANNetworkDto();
         dto.setName(name);
         dto.setTag(tag);
         dto.setGateway(gateway);
         dto.setAddress(address);
         dto.setMask(mask);
         dto.setPrimaryDNS(primaryDNS);
         dto.setSecondaryDNS(secondaryDNS);
         dto.setSufixDNS(suffixDNS);
         dto.setDefaultNetwork(defaultNetwork);
         dto.setUnmanaged(false);
         dto.setType(NetworkType.INTERNAL);

         PrivateNetwork network = new PrivateNetwork(context, dto);
         network.virtualDatacenter = virtualDatacenter;

         return network;
      }

      public static Builder fromPrivateNetwork(final PrivateNetwork in) {
         return PrivateNetwork.builder(in.context).name(in.getName()).tag(in.getTag()).gateway(in.getGateway())
               .address(in.getAddress()).mask(in.getMask()).primaryDNS(in.getPrimaryDNS())
               .secondaryDNS(in.getSecondaryDNS()).suffixDNS(in.getSuffixDNS()).defaultNetwork(in.getDefaultNetwork())
               .virtualDatacenter(in.virtualDatacenter);
      }
   }

   @Override
   public String toString() {
      return "Private " + super.toString();
   }

}
