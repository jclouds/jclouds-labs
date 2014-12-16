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

import org.jclouds.azurecompute.domain.Role;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.jamesmurty.utils.XMLBuilder;

public class RoleToXML implements Binder {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      Role role = Role.class.cast(input);

      try {
         XMLBuilder builder = XMLBuilder.create("PersistentVMRole", "http://schemas.microsoft.com/windowsazure")
                 .e("RoleName").t(role.roleName()).up()
                 .e("RoleType").t(role.roleType()).up()
                 .e("ConfigurationSets");

         if (!role.configurationSets().isEmpty()) {
            for (Role.ConfigurationSet configurationSet : role.configurationSets()) {
               XMLBuilder configBuilder = builder.e("ConfigurationSet"); // Network
               configBuilder.e("ConfigurationSetType").t(configurationSet.configurationSetType()).up();

               XMLBuilder inputEndpoints = configBuilder.e("InputEndpoints");
               for (Role.ConfigurationSet.InputEndpoint endpoint : configurationSet.inputEndpoints()) {
                  XMLBuilder inputBuilder = inputEndpoints.e("InputEndpoint");
                  inputBuilder.e("LocalPort").t(Integer.toString(endpoint.localPort())).up()
                          .e("Name").t(endpoint.name()).up()
                          .e("Port").t(Integer.toString(endpoint.port())).up()
                          .e("Protocol").t(endpoint.protocol().toLowerCase()).up()
                          .up(); //InputEndpoint
               }
               XMLBuilder subnetNames = configBuilder.e("SubnetNames");
               for (Role.ConfigurationSet.SubnetName subnetName : configurationSet.subnetNames()) {
                  subnetNames.e("SubnetName").t(subnetName.name()).up();
               }
               configBuilder.e("NetworkSecurityGroup").t(configurationSet.networkSecurityGroup()).up();
            }
         }
         builder.e("DataVirtualHardDisks").up()
                .e("OSVirtualHardDisk")
                .e("HostCaching").t(role.osVirtualHardDisk().hostCaching()).up()
                .e("DiskName").t(role.osVirtualHardDisk().diskName()).up()
                .e("MediaLink").t(role.osVirtualHardDisk().mediaLink().toString()).up()
                .e("SourceImageName").t(role.osVirtualHardDisk().sourceImageName()).up()
                .e("OS").t(role.osVirtualHardDisk().os().toString()).up()
                .up() // DataVirtualHardDisks
                .e("RoleSize").t(role.roleSize().getText());
         return (R) request.toBuilder().payload(builder.asString()).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }

}
