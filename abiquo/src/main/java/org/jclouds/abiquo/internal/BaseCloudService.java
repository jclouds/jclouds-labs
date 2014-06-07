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
package org.jclouds.abiquo.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.options.VirtualDatacenterOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineOptions;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.features.services.CloudService;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.strategy.cloud.ListVirtualAppliances;
import org.jclouds.abiquo.strategy.cloud.ListVirtualDatacenters;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.ApiContext;

import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.google.common.annotations.VisibleForTesting;

/**
 * Provides high level Abiquo cloud operations.
 */
@Singleton
public class BaseCloudService implements CloudService {
   @VisibleForTesting
   protected final ApiContext<AbiquoApi> context;

   @VisibleForTesting
   protected final ListVirtualDatacenters listVirtualDatacenters;

   @VisibleForTesting
   protected final ListVirtualAppliances listVirtualAppliances;

   @Inject
   protected BaseCloudService(final ApiContext<AbiquoApi> context, final ListVirtualDatacenters listVirtualDatacenters,
         final ListVirtualAppliances listVirtualAppliances) {
      this.context = checkNotNull(context, "context");
      this.listVirtualDatacenters = checkNotNull(listVirtualDatacenters, "listVirtualDatacenters");
      this.listVirtualAppliances = checkNotNull(listVirtualAppliances, "listVirtualAppliances");
   }

   /*********************** Virtual Datacenter ********************** */

   @Override
   public Iterable<VirtualDatacenter> listVirtualDatacenters() {
      return listVirtualDatacenters.execute();
   }

   @Override
   public Iterable<VirtualDatacenter> listVirtualDatacenters(final Enterprise enterprise) {
      checkNotNull(enterprise, ValidationErrors.NULL_RESOURCE + Enterprise.class);
      checkNotNull(enterprise.getId(), ValidationErrors.MISSING_REQUIRED_FIELD + " id in " + Enterprise.class);

      VirtualDatacenterOptions options = VirtualDatacenterOptions.builder().enterpriseId(enterprise.getId()).build();

      return listVirtualDatacenters.execute(options);
   }

   @Override
   public VirtualDatacenter getVirtualDatacenter(final Integer virtualDatacenterId) {
      VirtualDatacenterDto virtualDatacenter = context.getApi().getCloudApi().getVirtualDatacenter(virtualDatacenterId);
      return wrap(context, VirtualDatacenter.class, virtualDatacenter);
   }

   @Override
   public Iterable<VirtualDatacenter> getVirtualDatacenters(final List<Integer> virtualDatacenterIds) {
      return listVirtualDatacenters.execute(virtualDatacenterIds);
   }

   /*********************** Virtual Appliance ********************** */

   @Override
   public Iterable<VirtualAppliance> listVirtualAppliances() {
      return listVirtualAppliances.execute();
   }

   /*********************** Virtual Machine ********************** */

   @Override
   public Iterable<VirtualMachine> listVirtualMachines() {
      PagedIterable<VirtualMachineWithNodeExtendedDto> vms = context.getApi().getCloudApi().listAllVirtualMachines();
      return wrap(context, VirtualMachine.class, vms.concat());
   }

   @Override
   public Iterable<VirtualMachine> listVirtualMachines(VirtualMachineOptions options) {
      PaginatedCollection<VirtualMachineWithNodeExtendedDto, VirtualMachinesWithNodeExtendedDto> vms = context.getApi()
            .getCloudApi().listAllVirtualMachines(options);
      return wrap(context, VirtualMachine.class, vms.toPagedIterable().concat());
   }
}
