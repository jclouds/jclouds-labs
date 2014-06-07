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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.options.VirtualDatacenterOptions;
import org.jclouds.abiquo.strategy.ListRootEntities;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ApiContext;

import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * List virtual datacenters.
 */
@Singleton
public class ListVirtualDatacenters implements ListRootEntities<VirtualDatacenter> {
   protected final ApiContext<AbiquoApi> context;

   protected final ListeningExecutorService userExecutor;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   ListVirtualDatacenters(final ApiContext<AbiquoApi> context,
         @Named(Constants.PROPERTY_USER_THREADS) final ListeningExecutorService userExecutor) {
      this.context = checkNotNull(context, "context");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Iterable<VirtualDatacenter> execute() {
      return execute(userExecutor);
   }

   public Iterable<VirtualDatacenter> execute(final VirtualDatacenterOptions virtualDatacenterOptions) {
      VirtualDatacentersDto result = context.getApi().getCloudApi().listVirtualDatacenters(virtualDatacenterOptions);
      return wrap(context, VirtualDatacenter.class, result.getCollection());
   }

   public Iterable<VirtualDatacenter> execute(final Predicate<VirtualDatacenter> selector,
         final VirtualDatacenterOptions virtualDatacenterOptions) {
      return filter(execute(virtualDatacenterOptions), selector);
   }

   public Iterable<VirtualDatacenter> execute(final List<Integer> virtualDatacenterIds) {
      return execute(userExecutor, virtualDatacenterIds);
   }

   public Iterable<VirtualDatacenter> execute(ListeningExecutorService executor,
         final List<Integer> virtualDatacenterIds) {
      return listConcurrentVirtualDatacenters(executor, virtualDatacenterIds);
   }

   private Iterable<VirtualDatacenter> listConcurrentVirtualDatacenters(final ListeningExecutorService executor,
         final List<Integer> ids) {
      ListenableFuture<List<VirtualDatacenterDto>> futures = allAsList(transform(ids,
            new Function<Integer, ListenableFuture<VirtualDatacenterDto>>() {
               @Override
               public ListenableFuture<VirtualDatacenterDto> apply(final Integer input) {
                  return executor.submit(new Callable<VirtualDatacenterDto>() {
                     @Override
                     public VirtualDatacenterDto call() throws Exception {
                        return context.getApi().getCloudApi().getVirtualDatacenter(input);
                     }
                  });

               }
            }));

      logger.trace("getting virtual datacenters");
      return wrap(context, VirtualDatacenter.class, newArrayList(getUnchecked(futures)));
   }

   public Iterable<VirtualDatacenter> execute(ListeningExecutorService executor) {
      VirtualDatacenterOptions virtualDatacenterOptions = VirtualDatacenterOptions.builder().build();
      return execute(virtualDatacenterOptions);
   }

   public Iterable<VirtualDatacenter> execute(ListeningExecutorService executor, Predicate<VirtualDatacenter> selector) {
      return filter(execute(executor), selector);
   }

}
