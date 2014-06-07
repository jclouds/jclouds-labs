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
package org.jclouds.abiquo.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.handlers.AbiquoErrorHandler;
import org.jclouds.collect.Memoized;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ApiContext;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Configures the Abiquo connection.
 */
@ConfiguresHttpApi
public class AbiquoHttpApiModule extends HttpApiModule<AbiquoApi> {

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(AbiquoErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(AbiquoErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(AbiquoErrorHandler.class);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<User> getCurrentUser(final AtomicReference<AuthorizationException> authException,
         @Named(PROPERTY_SESSION_INTERVAL) final long seconds, final ApiContext<AbiquoApi> context) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, new Supplier<User>() {
         @Override
         public User get() {
            return wrap(context, User.class, context.getApi().getAdminApi().getCurrentUser());
         }
      }, seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Enterprise> getCurrentEnterprise(final AtomicReference<AuthorizationException> authException,
         @Named(PROPERTY_SESSION_INTERVAL) final long seconds, @Memoized final Supplier<User> currentUser) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
            new Supplier<Enterprise>() {
               @Override
               public Enterprise get() {
                  return currentUser.get().getEnterprise();
               }
            }, seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<Integer, Datacenter>> getAvailableRegionsIndexedById(
         final AtomicReference<AuthorizationException> authException,
         @Named(PROPERTY_SESSION_INTERVAL) final long seconds, @Memoized final Supplier<Enterprise> currentEnterprise) {
      Supplier<Map<Integer, Datacenter>> availableRegionsMapSupplier = Suppliers.compose(
            new Function<Iterable<Datacenter>, Map<Integer, Datacenter>>() {
               @Override
               public Map<Integer, Datacenter> apply(final Iterable<Datacenter> datacenters) {
                  // Index available regions by id
                  return Maps.uniqueIndex(datacenters, new Function<Datacenter, Integer>() {
                     @Override
                     public Integer apply(final Datacenter input) {
                        return input.getId();
                     }
                  });
               }
            }, new Supplier<Iterable<Datacenter>>() {
               @Override
               public Iterable<Datacenter> get() {
                  // Get the list of regions available for the user's tenant
                  return currentEnterprise.get().listAllowedDatacenters();
               }
            });

      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
            availableRegionsMapSupplier, seconds, TimeUnit.SECONDS);
   }
}
