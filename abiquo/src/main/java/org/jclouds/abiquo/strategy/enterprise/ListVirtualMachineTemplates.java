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
package org.jclouds.abiquo.strategy.enterprise;

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
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.strategy.ListEntities;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ApiContext;

import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * List all virtual machine templates available to an enterprise.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class ListVirtualMachineTemplates implements ListEntities<VirtualMachineTemplate, Enterprise> {
   protected final ApiContext<AbiquoApi> context;

   protected final ListeningExecutorService userExecutor;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   ListVirtualMachineTemplates(final ApiContext<AbiquoApi> context,
         @Named(Constants.PROPERTY_USER_THREADS) final ListeningExecutorService userExecutor) {
      super();
      this.context = checkNotNull(context, "context");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Iterable<VirtualMachineTemplate> execute(final Enterprise parent) {
      return execute(userExecutor, parent);
   }

   @Override
   public Iterable<VirtualMachineTemplate> execute(final Enterprise parent,
         final Predicate<VirtualMachineTemplate> selector) {
      return execute(userExecutor, parent, selector);
   }

   public Iterable<VirtualMachineTemplate> execute(ListeningExecutorService executor, final Enterprise parent) {
      // Find virtual machine templates in concurrent requests
      Iterable<Datacenter> dcs = parent.listAllowedDatacenters();
      Iterable<VirtualMachineTemplateDto> templates = listConcurrentTemplates(executor, parent, dcs);

      return wrap(context, VirtualMachineTemplate.class, templates);
   }

   public Iterable<VirtualMachineTemplate> execute(ListeningExecutorService executor, final Enterprise parent,
         final Predicate<VirtualMachineTemplate> selector) {
      return filter(execute(executor, parent), selector);
   }

   private Iterable<VirtualMachineTemplateDto> listConcurrentTemplates(final ListeningExecutorService executor,
         final Enterprise parent, final Iterable<Datacenter> dcs) {
      ListenableFuture<List<VirtualMachineTemplatesDto>> futures = allAsList(transform(dcs,
            new Function<Datacenter, ListenableFuture<VirtualMachineTemplatesDto>>() {
               @Override
               public ListenableFuture<VirtualMachineTemplatesDto> apply(final Datacenter input) {
                  return executor.submit(new Callable<VirtualMachineTemplatesDto>() {
                     @Override
                     public VirtualMachineTemplatesDto call() throws Exception {
                        return context.getApi().getVirtualMachineTemplateApi()
                              .listVirtualMachineTemplates(parent.getId(), input.getId());
                     }
                  });
               }
            }));

      logger.trace("getting virtual machine templates");
      return DomainWrapper.join(getUnchecked(futures));
   }
}
