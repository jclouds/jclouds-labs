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

import com.google.common.collect.ImmutableList;
import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.Guest;
import org.jclouds.dimensiondata.cloudcontrol.domain.NIC;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.Servers;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.domain.VirtualHardware;
import org.jclouds.dimensiondata.cloudcontrol.domain.VmTools;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

@Test(groups = "unit")
public class ServersParseTest extends BaseDimensionDataCloudControlParseTest<Servers> {

   @Override
   public String resource() {
      return "/servers.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Servers expected() {
      List<Server> servers = ImmutableList
            .of(Server.builder().id("b8246ba4-847d-475b-b296-f76787a69ca8").name("parser-test-server-name")
                  .description("parser-test-server-description").datacenterId("NA9").state(State.NORMAL)
                  .sourceImageId("1e44ab3f-2426-45ec-a1b5-827b2ce58836")
                  .createTime(parseDate("2016-03-10T13:05:21.000Z")).started(true).deployed(true).guest(Guest.builder()
                        .operatingSystem(
                              OperatingSystem.builder().id("CENTOS564").displayName("CENTOS5/64").family("UNIX")
                                    .build()).vmTools(VmTools.builder().apiVersion(9354).type(VmTools.Type.VMWARE_TOOLS)
                              .versionStatus(VmTools.VersionStatus.NEED_UPGRADE)
                              .runningStatus(VmTools.RunningStatus.RUNNING).build()).osCustomization(true).build())
                  .cpu(CPU.builder().count(2).speed("STANDARD").coresPerSocket(1).build()).memoryGb(4).disks(Collections
                        .singletonList(Disk.builder().id("0ba67812-d7b7-4c3f-b114-870fbea24d42").scsiId(0).sizeGb(10)
                              .speed("STANDARD").state("NORMAL").build())).networkInfo(NetworkInfo.builder().primaryNic(
                        NIC.builder().id("980a9fdd-4ea2-478b-85b4-f016349f1738").privateIpv4("10.0.0.8")
                              .ipv6("2607:f480:111:1575:c47:7479:2af8:3f1a")
                              .vlanId("6b25b02e-d3a2-4e69-8ca7-9bab605deebd")
                              .vlanId("6b25b02e-d3a2-4e69-8ca7-9bab605deebd").vlanName("vlan1").state("NORMAL").build())
                        .additionalNic(null).networkDomainId("690de302-bb80-49c6-b401-8c02bbefb945").build())
                  .virtualHardware(VirtualHardware.builder().upToDate(false).version("vmx-08").build())
                  .softwareLabels(Collections.emptyList()).build());
      return new Servers(servers, 1, 5, 5, 250);
   }
}
