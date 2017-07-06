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
package org.jclouds.dimensiondata.cloudcontrol.utils;

import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.features.NetworkApi;
import org.jclouds.dimensiondata.cloudcontrol.features.ServerApi;
import org.jclouds.dimensiondata.cloudcontrol.predicates.NetworkDomainState;
import org.jclouds.dimensiondata.cloudcontrol.predicates.ServerState;
import org.jclouds.dimensiondata.cloudcontrol.predicates.ServerStatus;
import org.jclouds.dimensiondata.cloudcontrol.predicates.VMToolsRunningStatus;
import org.jclouds.dimensiondata.cloudcontrol.predicates.VlanState;

import static org.jclouds.util.Predicates2.retry;

public class DimensionDataCloudControlResponseUtils {

   private static String convertServerId(String serverId) {
      return serverId.replaceAll("-", "_");
   }

   public static String generateFirewallRuleName(String serverId) {
      return String.format("fw.%s", convertServerId(serverId));
   }

   public static void waitForNetworkDomainState(NetworkApi api, String networkDomainId, State state, long timeoutMillis,
         String message) {
      boolean isNetworkDomainInState = retry(new NetworkDomainState(api, state), timeoutMillis).apply(networkDomainId);
      if (!isNetworkDomainInState) {
         throw new IllegalStateException(message);
      }
   }

   public static void waitForVlanState(NetworkApi api, String vlanId, State state, long timeoutMillis, String message) {
      boolean isVlanInState = retry(new VlanState(api, state), timeoutMillis).apply(vlanId);
      if (!isVlanInState) {
         throw new IllegalStateException(message);
      }
   }

   public static void waitForServerState(ServerApi api, String serverId, State state, long timeoutMillis,
         String message) {
      boolean isServerInState = retry(new ServerState(api, state), timeoutMillis).apply(serverId);
      if (!isServerInState) {
         throw new IllegalStateException(message);
      }
   }

   public static void waitForServerStatus(ServerApi api, String serverId, boolean started, boolean deployed,
         long timeoutMillis, String message) {
      boolean serverHasStatus = retry(new ServerStatus(api, started, deployed), timeoutMillis).apply(serverId);
      if (!serverHasStatus) {
         throw new IllegalStateException(message);
      }
   }

   public static void waitForVmToolsRunning(ServerApi api, String serverId, long timeoutMillis, String message) {
      boolean vmwareToolsRunning = retry(new VMToolsRunningStatus(api), timeoutMillis).apply(serverId);
      if (!vmwareToolsRunning) {
         throw new IllegalStateException(message);
      }
   }
}
