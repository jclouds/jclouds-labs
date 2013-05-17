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
package org.jclouds.abiquo.strategy.cloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineOptions;
import org.jclouds.abiquo.strategy.ListRootEntities;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ApiContext;

import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * List virtual machines in each virtual datacenter and each virtual appliance.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class ListVirtualMachines implements ListRootEntities<VirtualMachine> {
   protected final ApiContext<AbiquoApi> context;

   protected final ListeningExecutorService userExecutor;

   protected final ListVirtualAppliances listVirtualAppliances;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   ListVirtualMachines(final ApiContext<AbiquoApi> context,
         @Named(Constants.PROPERTY_USER_THREADS) final ListeningExecutorService userExecutor,
         final ListVirtualAppliances listVirtualAppliances) {
      super();
      this.context = checkNotNull(context, "context");
      this.listVirtualAppliances = checkNotNull(listVirtualAppliances, "listVirtualAppliances");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Iterable<VirtualMachine> execute() {
      return execute(userExecutor);
   }

   @Override
   public Iterable<VirtualMachine> execute(final Predicate<VirtualMachine> selector) {
      return execute(userExecutor, selector);
   }

   public Iterable<VirtualMachine> execute(ListeningExecutorService executor) {
      return execute(executor, VirtualMachineOptions.builder().disablePagination().build());
   }

   public Iterable<VirtualMachine> execute(ListeningExecutorService executor, final Predicate<VirtualMachine> selector) {
      return filter(execute(executor), selector);
   }

   public Iterable<VirtualMachine> execute(ListeningExecutorService executor, final VirtualMachineOptions options) {
      // Find virtual machines in concurrent requests
      Iterable<VirtualAppliance> vapps = listVirtualAppliances.execute(executor);
      Iterable<VirtualMachineWithNodeExtendedDto> vms = listConcurrentVirtualMachines(executor, vapps, options);

      return wrap(context, VirtualMachine.class, vms);
   }

   private Iterable<VirtualMachineWithNodeExtendedDto> listConcurrentVirtualMachines(
         final ListeningExecutorService executor, final Iterable<VirtualAppliance> vapps,
         final VirtualMachineOptions options) {
      ListenableFuture<List<VirtualMachinesWithNodeExtendedDto>> futures = allAsList(transform(vapps,
            new Function<VirtualAppliance, ListenableFuture<VirtualMachinesWithNodeExtendedDto>>() {
               @Override
               public ListenableFuture<VirtualMachinesWithNodeExtendedDto> apply(final VirtualAppliance input) {
                  return executor.submit(new Callable<VirtualMachinesWithNodeExtendedDto>() {
                     @Override
                     public VirtualMachinesWithNodeExtendedDto call() throws Exception {
                        return context.getApi().getCloudApi().listVirtualMachines(input.unwrap(), options);
                     }
                  });
               }
            }));

      logger.trace("getting virtual machines");
      return DomainWrapper.join(getUnchecked(futures));
   }
}
