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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoContext;
import org.jclouds.abiquo.features.services.AdministrationService;
import org.jclouds.abiquo.features.services.CloudService;
import org.jclouds.abiquo.features.services.EventService;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.Utils;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.location.Provider;
import org.jclouds.rest.ApiContext;

import com.google.common.reflect.TypeToken;

/**
 * Abiquo {@link ApiContext} implementation to expose high level Abiquo
 * functionalities.
 */
@Singleton
public class AbiquoContextImpl extends ComputeServiceContextImpl implements AbiquoContext {
   private final AdministrationService administrationService;

   private final CloudService cloudService;

   private final MonitoringService monitoringService;

   private final EventService eventService;

   @Inject
   public AbiquoContextImpl(@Provider final Context wrapped, @Provider final TypeToken<? extends Context> wrappedType,
         final ComputeService computeService, final Utils utils, final ApiContext<AbiquoApi> providerSpecificContext,
         final AdministrationService administrationService, final CloudService cloudService,
         final MonitoringService monitoringService, final EventService eventService) {
      super(wrapped, wrappedType, computeService, utils);
      this.administrationService = checkNotNull(administrationService, "administrationService");
      this.cloudService = checkNotNull(cloudService, "cloudService");
      this.monitoringService = checkNotNull(monitoringService, "monitoringService");
      this.eventService = checkNotNull(eventService, "eventService");
   }

   @Override
   public ApiContext<AbiquoApi> getApiContext() {
      return unwrap();
   }

   @Override
   public AdministrationService getAdministrationService() {
      return administrationService;
   }

   @Override
   public CloudService getCloudService() {
      return cloudService;
   }

   @Override
   public MonitoringService getMonitoringService() {
      return monitoringService;
   }

   @Override
   public EventService getEventService() {
      return eventService;
   }
}
