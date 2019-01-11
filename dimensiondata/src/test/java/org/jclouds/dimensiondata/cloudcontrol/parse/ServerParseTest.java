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

import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.Guest;
import org.jclouds.dimensiondata.cloudcontrol.domain.NIC;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.domain.VirtualHardware;
import org.jclouds.dimensiondata.cloudcontrol.domain.VmTools;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.Collections;

@Test(groups = "unit")
public class ServerParseTest extends BaseDimensionDataCloudControlParseTest<Server> {

   @Override
   public String resource() {
      return "/server.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Server expected() {

      return Server.builder().id("cb08c7ba-7a51-4e32-8d39-05d2270f8f8b").name("ServerApiLiveTest").datacenterId("EU6")
            .state(State.NORMAL).sourceImageId("56eb0b7c-15a7-4b63-b373-05b962e37554")
            .createTime(parseDate("2017-07-03T16:29:33.000Z")).started(true).deployed(true).guest(Guest.builder()
                  .operatingSystem(
                        OperatingSystem.builder().id("REDHAT764").displayName("REDHAT7/64").family("UNIX").build())
                  .vmTools(VmTools.builder().apiVersion(9356).type(VmTools.Type.VMWARE_TOOLS)
                        .versionStatus(VmTools.VersionStatus.CURRENT).runningStatus(VmTools.RunningStatus.NOT_RUNNING)
                        .build()).osCustomization(true).build())
            .cpu(CPU.builder().count(2).speed("STANDARD").coresPerSocket(1).build()).memoryGb(4).disks(Collections
                  .singletonList(
                        Disk.builder().id("918f12ba-5e5e-4cd6-87bd-60c18293c24d").scsiId(0).sizeGb(20).speed("STANDARD")
                              .state(State.NORMAL).build())).networkInfo(NetworkInfo.builder().primaryNic(
                  NIC.builder().id("f0c00cab-bfa3-4c51-8c0a-c52fdac1ae4b").privateIpv4("10.0.0.7")
                        .ipv6("2a00:47c0:111:1131:5851:1950:411c:3dd8").vlanId("7bd12a4d-4e83-4254-a266-174aa5f55187")
                        .vlanName("jclouds vlan").state(State.NORMAL).build()).additionalNic(Collections.<NIC>emptyList())
                  .networkDomainId("d122949b-8990-46d6-98f0-91c8676fc720").build())
            .virtualHardware(VirtualHardware.builder().upToDate(true).version("vmx-10").build())
            .softwareLabels(Collections.emptyList()).build();
   }
}
