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
import org.apache.jclouds.oneandone.rest.domain.Dvd;
import org.apache.jclouds.oneandone.rest.domain.FixedInstanceHardware;
import org.apache.jclouds.oneandone.rest.domain.Hardware;
import org.apache.jclouds.oneandone.rest.domain.HardwareFlavour;
import org.apache.jclouds.oneandone.rest.domain.Hdd;
import org.apache.jclouds.oneandone.rest.domain.Image;
import org.apache.jclouds.oneandone.rest.domain.PrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.ServerFirewallPolicy;
import org.apache.jclouds.oneandone.rest.domain.ServerIp;
import org.apache.jclouds.oneandone.rest.domain.ServerLoadBalancer;
import org.apache.jclouds.oneandone.rest.domain.ServerPrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.Snapshot;
import org.apache.jclouds.oneandone.rest.domain.Status;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ServerApiMockTest", singleThreaded = true)
public class ServerApiMockTest extends BaseOneAndOneApiMockTest {

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/list.json"))
      );

      List<Server> servers = serverApi().list();

      assertNotNull(servers);
      assertEquals(servers.size(), 10);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<Server> servers = serverApi().list();

      assertNotNull(servers);
      assertEquals(servers.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/list.options-query-test.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "test", null);
      List<Server> servers = serverApi().list(options);

      assertNotNull(servers);
      assertEquals(servers.size(), 9);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers?q=test");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "test", null);
      List<Server> servers = serverApi().list(options);

      assertNotNull(servers);
      assertEquals(servers.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers?q=test");
   }

   public void testGetServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.json"))
      );
      Server result = serverApi().get("serverId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId");
   }

   @Test
   public void testGetServer404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      Server result = serverApi().get("serverId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId");
   }

   @Test
   public void testListHardwareFlavours() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/list.flavours.json"))
      );
      List<HardwareFlavour> flavours = serverApi().listHardwareFlavours();

      assertNotNull(flavours);
      assertFalse(flavours.isEmpty());
      Assert.assertTrue(flavours.size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/fixed_instance_sizes");
   }

   @Test
   public void testGetHardwareFlavour() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.flavour.json"))
      );
      HardwareFlavour flavours = serverApi().getHardwareFlavour("flavourId");

      assertNotNull(flavours);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/fixed_instance_sizes/flavourId");
   }

   @Test
   public void testGetServerStatus() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.status.json"))
      );
      Status status = serverApi().getStatus("serverId");

      assertNotNull(status);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/status");
   }

   @Test
   public void testGetServersHardware() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.hardware.json"))
      );
      Hardware hardware = serverApi().getHardware("serverId");

      assertNotNull(hardware);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/hardware");
   }

   @Test
   public void testUpdateServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.json"))
      );
      Server response = serverApi().update("serverId", Server.UpdateServer.create("My Server remame", "My server rename description"));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/servers/serverId", "{\n"
              + "  \"name\": \"My Server remame\",\n"
              + "  \"description\": \"My server rename description\"\n"
              + "}"
      );
   }

   @Test
   public void testUpdateHardware() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.json"))
      );
      Server response = serverApi().updateHardware("serverId", Hardware.UpdateHardware.create(2.0, 2.0, 2.0));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/servers/serverId/hardware", "{\n"
              + "  \"vcore\": 2.0,\n"
              + "  \"cores_per_processor\": 2.0,\n"
              + "  \"ram\": 2.0\n"
              + "}"
      );
   }

   @Test
   public void testListHardwareHdds() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/list.hardware.hdds.json"))
      );
      List<Hdd> hdds = serverApi().listHdds("serverId");

      assertNotNull(hdds);
      assertFalse(hdds.isEmpty());
      Assert.assertTrue(hdds.size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/hardware/hdds");
   }

   @Test
   public void testAddHdds() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/add.hdds.json"))
      );
      List<Hdd.CreateHdd> requestList = new ArrayList<Hdd.CreateHdd>();
      requestList.add(Hdd.CreateHdd.create(40, Boolean.FALSE));
      Hdd.CreateHddList request = Hdd.CreateHddList.create(requestList);

      Server response = serverApi().addHdd("serverId", request);

      assertNotNull(response);
      Assert.assertTrue(response.hardware().hdds().size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/servers/serverId/hardware/hdds",
              "{\n"
              + "  \"hdds\":[\n"
              + "  {\n"
              + "    \"size\": 40,\n"
              + "    \"is_main\": false\n"
              + "  }\n"
              + "  ]\n"
              + "}"
      );
   }

   @Test
   public void testGetHdd() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.hdd.json"))
      );
      Hdd hdd = serverApi().getHdd("serverId", "hddId");

      assertNotNull(hdd);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/hardware/hdds/hddId");
   }

   @Test
   public void testUpdateHdd() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.json"))
      );
      Server hdd = serverApi().updateHdd("serverId", "hddId", 60);

      assertNotNull(hdd);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/servers/serverId/hardware/hdds/hddId",
              "{\n"
              + "  \"size\": 60\n"
              + "}"
      );
   }

   @Test
   public void testDeleteHdd() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/delete.json"))
      );
      Server hdd = serverApi().deleteHdd("serverId", "hddId");

      assertNotNull(hdd);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/hardware/hdds/hddId");
   }

   @Test
   public void testDeleteHdd404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      Server hdd = serverApi().deleteHdd("serverId", "hddId");

      assertEquals(hdd, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/hardware/hdds/hddId");
   }

   @Test
   public void testGetImage() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.image.json"))
      );
      Image image = serverApi().getImage("serverId");

      assertNotNull(image);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/image");
   }

   @Test
   public void testUpdateImage() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.image.json"))
      );
      Server.UpdateServerResponse hdd = serverApi().updateImage("serverId", Server.UpdateImage.create("id", "password"));

      assertNotNull(hdd);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/servers/serverId/image",
              "{\n"
              + "  \"id\": \"id\",\n"
              + "  \"password\": \"password\"\n"
              + "}"
      );
   }

   @Test
   public void testListIps() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/list.ip.json"))
      );
      List<ServerIp> ips = serverApi().listIps("serverId");

      assertNotNull(ips);
      assertFalse(ips.isEmpty());
      Assert.assertTrue(ips.size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/ips");
   }

   @Test
   public void testAddIp() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/add.hdds.json"))
      );

      Server response = serverApi().addIp("serverId", Types.IPType.IPV4);

      assertNotNull(response);
      Assert.assertTrue(response.hardware().hdds().size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/servers/serverId/ips",
              "{\n"
              + "  \"type\": \"IPV4\"\n"
              + "}"
      );
   }

   @Test
   public void testGetIp() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.ip.json"))
      );
      ServerIp ip = serverApi().getIp("serverId", "ipId");

      assertNotNull(ip);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/ips/ipId");
   }

   @Test
   public void testDeleteIp() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/delete.json"))
      );

      Server hdd = serverApi().deleteIp("serverId", "ipId");

      assertNotNull(hdd);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/ips/ipId");
   }

   @Test
   public void testDeleteIp404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );

      Server hdd = serverApi().deleteIp("serverId", "ipId");

      assertEquals(hdd, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/ips/ipId");
   }

   @Test
   public void testListIpFirewallPolicies() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/list.ip.firewallPolicies.json"))
      );
      List<ServerFirewallPolicy> policies = serverApi().listIpFirewallPolicies("serverId", "ipId");

      assertNotNull(policies);
      assertFalse(policies.isEmpty());
      Assert.assertTrue(policies.size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/ips/ipId/firewall_policy");
   }

   @Test
   public void testAddIpFirewallPolicy() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.json"))
      );

      Server response = serverApi().addFirewallPolicy("serverId", "ipId", "firewallPolicyId");

      assertNotNull(response);
      Assert.assertTrue(response.hardware().hdds().size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/servers/serverId/ips/ipId/firewall_policy",
              "{\n"
              + "  \"id\": \"firewallPolicyId\"\n"
              + "}"
      );
   }

   @Test
   public void testDeleteIpFirewallPolicy() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/delete.json"))
      );
      Server response = serverApi().deleteIpFirewallPolicy("serverId", "ipId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/ips/ipId/firewall_policy");
   }

   @Test
   public void testDeleteIpFirewallPolicy404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      Server response = serverApi().deleteIpFirewallPolicy("serverId", "ipId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/ips/ipId/firewall_policy");
   }

   @Test
   public void testListIpLoadBalancer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/list.ip.loadBalancers.json"))
      );
      List<ServerLoadBalancer> loadBalancers = serverApi().listIpLoadBalancer("serverId", "ipId");

      assertNotNull(loadBalancers);
      assertFalse(loadBalancers.isEmpty());
      Assert.assertTrue(loadBalancers.size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/ips/ipId/load_balancers");
   }

   @Test
   public void testAddIpLoadBalancer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.json"))
      );

      Server response = serverApi().addIpLoadBalancer("serverId", "ipId", "loadBalancerId");

      assertNotNull(response);
      Assert.assertTrue(response.hardware().hdds().size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/servers/serverId/ips/ipId/load_balancers",
              "{\n"
              + "  \"load_balancer_id\": \"loadBalancerId\"\n"
              + "}"
      );
   }

   @Test
   public void testDeleteIpLoadBalancer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/delete.json"))
      );
      Server response = serverApi().deleteIpLoadBalancer("serverId", "ipId", "loadBalancerId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/ips/ipId/load_balancers/loadBalancerId");
   }

   @Test
   public void testDeleteIpLoadBalancer404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      Server response = serverApi().deleteIpLoadBalancer("serverId", "ipId", "loadBalancerId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/ips/ipId/load_balancers/loadBalancerId");
   }

   @Test
   public void testGetDvd() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.dvd.json"))
      );
      Dvd dvd = serverApi().getDvd("serverId");

      assertNotNull(dvd);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/dvd");
   }

   @Test
   public void testLoadDvd() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.json"))
      );

      Server response = serverApi().loadDvd("serverId", "dvdId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/servers/serverId/dvd",
              "{\n"
              + "  \"id\": \"dvdId\"\n"
              + "}"
      );
   }

   @Test
   public void testDeletedvd() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/delete.json"))
      );
      Server response = serverApi().unloadDvd("serverId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/dvd");
   }

   @Test
   public void testDeletedvd404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      Server response = serverApi().unloadDvd("serverId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/dvd");
   }

   @Test
   public void testListPrivateNetwork() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/list.privatenetwork.json"))
      );
      List<ServerPrivateNetwork> privateNetwork = serverApi().listPrivateNetworks("serverId");

      assertNotNull(privateNetwork);
      assertFalse(privateNetwork.isEmpty());
      Assert.assertTrue(privateNetwork.size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/private_networks");
   }

   @Test
   public void testGetPrivateNetwork() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.privatenetwork.json"))
      );
      PrivateNetwork response = serverApi().getPrivateNetwork("serverId", "privateNetworkId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/private_networks/privateNetworkId");
   }

   @Test
   public void testAssignPrivateNetwork() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.json"))
      );

      Server response = serverApi().assignPrivateNetwork("serverId", "privateNetworkId");

      assertNotNull(response);
      Assert.assertTrue(response.hardware().hdds().size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/servers/serverId/private_networks",
              "{\n"
              + "  \"id\": \"privateNetworkId\"\n"
              + "}"
      );
   }

   @Test
   public void testDeletePrivateNetwork() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.json"))
      );
      Server response = serverApi().deletePrivateNetwork("serverId", "privateNetworkId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/private_networks/privateNetworkId");
   }

   @Test
   public void testDeletePrivateNetwork404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      Server response = serverApi().deletePrivateNetwork("serverId", "privateNetworkId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/private_networks/privateNetworkId");
   }

   @Test
   public void testListSnapshot() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/list.snapshot.json"))
      );
      List<Snapshot> snapshots = serverApi().listSnapshots("serverId");

      assertNotNull(snapshots);
      assertFalse(snapshots.isEmpty());
      Assert.assertTrue(snapshots.size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/servers/serverId/snapshots");
   }

   @Test
   public void testRestoreSnapshot() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.json"))
      );
      Server response = serverApi().restoreSnapshot("serverId", "snapshotId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/servers/serverId/snapshots/snapshotId");
   }

   @Test
   public void testCreateSnapshot() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.json"))
      );

      Server response = serverApi().createSnapshot("serverId");

      assertNotNull(response);
      Assert.assertTrue(response.hardware().hdds().size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/servers/serverId/snapshots");
   }

   @Test
   public void testDeleteSnapshot() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/delete.json"))
      );
      Server response = serverApi().deleteSnapshot("serverId", "snapshotId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/snapshots/snapshotId");
   }

   @Test
   public void testDeleteSnapshot404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      Server response = serverApi().deleteSnapshot("serverId", "snapshotId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId/snapshots/snapshotId");
   }

   @Test
   public void testCreateClone() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.json"))
      );

      Server response = serverApi().clone("serverId", Server.Clone.create("datadcenterId", "Copy of My server"));

      assertNotNull(response);
      Assert.assertTrue(response.hardware().hdds().size() > 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/servers/serverId/clone",
              "{\n"
              + "  \"name\": \"Copy of My server\",\n"
              + "  \"datacenter_id\": \"datadcenterId\"\n"
              + "}"
      );
   }

   @Test
   public void testCreateServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.json"))
      );
      List<Hdd.CreateHdd> hdds = new ArrayList<Hdd.CreateHdd>();
      Hdd.CreateHdd hdd = Hdd.CreateHdd.create(50, Boolean.TRUE);
      hdds.add(hdd);

      Hdd.CreateHddList hddsRequest = Hdd.CreateHddList.create(hdds);

      Hardware.CreateHardware hardware = Hardware.CreateHardware.create(2.0, 2.0, 2.0, hdds);
      Server response = serverApi().create(Server.CreateServer.create(
              "My server",
              "My server description",
              hardware,
              "applianceId",
              "datacenterId",
              "Test123!",
              null,
              Boolean.TRUE,
              null,
              null,
              null,
              null,
              null));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/servers",
              "{\"name\":\"My server\",\"description\":\"My server description\",\"hardware\":{\"vcore\":2.0,\"cores_per_processor\":2.0,\"ram\":2.0,\"hdds\":[{\"size\":50.0,\"is_main\":true}]},\"appliance_id\":\"applianceId\",\"datacenter_id\":\"datacenterId\",\"password\":\"Test123!\",\"power_on\":true}"
      );
   }

   @Test
   public void testCreateFixedInstanceServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/get.json"))
      );
      FixedInstanceHardware hardware = FixedInstanceHardware.create("fixedInstanceId");
      Server response = serverApi().createFixedInstanceServer(Server.CreateFixedInstanceServer.create(
              "name", "name", hardware, "applianceId", "datacenterId", "password",
              null, Boolean.TRUE, null, null, null, null));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/servers",
              "{\"name\":\"name\",\"description\":\"name\",\"hardware\":{\"fixed_instance_size_id\":\"fixedInstanceId\"},\"appliance_id\":\"applianceId\",\"datacenter_id\":\"datacenterId\",\"password\":\"password\",\"power_on\":true}"
      );
   }

   @Test
   public void testUpdateStauts() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/update.json"))
      );
      Server response = serverApi().updateStatus("serverId", Server.UpdateStatus.create(Types.ServerAction.POWER_OFF, Types.ServerActionMethod.SOFTWARE));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/servers/serverId/status/action",
              "{\n"
              + "  \"action\": \"POWER_OFF\",\n"
              + "  \"method\": \"SOFTWARE\"\n"
              + "}"
      );
   }

   @Test
   public void testDeleteServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/server/delete.json"))
      );
      Server response = serverApi().delete("serverId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId");
   }

   @Test
   public void testDeleteServer404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      Server response = serverApi().delete("serverId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/servers/serverId");
   }

   private ServerApi serverApi() {
      return api.serverApi();
   }

}
