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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWithLimitsWrapper;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.builder.LimitsBuilder;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineTemplateOptions;
import org.jclouds.abiquo.domain.cloud.options.VolumeOptions;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.infrastructure.Tier;
import org.jclouds.abiquo.domain.network.ExternalNetwork;
import org.jclouds.abiquo.domain.network.Network;
import org.jclouds.abiquo.domain.network.PrivateNetwork;
import org.jclouds.abiquo.domain.network.PublicIp;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.ApiContext;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.enumerator.StatefulInclusion;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;
import com.abiquo.server.core.infrastructure.storage.VolumesManagementDto;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * Represents a virtual datacenter.
 * <p>
 * Virtual datacenters expose a set of compute, storage and networking resources
 * that can be consumed by the tenants.
 * 
 * @see API: <a href=
 *      "http://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource">
 *      http
 *      ://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource</a>
 */
public class VirtualDatacenter extends DomainWithLimitsWrapper<VirtualDatacenterDto> {
   /** The enterprise where the rack belongs. */
   private Enterprise enterprise;

   /** The datacenter where the virtual datacenter will be deployed. */
   private Datacenter datacenter;

   /**
    * Constructor to be used only by the builder.
    */
   protected VirtualDatacenter(final ApiContext<AbiquoApi> context, final VirtualDatacenterDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * Delete the virtual datacenter.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource#VirtualDatacenterResource-DeleteanexistingVirtualDatacenter"
    *      > http://community.abiquo.com/display/ABI20/Virtual+Datacenter+
    *      Resource#
    *      VirtualDatacenterResource-DeleteanexistingVirtualDatacenter</a>
    */
   public void delete() {
      context.getApi().getCloudApi().deleteVirtualDatacenter(target);
      target = null;
   }

   /**
    * Creates the virtual datacenter.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource#VirtualDatacenterResource-CreateanewVirtualDatacenter"
    *      > http://community.abiquo.com/display/ABI20/Virtual+Datacenter+
    *      Resource# VirtualDatacenterResource-CreateanewVirtualDatacenter</a>
    */
   public void save() {
      target = context.getApi().getCloudApi().createVirtualDatacenter(target, datacenter.unwrap(), enterprise.unwrap());
   }

   /**
    * Updates the virtual datacenter information when some of its properties
    * have changed.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource#VirtualDatacenterResource-UpdatesanexistingVirtualDatacenter"
    *      > http://community.abiquo.com/display/ABI20/Virtual+Datacenter+
    *      Resource#
    *      VirtualDatacenterResource-UpdatesanexistingVirtualDatacenter</a>
    */
   public void update() {
      target = context.getApi().getCloudApi().updateVirtualDatacenter(target);
   }

   // Parent access

   /**
    * Gets the datacenter where this virtual datacenter is assigned.
    * 
    * @return The datacenter where this virtual datacenter is assigned.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Datacenter+Resource#DatacenterResource-RetrieveaDatacenter"
    *      > http://community.abiquo.com/display/ABI20/Datacenter+Resource#
    *      DatacenterResource- RetrieveaDatacenter</a>
    */
   public Datacenter getDatacenter() {
      final Integer datacenterId = target.getIdFromLink(ParentLinkName.DATACENTER);
      datacenter = find(getEnterprise().listAllowedDatacenters(), new Predicate<Datacenter>() {
         @Override
         public boolean apply(Datacenter input) {
            return input.getId().equals(datacenterId);
         }
      });
      return datacenter;
   }

   /**
    * Gets the enterprise that owns this virtual datacenter.
    * 
    * @return The enterprise that owns this virtual datacenter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Enterprise+Resource#EnterpriseResource-RetrieveanEnterprise"
    *      > http://community.abiquo.com/display/ABI20/Enterprise+Resource#
    *      EnterpriseResource- RetrieveanEnterprise</a>
    */
   public Enterprise getEnterprise() {
      Integer enterpriseId = target.getIdFromLink(ParentLinkName.ENTERPRISE);
      enterprise = wrap(context, Enterprise.class, context.getApi().getEnterpriseApi().getEnterprise(enterpriseId));
      return enterprise;
   }

   // Children access

   /**
    * Lists all the virtual appliances in the virtual datacenter.
    * 
    * @return The list of virtual appliances in the virtual datacenter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Appliance+Resource#VirtualApplianceResource-RetrievethelistofVirtualAppliances"
    *      >http://community.abiquo.com/display/ABI20/Virtual+Appliance+Resource
    *      # VirtualApplianceResource-RetrievethelistofVirtualAppliances</a>
    */
   public Iterable<VirtualAppliance> listVirtualAppliances() {
      VirtualAppliancesDto vapps = context.getApi().getCloudApi().listVirtualAppliances(target);
      return wrap(context, VirtualAppliance.class, vapps.getCollection());
   }

   /**
    * Gets the virtual appliance with the given id in the current virtual
    * datacenter.
    * 
    * @param id
    *           The id of the virtual appliance to get.
    * @return The virtual appliance.
    */
   public VirtualAppliance getVirtualAppliance(final Integer id) {
      VirtualApplianceDto vapp = context.getApi().getCloudApi().getVirtualAppliance(target, id);
      return wrap(context, VirtualAppliance.class, vapp);
   }

   /**
    * Lists the storage tiers that are available to the virtual datacenter.
    * 
    * @return The list of storage tiers that are available to the virtual
    *         datacenter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource#VirtualDatacenterResource-Retrieveenabledtiers"
    *      > http://community.abiquo.com/display/ABI20/Virtual+Datacenter+
    *      Resource# VirtualDatacenterResource-Retrieveenabledtiers</a>
    */
   public Iterable<Tier> listStorageTiers() {
      TiersDto tiers = context.getApi().getCloudApi().listStorageTiers(target);
      return wrap(context, Tier.class, tiers.getCollection());
   }

   /**
    * Gets the storage tier with the given id from the current virtual
    * datacenter.
    * 
    * @param id
    *           The id of the storage tier.
    * @return The storage tier.
    */
   public Tier getStorageTier(final Integer id) {
      TierDto tier = context.getApi().getCloudApi().getStorageTier(target, id);
      return wrap(context, Tier.class, tier);
   }

   /**
    * Lists all persistent volumes in the virtual datacenter.
    * 
    * @return The list of all persistent volumes in the virtual datacenter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Volume+Resource#VolumeResource-Retrievethelistofvolumes"
    *      > http://community.abiquo.com/display/ABI20/Volume+Resource#
    *      VolumeResource- Retrievethelistofvolumes</a>
    */
   public Iterable<Volume> listVolumes() {
      PagedIterable<VolumeManagementDto> volumes = context.getApi().getCloudApi().listVolumes(target);
      return wrap(context, Volume.class, volumes.concat());
   }

   public Iterable<Volume> listVolumes(VolumeOptions options) {
      PaginatedCollection<VolumeManagementDto, VolumesManagementDto> volumes = context.getApi().getCloudApi()
            .listVolumes(target, options);
      return wrap(context, Volume.class, volumes.toPagedIterable().concat());
   }

   public Volume getVolume(final Integer id) {
      VolumeManagementDto volume = context.getApi().getCloudApi().getVolume(target, id);
      return wrap(context, Volume.class, volume);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Hard+Disks+Resource#HardDisksResource-GetthelistofHardDisksofaVirtualDatacenter"
    *      > http://community.abiquo.com/display/ABI20/Hard+Disks+Resource#
    *      HardDisksResource- GetthelistofHardDisksofaVirtualDatacenter</a>
    */
   public Iterable<HardDisk> listHardDisks() {
      DisksManagementDto hardDisks = context.getApi().getCloudApi().listHardDisks(target);
      return wrap(context, HardDisk.class, hardDisks.getCollection());
   }

   public HardDisk getHardDisk(final Integer id) {
      DiskManagementDto hardDisk = context.getApi().getCloudApi().getHardDisk(target, id);
      return wrap(context, HardDisk.class, hardDisk);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource#VirtualDatacenterResource-GetdefaultVLANusedbydefaultinVirtualDatacenter"
    *      > http://community.abiquo.com/display/ABI20/Virtual+Datacenter+
    *      Resource#
    *      VirtualDatacenterResource-GetdefaultVLANusedbydefaultinVirtualDatacenter
    *      </a>
    */
   public Network<?> getDefaultNetwork() {
      VLANNetworkDto network = context.getApi().getCloudApi().getDefaultNetwork(target);
      return wrap(context, network.getType() == NetworkType.INTERNAL ? PrivateNetwork.class : ExternalNetwork.class,
            network);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Private+Network+Resource#PrivateNetworkResource-RetrievealistofPrivateNetworks"
    *      > http://community.abiquo.com/display/ABI20/Private+Network+Resource#
    *      PrivateNetworkResource -RetrievealistofPrivateNetworks</a>
    */
   public Iterable<PrivateNetwork> listPrivateNetworks() {
      VLANNetworksDto networks = context.getApi().getCloudApi().listPrivateNetworks(target);
      return wrap(context, PrivateNetwork.class, networks.getCollection());
   }

   public PrivateNetwork getPrivateNetwork(final Integer id) {
      VLANNetworkDto network = context.getApi().getCloudApi().getPrivateNetwork(target, id);
      return wrap(context, PrivateNetwork.class, network);
   }

   public Iterable<VirtualMachineTemplate> listAvailableTemplates() {
      PagedIterable<VirtualMachineTemplateDto> templates = context.getApi().getCloudApi()
            .listAvailableTemplates(target);
      return wrap(context, VirtualMachineTemplate.class, templates.concat());
   }

   public Iterable<VirtualMachineTemplate> listAvailableTemplates(final VirtualMachineTemplateOptions options) {
      PaginatedCollection<VirtualMachineTemplateDto, VirtualMachineTemplatesDto> templates = context.getApi()
            .getCloudApi().listAvailableTemplates(target, options);
      return wrap(context, VirtualMachineTemplate.class, templates.toPagedIterable().concat());
   }

   public VirtualMachineTemplate getAvailableTemplate(final Integer id) {
      PaginatedCollection<VirtualMachineTemplateDto, VirtualMachineTemplatesDto> templates = context.getApi()
            .getCloudApi()
            .listAvailableTemplates(target, VirtualMachineTemplateOptions.builder().idTemplate(id).build());

      FluentIterable<VirtualMachineTemplateDto> all = templates.toPagedIterable().concat();
      return wrap(context, VirtualMachineTemplate.class, all.first().orNull());
   }

   public VirtualMachineTemplate getAvailablePersistentTemplate(final Integer id) {
      PaginatedCollection<VirtualMachineTemplateDto, VirtualMachineTemplatesDto> templates = context
            .getApi()
            .getCloudApi()
            .listAvailableTemplates(target,
                  VirtualMachineTemplateOptions.builder().idTemplate(id).persistent(StatefulInclusion.ALL).build());

      FluentIterable<VirtualMachineTemplateDto> all = templates.toPagedIterable().concat();
      return wrap(context, VirtualMachineTemplate.class, all.first().orNull());
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource#VirtualDatacenterResource-ListofPublicIPstopurchasebyVirtualDatacenter"
    *      > http://community.abiquo.com/display/ABI20/Virtual+Datacenter+
    *      Resource#
    *      VirtualDatacenterResource-ListofPublicIPstopurchasebyVirtualDatacenter
    *      </a>
    */
   public Iterable<PublicIp> listAvailablePublicIps() {
      PagedIterable<PublicIpDto> ips = context.getApi().getCloudApi().listAvailablePublicIps(target);
      return wrap(context, PublicIp.class, ips.concat());
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource#VirtualDatacenterResource-ListofpurchasedPublicIPsbyVirtualDatacenter"
    *      > http://community.abiquo.com/display/ABI20/Virtual+Datacenter+
    *      Resource#
    *      VirtualDatacenterResource-ListofpurchasedPublicIPsbyVirtualDatacenter
    *      </a>
    */
   public Iterable<PublicIp> listPurchasedPublicIps() {
      PagedIterable<PublicIpDto> ips = context.getApi().getCloudApi().listPurchasedPublicIps(target);
      return wrap(context, PublicIp.class, ips.concat());
   }

   public void purchasePublicIp(final PublicIp ip) {
      checkNotNull(ip.unwrap().searchLink("purchase"), ValidationErrors.MISSING_REQUIRED_LINK);
      context.getApi().getCloudApi().purchasePublicIp(ip.unwrap());
   }

   public void releasePublicIp(final PublicIp ip) {
      checkNotNull(ip.unwrap().searchLink("release"), ValidationErrors.MISSING_REQUIRED_LINK);
      context.getApi().getCloudApi().releasePublicIp(ip.unwrap());
   }

   // Actions

   public void setDefaultNetwork(final Network<?> network) {
      context.getApi().getCloudApi().setDefaultNetwork(target, network.unwrap());
   }

   // Builder

   public static Builder builder(final ApiContext<AbiquoApi> context, final Datacenter datacenter,
         final Enterprise enterprise) {
      return new Builder(context, datacenter, enterprise);
   }

   public static class Builder extends LimitsBuilder<Builder> {
      private ApiContext<AbiquoApi> context;

      private String name;

      private HypervisorType hypervisorType;

      private Enterprise enterprise;

      private Datacenter datacenter;

      private PrivateNetwork network;

      public Builder(final ApiContext<AbiquoApi> context, final Datacenter datacenter, final Enterprise enterprise) {
         super();
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.datacenter = datacenter;
         checkNotNull(enterprise, ValidationErrors.NULL_RESOURCE + Enterprise.class);
         this.enterprise = enterprise;
         this.context = context;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder hypervisorType(final HypervisorType hypervisorType) {
         this.hypervisorType = hypervisorType;
         return this;
      }

      public Builder datacenter(final Datacenter datacenter) {
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.datacenter = datacenter;
         return this;
      }

      public Builder enterprise(final Enterprise enterprise) {
         checkNotNull(enterprise, ValidationErrors.NULL_RESOURCE + Enterprise.class);
         this.enterprise = enterprise;
         return this;
      }

      public Builder network(final PrivateNetwork network) {
         checkNotNull(network, ValidationErrors.NULL_RESOURCE + PrivateNetwork.class);
         this.network = network;
         return this;
      }

      public VirtualDatacenter build() {
         VirtualDatacenterDto dto = new VirtualDatacenterDto();
         dto.setName(name);
         dto.setRamLimitsInMb(ramSoftLimitInMb, ramHardLimitInMb);
         dto.setCpuCountLimits(cpuCountSoftLimit, cpuCountHardLimit);
         dto.setHdLimitsInMb(hdSoftLimitInMb, hdHardLimitInMb);
         dto.setStorageLimits(storageSoft, storageHard);
         dto.setVlansLimits(vlansSoft, vlansHard);
         dto.setPublicIPLimits(publicIpsSoft, publicIpsHard);
         dto.setName(name);
         dto.setHypervisorType(hypervisorType);
         dto.setVlan(network.unwrap());

         VirtualDatacenter virtualDatacenter = new VirtualDatacenter(context, dto);
         virtualDatacenter.datacenter = datacenter;
         virtualDatacenter.enterprise = enterprise;

         return virtualDatacenter;
      }

      public static Builder fromVirtualDatacenter(final VirtualDatacenter in) {
         return VirtualDatacenter.builder(in.context, in.datacenter, in.enterprise).name(in.getName())
               .ramLimits(in.getRamSoftLimitInMb(), in.getRamHardLimitInMb())
               .cpuCountLimits(in.getCpuCountSoftLimit(), in.getCpuCountHardLimit())
               .hdLimitsInMb(in.getHdSoftLimitInBytes(), in.getHdHardLimitInBytes())
               .storageLimits(in.getStorageSoft(), in.getStorageHard())
               .vlansLimits(in.getVlansSoft(), in.getVlansHard())
               .publicIpsLimits(in.getPublicIpsSoft(), in.getPublicIpsHard()).hypervisorType(in.getHypervisorType());
      }
   }

   // Delegate methods

   public HypervisorType getHypervisorType() {
      return target.getHypervisorType();
   }

   public Integer getId() {
      return target.getId();
   }

   public String getName() {
      return target.getName();
   }

   public void setHypervisorType(final HypervisorType hypervisorType) {
      target.setHypervisorType(hypervisorType);
   }

   public void setName(final String name) {
      target.setName(name);
   }

   @Override
   public String toString() {
      return "VirtualDatacenter [id=" + getId() + ", type=" + getHypervisorType() + ", name=" + getName() + "]";
   }

}
