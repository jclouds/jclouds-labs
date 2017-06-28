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
import org.jclouds.dimensiondata.cloudcontrol.predicates.NetworkDomainStatus;
import org.jclouds.dimensiondata.cloudcontrol.predicates.VlanStatus;

import static org.jclouds.util.Predicates2.retry;

public class DimensionDataCloudControlResponseUtils {

   private static String convertServerId(String serverId) {
      return serverId.replaceAll("-", "_");
   }

   public static String generateFirewallRuleName(String serverId) {
      return String.format("fw.%s", convertServerId(serverId));
   }

   public static void waitForNetworkDomainStatus(NetworkApi api, String networkDomainId, State state,
         long timeoutMillis, String message) {
      boolean isNetworkDomainInState = retry(new NetworkDomainStatus(api, state), timeoutMillis).apply(networkDomainId);
      if (!isNetworkDomainInState) {
         throw new IllegalStateException(message);
      }
   }

   public static void waitForVlanStatus(NetworkApi api, String vlanId, State state, long timeoutMillis,
         String message) {
      boolean isVlanInstate = retry(new VlanStatus(api, state), timeoutMillis).apply(vlanId);
      if (!isVlanInstate) {
         throw new IllegalStateException(message);
      }
   }

}
