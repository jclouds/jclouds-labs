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
package org.apache.jclouds.oneandone.rest.features;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.jclouds.oneandone.rest.domain.MonitoringCenter;
import org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericDateQueryOptions;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "MonitoringCenterApiLiveTest")
public class MonitoringCenterApiLiveTest extends BaseOneAndOneLiveTest {

   private Server currentServer;
   private MonitoringPolicy currentPolicy;

   private MonitoringCenterApi monitoringCenterApi() {

      return api.monitoringCenterApi();
   }

   @BeforeClass
   public void setupTest() throws InterruptedException {
      Random rand = new Random();
      int randomName = rand.nextInt(100);

      currentServer = createServer("Monitoring Center jclouds server" + randomName);
      assertNodeAvailable(currentServer);

      List<MonitoringPolicy.Port.AddPort> ports = new ArrayList<MonitoringPolicy.Port.AddPort>();
      MonitoringPolicy.Port.AddPort port = MonitoringPolicy.Port.AddPort.create(80, Types.AlertIfType.RESPONDING, true, Types.ProtocolType.TCP);
      ports.add(port);

      List<org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy.Process.AddProcess> processes = new ArrayList<org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy.Process.AddProcess>();
      org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy.Process.AddProcess process = org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy.Process.AddProcess.create("", Types.AlertIfType.RESPONDING, true);
      processes.add(process);

      MonitoringPolicy.Threshold.Cpu.Warning warning = MonitoringPolicy.Threshold.Cpu.Warning.create(90, true);
      MonitoringPolicy.Threshold.Cpu.Critical critical = MonitoringPolicy.Threshold.Cpu.Critical.create(95, true);
      MonitoringPolicy.Threshold.Cpu cpu = MonitoringPolicy.Threshold.Cpu.create(warning, critical);

      MonitoringPolicy.Threshold.Ram.Warning ramWarning = MonitoringPolicy.Threshold.Ram.Warning.create(90, true);
      MonitoringPolicy.Threshold.Ram.Critical ramCritical = MonitoringPolicy.Threshold.Ram.Critical.create(95, true);
      MonitoringPolicy.Threshold.Ram ram = MonitoringPolicy.Threshold.Ram.create(ramWarning, ramCritical);

      MonitoringPolicy.Threshold.Disk.Warning diskWarning = MonitoringPolicy.Threshold.Disk.Warning.create(90, true);
      MonitoringPolicy.Threshold.Disk.Critical diskCritical = MonitoringPolicy.Threshold.Disk.Critical.create(95, true);
      MonitoringPolicy.Threshold.Disk disk = MonitoringPolicy.Threshold.Disk.create(diskWarning, diskCritical);

      MonitoringPolicy.Threshold.InternalPing.Warning pingWarning = MonitoringPolicy.Threshold.InternalPing.Warning.create(50, true);
      MonitoringPolicy.Threshold.InternalPing.Critical pingCritical = MonitoringPolicy.Threshold.InternalPing.Critical.create(100, true);
      MonitoringPolicy.Threshold.InternalPing ping = MonitoringPolicy.Threshold.InternalPing.create(pingWarning, pingCritical);

      MonitoringPolicy.Threshold.Transfer.Warning tranWarning = MonitoringPolicy.Threshold.Transfer.Warning.create(1000, true);
      MonitoringPolicy.Threshold.Transfer.Critical tranCritical = MonitoringPolicy.Threshold.Transfer.Critical.create(2000, true);
      MonitoringPolicy.Threshold.Transfer transfer = MonitoringPolicy.Threshold.Transfer.create(tranWarning, tranCritical);

      MonitoringPolicy.Threshold threshold = MonitoringPolicy.Threshold.create(cpu, ram, disk, transfer, ping);
      MonitoringPolicy.CreatePolicy payload = MonitoringPolicy.CreatePolicy.builder()
              .name("jclouds policy" + randomName)
              .agent(true)
              .email("j@clouds.com")
              .ports(ports)
              .processes(processes)
              .description("dsec")
              .thresholds(threshold)
              .build();
      currentPolicy = api.monitoringPolicyApi().create(payload);

      List<String> servers = new ArrayList<String>();
      servers.add(currentServer.id());
      MonitoringPolicy response = api.monitoringPolicyApi().attachServer(currentPolicy.id(), MonitoringPolicy.Server.CreateServer.create(servers));
      assertNotNull(response);
   }

   @Test
   public void testList() {
      List<MonitoringCenter> result = monitoringCenterApi().list();

      assertNotNull(result);
      Assert.assertTrue(result.size() >= 1);
   }

   @Test
   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(1, 1, null, null, null);
      List<MonitoringCenter> resultWithQuery = monitoringCenterApi().list(options);

      assertNotNull(resultWithQuery);
      Assert.assertTrue(resultWithQuery.size() >= 1);
   }

   @Test
   public void testGetCustomPeriod() {
      GenericDateQueryOptions options = new GenericDateQueryOptions();
      Calendar cal = Calendar.getInstance();
      Date end = cal.getTime();
      cal.add(Calendar.DATE, -2);
      Date start = cal.getTime();
      options.customPeriod(start, end);
      MonitoringCenter result = monitoringCenterApi().get(currentServer.id(), options);
      assertNotNull(result);
   }

   @Test
   public void testGetFixedPeriod() {
      GenericDateQueryOptions options = new GenericDateQueryOptions();
      options.fixedPeriods(Types.PeriodType.LAST_7D);
      MonitoringCenter result = monitoringCenterApi().get(currentServer.id(), options);
      assertNotNull(result);
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      deleteServer(currentServer.id());
   }
}
