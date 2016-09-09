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
package org.apache.jclouds.oneandone.rest.internal;

import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.jclouds.oneandone.rest.OneAndOneApi;
import org.apache.jclouds.oneandone.rest.OneAndOneProviderMetadata;
import org.apache.jclouds.oneandone.rest.config.OneAndOneConstants;
import org.apache.jclouds.oneandone.rest.config.OneAndOneProperties;
import org.apache.jclouds.oneandone.rest.domain.Hardware;
import org.apache.jclouds.oneandone.rest.domain.Hdd;
import org.apache.jclouds.oneandone.rest.domain.PrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.ids.ServerPrivateNetworkRef;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.util.Predicates2;
import static org.testng.Assert.assertTrue;

public class BaseOneAndOneLiveTest extends BaseApiLiveTest<OneAndOneApi> {

   Predicate<Server> waitUntilServerReady;
   Predicate<ServerPrivateNetworkRef> waitUntilPrivateNetworkReady;
   private static final OneAndOneProviderMetadata METADATA = new OneAndOneProviderMetadata();
   OneAndOneConstants constants;

   public BaseOneAndOneLiveTest() {
      provider = "oneandone";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, OneAndOneProperties.AUTH_TOKEN);
      return props;
   }

   @Override
   protected OneAndOneApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      constants = injector.getInstance(OneAndOneConstants.class);
      Predicate<Server> serverAvailableCheck = new Predicate<Server>() {
         @Override
         public boolean apply(Server currentServer) {
            Server server = api.serverApi().get(currentServer.id());

            if ((server.status().state() != Types.ServerState.POWERED_OFF
                    && server.status().state() != Types.ServerState.POWERED_ON)
                    || server.status().percent() != 0) {
               return false;
            } else {
               return true;
            }
         }
      };

      Predicate<ServerPrivateNetworkRef> privateNetworkAvailableCheck = new Predicate<ServerPrivateNetworkRef>() {
         @Override
         public boolean apply(ServerPrivateNetworkRef networkRef) {
            PrivateNetwork server = api.serverApi().getPrivateNetwork(networkRef.serverId(), networkRef.privateNetworkId());
            return server.state() != Types.GenericState.ACTIVE;
         }
      };
      waitUntilPrivateNetworkReady = Predicates2.retry(privateNetworkAvailableCheck, constants.pollTimeout(), constants.pollPeriod(), constants.pollMaxPeriod(), TimeUnit.SECONDS);
      waitUntilServerReady = Predicates2.retry(serverAvailableCheck, constants.pollTimeout(), constants.pollPeriod(), constants.pollMaxPeriod(), TimeUnit.SECONDS);
      return injector.getInstance(OneAndOneApi.class);
   }

   protected Server createServer(String serverName) {

      List<Hdd.CreateHdd> hdds = new ArrayList<Hdd.CreateHdd>();
      Hdd.CreateHdd hdd = Hdd.CreateHdd.create(30, Boolean.TRUE);
      hdds.add(hdd);
      Hardware.CreateHardware hardware = Hardware.CreateHardware.create(4.0, 1.0, 2.0, hdds);
      return api.serverApi().create(Server.CreateServer.builder()
              .name(serverName)
              .description("testing with jclouds")
              .hardware(hardware)
              .applianceId("81504C620D98BCEBAA5202D145203B4B")
              .dataCenterId("908DC2072407C94C8054610AD5A53B8C")
              .password("Test123!")
              .powerOn(Boolean.TRUE).build());
   }

   protected Server updateServerStatus(Server server) {
      assertNodeAvailable(server);
      return api.serverApi().get(server.id());

   }

   protected void assertNodeAvailable(Server server) {
      assertTrue(waitUntilServerReady.apply(server), String.format("Server %s is not Ready", server));
   }

   protected void assertPrivateNetworkAvailable(ServerPrivateNetworkRef ref) {
      assertTrue(waitUntilPrivateNetworkReady.apply(ref), String.format("ServerPrivateNetworkRef %s is not Ready", ref));
   }

   protected Server deleteServer(String serverId) {
      return api.serverApi().delete(serverId);
   }

   protected Server turnOnServer(String serverId) {
      return api.serverApi().updateStatus(serverId, Server.UpdateStatus.create(Types.ServerAction.POWER_ON, Types.ServerActionMethod.SOFTWARE));
   }
   protected Server turnOFFServer(String serverId) {
      return api.serverApi().updateStatus(serverId, Server.UpdateStatus.create(Types.ServerAction.POWER_OFF, Types.ServerActionMethod.SOFTWARE));
}
}
