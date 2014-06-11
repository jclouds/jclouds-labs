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
package org.jclouds.cloudsigma2.compute.functions;

import com.google.common.base.Function;
import org.jclouds.cloudsigma2.domain.IPConfiguration;
import org.jclouds.cloudsigma2.domain.IPConfigurationType;
import org.jclouds.cloudsigma2.domain.NIC;
import org.jclouds.cloudsigma2.domain.NICStats;

import javax.inject.Singleton;

@Singleton
public final class NICToAddress implements Function<NIC, String> {

   @Override
   public String apply(NIC nic) {
      IPConfiguration ipV4Configuration = nic.getIpV4Configuration();
      IPConfiguration ipV6Configuration = nic.getIpV6Configuration();
      if (ipV4Configuration != null) {
         if (ipV4Configuration.getIp() != null) {
            return ipV4Configuration.getIp().getUuid();
         } else if (ipV4Configuration.getConfigurationType().equals(IPConfigurationType.DHCP)) {
            NICStats runtime = nic.getRuntime();
            if (runtime != null && runtime.getIpV4() != null) {
               return runtime.getIpV4().getUuid();
            }
         }
      } else if (ipV6Configuration != null) {
         if (ipV6Configuration.getIp() != null) {
            return ipV6Configuration.getIp().getUuid();
         } else if (ipV6Configuration.getConfigurationType().equals(IPConfigurationType.DHCP)) {
            NICStats runtime = nic.getRuntime();
            if (runtime != null && runtime.getIpV6() != null) {
               return runtime.getIpV6().getUuid();
            }
         }
      }
      return null;
   }
}
