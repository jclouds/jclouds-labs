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
package org.jclouds.azurecompute.binders;

import static com.google.common.base.Throwables.propagate;

import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.azurecompute.domain.NetworkConfiguration.Subnet;
import org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkSite;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.jamesmurty.utils.XMLBuilder;

public class NetworkConfigurationToXML implements Binder {

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      NetworkConfiguration networkConfiguration = NetworkConfiguration.class.cast(input);

      try {
         XMLBuilder builder = XMLBuilder.create("NetworkConfiguration", "http://schemas.microsoft.com/ServiceHosting/2011/07/NetworkConfiguration")
                 .e("VirtualNetworkConfiguration");
         if (networkConfiguration.virtualNetworkConfiguration().dns() == null) {
            builder.e("Dns");
         } else {
            builder.e("Dns").t(networkConfiguration.virtualNetworkConfiguration().dns());
         }
         if (!networkConfiguration.virtualNetworkConfiguration().virtualNetworkSites().isEmpty()) {
            XMLBuilder virtualNetworkSitesBuilder = builder.e("VirtualNetworkSites");
            for (VirtualNetworkSite virtualNetworkSite : networkConfiguration.virtualNetworkConfiguration().virtualNetworkSites()) {
               XMLBuilder virtualNetworkSiteBuilder = virtualNetworkSitesBuilder.e("VirtualNetworkSite").a("name",
                       virtualNetworkSite.name()).a("Location", virtualNetworkSite.location());
               virtualNetworkSiteBuilder.e("AddressSpace")
                                        .e("AddressPrefix").t(virtualNetworkSite.addressSpace().addressPrefix()).up();
               XMLBuilder subnetBuilder = virtualNetworkSiteBuilder.e("Subnets");
                  for (Subnet subnet : virtualNetworkSite.subnets()) {
                     subnetBuilder.e("Subnet").a("name", subnet.name()).e("AddressPrefix").t(subnet.addressPrefix());
               }
            }
         }
         return (R) request.toBuilder().payload(builder.asString()).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }

}
