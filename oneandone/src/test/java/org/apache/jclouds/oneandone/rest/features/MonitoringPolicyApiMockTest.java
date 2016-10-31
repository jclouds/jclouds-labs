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

import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.ArrayList;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "MonitoringPolicyApiMockTest", singleThreaded = true)
public class MonitoringPolicyApiMockTest extends BaseOneAndOneApiMockTest {

   private MonitoringPolicyApi monitoringPolicyApi() {
      return api.monitoringPolicyApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/list.json"))
      );

      List<MonitoringPolicy> result = monitoringPolicyApi().list();

      assertEquals(result.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<MonitoringPolicy> result = monitoringPolicyApi().list();

      assertEquals(result.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<MonitoringPolicy> result = monitoringPolicyApi().list(options);

      assertEquals(result.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<MonitoringPolicy> result = monitoringPolicyApi().list(options);

      assertEquals(result.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies?q=New");
   }

   @Test
   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );
      MonitoringPolicy result = monitoringPolicyApi().get("monitoringpolicyId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/monitoringpolicyId");
   }

   @Test
   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      MonitoringPolicy result = monitoringPolicyApi().get("monitoringpolicyId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/monitoringpolicyId");
   }

   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );
      List<MonitoringPolicy.Port.AddPort> ports = new ArrayList<MonitoringPolicy.Port.AddPort>();
      MonitoringPolicy.Port.AddPort port = MonitoringPolicy.Port.AddPort.create(80, Types.AlertIfType.RESPONDING, true, Types.ProtocolType.TCP);
      ports.add(port);
      List<org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy.Process.AddProcess> processes = new ArrayList<org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy.Process.AddProcess>();
      org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy.Process.AddProcess process = org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy.Process.AddProcess.create("", Types.AlertIfType.RESPONDING, true);
      processes.add(process);

      MonitoringPolicy.Threshold.Cpu.Warning warning = MonitoringPolicy.Threshold.Cpu.Warning.create(90, true);
      MonitoringPolicy.Threshold.Cpu.Critical critical = MonitoringPolicy.Threshold.Cpu.Critical.create(90, true);
      MonitoringPolicy.Threshold.Cpu cpu = MonitoringPolicy.Threshold.Cpu.create(warning, critical);

      MonitoringPolicy.Threshold.Ram.Warning ramWarning = MonitoringPolicy.Threshold.Ram.Warning.create(90, true);
      MonitoringPolicy.Threshold.Ram.Critical ramCritical = MonitoringPolicy.Threshold.Ram.Critical.create(90, true);
      MonitoringPolicy.Threshold.Ram ram = MonitoringPolicy.Threshold.Ram.create(ramWarning, ramCritical);

      MonitoringPolicy.Threshold.Disk.Warning diskWarning = MonitoringPolicy.Threshold.Disk.Warning.create(90, true);
      MonitoringPolicy.Threshold.Disk.Critical diskCritical = MonitoringPolicy.Threshold.Disk.Critical.create(95, true);
      MonitoringPolicy.Threshold.Disk disk = MonitoringPolicy.Threshold.Disk.create(diskWarning, diskCritical);

      MonitoringPolicy.Threshold.InternalPing.Warning pingWarning = MonitoringPolicy.Threshold.InternalPing.Warning.create(90, true);
      MonitoringPolicy.Threshold.InternalPing.Critical pingCritical = MonitoringPolicy.Threshold.InternalPing.Critical.create(90, true);
      MonitoringPolicy.Threshold.InternalPing ping = MonitoringPolicy.Threshold.InternalPing.create(pingWarning, pingCritical);

      MonitoringPolicy.Threshold.Transfer.Warning tranWarning = MonitoringPolicy.Threshold.Transfer.Warning.create(90, true);
      MonitoringPolicy.Threshold.Transfer.Critical tranCritical = MonitoringPolicy.Threshold.Transfer.Critical.create(90, true);
      MonitoringPolicy.Threshold.Transfer transfer = MonitoringPolicy.Threshold.Transfer.create(tranWarning, tranCritical);

      MonitoringPolicy.Threshold threshold = MonitoringPolicy.Threshold.create(cpu, ram, disk, transfer, ping);
      MonitoringPolicy.CreatePolicy payload = MonitoringPolicy.CreatePolicy.builder()
              .name("name")
              .agent(true)
              .email("email")
              .ports(ports)
              .processes(processes)
              .description("dsec")
              .thresholds(threshold)
              .build();
      MonitoringPolicy response = monitoringPolicyApi().create(payload);

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/monitoring_policies", "{\"name\":\"name\",\"description\":\"dsec\",\"email\":\"email\",\"agent\":true,"
              + "\"thresholds\":{\"cpu\":{\"warning\":{\"value\":90,\"alert\":true},\"critical\":{\"value\":90,\"alert\":true}},"
              + "\"ram\":{\"warning\":{\"value\":90,\"alert\":true},\"critical\":{\"value\":90,\"alert\":true}},"
              + "\"disk\":{\"warning\":{\"value\":90,\"alert\":true},\"critical\":{\"value\":95,\"alert\":true}},"
              + "\"transfer\":{\"warning\":{\"value\":90,\"alert\":true},\"critical\":{\"value\":90,\"alert\":true}},"
              + "\"internal_ping\":{\"warning\":{\"value\":90,\"alert\":true},\"critical\":{\"value\":90,\"alert\":true}}},"
              + "\"ports\":[{\"port\":80,\"alert_if\":\"RESPONDING\",\"email_notification\":true,\"protocol\":\"TCP\"}],"
              + "\"processes\":[{\"process\":\"\",\"alert_if\":\"RESPONDING\",\"email_notification\":true}]}");
   }

   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );

      MonitoringPolicy.Threshold.Cpu.Warning warning = MonitoringPolicy.Threshold.Cpu.Warning.create(90, true);
      MonitoringPolicy.Threshold.Cpu.Critical critical = MonitoringPolicy.Threshold.Cpu.Critical.create(90, true);
      MonitoringPolicy.Threshold.Cpu cpu = MonitoringPolicy.Threshold.Cpu.create(warning, critical);

      MonitoringPolicy.Threshold.Ram.Warning ramWarning = MonitoringPolicy.Threshold.Ram.Warning.create(90, true);
      MonitoringPolicy.Threshold.Ram.Critical ramCritical = MonitoringPolicy.Threshold.Ram.Critical.create(90, true);
      MonitoringPolicy.Threshold.Ram ram = MonitoringPolicy.Threshold.Ram.create(ramWarning, ramCritical);

      MonitoringPolicy.Threshold.Disk.Warning diskWarning = MonitoringPolicy.Threshold.Disk.Warning.create(90, true);
      MonitoringPolicy.Threshold.Disk.Critical diskCritical = MonitoringPolicy.Threshold.Disk.Critical.create(95, true);
      MonitoringPolicy.Threshold.Disk disk = MonitoringPolicy.Threshold.Disk.create(diskWarning, diskCritical);

      MonitoringPolicy.Threshold.InternalPing.Warning pingWarning = MonitoringPolicy.Threshold.InternalPing.Warning.create(90, true);
      MonitoringPolicy.Threshold.InternalPing.Critical pingCritical = MonitoringPolicy.Threshold.InternalPing.Critical.create(90, true);
      MonitoringPolicy.Threshold.InternalPing ping = MonitoringPolicy.Threshold.InternalPing.create(pingWarning, pingCritical);

      MonitoringPolicy.Threshold.Transfer.Warning tranWarning = MonitoringPolicy.Threshold.Transfer.Warning.create(90, true);
      MonitoringPolicy.Threshold.Transfer.Critical tranCritical = MonitoringPolicy.Threshold.Transfer.Critical.create(90, true);
      MonitoringPolicy.Threshold.Transfer transfer = MonitoringPolicy.Threshold.Transfer.create(tranWarning, tranCritical);

      MonitoringPolicy.Threshold threshold = MonitoringPolicy.Threshold.create(cpu, ram, disk, transfer, ping);
      MonitoringPolicy.UpdatePolicy payload = MonitoringPolicy.UpdatePolicy.builder()
              .name("name")
              .agent(true)
              .email("email")
              .description("dsec")
              .thresholds(threshold)
              .build();
      MonitoringPolicy response = monitoringPolicyApi().update("monitoringpolicyId", payload);

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/monitoring_policies/monitoringpolicyId", "{\"name\":\"name\",\"description\":\"dsec\",\"email\":\"email\","
              + "\"agent\":true,\"thresholds\":{\"cpu\":{\"warning\":{\"value\":90,\"alert\":true},\"critical\":{\"value\":90,\"alert\":true}},"
              + "\"ram\":{\"warning\":{\"value\":90,\"alert\":true},\"critical\":{\"value\":90,\"alert\":true}},"
              + "\"disk\":{\"warning\":{\"value\":90,\"alert\":true},\"critical\":{\"value\":95,\"alert\":true}},"
              + "\"transfer\":{\"warning\":{\"value\":90,\"alert\":true},\"critical\":{\"value\":90,\"alert\":true}},"
              + "\"internal_ping\":{\"warning\":{\"value\":90,\"alert\":true},\"critical\":{\"value\":90,\"alert\":true}}}}");
   }

   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );
      MonitoringPolicy response = monitoringPolicyApi().delete("monitoringpolicyId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/monitoring_policies/monitoringpolicyId");
   }

   @Test
   public void testDelete404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      MonitoringPolicy response = monitoringPolicyApi().delete("monitoringpolicyId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/monitoring_policies/monitoringpolicyId");
   }

   @Test
   public void testListPorts() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/list.ports.json"))
      );

      List<MonitoringPolicy.Port> result = monitoringPolicyApi().listPorts("policyId");

      assertNotNull(result);
      assertEquals(result.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/ports");
   }

   @Test
   public void testListPorts404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<MonitoringPolicy.Port> result = monitoringPolicyApi().listPorts("policyId");

      assertEquals(result.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/ports");
   }

   @Test
   public void testGetPort() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.port.json"))
      );
      MonitoringPolicy.Port result = monitoringPolicyApi().getPort("policyId", "portId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/ports/portId");
   }

   @Test
   public void testGetPort404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      MonitoringPolicy.Port result = monitoringPolicyApi().getPort("policyId", "portId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/ports/portId");
   }

   @Test
   public void testAddPort() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );

      List<MonitoringPolicy.Port.AddPort> ports = new ArrayList<MonitoringPolicy.Port.AddPort>();
      MonitoringPolicy.Port.AddPort port = MonitoringPolicy.Port.AddPort.create(80, Types.AlertIfType.RESPONDING, true, Types.ProtocolType.TCP);
      ports.add(port);

      MonitoringPolicy response = monitoringPolicyApi().addPort("policyId", MonitoringPolicy.Port.CreatePort.create(ports));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/monitoring_policies/policyId/ports", "{\"ports\":[{\"port\":80,\"alert_if\":\"RESPONDING\",\"email_notification\":true,\"protocol\":\"TCP\"}]}");
   }

   @Test
   public void testUpdatePort() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );

      MonitoringPolicy.Port.AddPort port = MonitoringPolicy.Port.AddPort.create(80, Types.AlertIfType.RESPONDING, true, Types.ProtocolType.TCP);

      MonitoringPolicy response = monitoringPolicyApi().updatePort("policyId", "portId", MonitoringPolicy.Port.UpdatePort.create(port));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/monitoring_policies/policyId/ports/portId", "{\"ports\":{\"port\":80,\"alert_if\":\"RESPONDING\",\"email_notification\":true,\"protocol\":\"TCP\"}}");
   }

   @Test
   public void testDeletePort() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );
      MonitoringPolicy response = monitoringPolicyApi().deletePort("policyId", "portId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/monitoring_policies/policyId/ports/portId");
   }

   @Test
   public void testDeletePort404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      MonitoringPolicy response = monitoringPolicyApi().deletePort("policyId", "portId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/monitoring_policies/policyId/ports/portId");
   }

   @Test
   public void testListProcesses() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/list.process.json"))
      );

      List<MonitoringPolicy.Process> result = monitoringPolicyApi().listProcesses("policyId");

      assertNotNull(result);
      assertEquals(result.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/processes");
   }

   @Test
   public void testListProcesses404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<MonitoringPolicy.Process> result = monitoringPolicyApi().listProcesses("policyId");

      assertEquals(result.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/processes");
   }

   @Test
   public void testGetProcess() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.process.json"))
      );
      MonitoringPolicy.Process result = monitoringPolicyApi().getProcess("policyId", "processId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/processes/processId");
   }

   @Test
   public void testGetProcess404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      MonitoringPolicy.Process result = monitoringPolicyApi().getProcess("policyId", "processId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/processes/processId");
   }

   @Test
   public void testAddProcess() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );

      List<MonitoringPolicy.Process.AddProcess> processes = new ArrayList<MonitoringPolicy.Process.AddProcess>();
      MonitoringPolicy.Process.AddProcess process = MonitoringPolicy.Process.AddProcess.create("process", Types.AlertIfType.RESPONDING, true);
      processes.add(process);
      MonitoringPolicy response = monitoringPolicyApi().addProcess("policyId", MonitoringPolicy.Process.CreateProcess.create(processes));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/monitoring_policies/policyId/processes", "{\"processes\":[{\"process\":\"process\",\"alert_if\":\"RESPONDING\",\"email_notification\":true}]}");
   }

   @Test
   public void testUpdateProcess() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );

      MonitoringPolicy.Process.AddProcess process = MonitoringPolicy.Process.AddProcess.create("process", Types.AlertIfType.RESPONDING, true);

      MonitoringPolicy response = monitoringPolicyApi().updateProcess("policyId", "processId", MonitoringPolicy.Process.UpdateProcess.create(process));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/monitoring_policies/policyId/processes/processId", "{\"processes\":{\"process\":\"process\",\"alert_if\":\"RESPONDING\",\"email_notification\":true}}");
   }

   @Test
   public void testDeleteProcess() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );
      MonitoringPolicy response = monitoringPolicyApi().deleteProcess("policyId", "processId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/monitoring_policies/policyId/processes/processId");
   }

   @Test
   public void testDeleteProcess404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      MonitoringPolicy response = monitoringPolicyApi().deleteProcess("policyId", "processId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/monitoring_policies/policyId/processes/processId");
   }

   @Test
   public void testListServers() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/list.servers.json"))
      );

      List<MonitoringPolicy.Server> servers = monitoringPolicyApi().listServers("policyId");

      assertEquals(servers.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/servers");
   }

   @Test
   public void testListServers404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<MonitoringPolicy.Server> servers = monitoringPolicyApi().listServers("policyId");

      assertEquals(servers.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/servers");
   }

   @Test
   public void testGetServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.server.json"))
      );
      MonitoringPolicy.Server result = monitoringPolicyApi().getServer("policyId", "serverId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/servers/serverId");
   }

   @Test
   public void testGetServer404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      MonitoringPolicy.Server result = monitoringPolicyApi().getServer("policyId", "serverId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_policies/policyId/servers/serverId");
   }

   @Test
   public void testAttachServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );

      List<String> servers = new ArrayList<String>();
      String toAdd = "serverid";
      servers.add(toAdd);
      MonitoringPolicy response = monitoringPolicyApi().attachServer("policyId", MonitoringPolicy.Server.CreateServer.create(servers));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/monitoring_policies/policyId/servers", "{\"servers\":[\"serverid\"]}");
   }

   @Test
   public void testDetachServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringpolicy/get.json"))
      );
      MonitoringPolicy response = monitoringPolicyApi().detachServer("policyId", "serverId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/monitoring_policies/policyId/servers/serverId");
   }

   @Test
   public void testDetachServer404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      MonitoringPolicy response = monitoringPolicyApi().detachServer("policyId", "serverId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/monitoring_policies/policyId/servers/serverId");
   }

}
