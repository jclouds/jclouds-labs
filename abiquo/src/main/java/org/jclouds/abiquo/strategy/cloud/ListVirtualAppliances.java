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
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.strategy.ListRootEntities;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ApiContext;

import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * List virtual appliances in each virtual datacenter.
 */
@Singleton
public class ListVirtualAppliances implements ListRootEntities<VirtualAppliance> {
   protected final ApiContext<AbiquoApi> context;

   protected final ListVirtualDatacenters listVirtualDatacenters;

   protected final ListeningExecutorService userExecutor;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   ListVirtualAppliances(final ApiContext<AbiquoApi> context,
         @Named(Constants.PROPERTY_USER_THREADS) final ListeningExecutorService userExecutor,
         final ListVirtualDatacenters listVirtualDatacenters) {
      this.context = checkNotNull(context, "context");
      this.listVirtualDatacenters = checkNotNull(listVirtualDatacenters, "listVirtualDatacenters");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Iterable<VirtualAppliance> execute() {
      return execute(userExecutor);
   }

   private Iterable<VirtualApplianceDto> listConcurrentVirtualAppliances(final ListeningExecutorService executor,
         final Iterable<VirtualDatacenter> vdcs) {
      ListenableFuture<List<VirtualAppliancesDto>> futures = allAsList(transform(vdcs,
            new Function<VirtualDatacenter, ListenableFuture<VirtualAppliancesDto>>() {
               @Override
               public ListenableFuture<VirtualAppliancesDto> apply(final VirtualDatacenter input) {
                  return executor.submit(new Callable<VirtualAppliancesDto>() {
                     @Override
                     public VirtualAppliancesDto call() throws Exception {
                        return context.getApi().getCloudApi().listVirtualAppliances(input.unwrap());
                     }
                  });
               }
            }));

      logger.trace("getting virtual appliances");
      return DomainWrapper.join(getUnchecked(futures));
   }

   public Iterable<VirtualAppliance> execute(ListeningExecutorService executor) {
      // / Find virtual appliances in concurrent requests
      Iterable<VirtualDatacenter> vdcs = listVirtualDatacenters.execute(executor);
      Iterable<VirtualApplianceDto> vapps = listConcurrentVirtualAppliances(executor, vdcs);

      return wrap(context, VirtualAppliance.class, vapps);
   }

   public Iterable<VirtualAppliance> execute(ListeningExecutorService executor, Predicate<VirtualAppliance> selector) {
      return filter(execute(executor), selector);
   }
}
