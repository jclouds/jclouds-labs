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
package org.apache.jclouds.profitbricks.rest.internal;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.jclouds.profitbricks.rest.ProfitBricksApi;
import org.apache.jclouds.profitbricks.rest.util.ApiPredicatesModule.ComputeConstants;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_DATACENTER;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.TIMEOUT_NODE_RUNNING;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.TIMEOUT_NODE_SUSPENDED;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.LicenceType;
import org.apache.jclouds.profitbricks.rest.domain.Location;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.apache.jclouds.profitbricks.rest.ids.ServerRef;
import org.apache.jclouds.profitbricks.rest.ids.VolumeRef;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.util.Predicates2;
import static org.testng.Assert.assertTrue;

public class BaseProfitBricksLiveTest extends BaseApiLiveTest<ProfitBricksApi> {
   
   public static final Location TestLocation = Location.US_LASDEV;
   
   private Predicate<String> dataCenterAvailable;
   private Predicate<ServerRef> serverRunning;
   private Predicate<ServerRef> serverSuspended;
   private Predicate<ServerRef> serverAvailable;
   private Predicate<ServerRef> serverRemoved;
   private Predicate<VolumeRef> volumeAvailable;
   
   ComputeConstants computeConstants;
   
   public BaseProfitBricksLiveTest() {
      provider = "profitbricks-rest";      
   }
  
   @Override
   protected ProfitBricksApi create(Properties props, Iterable<Module> modules) {
      
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      
      computeConstants = injector.getInstance(ComputeConstants.class);
      
      dataCenterAvailable = injector.getInstance(
         Key.get(new TypeLiteral<Predicate<String>>() {}, Names.named(POLL_PREDICATE_DATACENTER))
      );
      
      serverRunning = injector.getInstance(
         Key.get(new TypeLiteral<Predicate<ServerRef>>() {}, Names.named(TIMEOUT_NODE_RUNNING))
      );
      
      serverSuspended = injector.getInstance(
         Key.get(new TypeLiteral<Predicate<ServerRef>>() {}, Names.named(TIMEOUT_NODE_SUSPENDED))
      );
   
      ComputeConstants c = computeConstants;
      
      Predicate<ServerRef> serverAvailableCheck = new Predicate<ServerRef>() {
         @Override
         public boolean apply(ServerRef serverRef) {
            Server server = api.serverApi().getServer(serverRef.dataCenterId(), serverRef.serverId());

            if (server == null || server.metadata().state() == null)
               return false;

            return server.metadata().state() == State.AVAILABLE;
         }
      };
      
      serverAvailable = Predicates2.retry(serverAvailableCheck, c.pollTimeout(), c.pollPeriod(), c.pollMaxPeriod(), TimeUnit.SECONDS);
      
      Predicate<ServerRef> serverRemovedPredicate = new Predicate<ServerRef>() {
         @Override
         public boolean apply(ServerRef serverRef) {
            return api.serverApi().getServer(serverRef.dataCenterId(), serverRef.serverId()) == null;
         }
      };
      
      serverRemoved = Predicates2.retry(serverRemovedPredicate, c.pollTimeout(), c.pollPeriod(), c.pollMaxPeriod(), TimeUnit.SECONDS);
      
      Predicate<VolumeRef> volumeAvailablePredicate = new Predicate<VolumeRef>() {
         @Override
         public boolean apply(VolumeRef volumeRef) {
            Volume volume = api.volumeApi().getVolume(volumeRef.dataCenterId(), volumeRef.volumeId());
            
            if (volume == null || volume.metadata() == null)
               return false;

            return volume.metadata().state() == State.AVAILABLE;
         }
      };
      
      volumeAvailable = Predicates2.retry(volumeAvailablePredicate, c.pollTimeout(), c.pollPeriod(), c.pollMaxPeriod(), TimeUnit.SECONDS);
              
      return injector.getInstance(ProfitBricksApi.class);
   }
   
   protected <T> void assertRandom(Predicate<T> check, T arguments) {
      ComputeConstants c = computeConstants;
      Predicate<T>checkPoll = Predicates2.retry(check, c.pollTimeout(), c.pollPeriod(), c.pollMaxPeriod(), TimeUnit.SECONDS);
      assertTrue(checkPoll.apply(arguments), "Random check failed in the configured timeout");
   }

   protected void assertDataCenterAvailable(DataCenter dataCenter) {
      assertDataCenterAvailable(dataCenter.id());
   }

   protected void assertDataCenterAvailable(String dataCenterId) {
      assertTrue(dataCenterAvailable.apply(dataCenterId),
              String.format("Datacenter %s wasn't available in the configured timeout", dataCenterId));
   }

   protected void assertNodeRunning(ServerRef serverRef) {
      assertTrue(serverRunning.apply(serverRef), String.format("Server %s did not start in the configured timeout", serverRef));
   }

   protected void assertNodeSuspended(ServerRef serverRef) {
      assertTrue(serverSuspended.apply(serverRef), String.format("Server %s did not stop in the configured timeout", serverRef));
   }
   
   protected void assertNodeRemoved(ServerRef serverRef) {
      assertTrue(serverRemoved.apply(serverRef), String.format("Server %s was not removed in the configured timeout", serverRef));
   }
   
   protected void assertNodeAvailable(ServerRef serverRef) {
      assertTrue(serverAvailable.apply(serverRef), String.format("Server %s is not available", serverRef));
   }
   
   protected void assertVolumeAvailable(VolumeRef volumeRef) {
      assertTrue(volumeAvailable.apply(volumeRef),
              String.format("Volume %s wasn't available in the configured timeout", volumeRef.volumeId()));
   }
   
   protected DataCenter createDataCenter() {
      return api.dataCenterApi().create("test-data-center", "example description", TestLocation.value());
   }
   
   protected void deleteDataCenter(String id) {
      api.dataCenterApi().delete(id);
   }
   
   protected Volume createVolume(DataCenter dataCenter) {
      return api.volumeApi().createVolume(
              Volume.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("jclouds-volume")
              .size(3)
              .licenceType(LicenceType.LINUX)
              .build());
   }
   
   protected String complexId(String ... ids) {
      return Joiner.on(",").join(ids);
   }

}
