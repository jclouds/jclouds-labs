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
package org.apache.jclouds.oneandone.rest;

import java.io.Closeable;
import org.apache.jclouds.oneandone.rest.features.DataCenterApi;
import org.apache.jclouds.oneandone.rest.features.FirewallPolicyApi;
import org.apache.jclouds.oneandone.rest.features.ImageApi;
import org.apache.jclouds.oneandone.rest.features.LoadBalancerApi;
import org.apache.jclouds.oneandone.rest.features.MonitoringCenterApi;
import org.apache.jclouds.oneandone.rest.features.MonitoringPolicyApi;
import org.apache.jclouds.oneandone.rest.features.PrivateNetworkApi;
import org.apache.jclouds.oneandone.rest.features.PublicIpApi;
import org.apache.jclouds.oneandone.rest.features.ServerApi;
import org.apache.jclouds.oneandone.rest.features.ServerApplianceApi;
import org.apache.jclouds.oneandone.rest.features.SharedStorageApi;
import org.apache.jclouds.oneandone.rest.features.VpnApi;
import org.jclouds.rest.annotations.Delegate;

public interface OneAndOneApi extends Closeable {

   @Delegate
   ServerApi serverApi();

   @Delegate
   ImageApi imageApi();

   @Delegate
   SharedStorageApi sharedStorageApi();

   @Delegate
   FirewallPolicyApi firewallPolicyApi();

   @Delegate
   LoadBalancerApi loadBalancerApi();

   @Delegate
   PublicIpApi publicIpApi();

   @Delegate
   PrivateNetworkApi privateNetworkApi();

   @Delegate
   VpnApi vpnApi();

   @Delegate
   MonitoringCenterApi monitoringCenterApi();

   @Delegate
   MonitoringPolicyApi monitoringPolicyApi();

   @Delegate
   DataCenterApi dataCenterApi();

   @Delegate
   ServerApplianceApi serverApplianceApi();
}
