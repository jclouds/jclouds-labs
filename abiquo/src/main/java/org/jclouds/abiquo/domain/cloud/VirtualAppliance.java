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

import java.util.Arrays;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineOptions;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.domain.task.VirtualMachineTask;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.ApiContext;

import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualApplianceStateDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualMachineTaskDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;

/**
 * Represents a virtual appliance.
 * <p>
 * A virtual appliance is a logic container for virtual machines that together
 * make an appliance.
 * 
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/Virtual+Appliance+Resource"
 *      >
 *      http://community.abiquo.com/display/ABI20/Virtual+Appliance+Resource</a>
 */
public class VirtualAppliance extends DomainWrapper<VirtualApplianceDto> {
   /** The virtual datacenter where the virtual appliance belongs. */
   private VirtualDatacenter virtualDatacenter;

   /**
    * Constructor to be used only by the builder.
    */
   protected VirtualAppliance(final ApiContext<AbiquoApi> context, final VirtualApplianceDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * Deletes the virtual appliance.
    */
   public void delete() {
      context.getApi().getCloudApi().deleteVirtualAppliance(target);
      target = null;
   }

   /**
    * Creates the virtual appliance in the selected virtual datacenter.
    */
   public void save() {
      target = context.getApi().getCloudApi().createVirtualAppliance(virtualDatacenter.unwrap(), target);
   }

   /**
    * Updates the virtual appliance information when some of its properties have
    * changed.
    */
   public void update() {
      target = context.getApi().getCloudApi().updateVirtualAppliance(target);
   }

   // Parent access

   /**
    * Gets the virtual datacenter where the virtual appliance belongs to.
    * 
    * @return The virtual datacenter where the virtual appliance belongs to.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource#VirtualDatacenterResource-RetrieveaVirtualDatacenter"
    *      > http://community.abiquo.com/display/ABI20/Virtual+Datacenter+
    *      Resource# VirtualDatacenterResource-RetrieveaVirtualDatacenter</a>
    */
   public VirtualDatacenter getVirtualDatacenter() {
      Integer virtualDatacenterId = target.getIdFromLink(ParentLinkName.VIRTUAL_DATACENTER);
      VirtualDatacenterDto dto = context.getApi().getCloudApi().getVirtualDatacenter(virtualDatacenterId);
      virtualDatacenter = wrap(context, VirtualDatacenter.class, dto);
      return virtualDatacenter;
   }

   /**
    * Gets the enterprise where the virtual appliance belongs to.
    * 
    * @return The enterprise where the virtual appliance belongs to.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Enterprise+Resource#EnterpriseResource-RetrieveaEnterprise"
    *      > http://community.abiquo.com/display/ABI20/Enterprise+Resource#
    *      EnterpriseResource- RetrieveaEnterprise</a>
    */
   public Enterprise getEnterprise() {
      Integer enterpriseId = target.getIdFromLink(ParentLinkName.ENTERPRISE);
      EnterpriseDto dto = context.getApi().getEnterpriseApi().getEnterprise(enterpriseId);
      return wrap(context, Enterprise.class, dto);
   }

   /**
    * Gets the current state of the virtual appliance.
    * 
    * @return The current state of the virtual appliance.
    */
   public VirtualApplianceState getState() {
      VirtualApplianceStateDto stateDto = context.getApi().getCloudApi().getVirtualApplianceState(target);
      return stateDto.getPower();
   }

   // Children access

   /**
    * Gets the list of virtual machines in the virtual appliance.
    * 
    * @return The list of virtual machines in the virtual appliance.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI18/Virtual+Machine+Resource#VirtualMachineResource-RetrievethelistofVirtualMachines."
    *      > http://community.abiquo.com/display/ABI18/Virtual+Machine+Resource#
    *      VirtualMachineResource -RetrievethelistofVirtualMachines.</a>
    */
   public Iterable<VirtualMachine> listVirtualMachines() {
      PagedIterable<VirtualMachineWithNodeExtendedDto> vms = context.getApi().getCloudApi().listVirtualMachines(target);
      return wrap(context, VirtualMachine.class, vms.concat());
   }

   /**
    * Gets the list of virtual machines in the virtual appliance.
    * 
    * @return The list of virtual machines in the virtual appliance.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI18/Virtual+Machine+Resource#VirtualMachineResource-RetrievethelistofVirtualMachines."
    *      > http://community.abiquo.com/display/ABI18/Virtual+Machine+Resource#
    *      VirtualMachineResource -RetrievethelistofVirtualMachines.</a>
    */
   public Iterable<VirtualMachine> listVirtualMachines(final VirtualMachineOptions options) {
      PaginatedCollection<VirtualMachineWithNodeExtendedDto, VirtualMachinesWithNodeExtendedDto> vms = context.getApi()
            .getCloudApi().listVirtualMachines(target, options);
      return wrap(context, VirtualMachine.class, vms.toPagedIterable().concat());
   }

   /**
    * Gets a concrete virtual machine in the virtual appliance.
    * 
    * @param id
    *           The id of the virtual machine.
    * @return The requested virtual machine.
    */
   public VirtualMachine getVirtualMachine(final Integer id) {
      VirtualMachineWithNodeExtendedDto vm = context.getApi().getCloudApi().getVirtualMachine(target, id);
      return wrap(context, VirtualMachine.class, vm);
   }

   // Actions

   /**
    * Deploys the virtual appliance.
    * <p>
    * This method will start the deployment of all the virtual machines in the
    * virtual appliance, and will return an {@link AsyncTask} reference for each
    * deployment operation. The deployment will finish when all individual tasks
    * finish.
    * 
    * @return The list of tasks corresponding to the deploy process of each
    *         virtual machine in the appliance.
    */
   public VirtualMachineTask[] deploy() {
      return deploy(false);
   }

   /**
    * Deploys the virtual appliance.
    * <p>
    * This method will start the deployment of all the virtual machines in the
    * virtual appliance, and will return an {@link AsyncTask} reference for each
    * deploy operation. The deployment will finish when all individual tasks
    * finish.
    * 
    * @param forceEnterpriseSoftLimits
    *           Boolean indicating if the deployment must be executed even if
    *           the enterprise soft limits are reached.
    * @return The list of tasks corresponding to the deploy process of each
    *         virtual machine in the appliance.
    */
   public VirtualMachineTask[] deploy(final boolean forceEnterpriseSoftLimits) {
      VirtualMachineTaskDto force = new VirtualMachineTaskDto();
      force.setForceEnterpriseSoftLimits(forceEnterpriseSoftLimits);

      AcceptedRequestDto<String> response = context.getApi().getCloudApi().deployVirtualAppliance(unwrap(), force);

      AsyncTask<?, ?>[] tasks = getTasks(response);
      return Arrays.copyOf(tasks, tasks.length, VirtualMachineTask[].class);
   }

   /**
    * Undeploys the virtual appliance.
    * <p>
    * This method will start the undeploy of all the virtual machines in the
    * virtual appliance, and will return an {@link AsyncTask} reference for each
    * undeploy operation. The undeploy will finish when all individual tasks
    * finish.
    * 
    * @return The list of tasks corresponding to the undeploy process of each
    *         virtual machine in the appliance.
    */
   public VirtualMachineTask[] undeploy() {
      return undeploy(false);
   }

   /**
    * Undeploys the virtual appliance.
    * <p>
    * This method will start the undeploy of all the virtual machines in the
    * virtual appliance, and will return an {@link AsyncTask} reference for each
    * undeploy operation. The undeploy will finish when all individual tasks
    * finish.
    * 
    * @param forceUndeploy
    *           Boolean flag to force the undeploy even if the virtual appliance
    *           contains imported virtual machines.
    * @return The list of tasks corresponding to the undeploy process of each
    *         virtual machine in the appliance.
    */
   public VirtualMachineTask[] undeploy(final boolean forceUndeploy) {
      VirtualMachineTaskDto force = new VirtualMachineTaskDto();
      force.setForceUndeploy(forceUndeploy);

      AcceptedRequestDto<String> response = context.getApi().getCloudApi().undeployVirtualAppliance(unwrap(), force);

      AsyncTask<?, ?>[] tasks = getTasks(response);
      return Arrays.copyOf(tasks, tasks.length, VirtualMachineTask[].class);
   }

   /**
    * Returns a String message with the price info of the virtual appliance.
    * 
    * @return The price of the virtual appliance
    */
   public String getPrice() {
      return context.getApi().getCloudApi().getVirtualAppliancePrice(target);
   }

   // Builder

   public static Builder builder(final ApiContext<AbiquoApi> context, final VirtualDatacenter virtualDatacenter) {
      return new Builder(context, virtualDatacenter);
   }

   public static class Builder {
      private ApiContext<AbiquoApi> context;

      private String name;

      private VirtualDatacenter virtualDatacenter;

      public Builder(final ApiContext<AbiquoApi> context, final VirtualDatacenter virtualDatacenter) {
         super();
         checkNotNull(virtualDatacenter, ValidationErrors.NULL_RESOURCE + VirtualDatacenter.class);
         this.virtualDatacenter = virtualDatacenter;
         this.context = context;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder virtualDatacenter(final VirtualDatacenter virtualDatacenter) {
         checkNotNull(virtualDatacenter, ValidationErrors.NULL_RESOURCE + VirtualDatacenter.class);
         this.virtualDatacenter = virtualDatacenter;
         return this;
      }

      public VirtualAppliance build() {
         VirtualApplianceDto dto = new VirtualApplianceDto();
         dto.setName(name);

         VirtualAppliance virtualAppliance = new VirtualAppliance(context, dto);
         virtualAppliance.virtualDatacenter = virtualDatacenter;

         return virtualAppliance;
      }

      public static Builder fromVirtualAppliance(final VirtualAppliance in) {
         return VirtualAppliance.builder(in.context, in.virtualDatacenter).name(in.getName());
      }
   }

   // Delegate methods

   public int getError() {
      return target.getError();
   }

   public Integer getId() {
      return target.getId();
   }

   public String getName() {
      return target.getName();
   }

   public void setHighDisponibility(final int highDisponibility) {
      target.setHighDisponibility(highDisponibility);
   }

   public void setName(final String name) {
      target.setName(name);
   }

   public void setPublicApp(final int publicApp) {
      target.setPublicApp(publicApp);
   }

   @Override
   public String toString() {
      return "VirtualAppliance [id=" + getId() + ", name=" + getName() + "]";
   }

}
