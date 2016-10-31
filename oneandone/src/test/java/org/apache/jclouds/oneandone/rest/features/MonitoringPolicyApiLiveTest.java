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
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "MonitoringPolicyApiLiveTest")
public class MonitoringPolicyApiLiveTest extends BaseOneAndOneLiveTest {

   private MonitoringPolicy currentPolicy;
   private MonitoringPolicy.Port currentPort;
   private MonitoringPolicy.Process currentProcess;
   private List<MonitoringPolicy> policies;
   private Server currentServer;

   private MonitoringPolicyApi monitoringPolicyApi() {

      return api.monitoringPolicyApi();
   }

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("Monitoring Policy jclouds server");
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
              .name("jclouds policy")
              .agent(true)
              .email("j@clouds.com")
              .ports(ports)
              .processes(processes)
              .description("dsec")
              .thresholds(threshold)
              .build();
      currentPolicy = monitoringPolicyApi().create(payload);
   }

   @Test
   public void testList() {
      policies = monitoringPolicyApi().list();

      assertNotNull(policies);
      Assert.assertTrue(policies.size() > 0);
   }

   @Test
   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "jclouds", null);
      List<MonitoringPolicy> resultWithQuery = monitoringPolicyApi().list(options);

      assertNotNull(resultWithQuery);
      Assert.assertTrue(resultWithQuery.size() > 0);
   }

   @Test
   public void testGet() {
      MonitoringPolicy result = monitoringPolicyApi().get(currentPolicy.id());

      assertNotNull(result);
      assertEquals(result.id(), currentPolicy.id());
   }

   @Test(dependsOnMethods = "testGet")
   public void testUpdate() throws InterruptedException {
      String updatedName = "updatejclouds PN";
      MonitoringPolicy.Threshold.Cpu.Warning warning = MonitoringPolicy.Threshold.Cpu.Warning.create(91, true);
      MonitoringPolicy.Threshold.Cpu.Critical critical = MonitoringPolicy.Threshold.Cpu.Critical.create(96, true);
      MonitoringPolicy.Threshold.Cpu cpu = MonitoringPolicy.Threshold.Cpu.create(warning, critical);

      MonitoringPolicy.Threshold.Ram.Warning ramWarning = MonitoringPolicy.Threshold.Ram.Warning.create(91, true);
      MonitoringPolicy.Threshold.Ram.Critical ramCritical = MonitoringPolicy.Threshold.Ram.Critical.create(96, true);
      MonitoringPolicy.Threshold.Ram ram = MonitoringPolicy.Threshold.Ram.create(ramWarning, ramCritical);

      MonitoringPolicy.Threshold.Disk.Warning diskWarning = MonitoringPolicy.Threshold.Disk.Warning.create(90, true);
      MonitoringPolicy.Threshold.Disk.Critical diskCritical = MonitoringPolicy.Threshold.Disk.Critical.create(95, true);
      MonitoringPolicy.Threshold.Disk disk = MonitoringPolicy.Threshold.Disk.create(diskWarning, diskCritical);

      MonitoringPolicy.Threshold.InternalPing.Warning pingWarning = MonitoringPolicy.Threshold.InternalPing.Warning.create(50, true);
      MonitoringPolicy.Threshold.InternalPing.Critical pingCritical = MonitoringPolicy.Threshold.InternalPing.Critical.create(100, true);
      MonitoringPolicy.Threshold.InternalPing ping = MonitoringPolicy.Threshold.InternalPing.create(pingWarning, pingCritical);

      MonitoringPolicy.Threshold.Transfer.Warning tranWarning = MonitoringPolicy.Threshold.Transfer.Warning.create(1100, true);
      MonitoringPolicy.Threshold.Transfer.Critical tranCritical = MonitoringPolicy.Threshold.Transfer.Critical.create(2100, true);
      MonitoringPolicy.Threshold.Transfer transfer = MonitoringPolicy.Threshold.Transfer.create(tranWarning, tranCritical);

      MonitoringPolicy.Threshold threshold = MonitoringPolicy.Threshold.create(cpu, ram, disk, transfer, ping);
      MonitoringPolicy.UpdatePolicy payload = MonitoringPolicy.UpdatePolicy.builder()
              .name(updatedName)
              .agent(true)
              .email("j@jtestclouds.com")
              .description("dsec")
              .thresholds(threshold)
              .build();

      MonitoringPolicy updateResult = monitoringPolicyApi().update(currentPolicy.id(), payload);

      assertNotNull(updateResult);
      assertEquals(updateResult.name(), updatedName);

   }

   @Test(dependsOnMethods = "testUpdate")
   public void testAddPort() throws InterruptedException {
      List<MonitoringPolicy.Port.AddPort> ports = new ArrayList<MonitoringPolicy.Port.AddPort>();
      MonitoringPolicy.Port.AddPort port = MonitoringPolicy.Port.AddPort.create(80, Types.AlertIfType.RESPONDING, true, Types.ProtocolType.TCP);
      ports.add(port);
      MonitoringPolicy response = monitoringPolicyApi().addPort(currentPolicy.id(), MonitoringPolicy.Port.CreatePort.create(ports));
      
      assertNotNull(response);
      assertNotNull(response.ports().get(0));
      currentPort = response.ports().get(0);
   }

   @Test(dependsOnMethods = "testAddPort")
   public void testListPorts() throws InterruptedException {
      List<MonitoringPolicy.Port> result = monitoringPolicyApi().listPorts(currentPolicy.id());

      assertNotNull(result);
      assertEquals(result.size(), 1);
   }

   @Test(dependsOnMethods = "testUpdatePort")
   public void testGetPort() throws InterruptedException {
      MonitoringPolicy.Port result = monitoringPolicyApi().getPort(currentPolicy.id(), currentPort.id());
      assertNotNull(result);
   }

   @Test(dependsOnMethods = "testListPorts")
   public void testUpdatePort() throws InterruptedException {

      MonitoringPolicy.Port.AddPort port = MonitoringPolicy.Port.AddPort.create(80, Types.AlertIfType.RESPONDING, true, Types.ProtocolType.TCP);

      MonitoringPolicy response = monitoringPolicyApi().updatePort(currentPolicy.id(), currentPort.id(), MonitoringPolicy.Port.UpdatePort.create(port));
      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testGetPort")
   public void testDeletePort() throws InterruptedException {
      MonitoringPolicy response = monitoringPolicyApi().deletePort(currentPolicy.id(), currentPort.id());

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testDeletePort")
   public void testAddProcess() throws InterruptedException {
      List<MonitoringPolicy.Process.AddProcess> processes = new ArrayList<MonitoringPolicy.Process.AddProcess>();
      MonitoringPolicy.Process.AddProcess process = MonitoringPolicy.Process.AddProcess.create("process", Types.AlertIfType.RESPONDING, true);
      processes.add(process);
      MonitoringPolicy response = monitoringPolicyApi().addProcess(currentPolicy.id(), MonitoringPolicy.Process.CreateProcess.create(processes));
      currentProcess = response.processes().get(0);

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testAddProcess")
   public void testListProcesses() throws InterruptedException {
      List<MonitoringPolicy.Process> result = monitoringPolicyApi().listProcesses(currentPolicy.id());

      assertNotNull(result);
      assertEquals(result.size(), 2);
   }

   @Test(dependsOnMethods = "testUpdateProcess")
   public void testGetProcess() throws InterruptedException {
      MonitoringPolicy.Process result = monitoringPolicyApi().getProcess(currentPolicy.id(), currentProcess.id());
      assertNotNull(result);
   }

   @Test(dependsOnMethods = "testListProcesses")
   public void testUpdateProcess() throws InterruptedException {

      MonitoringPolicy.Process.AddProcess process = MonitoringPolicy.Process.AddProcess.create("process", Types.AlertIfType.RESPONDING, true);
      MonitoringPolicy response = monitoringPolicyApi().updateProcess(currentPolicy.id(), currentProcess.id(), MonitoringPolicy.Process.UpdateProcess.create(process));
      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testGetProcess")
   public void testDeleteProcess() throws InterruptedException {
      MonitoringPolicy response = monitoringPolicyApi().deleteProcess(currentPolicy.id(), currentProcess.id());

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testAttachServer")
   public void testListServers() throws InterruptedException {
      List<MonitoringPolicy.Server> servers = monitoringPolicyApi().listServers(currentPolicy.id());

      assertNotNull(servers);
      Assert.assertTrue(servers.size() > 0);
   }

   @Test(dependsOnMethods = "testListServers")
   public void testGetServer() throws InterruptedException {
      MonitoringPolicy.Server result = monitoringPolicyApi().getServer(currentPolicy.id(), currentServer.id());

      assertNotNull(result);
   }

   @Test(dependsOnMethods = "testDeleteProcess")
   public void testAttachServer() throws InterruptedException {
      List<String> servers = new ArrayList<String>();
      servers.add(currentServer.id());

      MonitoringPolicy response = monitoringPolicyApi().attachServer(currentPolicy.id(), MonitoringPolicy.Server.CreateServer.create(servers));
      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testGetServer")
   public void testDetachServer() throws InterruptedException {
      MonitoringPolicy response = monitoringPolicyApi().detachServer(currentPolicy.id(), currentServer.id());

      assertNotNull(response);
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      if (currentServer != null) {
         assertNodeAvailable(currentServer);
         deleteServer(currentServer.id());
      }
      if (currentPolicy != null) {
         monitoringPolicyApi().delete(currentPolicy.id());
      }
   }

}
