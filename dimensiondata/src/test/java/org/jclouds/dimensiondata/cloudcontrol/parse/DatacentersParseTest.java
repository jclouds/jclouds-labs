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
package org.jclouds.dimensiondata.cloudcontrol.parse;

import org.jclouds.dimensiondata.cloudcontrol.domain.Backup;
import org.jclouds.dimensiondata.cloudcontrol.domain.ConsoleAccess;
import org.jclouds.dimensiondata.cloudcontrol.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontrol.domain.Datacenters;
import org.jclouds.dimensiondata.cloudcontrol.domain.Hypervisor;
import org.jclouds.dimensiondata.cloudcontrol.domain.Monitoring;
import org.jclouds.dimensiondata.cloudcontrol.domain.Networking;
import org.jclouds.dimensiondata.cloudcontrol.domain.Property;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlParseTest;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

public class DatacentersParseTest extends BaseDimensionDataCloudControlParseTest<Datacenters> {

   @Override
   public String resource() {
      return "/datacenters.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Datacenters expected() {
      final List<Hypervisor.DiskSpeed> diskSpeeds = Collections.singletonList(
            Hypervisor.DiskSpeed.create("STANDARD", "Standard", "STD", "Standard Disk Speed", true, true));
      final List<Hypervisor.CpuSpeed> cpuSpeeds = Collections

            .singletonList(Hypervisor.CpuSpeed.create("STANDARD", "Standard", "Standard CPU Speed", true, true));
      final Hypervisor hypervisor = Hypervisor.builder().diskSpeeds(diskSpeeds).cpuSpeeds(cpuSpeeds)
            .properties(Collections.singletonList(Property.create("MIN_DISK_SIZE_GB", "10")))
            .maintenanceStatus("NORMAL").type("VMWARE").build();

      final Datacenter datacenter = Datacenter.builder().displayName("US - West").city("Santa Clara")
            .state("California").country("US").vpnUrl("https://na3.cloud-vpn.net").ftpsHost("ftps-na.cloud-vpn.net")
            .networking(Networking.builder()
                  .properties(Collections.singletonList(Property.create("MAX_SERVER_TO_VIP_CONNECTIONS", "20")))
                  .type("1").maintenanceStatus("NORMAL").build()).hypervisor(hypervisor).backup(
                  Backup.builder().maintenanceStatus("NORMAL").type("COMMVAULT")
                        .properties(Collections.<Property>emptyList()).build()).consoleAccess(
                  ConsoleAccess.builder().properties(Collections.<Property>emptyList()).maintenanceStatus("NORMAL")
                        .build()).monitoring(
                  Monitoring.builder().maintenanceStatus("NORMAL").properties(Collections.<Property>emptyList())
                        .build()).type("MCP 1.0").id("NA3").build();

      return new Datacenters(Collections.singletonList(datacenter), 1, 1, 1, 250);
   }
}
