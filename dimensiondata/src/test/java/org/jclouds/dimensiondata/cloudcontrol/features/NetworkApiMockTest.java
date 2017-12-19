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
package org.jclouds.dimensiondata.cloudcontrol.features;

import com.google.common.collect.Lists;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRuleTarget;
import org.jclouds.dimensiondata.cloudcontrol.domain.IpRange;
import org.jclouds.dimensiondata.cloudcontrol.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontrol.domain.Placement;
import org.jclouds.dimensiondata.cloudcontrol.domain.PublicIpBlock;
import org.jclouds.dimensiondata.cloudcontrol.domain.Vlan;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseAccountAwareCloudControlMockTest;
import org.jclouds.dimensiondata.cloudcontrol.parse.PublicIpBlocksParseTest;
import org.jclouds.dimensiondata.cloudcontrol.parse.VlansParseTest;
import org.jclouds.http.Uris;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.util.List;

import static ch.qos.logback.core.net.ssl.SSL.DEFAULT_PROTOCOL;
import static com.google.common.collect.Iterables.size;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.POST;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Mock tests for the {@link org.jclouds.dimensiondata.cloudcontrol.features.NetworkApi} class.
 */
@Test(groups = "unit", testName = "NetworkApiMockTest", singleThreaded = true)
public class NetworkApiMockTest extends BaseAccountAwareCloudControlMockTest {

   public static final String DEFAULT_ACTION = "ACCEPT_DECISIVELY";
   public static final String DEFAULT_IP_VERSION = "IPV4";

   public void testListNetworkDomains() throws Exception {
      server.enqueue(jsonResponse("/networkDomains.json"));
      Iterable<NetworkDomain> networkDomains = api().listNetworkDomains().concat();
      assertEquals(size(networkDomains), 1); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain");
   }

   public void testListNetworkDomainsWithPagination() throws Exception {
      server.enqueue(jsonResponse("/networkDomains-page1.json"));
      server.enqueue(jsonResponse("/networkDomains-page2.json"));
      Iterable<NetworkDomain> networkDomains = api().listNetworkDomains().concat().toList();

      consumeIterableAndAssertAdditionalPagesRequested(networkDomains, 20, 0);

      assertSent(HttpMethod.GET, expectedListNetworkDomainsUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListNetworkDomainsUriBuilder(), 2, false).toString());
   }

   public void testGetNetworkDomain() throws Exception {
      server.enqueue(jsonResponse("/networkDomain.json"));
      api().getNetworkDomain("networkDomainId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain/networkDomainId");
   }

   public void testGetNetworkDomain_404() throws Exception {
      server.enqueue(response404());
      api().listNetworkDomainsWithDatacenterIdAndName("testDatacenterId", "testName").concat();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain"
            + "?datacenterId=testDatacenterId&name=testName");
   }

   public void testListNetworkDomainsWithName() throws Exception {
      server.enqueue(jsonResponse("/networkDomains.json"));
      api().listNetworkDomainsWithDatacenterIdAndName("testDatacenterId", "testName").concat();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain"
            + "?datacenterId=testDatacenterId&name=testName");
   }

   public void testListNetworkDomainsWithNameWithPagination() throws Exception {
      server.enqueue(jsonResponse("/networkDomains-page1.json"));
      server.enqueue(jsonResponse("/networkDomains-page2.json"));
      Iterable<NetworkDomain> networkDomains = api.getNetworkApi()
            .listNetworkDomainsWithDatacenterIdAndName("testDatacenterId", "testName").concat();

      consumeIterableAndAssertAdditionalPagesRequested(networkDomains, 20, 1);

      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain"
            + "?datacenterId=testDatacenterId&name=testName");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain" + "?pageNumber=2");
   }

   public void testListNetworkDomainsWithName_404() throws Exception {
      server.enqueue(response404());
      api().listNetworkDomainsWithDatacenterIdAndName("testDatacenterId", "testName").concat();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain"
            + "?datacenterId=testDatacenterId&name=testName");
   }

   public void testListVlansWithNetworkDomainId() throws Exception {
      server.enqueue(jsonResponse("/vlans.json"));
      api().listVlans("testNetworkDomainId");
      assertSent(GET,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan?networkDomainId=testNetworkDomainId");
   }

   public void testListVlansWithNetworkDomainId_404() throws Exception {
      server.enqueue(response404());
      api().listVlans("testNetworkDomainId");
      assertSent(GET,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan?networkDomainId=testNetworkDomainId");
   }

   public void testListVlanssWithPagination() throws Exception {
      server.enqueue(jsonResponse("/vlans-page1.json"));
      server.enqueue(jsonResponse("/vlans-page2.json"));
      Iterable<Vlan> vlans = api().listVlans("12345").concat().toList();

      consumeIterableAndAssertAdditionalPagesRequested(vlans, 20, 0);

      assertSent(HttpMethod.GET, expectedListVlansRulesUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListVlansRulesUriBuilder(), 2, false).toString());
   }

   public void testListVlans() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/vlans.json")));
      assertEquals(api().listVlans("12345").concat().toList(), new VlansParseTest().expected().toList());
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan?networkDomainId=12345");
   }

   public void testListVlans_404() throws Exception {
      server.enqueue(response404());
      assertTrue(api().listVlans("12345").concat().isEmpty());
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan?networkDomainId=12345");
   }

   public void testGetVlan() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/vlan.json")));
      api().getVlan("12345");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan/12345");
   }

   public void testGetVlan_404() throws Exception {
      server.enqueue(response404());
      api().getVlan("12345");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan/12345");
   }

   public void testListPublicIPv4AddressBlock() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/publicIpBlocks.json")));
      assertEquals(api().listPublicIPv4AddressBlocks("12345").concat().toList(),
            new PublicIpBlocksParseTest().expected().toList());
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/publicIpBlock?networkDomainId=12345");
   }

   public void testListPublicIPv4AddressBlock_404() throws Exception {
      server.enqueue(response404());
      assertTrue(api().listPublicIPv4AddressBlocks("12345").concat().isEmpty());
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/publicIpBlock?networkDomainId=12345");
   }

   public void testListPublicIPv4AddressBlockWithPagination() throws Exception {
      server.enqueue(jsonResponse("/publicIpBlocks-page1.json"));
      server.enqueue(jsonResponse("/publicIpBlocks-page2.json"));
      Iterable<PublicIpBlock> ipBlocks = api().listPublicIPv4AddressBlocks("12345").concat().toList();

      consumeIterableAndAssertAdditionalPagesRequested(ipBlocks, 20, 0);

      assertSent(HttpMethod.GET, expectedListPublicIpBlockUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListPublicIpBlockUriBuilder(), 2, false).toString());
   }

   public void testCreateFirewallRule() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"CREATE_FIREWALL_RULE\",\n" + "\"responseCode\": \"OK\",\n"
                  + "\"message\": \"Request create Firewall Rule 'My.Rule' successful\", \"info\": [\n" + "{\n"
                  + "\"name\": \"firewallRuleId\",\n" + "\"value\": \"dc545f3e-823c-4500-93c9-8d7f576311de\"\n"
                  + "} ],\n" + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api().createFirewallRule("123456", "test", DEFAULT_ACTION, DEFAULT_IP_VERSION, DEFAULT_PROTOCOL,
            FirewallRuleTarget.builder().ip(IpRange.create("ANY", null)).build(),
            FirewallRuleTarget.builder().ip(IpRange.create("ANY", null)).build(), true,
            Placement.builder().position("LAST").build());
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/createFirewallRule");
   }

   public void testDeleteNetworkDomain() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"DELETE_NETWORK_DOMAIN\",\n" + "\"responseCode\": \"IN_PROGRESS\",\n"
                  + "\"message\": \"Request to Delete Network Domain (Id:12623a68-ebdb-11e3-9153-001b21cfdbe0)"
                  + " has been accepted and is being processed\", \"info\": [],\n" + "\"warning\": [],\n"
                  + "\"error\": [],\n" + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api().deleteNetworkDomain("networkDomainId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteNetworkDomain");
   }

   public void testDeleteNetworkDomain_404() throws Exception {
      server.enqueue(response404());
      api().deleteNetworkDomain("networkDomainId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteNetworkDomain");
   }

   public void testRemovePublicIpBlock() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"REMOVE_PUBLIC_IP_BLOCK\",\n" + "\"responseCode\": \"OK\",\n"
                  + "\"message\": \"Public Ip Block (Id:12623a68-ebdb-11e3-9153-001b21cfdbe0) has been removed successfully\","
                  + " \"info\": [],\n" + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api().removePublicIpBlock("publicIpBlockId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/removePublicIpBlock");
   }

   public void testRemovePublicIpBlock_404() throws Exception {
      server.enqueue(response404());
      api().removePublicIpBlock("publicIpBlockId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/removePublicIpBlock");
   }

   public void testGetPublicIpBlock() throws Exception {
      server.enqueue(jsonResponse("/publicIpBlock.json"));
      api().getPublicIPv4AddressBlock("publicIpBlockId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/publicIpBlock/publicIpBlockId");
   }

   public void testGetPublicIpBlock_404() throws Exception {
      server.enqueue(response404());
      api().getPublicIPv4AddressBlock("publicIpBlockId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/publicIpBlock/publicIpBlockId");
   }

   public void testCreateNatRule() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"CREATE_NAT_RULE\",\n" + "\"responseCode\": \"OK\",\n"
                  + "\"message\": \"Create Nat Rule Complete\", \"info\": [\n" + "{\n" + "\"name\": \"natRuleId\",\n"
                  + "\"value\": \"dc545f3e-823c-4500-93c9-8d7f576311de\"\n" + "} ],\n" + "\"warning\": [],\n"
                  + "\"error\": [],\n" + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api().createNatRule("networkDomainId", "10.0.0.5", "155.143.0.54");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/createNatRule");
   }

   public void testListNatRules() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/natRules.json")));
      api().listNatRules("12345").concat().toList();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/natRule?networkDomainId=12345");
   }

   public void testListNatRulesWithPagination() throws Exception {
      server.enqueue(jsonResponse("/natRules-page1.json"));
      server.enqueue(jsonResponse("/natRules-page2.json"));
      Iterable<NatRule> natRules = api().listNatRules("12345").concat().toList();

      consumeIterableAndAssertAdditionalPagesRequested(natRules, 20, 0);

      assertSent(HttpMethod.GET, expectedListNatRulesUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListNatRulesUriBuilder(), 2, false).toString());
   }

   public void testListNatRules_404() throws Exception {
      server.enqueue(response404());
      assertTrue(api().listNatRules("12345").concat().isEmpty());
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/natRule?networkDomainId=12345");
   }

   public void testGetNatRule() throws Exception {
      server.enqueue(jsonResponse("/natRule.json"));
      api().getNatRule("natRuleId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/natRule/natRuleId");
   }

   public void testGetNatRule_404() throws Exception {
      server.enqueue(response404());
      api().getNatRule("natRuleId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/natRule/natRuleId");
   }

   public void testListFirewallRules() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/firewallRules.json")));
      api().listFirewallRules("12345").concat().toList();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/firewallRule?networkDomainId=12345");
   }

   public void testListFirewallRules_404() throws Exception {
      server.enqueue(response404());
      api().listFirewallRules("12345").concat().toList();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/firewallRule?networkDomainId=12345");
   }

   public void testListFirewallRulesWithPagination() throws Exception {
      server.enqueue(jsonResponse("/firewallRules-page1.json"));
      server.enqueue(jsonResponse("/firewallRules-page2.json"));
      Iterable<FirewallRule> firewallRules = api().listFirewallRules("12345").concat().toList();

      consumeIterableAndAssertAdditionalPagesRequested(firewallRules, 15, 0);

      assertSent(HttpMethod.GET, expectedListFirewallRulesUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListFirewallRulesUriBuilder(), 2, false).toString());
   }

   public void testDeleteFirewallRule() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"DELETE_FIREWALL_RULE\",\n" + "\"responseCode\": \"IN_PROGRESS\",\n"
                  + "\"message\": \"Delete Firewall Rule (Id:12623a68-ebdb-11e3-9153-001b21cfdbe0) complete\", "
                  + "\"info\": [],\n" + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api().deleteFirewallRule("firewallRuleId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteFirewallRule");
   }

   public void testDeleteFirewallRule_404() throws Exception {
      server.enqueue(response404());
      api().deleteFirewallRule("firewallRuleId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteFirewallRule");
   }

   public void testGetPortList() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/portList.json")));
      api().getPortList("portListId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/portList/portListId");
   }

   public void testGetPortList_404() throws Exception {
      server.enqueue(response404());
      api().getPortList("portListId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/portList/portListId");
   }

   public void testDeletePortList() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"DELETE_PORT_LIST\",\n" + "\"responseCode\": \"IN_PROGRESS\",\n"
                  + "\"message\": \"Port List with id portListId has been deleted.\", " + "\"info\": [],\n"
                  + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api().deletePortList("portListId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deletePortList");
   }

   public void testDeletePortList_404() throws Exception {
      server.enqueue(response404());
      api().deletePortList("portListId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deletePortList");
   }

   public void testDeployNetworkDomain() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"DEPLOY_NETWORK_DOMAIN\",\n" + "\"responseCode\": \"IN_PROGRESS\",\n"
                  + "\"message\": \"Request to deploy Network Domain 'test' has been accepted and is being processed.\", "
                  + "\"info\": [\n" + "{\n" + "\"name\": \"networkDomainId\",\n"
                  + "\"value\": \"f14a871f-9a25-470c-aef8-51e13202e1aa\"\n" + "} ],\n" + "\"warning\": [],\n"
                  + "\"error\": [],\n" + "\"requestId\": \"NA9/2017-03-05T13:46:34.848-05:00/7e9fffe7-190b-46f2-9107-\n"
                  + "9d52fe57d0ad\" }"));
      api().deployNetworkDomain("NA9", "test", "test description", NetworkDomain.Type.ESSENTIALS.name());
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deployNetworkDomain");
      assertBodyContains(recordedRequest,
            "{\"datacenterId\":\"NA9\",\"name\":\"test\",\"description\":\"test description\",\"type\":\"ESSENTIALS\"}");
   }

   public void testDeployVlan() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"DEPLOY_VLAN\",\n" + "\"responseCode\": \"IN_PROGRESS\",\n"
                  + "\"message\": \"Request to Deploy VLAN 'test' has been accepted and is being processed.\", "
                  + "\"info\": [\n" + "{\n" + "\"name\": \"vlanId\",\n"
                  + "\"value\": \"0e56433f-d808-4669-821d-812769517ff8\"\n" + "} ],\n" + "\"warning\": [],\n"
                  + "\"error\": [],\n" + "\"requestId\": \"NA9/2017-03-05T13:46:34.848-05:00/7e9fffe7-190b-46f2-9107-\n"
                  + "9d52fe57d0ad\" }"));
      api.getNetworkApi()
            .deployVlan("f14a871f-9a25-470c-aef8-51e13202e1aa", "test", "test description", "10.0.3.0", 23);
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deployVlan");
      assertBodyContains(recordedRequest,
            "{\"networkDomainId\":\"f14a871f-9a25-470c-aef8-51e13202e1aa\",\"name\":\"test\","
                  + "\"description\":\"test description\",\"privateIpv4BaseAddress\":\"10.0.3.0\",\"privateIpv4PrefixSize\":23}");
   }

   public void testDeleteVlan() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"DELETE_VLAN\",\n" + "\"responseCode\": \"IN_PROGRESS\",\n"
                  + "\"message\": \"Request to Delete VLAN (Id:0e56433f-d808-4669-821d-812769517ff8) has been accepted and is being processed.\", "
                  + "\"info\": [],\n" + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2017-03-05T13:46:34.848-05:00/7e9fffe7-190b-46f2-9107-\n"
                  + "9d52fe57d0ad\" }"));
      api().deleteVlan("0e56433f-d808-4669-821d-812769517ff8");
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteVlan");
      assertBodyContains(recordedRequest, "{\"id\":\"0e56433f-d808-4669-821d-812769517ff8\"}");
   }

   public void testDeleteVlan_404() throws Exception {
      server.enqueue(response404());
      api().deleteVlan("0e56433f-d808-4669-821d-812769517ff8");
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteVlan");
      assertBodyContains(recordedRequest, "{\"id\":\"0e56433f-d808-4669-821d-812769517ff8\"}");
   }

   public void testAddPublicIpBlock() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"ADD_PUBLIC_IP_BLOCK\",\n" + "\"responseCode\": \"OK\",\n"
                  + "\"message\": \"Public IPv4 Address Block has been added successfully to"
                  + "Network Domain 484174a2-ae74-4658-9e56-50fc90e086cf.\",\n" + "\"info\": [\n" + "{\n"
                  + "\"name\": \"ipBlockId\",\n" + "\"value\": \"4487241a-f0ca-11e3-9315-d4bed9b167ba\"\n" + "} ],\n"
                  + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2017-03-05T13:46:34.848-05:00/7e9fffe7-190b-46"
                  + "f2-9107-9d52fe57d0ad\" }"));
      api().addPublicIpBlock("484174a2-ae74-4658-9e56-50fc90e086cf");
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/addPublicIpBlock");
      assertBodyContains(recordedRequest, "{\"networkDomainId\":\"484174a2-ae74-4658-9e56-50fc90e086cf\"}");
   }

   public void testDeleteNatRule() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"DELETE_NAT_RULE\",\n" + "\"responseCode\": \"OK\",\n"
                  + "\"message\": \"NAT Rule with Id 2169a38e-5692-497e-a22a-701a838a6539 has been deleted.\", "
                  + "\"info\": [],\n" + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2017-03-05T13:46:34.848-05:00/7e9fffe7-190b-46f2-9107-\n"
                  + "9d52fe57d0ad\" }"));
      api().deleteNatRule("2169a38e-5692-497e-a22a-701a838a6539");
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteNatRule");
      assertBodyContains(recordedRequest, "{\"id\":\"2169a38e-5692-497e-a22a-701a838a6539\"}");
   }

   public void testDeleteNatRule_404() throws Exception {
      server.enqueue(response404());
      api().deleteNatRule("2169a38e-5692-497e-a22a-701a838a6539");
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteNatRule");
      assertBodyContains(recordedRequest, "{\"id\":\"2169a38e-5692-497e-a22a-701a838a6539\"}");
   }

   public void testCreatePortList() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"CREATE_PORT_LIST\",\n" + "\"responseCode\": \"OK\",\n"
                  + "\"message\": \"Port List 'TestPortList' has been created.\", " + "\"info\": [\n" + "{\n"
                  + "\"name\": \"portListId\",\n" + "\"value\": \"9e6b496d-5261-4542-91aa-b50c7f569c54\"\n" + "} ],\n"
                  + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2017-03-05T13:46:34.848-05:00/7e9fffe7-190b-46f2-9107-\n"
                  + "9d52fe57d0ad\" }"));
      List<FirewallRuleTarget.Port> portList = Lists
            .newArrayList(FirewallRuleTarget.Port.create(8080, null), FirewallRuleTarget.Port.create(8899, 9023),
                  FirewallRuleTarget.Port.create(9500, null));
      List<String> childPortListId = Lists
            .newArrayList("1ecf8cd4-dcda-4783-b4ba-b50eb541b813", "54da883e-a023-11e5-a668-426c57702d90");
      api.getNetworkApi()
            .createPortList("db707da6-5785-4a56-ad58-2f8058708d95", "TestPortList", "Test Port List", portList,
                  childPortListId);
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/createPortList");
      assertBodyContains(recordedRequest,
            "{\"networkDomainId\":\"db707da6-5785-4a56-ad58-2f8058708d95\",\"name\":\"TestPortList\","
                  + "\"description\":\"Test Port List\",\"port\":["
                  + "{\"begin\":8080},{\"begin\":8899,\"end\":9023},{\"begin\":9500}],"
                  + "\"childPortListId\":[\"1ecf8cd4-dcda-4783-b4ba-b50eb541b813\",\"54da883e-a023-11e5-a668-426c57702d90\"]}");
   }

   private Uris.UriBuilder expectedListFirewallRulesUriBuilder() {
      Uris.UriBuilder uriBuilder = Uris
            .uriBuilder("/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/firewallRule");
      uriBuilder.addQuery("networkDomainId", "12345");
      return uriBuilder;
   }

   private Uris.UriBuilder expectedListNatRulesUriBuilder() {
      Uris.UriBuilder uriBuilder = Uris
            .uriBuilder("/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/natRule");
      uriBuilder.addQuery("networkDomainId", "12345");
      return uriBuilder;
   }

   private Uris.UriBuilder expectedListNetworkDomainsUriBuilder() {
      return Uris.uriBuilder("/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain");
   }

   private Uris.UriBuilder expectedListVlansRulesUriBuilder() {
      Uris.UriBuilder uriBuilder = Uris
            .uriBuilder("/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan");
      uriBuilder.addQuery("networkDomainId", "12345");
      return uriBuilder;
   }

   private Uris.UriBuilder expectedListPublicIpBlockUriBuilder() {
      Uris.UriBuilder uriBuilder = Uris
            .uriBuilder("/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/publicIpBlock");
      uriBuilder.addQuery("networkDomainId", "12345");
      return uriBuilder;
   }

   private NetworkApi api() {
      return api.getNetworkApi();
   }
}
