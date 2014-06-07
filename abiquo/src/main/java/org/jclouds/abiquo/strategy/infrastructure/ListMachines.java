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
package org.jclouds.abiquo.strategy.infrastructure;

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
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.infrastructure.Machine;
import org.jclouds.abiquo.strategy.ListRootEntities;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ApiContext;

import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * List machines in each datacenter and rack.
 */
@Singleton
public class ListMachines implements ListRootEntities<Machine> {
   protected ApiContext<AbiquoApi> context;

   protected final ListeningExecutorService userExecutor;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   ListMachines(final ApiContext<AbiquoApi> context,
         @Named(Constants.PROPERTY_USER_THREADS) final ListeningExecutorService userExecutor) {
      super();
      this.context = checkNotNull(context, "context");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Iterable<Machine> execute() {
      return execute(userExecutor);
   }

   public Iterable<Machine> execute(ListeningExecutorService executor) {
      // Find machines in concurrent requests
      DatacentersDto result = context.getApi().getInfrastructureApi().listDatacenters();
      Iterable<Datacenter> datacenters = wrap(context, Datacenter.class, result.getCollection());
      Iterable<RackDto> racks = listConcurrentRacks(executor, datacenters);
      Iterable<MachineDto> machines = listConcurrentMachines(executor, racks);

      return wrap(context, Machine.class, machines);
   }

   public Iterable<Machine> execute(ListeningExecutorService executor, final Predicate<Machine> selector) {
      return filter(execute(executor), selector);
   }

   private Iterable<RackDto> listConcurrentRacks(final ListeningExecutorService executor,
         final Iterable<Datacenter> datacenters) {
      ListenableFuture<List<RacksDto>> futures = allAsList(transform(datacenters,
            new Function<Datacenter, ListenableFuture<RacksDto>>() {
               @Override
               public ListenableFuture<RacksDto> apply(final Datacenter input) {
                  return executor.submit(new Callable<RacksDto>() {
                     @Override
                     public RacksDto call() throws Exception {
                        return context.getApi().getInfrastructureApi().listRacks(input.unwrap());
                     }
                  });
               }
            }));

      logger.trace("getting racks");
      return DomainWrapper.join(getUnchecked(futures));
   }

   private Iterable<MachineDto> listConcurrentMachines(final ListeningExecutorService executor,
         final Iterable<RackDto> racks) {
      ListenableFuture<List<MachinesDto>> futures = allAsList(transform(racks,
            new Function<RackDto, ListenableFuture<MachinesDto>>() {
               @Override
               public ListenableFuture<MachinesDto> apply(final RackDto input) {
                  return executor.submit(new Callable<MachinesDto>() {
                     @Override
                     public MachinesDto call() throws Exception {
                        return context.getApi().getInfrastructureApi().listMachines(input);
                     }
                  });
               }
            }));

      logger.trace("getting machines");
      return DomainWrapper.join(getUnchecked(futures));
   }
}
