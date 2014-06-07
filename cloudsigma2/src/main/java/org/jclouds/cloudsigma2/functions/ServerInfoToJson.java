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
package org.jclouds.cloudsigma2.functions;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jclouds.cloudsigma2.domain.IPConfiguration;
import org.jclouds.cloudsigma2.domain.NIC;
import org.jclouds.cloudsigma2.domain.ServerDrive;
import org.jclouds.cloudsigma2.domain.ServerInfo;

import javax.inject.Singleton;

@Singleton
public class ServerInfoToJson implements Function<ServerInfo, JsonObject> {
   @Override
   public JsonObject apply(ServerInfo input) {
      JsonObject serverObject = new JsonObject();

      if (input.getName() != null) {
         serverObject.addProperty("name", input.getName());
      }

      if (input.getCpu() > 0) {
         serverObject.addProperty("cpu", input.getCpu());
      }

      if (input.getMemory() != null) {
         serverObject.addProperty("mem", input.getMemory().toString());
      }

      if (input.getMeta() != null) {
         serverObject.add("meta", new JsonParser().parse(new Gson().toJson(input.getMeta())));
      }

      if (input.getRequirements() != null) {
         serverObject.add("requirements", new JsonParser().parse(new Gson().toJson(input.getRequirements())));
      }

      if (input.getTags() != null) {
         serverObject.add("tags", new JsonParser().parse(new Gson().toJson(input.getTags())));
      }

      if (input.getVncPassword() != null) {
         serverObject.addProperty("vnc_password", input.getVncPassword());
      }

      if (input.getNics() != null) {
         JsonArray nics = new JsonArray();

         for (NIC nic : input.getNics()) {
            JsonObject nicObject = new JsonObject();

            if (nic.getFirewallPolicy() != null) {
               nicObject.addProperty("firewall_policy", nic.getFirewallPolicy().getUuid());
            }

            if (nic.getVlan() != null) {
               nicObject.addProperty("vlan", nic.getVlan().getUuid());
            } else if (nic.getIpV4Configuration() != null) {
               nicObject.add("ip_v4_conf", ipConfigurationToJsonObject(nic.getIpV4Configuration()));

               if (nic.getModel() != null) {
                  nicObject.addProperty("model", nic.getModel().value());
               }
               if (nic.getMac() != null) {
                  nicObject.addProperty("mac", nic.getMac());
               }
            } else if (nic.getIpV6Configuration() != null) {
               nicObject.add("ip_v6_conf", ipConfigurationToJsonObject(nic.getIpV6Configuration()));

               if (nic.getModel() != null) {
                  nicObject.addProperty("model", nic.getModel().value());
               }
               if (nic.getMac() != null) {
                  nicObject.addProperty("mac", nic.getMac());
               }
            }

            nics.add(nicObject);
         }

         serverObject.add("nics", nics);
      }

      if (input.getDrives() != null) {
         JsonArray serverDrives = new JsonArray();

         for (ServerDrive serverDrive : input.getDrives()) {
            JsonObject driveObject = new JsonObject();
            driveObject.addProperty("boot_order", serverDrive.getBootOrder());

            if (serverDrive.getDeviceChannel() != null) {
               driveObject.addProperty("dev_channel", serverDrive.getDeviceChannel());
            }

            if (serverDrive.getDeviceEmulationType() != null) {
               driveObject.addProperty("device", serverDrive.getDeviceEmulationType().value());
            }

            if (serverDrive.getDriveUuid() != null) {
               driveObject.addProperty("drive", serverDrive.getDriveUuid());
            } else if (serverDrive.getDrive() != null) {
               driveObject.addProperty("drive", serverDrive.getDrive().getUuid());
            }

            serverDrives.add(driveObject);
         }
         serverObject.add("drives", serverDrives);
      }

      return serverObject;
   }

   private JsonObject ipConfigurationToJsonObject(IPConfiguration ipConfiguration) {
      JsonObject ipConfObject = new JsonObject();
      if (ipConfiguration.getConfigurationType() != null) {
         ipConfObject.addProperty("conf", ipConfiguration.getConfigurationType().value());
      }
      if (ipConfiguration.getIp() != null) {
         ipConfObject.addProperty("ip", ipConfiguration.getIp().getUuid());
      }
      return ipConfObject;
   }
}
