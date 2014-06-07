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
package org.jclouds.abiquo.domain.enterprise;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWithLimitsWrapper;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.builder.LimitsBuilder;
import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.enterprise.options.UserOptions;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.infrastructure.Machine;
import org.jclouds.abiquo.domain.network.ExternalNetwork;
import org.jclouds.abiquo.domain.network.UnmanagedNetwork;
import org.jclouds.abiquo.strategy.enterprise.ListVirtualMachineTemplates;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.ApiContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListsDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.DatacentersLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;
import com.abiquo.server.core.enterprise.RolesDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * Represents a tenant.
 * <p>
 * Each tenant has a set of available locations, and a set of compute,
 * networking and storage resources that can be consumed in the assigned
 * locations.
 */
public class Enterprise extends DomainWithLimitsWrapper<EnterpriseDto> {
   /** The default value for the reservation restricted flag. */
   private static final boolean DEFAULT_RESERVATION_RESTRICTED = false;

   /**
    * Constructor to be used only by the builder.
    */
   protected Enterprise(final ApiContext<AbiquoApi> context, final EnterpriseDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * Delete the enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/EnterpriseResource#EnterpriseResource-DeleteanexistingEnterprise"
    *      > http://community.abiquo.com/display/ABI20/EnterpriseResource#
    *      EnterpriseResource- DeleteanexistingEnterprise</a>
    */
   public void delete() {
      context.getApi().getEnterpriseApi().deleteEnterprise(target);
      target = null;
   }

   /**
    * Create a new enterprise in Abiquo.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/EnterpriseResource#EnterpriseResource-CreatesanewEnterprise"
    *      > http://community.abiquo.com/display/ABI20/EnterpriseResource#
    *      EnterpriseResource- CreatesanewEnterprise</a>
    */
   public void save() {
      target = context.getApi().getEnterpriseApi().createEnterprise(target);
   }

   /**
    * Update enterprise information in the server with the data from this
    * enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/EnterpriseResource#EnterpriseResource-Updateanexistingenterprise"
    *      > http://community.abiquo.com/display/ABI20/EnterpriseResource#
    *      EnterpriseResource- Updateanexistingenterprise</a>
    */
   public void update() {
      target = context.getApi().getEnterpriseApi().updateEnterprise(target);
   }

   // Children access

   /**
    * Retrieve the list of virtual datacenters by this enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/EnterpriseResource#EnterpriseResource-RetrievealistofvitualdatacentersbyanEnterprise"
    *      > http://community.abiquo.com/display/ABI20/EnterpriseResource#
    *      EnterpriseResource-
    *      RetrievealistofvitualdatacentersbyanEnterprise</a>
    * @return List of virtual datacenters in this enterprise.
    */
   public Iterable<VirtualDatacenter> listVirtualDatacenters() {
      VirtualDatacentersDto dto = context.getApi().getEnterpriseApi().listVirtualDatacenters(target);
      return wrap(context, VirtualDatacenter.class, dto.getCollection());
   }

   /**
    * Retrieve the list of template definition lists of the enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/TemplateDefinitionListResource#TemplateDefinitionListResource-Retrievealltemplatedefinitionlists"
    *      > http://community.abiquo.com/display/ABI20/
    *      TemplateDefinitionListResource#
    *      TemplateDefinitionListResource-Retrievealltemplatedefinitionlists</a>
    * @return List of template definition lists of the enterprise.
    */
   public Iterable<TemplateDefinitionList> listTemplateDefinitionLists() {
      TemplateDefinitionListsDto dto = context.getApi().getEnterpriseApi().listTemplateDefinitionLists(target);
      return wrap(context, TemplateDefinitionList.class, dto.getCollection());
   }

   /**
    * Retrieve a single template definition list.
    * 
    * @param id
    *           Unique ID of the template definition list for this enterprise.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/TemplateDefinitionListResource#TemplateDefinitionListResource-Retrieveatemplatedefinitionlist"
    *      > http://community.abiquo.com/display/ABI20/
    *      TemplateDefinitionListResource#
    *      TemplateDefinitionListResource-Retrieveatemplatedefinitionlist</a>
    * @return Template definition with the given id or <code>null</code> if it
    *         does not exist.
    */
   public TemplateDefinitionList getTemplateDefinitionList(final Integer id) {
      TemplateDefinitionListDto templateList = context.getApi().getEnterpriseApi()
            .getTemplateDefinitionList(target, id);
      return wrap(context, TemplateDefinitionList.class, templateList);
   }

   /**
    * Retrieve the list of datacenter limits by enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterLimitsResource#DatacenterLimitsResource-Retrievelimitsbyenterprise"
    *      > http://community.abiquo.com/display/ABI20/DatacenterLimitsResource#
    *      DatacenterLimitsResource-Retrievelimitsbyenterprise</a>
    * @return List of datacenter limits by enterprise.
    */
   public Iterable<Limits> listLimits() {
      DatacentersLimitsDto dto = context.getApi().getEnterpriseApi().listLimits(this.unwrap());
      return wrap(context, Limits.class, dto.getCollection());
   }

   /**
    * Retrieve the defined properties of the given enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/EnterprisePropertiesResource#EnterprisePropertiesResource-Retrievethepropertiesforanenterprise"
    *      > http://community.abiquo.com/display/ABI20/
    *      EnterprisePropertiesResource#
    *      EnterprisePropertiesResource-Retrievethepropertiesforanenterprise</a>
    * @return The defined properties of the given enterprise.
    */
   public EnterpriseProperties getEnterpriseProperties() {
      EnterprisePropertiesDto dto = context.getApi().getEnterpriseApi().getEnterpriseProperties(this.unwrap());
      return wrap(context, EnterpriseProperties.class, dto);
   }

   /**
    * Retrieve the list of users of this enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/UserResource#UserResource-Retrievealistofusers"
    *      >
    *      http://community.abiquo.com/display/ABI20/UserResource#UserResource-
    *      Retrievealistofusers</a>
    * @return List of users of this enterprise.
    */
   public Iterable<User> listUsers() {
      PagedIterable<UserDto> dto = context.getApi().getEnterpriseApi().listUsers(target);
      return wrap(context, User.class, dto.concat());
   }

   /**
    * Retrieve the list of users of this enterprise, allowing pagination
    * 
    * @param options
    *           User options
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/UserResource#UserResource-Retrievealistofusers"
    *      >
    *      http://community.abiquo.com/display/ABI20/UserResource#UserResource-
    *      Retrievealistofusers</a>
    * @return List of users of this enterprise, according to the specified
    *         pagination options
    */
   public Iterable<User> listUsers(final UserOptions options) {
      PaginatedCollection<UserDto, UsersDto> dto = context.getApi().getEnterpriseApi().listUsers(target, options);
      return wrap(context, User.class, dto.toPagedIterable().concat());
   }

   /**
    * Retrieve a single user.
    * 
    * @param id
    *           Unique ID of the user in this enterprise.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/UserResource#UserResource-Retrieveauser"
    *      >
    *      http://community.abiquo.com/display/ABI20/UserResource#UserResource-
    *      Retrieveauser</a>
    * @return User with the given id or <code>null</code> if it does not exist.
    */
   public User getUser(final Integer id) {
      UserDto user = context.getApi().getEnterpriseApi().getUser(target, id);
      return wrap(context, User.class, user);
   }

   /**
    * Retrieve the list of roles defined by this enterprise.
    * 
    * @return List of roles by this enterprise.
    */
   public Iterable<Role> listRoles() {
      RolesDto dto = context.getApi().getAdminApi().listRoles(target);
      return wrap(context, Role.class, dto.getCollection());
   }

   public Iterable<VirtualMachineTemplate> listTemplatesInRepository(final Datacenter datacenter) {
      PagedIterable<VirtualMachineTemplateDto> templates = context.getApi().getVirtualMachineTemplateApi()
            .listVirtualMachineTemplates(target.getId(), datacenter.getId());
      return wrap(context, VirtualMachineTemplate.class, templates.concat());
   }

   public VirtualMachineTemplate getTemplateInRepository(final Datacenter datacenter, final Integer id) {
      VirtualMachineTemplateDto template = context.getApi().getVirtualMachineTemplateApi()
            .getVirtualMachineTemplate(target.getId(), datacenter.getId(), id);
      return wrap(context, VirtualMachineTemplate.class, template);
   }

   public Iterable<VirtualMachineTemplate> listTemplates() {
      ListVirtualMachineTemplates strategy = context.utils().injector().getInstance(ListVirtualMachineTemplates.class);
      return strategy.execute(this);
   }

   public Iterable<Datacenter> listAllowedDatacenters() {
      DatacentersDto datacenters = context.getApi().getEnterpriseApi().listAllowedDatacenters(target.getId());
      return wrap(context, Datacenter.class, datacenters.getCollection());
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Enterprise+Resource#EnterpriseResource-Getthelistofexternalnetworks"
    *      > http://community.abiquo.com/display/ABI20/Enterprise+Resource#
    *      EnterpriseResource- Getthelistofexternalnetworks</a>
    */
   public Iterable<ExternalNetwork> listExternalNetworks(final Datacenter datacenter) {
      DatacenterLimitsDto limitForDatacenter = getLimits(datacenter);

      HttpResponse response = context.getApi().get(limitForDatacenter.searchLink("externalnetworks"));

      ParseXMLWithJAXB<VLANNetworksDto> parser = new ParseXMLWithJAXB<VLANNetworksDto>(context.utils().xml(),
            TypeLiteral.get(VLANNetworksDto.class));

      return wrap(context, ExternalNetwork.class, parser.apply(response).getCollection());
   }

   public Iterable<UnmanagedNetwork> listUnmanagedNetworks(final Datacenter datacenter) {
      DatacenterLimitsDto limitForDatacenter = getLimits(datacenter);

      // The "rel" for the unmanaged networks is the same than the one used for
      // external networks
      HttpResponse response = context.getApi().get(limitForDatacenter.searchLink("externalnetworks"));

      ParseXMLWithJAXB<VLANNetworksDto> parser = new ParseXMLWithJAXB<VLANNetworksDto>(context.utils().xml(),
            TypeLiteral.get(VLANNetworksDto.class));

      return wrap(context, UnmanagedNetwork.class, parser.apply(response).getCollection());
   }

   /**
    * Retrieve the list of virtual appliances by this enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/EnterpriseResource#EnterpriseResource-RetrievethelistofvirtualappliancesbyanEnterprise"
    *      > http://community.abiquo.com/display/ABI20/EnterpriseResource#
    *      EnterpriseResource-
    *      RetrievethelistofvirtualappliancesbyanEnterprise</a>
    * @return List of virtual appliances by this enterprise.
    */
   public Iterable<VirtualAppliance> listVirtualAppliances() {
      VirtualAppliancesDto virtualAppliances = context.getApi().getEnterpriseApi().listVirtualAppliances(target);
      return wrap(context, VirtualAppliance.class, virtualAppliances.getCollection());
   }

   /**
    * Retrieve the list of virtual machines by this enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/EnterpriseResource#EnterpriseResource-RetrievealistofvirtualmachinesbyanEnterprise"
    *      > http://community.abiquo.com/display/ABI20/EnterpriseResource#
    *      EnterpriseResource- RetrievealistofvirtualmachinesbyanEnterprise</a>
    * @return List of virtual machines by this enterprise.
    */
   public Iterable<VirtualMachine> listVirtualMachines() {
      VirtualMachinesWithNodeExtendedDto machines = context.getApi().getEnterpriseApi().listVirtualMachines(target);
      return wrap(context, VirtualMachine.class, machines.getCollection());
   }

   public Iterable<Machine> listReservedMachines() {
      MachinesDto machines = context.getApi().getEnterpriseApi().listReservedMachines(target);
      return wrap(context, Machine.class, machines.getCollection());
   }

   // Actions

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Datacenter+Repository+Resource#DatacenterRepositoryResource-SynchronizetheDatacenterRepositorywiththerepository"
    *      > http://community.abiquo.com/display/ABI20/Datacenter+Repository+
    *      Resource# DatacenterRepositoryResource-
    *      SynchronizetheDatacenterRepositorywiththerepository</a>
    */
   public void refreshTemplateRepository(final Datacenter datacenter) {
      context.getApi().getEnterpriseApi().refreshTemplateRepository(target.getId(), datacenter.getId());
   }

   /**
    * Allows the given datacenter to be used by this enterprise. Creates a
    * {@link Limits} object if not exists.
    * 
    * @param datacenter
    *           The datacenter.
    * @return Default datacenter limits of the enterprise for the given
    *         datacenter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Datacenter+Limits+Resource#DatacenterLimitsResource-CreateanewLimitforanenterpriseinadatacenter"
    *      > http://community.abiquo.com/display/ABI20/Datacenter+Limits+
    *      Resource#
    *      DatacenterLimitsResource-CreateanewLimitforanenterpriseinadatacenter
    *      </a>
    */
   public Limits allowDatacenter(final Datacenter datacenter) {
      DatacenterLimitsDto dto;

      try {
         // Create new limits
         Limits limits = Limits.builder(context, datacenter).build();

         // Save new limits
         dto = context.getApi().getEnterpriseApi().createLimits(target, datacenter.unwrap(), limits.unwrap());
      } catch (AbiquoException ex) {
         // Controlled error to allow duplicated authorizations
         if (ex.hasError("LIMIT-7")) {
            DatacentersLimitsDto limits = context.getApi().getEnterpriseApi().getLimits(target, datacenter.unwrap());
            // Should be only one limit
            dto = limits.getCollection().get(0);
         } else {
            throw ex;
         }
      }

      return wrap(context, Limits.class, dto);
   }

   /**
    * Prohibit the given datacenter to be used by this enterprise. Deletes a
    * {@link Limits} object.
    * 
    * @param datacenter
    *           The datacenter.
    * @return Default datacenter limits of the enterprise for the given
    *         datacenter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Datacenter+Limits+Resource#DatacenterLimitsResource-Deleteanexistinglimitforanenterpriseinadatacenter"
    *      > http://community.abiquo.com/display/ABI20/Datacenter+Limits+
    *      Resource# DatacenterLimitsResource-
    *      Deleteanexistinglimitforanenterpriseinadatacenter</a>
    */
   public void prohibitDatacenter(final Datacenter datacenter) {
      // Get limits
      DatacentersLimitsDto dto = context.getApi().getEnterpriseApi().getLimits(target, datacenter.unwrap());

      // Delete limits (if any)
      if (dto != null && !dto.isEmpty()) {
         // Should be only one limit
         context.getApi().getEnterpriseApi().deleteLimits(dto.getCollection().get(0));
      }
   }

   /**
    * Disables chef in the enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Enterprise+Resource#EnterpriseResource-DisableChefinanexistingEnterprise"
    *      > http://community.abiquo.com/display/ABI20/Enterprise+Resource#
    *      EnterpriseResource- DisableChefinanexistingEnterprise</a>
    */
   public void disableChef() {
      target.setChefClient(null);
      target.setChefClientCertificate(null);
      target.setChefURL(null);
      target.setChefValidator(null);
      target.setChefValidatorCertificate(null);
      update();
   }

   // Builder

   public static Builder builder(final ApiContext<AbiquoApi> context) {
      return new Builder(context);
   }

   public static class Builder extends LimitsBuilder<Builder> {
      private ApiContext<AbiquoApi> context;

      private String name;

      protected Long repositorySoft = Long.valueOf(DEFAULT_LIMITS);

      protected Long repositoryHard = Long.valueOf(DEFAULT_LIMITS);

      private Boolean isReservationRestricted = DEFAULT_RESERVATION_RESTRICTED;

      private String chefURL;

      private String chefClient;

      private String chefValidator;

      private String chefApiCertificate;

      private String chefValidatorCertificate;

      public Builder(final ApiContext<AbiquoApi> context) {
         super();
         this.context = context;
      }

      public Builder isReservationRestricted(final boolean isReservationRestricted) {
         this.isReservationRestricted = isReservationRestricted;
         return this;
      }

      public Builder chefURL(final String chefURL) {
         this.chefURL = chefURL;
         return this;
      }

      public Builder chefClient(final String chefClient) {
         this.chefClient = chefClient;
         return this;
      }

      public Builder chefApiCertificate(final String chefApiCertificate) {
         this.chefApiCertificate = chefApiCertificate;
         return this;
      }

      public Builder chefValidator(final String chefValidator) {
         this.chefValidator = chefValidator;
         return this;
      }

      public Builder chefValidatorCertificate(final String chefValidatorCertificate) {
         this.chefValidatorCertificate = chefValidatorCertificate;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder repositoryLimits(final long soft, final long hard) {
         this.repositorySoft = soft;
         this.repositoryHard = hard;
         return this;
      }

      public Enterprise build() {
         EnterpriseDto dto = new EnterpriseDto();
         dto.setName(name);
         dto.setRamLimitsInMb(ramSoftLimitInMb, ramHardLimitInMb);
         dto.setCpuCountLimits(cpuCountSoftLimit, cpuCountHardLimit);
         dto.setHdLimitsInMb(hdSoftLimitInMb, hdHardLimitInMb);
         dto.setStorageLimits(storageSoft, storageHard);
         dto.setVlansLimits(vlansSoft, vlansHard);
         dto.setPublicIPLimits(publicIpsSoft, publicIpsHard);
         dto.setRepositoryLimits(repositorySoft, repositoryHard);
         dto.setIsReservationRestricted(isReservationRestricted);
         dto.setChefClient(chefClient);
         dto.setChefClientCertificate(chefApiCertificate);
         dto.setChefURL(chefURL);
         dto.setChefValidator(chefValidator);
         dto.setChefValidatorCertificate(chefValidatorCertificate);

         return new Enterprise(context, dto);
      }

      public static Builder fromEnterprise(final Enterprise in) {
         return Enterprise.builder(in.context).name(in.getName())
               .ramLimits(in.getRamSoftLimitInMb(), in.getRamHardLimitInMb())
               .cpuCountLimits(in.getCpuCountSoftLimit(), in.getCpuCountHardLimit())
               .hdLimitsInMb(in.getHdSoftLimitInBytes(), in.getHdHardLimitInBytes())
               .storageLimits(in.getStorageSoft(), in.getStorageHard())
               .vlansLimits(in.getVlansSoft(), in.getVlansHard())
               .publicIpsLimits(in.getPublicIpsSoft(), in.getPublicIpsHard())
               .repositoryLimits(in.getRepositorySoft(), in.getRepositoryHard())
               .isReservationRestricted(in.getIsReservationRestricted()).chefClient(in.getChefClient())
               .chefApiCertificate(in.getChefApiCertificate()).chefURL(in.getChefURL())
               .chefValidator(in.getChefValidator()).chefValidatorCertificate(in.getChefValidatorCertificate());
      }
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public boolean getIsReservationRestricted() {
      return target.getIsReservationRestricted();
   }

   public String getName() {
      return target.getName();
   }

   public long getRepositoryHard() {
      return target.getRepositoryHard();
   }

   public long getRepositorySoft() {
      return target.getRepositorySoft();
   }

   public void setIsReservationRestricted(final boolean isReservationRestricted) {
      target.setIsReservationRestricted(isReservationRestricted);
   }

   public void setName(final String name) {
      target.setName(name);
   }

   public void setRepositoryHard(final long repositoryHard) {
      target.setRepositoryHard(repositoryHard);
   }

   public void setRepositoryLimits(final long soft, final long hard) {
      target.setRepositoryLimits(soft, hard);
   }

   public void setRepositorySoft(final long repositorySoft) {
      target.setRepositorySoft(repositorySoft);
   }

   public String getChefClient() {
      return target.getChefClient();
   }

   public String getChefApiCertificate() {
      return target.getChefClientCertificate();
   }

   public String getChefURL() {
      return target.getChefURL();
   }

   public String getChefValidator() {
      return target.getChefValidator();
   }

   public String getChefValidatorCertificate() {
      return target.getChefValidatorCertificate();
   }

   public void setChefClient(final String chefClient) {
      target.setChefClient(chefClient);
   }

   public void setChefClientCertificate(final String chefClientCertificate) {
      target.setChefClientCertificate(chefClientCertificate);
   }

   public void setChefURL(final String chefURL) {
      target.setChefURL(chefURL);
   }

   public void setChefValidator(final String chefValidator) {
      target.setChefValidator(chefValidator);
   }

   public void setChefValidatorCertificate(final String chefValidatorCertificate) {
      target.setChefValidatorCertificate(chefValidatorCertificate);
   }

   @Override
   public String toString() {
      return "Enterprise [id=" + getId() + ", isReservationRestricted=" + getIsReservationRestricted() + ", name="
            + getName() + "]";
   }

   private DatacenterLimitsDto getLimits(final Datacenter datacenter) {
      DatacentersLimitsDto limits = context.getApi().getEnterpriseApi().listLimits(target);

      return Iterables.find(limits.getCollection(), new Predicate<DatacenterLimitsDto>() {
         @Override
         public boolean apply(final DatacenterLimitsDto input) {
            RESTLink datacenterLink = input.searchLink("datacenter");
            return datacenterLink != null
                  && datacenterLink.getHref().equals(datacenter.unwrap().getEditLink().getHref());
         }
      });
   }
}
