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
package org.jclouds.abiquo.domain.infrastructure;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.infrastructure.options.MachineOptions;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.ApiContext;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.MachineIpmiState;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.DatastoresDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachineIpmiStateDto;
import com.abiquo.server.core.infrastructure.MachineStateDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.network.NetworkInterfacesDto;
import com.google.inject.TypeLiteral;

/**
 * Adds high level functionality to {@link MachineDto}. This resource allows you
 * to manage physical machines in the cloud infrastructure.
 * 
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/MachineResource">
 *      http://community.abiquo.com/display/ABI20/MachineResource</a>
 */
public class Machine extends DomainWrapper<MachineDto> {
   /** The default virtual ram used in MB. */
   protected static final int DEFAULT_VRAM_USED = 1;

   /** The default virtual cpu used in MB. */
   protected static final int DEFAULT_VCPU_USED = 1;

   /** The rack where the machine belongs. */
   protected Rack rack;

   /**
    * Constructor to be used only by the builder.
    */
   protected Machine(final ApiContext<AbiquoApi> context, final MachineDto target) {
      super(context, target);
   }

   /**
    * Create a new physical machine in Abiquo. The best way to create a machine
    * if first calling {@link Datacenter#discoverSingleMachine} or
    * {@link Datacenter#discoverMultipleMachines}. This will return a new
    * {@link Machine}. The following steps are: enabling a datastore, selecting
    * a virtual switch and choosing a rack. Refer link for more information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveremotemachineinformation</a>
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/MachineResource#MachineResource-Createamachine"
    *      > http://community.abiquo.com/display/ABI20/MachineResource#
    *      MachineResource- Createamachine</a>
    */
   public void save() {
      target = context.getApi().getInfrastructureApi().createMachine(rack.unwrap(), target);
   }

   public void delete() {
      context.getApi().getInfrastructureApi().deleteMachine(target);
      target = null;
   }

   public void update() {
      target = context.getApi().getInfrastructureApi().updateMachine(target);
   }

   public MachineState check() {
      MachineStateDto dto = context.getApi().getInfrastructureApi().checkMachineState(target, true);
      MachineState state = dto.getState();
      target.setState(state);
      return state;
   }

   public MachineIpmiState checkIpmi() {
      MachineIpmiStateDto dto = context.getApi().getInfrastructureApi().checkMachineIpmiState(target);
      return dto.getState();
   }

   // Parent access
   /**
    * Retrieve the unmanaged rack where the machine is.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-RetrieveaRack"
    *      >
    *      http://community.abiquo.com/display/ABI20/RackResource#RackResource-
    *      RetrieveaRack</a>
    */
   public Rack getRack() {
      RESTLink link = checkNotNull(target.searchLink(ParentLinkName.RACK), ValidationErrors.MISSING_REQUIRED_LINK + " "
            + ParentLinkName.RACK);

      HttpResponse response = context.getApi().get(link);

      ParseXMLWithJAXB<RackDto> parser = new ParseXMLWithJAXB<RackDto>(context.utils().xml(),
            TypeLiteral.get(RackDto.class));

      return wrap(context, Rack.class, parser.apply(response));
   }

   // Children access

   public Iterable<Datastore> getDatastores() {
      return wrap(context, Datastore.class, target.getDatastores().getCollection());
   }

   public Iterable<NetworkInterface> getNetworkInterfaces() {
      return wrap(context, NetworkInterface.class, target.getNetworkInterfaces().getCollection());
   }

   /**
    * Gets the list of virtual machines in the physical machine.
    * 
    * @return The list of virtual machines in the physical machine.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Machine+Resource#MachineResource-Retrievethelistofvirtualmachinesbymachine'shypervisor"
    *      > http://community.abiquo.com/display/ABI20/Machine+Resource#
    *      MachineResource-
    *      Retrievethelistofvirtualmachinesbymachine'shypervisor</a>
    */
   public Iterable<VirtualMachine> listVirtualMachines() {
      MachineOptions options = MachineOptions.builder().sync(false).build();
      VirtualMachinesWithNodeExtendedDto vms = context.getApi().getInfrastructureApi()
            .listVirtualMachinesByMachine(target, options);
      return wrap(context, VirtualMachine.class, vms.getCollection());
   }

   public VirtualMachine getVirtualMachine(final Integer virtualMachineId) {
      VirtualMachineWithNodeExtendedDto vm = context.getApi().getInfrastructureApi()
            .getVirtualMachine(target, virtualMachineId);
      return wrap(context, VirtualMachine.class, vm);
   }

   /**
    * Gets the list of virtual machines in the physical machine synchronizing
    * virtual machines from remote hypervisor with abiquo's database.
    * 
    * @return The list of virtual machines in the physical machine.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Machine+Resource#MachineResource-Retrievethelistofvirtualmachinesbymachine'shypervisor"
    *      > http://community.abiquo.com/display/ABI20/Machine+Resource#
    *      MachineResource-
    *      Retrievethelistofvirtualmachinesbymachine'shypervisor</a>
    */
   public Iterable<VirtualMachine> listRemoteVirtualMachines() {
      MachineOptions options = MachineOptions.builder().sync(true).build();
      VirtualMachinesWithNodeExtendedDto vms = context.getApi().getInfrastructureApi()
            .listVirtualMachinesByMachine(target, options);
      return wrap(context, VirtualMachine.class, vms.getCollection());
   }

   /**
    * Reserve the machine for the given enterprise.
    * <p>
    * When a {@link Machine} is reserved for an {@link Enterprise}, only the
    * users of that enterprise will be able to deploy {@link VirtualMachine}s in
    * it.
    * 
    * @param enterprise
    *           The enterprise reserving the machine.
    */
   public void reserveFor(final Enterprise enterprise) {
      target = context.getApi().getInfrastructureApi().reserveMachine(enterprise.unwrap(), target);
   }

   /**
    * Cancels the machine reservation for the given enterprise.
    * 
    * @param enterprise
    *           The enterprise to cancel reservation for.
    */
   public void cancelReservationFor(final Enterprise enterprise) {
      context.getApi().getInfrastructureApi().cancelReservation(enterprise.unwrap(), target);
      target.getLinks().remove(target.searchLink(ParentLinkName.ENTERPRISE));
   }

   /**
    * Check if the machine is reserved.
    * 
    * @return Boolean indicating if the machine is reserved for an enterprise.
    */
   public boolean isReserved() {
      return target.searchLink(ParentLinkName.ENTERPRISE) != null;
   }

   /**
    * Get the enterprise that has reserved the machine or <code>null</code> if
    * the machine is not reserved.
    * 
    * @return The enterprise that has reserved the machine or <code>null</code>
    *         if the machine is not reserved.
    */
   public Enterprise getOwnerEnterprise() {
      if (!isReserved()) {
         return null;
      }

      EnterpriseDto enterprise = context.getApi().getEnterpriseApi()
            .getEnterprise(target.getIdFromLink(ParentLinkName.ENTERPRISE));

      return wrap(context, Enterprise.class, enterprise);
   }

   // Builder

   public static Builder builder(final ApiContext<AbiquoApi> context, final Rack rack) {
      return new Builder(context, rack);
   }

   public static class Builder {
      private ApiContext<AbiquoApi> context;

      private String name;

      private String description;

      private Integer virtualRamInMb;

      private Integer virtualRamUsedInMb = DEFAULT_VRAM_USED;

      private Integer virtualCpuCores;

      private Integer virtualCpusUsed = DEFAULT_VCPU_USED;

      private Integer port;

      private String ip;

      private MachineState state = MachineState.STOPPED;

      private String ipService;

      private HypervisorType type;

      private String user;

      private String password;

      private Iterable<Datastore> datastores;

      private Iterable<NetworkInterface> networkInterfaces;

      private String ipmiIp;

      private Integer ipmiPort;

      private String ipmiUser;

      private String ipmiPassword;

      private Rack rack;

      public Builder(final ApiContext<AbiquoApi> context, final Rack rack) {
         super();
         checkNotNull(rack, ValidationErrors.NULL_RESOURCE + Rack.class);
         this.rack = rack;
         this.context = context;
      }

      public Builder state(final MachineState state) {
         this.state = state;
         return this;
      }

      public Builder ipmiPassword(final String ipmiPassword) {
         this.ipmiPassword = ipmiPassword;
         return this;
      }

      public Builder ipmiUser(final String ipmiUser) {
         this.ipmiUser = ipmiUser;
         return this;
      }

      public Builder ipmiPort(final int ipmiPort) {
         this.ipmiPort = ipmiPort;
         return this;
      }

      public Builder ipmiIp(final String ipmiIp) {
         this.ipmiIp = ipmiIp;
         return this;
      }

      public Builder user(final String user) {
         this.user = user;
         return this;
      }

      public Builder ip(final String ip) {
         this.ip = ip;
         if (ipService == null) {
            ipService = ip;
         }
         return this;
      }

      public Builder ipService(final String ipService) {
         this.ipService = ipService;
         return this;
      }

      public Builder password(final String password) {
         this.password = password;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public Builder port(final int port) {
         this.port = port;
         return this;
      }

      public Builder datastores(final Iterable<Datastore> datastores) {
         this.datastores = datastores;
         return this;
      }

      public Builder networkInterfaces(final Iterable<NetworkInterface> networkInterfaces) {
         this.networkInterfaces = networkInterfaces;
         return this;
      }

      public Builder virtualRamInMb(final int virtualRamInMb) {
         this.virtualRamInMb = virtualRamInMb;
         return this;
      }

      public Builder virtualRamUsedInMb(final int virtualRamUsedInMb) {
         this.virtualRamUsedInMb = virtualRamUsedInMb;
         return this;
      }

      public Builder virtualCpuCores(final int virtualCpuCores) {
         this.virtualCpuCores = virtualCpuCores;
         return this;
      }

      public Builder virtualCpusUsed(final int virtualCpusUsed) {
         this.virtualCpusUsed = virtualCpusUsed;
         return this;
      }

      public Builder hypervisorType(final HypervisorType hypervisorType) {
         this.type = hypervisorType;

         // Sets default hypervisor port
         if (this.port == null) {
            this.port = hypervisorType.defaultPort;
         }

         return this;
      }

      public Builder rack(final Rack rack) {
         checkNotNull(rack, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.rack = rack;
         return this;
      }

      public Machine build() {
         MachineDto dto = new MachineDto();
         dto.setName(name);
         dto.setDescription(description);
         dto.setVirtualRamInMb(virtualRamInMb);
         dto.setVirtualRamUsedInMb(virtualRamUsedInMb);
         dto.setVirtualCpuCores(virtualCpuCores);
         dto.setVirtualCpusUsed(virtualCpusUsed);
         if (port != null) {
            dto.setPort(port);
         }
         dto.setIp(ip);
         dto.setIpService(ipService);
         dto.setType(type);
         dto.setUser(user);
         dto.setPassword(password);
         dto.setIpmiIP(ipmiIp);
         dto.setIpmiPassword(ipmiPassword);
         if (ipmiPort != null) {
            dto.setIpmiPort(ipmiPort);
         }
         dto.setIpmiUser(ipmiUser);
         dto.setState(state);

         DatastoresDto datastoresDto = new DatastoresDto();
         datastoresDto.getCollection().addAll(copyOf(unwrap(datastores)));
         dto.setDatastores(datastoresDto);

         NetworkInterfacesDto networkInterfacesDto = new NetworkInterfacesDto();
         networkInterfacesDto.getCollection().addAll(copyOf(unwrap(networkInterfaces)));
         dto.setNetworkInterfaces(networkInterfacesDto);

         Machine machine = new Machine(context, dto);
         machine.rack = rack;

         return machine;
      }

      public static Builder fromMachine(final Machine in) {
         Builder builder = Machine.builder(in.context, in.rack).name(in.getName()).description(in.getDescription())
               .virtualCpuCores(in.getVirtualCpuCores()).virtualCpusUsed(in.getVirtualCpusUsed())
               .virtualRamInMb(in.getVirtualRamInMb()).virtualRamUsedInMb(in.getVirtualRamUsedInMb())
               .port(in.getPort()).ip(in.getIp()).ipService(in.getIpService()).hypervisorType(in.getType())
               .user(in.getUser()).password(in.getPassword()).ipmiIp(in.getIpmiIp()).ipmiPassword(in.getIpmiPassword())
               .ipmiUser(in.getIpmiUser()).state(in.getState()).datastores(in.getDatastores())
               .networkInterfaces(in.getNetworkInterfaces());

         // Parameters that can be null
         if (in.getIpmiPort() != null) {
            builder.ipmiPort(in.getIpmiPort());
         }

         return builder;
      }
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public String getIp() {
      return target.getIp();
   }

   public String getIpmiIp() {
      return target.getIpmiIP();
   }

   public String getIpmiPassword() {
      return target.getIpmiPassword();
   }

   public Integer getIpmiPort() {
      return target.getIpmiPort();
   }

   public String getIpmiUser() {
      return target.getIpmiUser();
   }

   public String getIpService() {
      return target.getIpService();
   }

   public String getName() {
      return target.getName();
   }

   public String getPassword() {
      return target.getPassword();
   }

   public Integer getPort() {
      return target.getPort();
   }

   public MachineState getState() {
      return target.getState();
   }

   public HypervisorType getType() {
      return target.getType();
   }

   public String getUser() {
      return target.getUser();
   }

   public Integer getVirtualCpuCores() {
      return target.getVirtualCpuCores();
   }

   public Integer getVirtualCpusUsed() {
      return target.getVirtualCpusUsed();
   }

   public Integer getVirtualRamInMb() {
      return target.getVirtualRamInMb();
   }

   public Integer getVirtualRamUsedInMb() {
      return target.getVirtualRamUsedInMb();
   }

   public void setDatastores(final List<Datastore> datastores) {
      DatastoresDto datastoresDto = new DatastoresDto();
      datastoresDto.getCollection().addAll(copyOf(unwrap(datastores)));
      target.setDatastores(datastoresDto);
   }

   public void setDescription(final String description) {
      target.setDescription(description);
   }

   public void setIp(final String ip) {
      target.setIp(ip);
   }

   public void setIpmiIp(final String ipmiIp) {
      target.setIpmiIP(ipmiIp);
   }

   public void setIpmiPassword(final String ipmiPassword) {
      target.setIpmiPassword(ipmiPassword);
   }

   public void setIpmiPort(final Integer ipmiPort) {
      target.setIpmiPort(ipmiPort);
   }

   public void setIpmiUser(final String ipmiUser) {
      target.setIpmiUser(ipmiUser);
   }

   public void setIpService(final String ipService) {
      target.setIpService(ipService);
   }

   public void setName(final String name) {
      target.setName(name);
   }

   public void setPassword(final String password) {
      target.setPassword(password);
   }

   public void setPort(final Integer port) {
      target.setPort(port);
   }

   public void setState(final MachineState state) {
      target.setState(state);
   }

   public void setType(final HypervisorType type) {
      target.setType(type);
   }

   public void setUser(final String user) {
      target.setUser(user);
   }

   public void setVirtualCpuCores(final Integer virtualCpuCores) {
      target.setVirtualCpuCores(virtualCpuCores);
   }

   public void setVirtualCpusUsed(final Integer virtualCpusUsed) {
      target.setVirtualCpusUsed(virtualCpusUsed);
   }

   public void setVirtualRamInMb(final Integer virtualRamInMb) {
      target.setVirtualRamInMb(virtualRamInMb);
   }

   public void setVirtualRamUsedInMb(final Integer virtualRamUsedInMb) {
      target.setVirtualRamUsedInMb(virtualRamUsedInMb);
   }

   public String getDescription() {
      return target.getDescription();
   }

   public void setRack(final Rack rack) {
      this.rack = rack;
   }

   @Override
   public String toString() {
      return "Machine [id=" + getId() + ", ip=" + getIp() + ", ipmiIp=" + getIpmiIp() + ", ipmiPassword="
            + getIpmiPassword() + ", ipmiPort=" + getIpmiPort() + ", ipmiUser=" + getIpmiUser() + ", ipService="
            + getIpService() + ", name=" + getName() + ", password=" + getPassword() + ", port=" + getPort()
            + ", state=" + getState() + ", type=" + getType() + ", user=" + getUser() + ", virtualCpuCores="
            + getVirtualCpuCores() + ", virtualCpusUsed=" + getVirtualCpusUsed() + ", getVirtualRamInMb()="
            + getVirtualRamInMb() + ", virtualRamUsedInMb=" + getVirtualRamUsedInMb() + ", description="
            + getDescription() + "]";
   }

}
