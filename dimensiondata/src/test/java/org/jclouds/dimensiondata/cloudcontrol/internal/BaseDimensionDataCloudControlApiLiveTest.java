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
package org.jclouds.dimensiondata.cloudcontrol.internal;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApiMetadata;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlProviderMetadata;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.ApiContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.newDirectExecutorService;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.NETWORK_DOMAIN_DELETED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.NETWORK_DOMAIN_NORMAL_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.SERVER_DELETED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.SERVER_NORMAL_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.SERVER_STARTED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.SERVER_STOPPED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.VLAN_DELETED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.VLAN_NORMAL_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.VM_TOOLS_RUNNING_PREDICATE;

@Test(groups = "live")
public class BaseDimensionDataCloudControlApiLiveTest extends BaseApiLiveTest<DimensionDataCloudControlApi> {

   protected ApiContext<DimensionDataCloudControlApi> ctx;
   private final Set<Module> modules = ImmutableSet.<Module>of(new ExecutorServiceModule(newDirectExecutorService()));
   protected Set<String> datacenters;

   protected static final String PREPARED_CUSTOMER_IMAGE_ID = "fb438e00-10f8-47ac-a434-f3f9461c3a76";
   protected static final String NETWORK_DOMAIN_ID = System
         .getProperty("networkDomainId", "690de302-bb80-49c6-b401-8c02bbefb945");
   protected static final String VLAN_ID = System.getProperty("vlanId", "6b25b02e-d3a2-4e69-8ca7-9bab605deebd");
   protected static final String IMAGE_ID = System.getProperty("imageId", "4c02126c-32fc-4b4c-9466-9824c1b5aa0f");
   protected static final String PREPARED_NETWORK_DOMAIN_ID = System
         .getProperty("networkDomainId", "d122949b-8990-46d6-98f0-91c8676fc720");
   protected static final String PREPARED_PRIVATE_IPV4_ADDRESS = "10.0.0.6";
   protected static final String SERVER_ID = System.getProperty("serverId", "b1c537bb-018c-49ba-beef-e0600e948149");

   protected Predicate<String> vlanDeletedPredicate;
   protected Predicate<String> vlanNormalPredicate;
   protected Predicate<String> networkDomainDeletedPredicate;
   protected Predicate<String> networkDomainNormalPredicate;
   protected Predicate<String> serverStoppedPredicate;
   protected Predicate<String> serverStartedPredicate;
   protected Predicate<String> serverDeletedPredicate;
   protected Predicate<String> serverNormalPredicate;
   protected Predicate<String> vmtoolsRunningPredicate;

   public BaseDimensionDataCloudControlApiLiveTest() {
      provider = "dimensiondata-cloudcontrol";
   }

   @BeforeClass
   public void setUp() {
      ctx = ContextBuilder.newBuilder(DimensionDataCloudControlProviderMetadata.builder().build()).credentials("", "")
            .modules(modules).overrides(new Properties()).build();
      datacenters = getZones();
   }

   private Set<String> getZones() {
      return ctx.utils().injector().getInstance(ZoneIdsSupplier.class).get();
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new DimensionDataCloudControlApiMetadata();
   }

   @Override
   protected LoggingModule getLoggingModule() {
      return new SLF4JLoggingModule();
   }

   @Override
   protected DimensionDataCloudControlApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      vlanDeletedPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(VLAN_DELETED_PREDICATE)));
      vlanNormalPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(VLAN_NORMAL_PREDICATE)));
      networkDomainDeletedPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(NETWORK_DOMAIN_DELETED_PREDICATE)));
      networkDomainNormalPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(NETWORK_DOMAIN_NORMAL_PREDICATE)));
      serverStartedPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(SERVER_STARTED_PREDICATE)));
      serverStoppedPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(SERVER_STOPPED_PREDICATE)));
      serverDeletedPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(SERVER_DELETED_PREDICATE)));
      serverNormalPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(SERVER_NORMAL_PREDICATE)));
      vmtoolsRunningPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(VM_TOOLS_RUNNING_PREDICATE)));

      return injector.getInstance(DimensionDataCloudControlApi.class);
   }

}
