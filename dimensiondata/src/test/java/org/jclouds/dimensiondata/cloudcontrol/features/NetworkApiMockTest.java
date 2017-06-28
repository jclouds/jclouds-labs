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

import com.squareup.okhttp.mockwebserver.MockResponse;
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
      Iterable<NetworkDomain> networkDomains = api.getNetworkApi().listNetworkDomains().concat();
      assertEquals(size(networkDomains), 1); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain");
   }

   public void testListNetworkDomainsWithPagination() throws Exception {
      server.enqueue(jsonResponse("/networkDomains-page1.json"));
      server.enqueue(jsonResponse("/networkDomains-page2.json"));
      Iterable<NetworkDomain> networkDomains = api.getNetworkApi().listNetworkDomains().concat().toList();

      consumeIterableAndAssertAdditionalPagesRequested(networkDomains, 20, 0);

      assertSent(HttpMethod.GET, expectedListNetworkDomainsUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListNetworkDomainsUriBuilder(), 2).toString());
   }

   public void testGetNetworkDomain() throws Exception {
      server.enqueue(jsonResponse("/networkDomain.json"));
      api.getNetworkApi().getNetworkDomain("networkDomainId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain/networkDomainId");
   }

   public void testGetNetworkDomain_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().listNetworkDomainsWithDatacenterIdAndName("testDatacenterId", "testName").concat();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain"
            + "?datacenterId=testDatacenterId&name=testName");
   }

   public void testListNetworkDomainsWithName() throws Exception {
      server.enqueue(jsonResponse("/networkDomains.json"));
      api.getNetworkApi().listNetworkDomainsWithDatacenterIdAndName("testDatacenterId", "testName").concat();
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
      api.getNetworkApi().listNetworkDomainsWithDatacenterIdAndName("testDatacenterId", "testName").concat();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/networkDomain"
            + "?datacenterId=testDatacenterId&name=testName");
   }

   public void testListVlansWithNetworkDomainId() throws Exception {
      server.enqueue(jsonResponse("/vlans.json"));
      api.getNetworkApi().listVlans("testNetworkDomainId");
      assertSent(GET,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan?networkDomainId=testNetworkDomainId");
   }

   public void testListVlansWithNetworkDomainId_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().listVlans("testNetworkDomainId");
      assertSent(GET,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan?networkDomainId=testNetworkDomainId");
   }

   public void testListVlanssWithPagination() throws Exception {
      server.enqueue(jsonResponse("/vlans-page1.json"));
      server.enqueue(jsonResponse("/vlans-page2.json"));
      Iterable<Vlan> vlans = api.getNetworkApi().listVlans("12345").concat().toList();

      consumeIterableAndAssertAdditionalPagesRequested(vlans, 20, 0);

      assertSent(HttpMethod.GET, expectedListVlansRulesUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListVlansRulesUriBuilder(), 2).toString());
   }

   public void testListVlans() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/vlans.json")));
      assertEquals(api.getNetworkApi().listVlans("12345").concat().toList(), new VlansParseTest().expected().toList());
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan?networkDomainId=12345");
   }

   public void testListVlans_404() throws Exception {
      server.enqueue(response404());
      assertTrue(api.getNetworkApi().listVlans("12345").concat().isEmpty());
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan?networkDomainId=12345");
   }

   public void testGetVlan() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/vlan.json")));
      api.getNetworkApi().getVlan("12345");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan/12345");
   }

   public void testGetVlan_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().getVlan("12345");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/vlan/12345");
   }

   public void testListPublicIPv4AddressBlock() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/publicIpBlocks.json")));
      assertEquals(api.getNetworkApi().listPublicIPv4AddressBlocks("12345").concat().toList(),
            new PublicIpBlocksParseTest().expected().toList());
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/publicIpBlock?networkDomainId=12345");
   }

   public void testListPublicIPv4AddressBlock_404() throws Exception {
      server.enqueue(response404());
      assertTrue(api.getNetworkApi().listPublicIPv4AddressBlocks("12345").concat().isEmpty());
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/publicIpBlock?networkDomainId=12345");
   }

   public void testListPublicIPv4AddressBlockWithPagination() throws Exception {
      server.enqueue(jsonResponse("/publicIpBlocks-page1.json"));
      server.enqueue(jsonResponse("/publicIpBlocks-page2.json"));
      Iterable<PublicIpBlock> ipBlocks = api.getNetworkApi().listPublicIPv4AddressBlocks("12345").concat().toList();

      consumeIterableAndAssertAdditionalPagesRequested(ipBlocks, 20, 0);

      assertSent(HttpMethod.GET, expectedListPublicIpBlockUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListPublicIpBlockUriBuilder(), 2).toString());
   }

   public void testCreateFirewallRule() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"CREATE_FIREWALL_RULE\",\n" + "\"responseCode\": \"OK\",\n"
                  + "\"message\": \"Request create Firewall Rule 'My.Rule' successful\", \"info\": [\n" + "{\n"
                  + "\"name\": \"firewallRuleId\",\n" + "\"value\": \"dc545f3e-823c-4500-93c9-8d7f576311de\"\n"
                  + "} ],\n" + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api.getNetworkApi().createFirewallRule("123456", "test", DEFAULT_ACTION, DEFAULT_IP_VERSION, DEFAULT_PROTOCOL,
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
      api.getNetworkApi().deleteNetworkDomain("networkDomainId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteNetworkDomain");
   }

   public void testDeleteNetworkDomain_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().deleteNetworkDomain("networkDomainId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteNetworkDomain");
   }

   public void testRemovePublicIpBlock() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"REMOVE_PUBLIC_IP_BLOCK\",\n" + "\"responseCode\": \"OK\",\n"
                  + "\"message\": \"Public Ip Block (Id:12623a68-ebdb-11e3-9153-001b21cfdbe0) has been removed successfully\","
                  + " \"info\": [],\n" + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api.getNetworkApi().removePublicIpBlock("publicIpBlockId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/removePublicIpBlock");
   }

   public void testRemovePublicIpBlock_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().removePublicIpBlock("publicIpBlockId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/removePublicIpBlock");
   }

   public void testGetPublicIpBlock() throws Exception {
      server.enqueue(jsonResponse("/publicIpBlock.json"));
      api.getNetworkApi().getPublicIPv4AddressBlock("publicIpBlockId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/publicIpBlock/publicIpBlockId");
   }

   public void testGetPublicIpBlock_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().getPublicIPv4AddressBlock("publicIpBlockId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/publicIpBlock/publicIpBlockId");
   }

   public void testCreateNatRule() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"CREATE_NAT_RULE\",\n" + "\"responseCode\": \"OK\",\n"
                  + "\"message\": \"Create Nat Rule Complete\", \"info\": [\n" + "{\n" + "\"name\": \"natRuleId\",\n"
                  + "\"value\": \"dc545f3e-823c-4500-93c9-8d7f576311de\"\n" + "} ],\n" + "\"warning\": [],\n"
                  + "\"error\": [],\n" + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api.getNetworkApi().createNatRule("networkDomainId", "10.0.0.5", "155.143.0.54");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/createNatRule");
   }

   public void testListNatRules() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/natRules.json")));
      api.getNetworkApi().listNatRules("12345").concat().toList();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/natRule?networkDomainId=12345");
   }

   public void testListNatRulesWithPagination() throws Exception {
      server.enqueue(jsonResponse("/natRules-page1.json"));
      server.enqueue(jsonResponse("/natRules-page2.json"));
      Iterable<NatRule> natRules = api.getNetworkApi().listNatRules("12345").concat().toList();

      consumeIterableAndAssertAdditionalPagesRequested(natRules, 20, 0);

      assertSent(HttpMethod.GET, expectedListNatRulesUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListNatRulesUriBuilder(), 2).toString());
   }

   public void testListNatRules_404() throws Exception {
      server.enqueue(response404());
      assertTrue(api.getNetworkApi().listNatRules("12345").concat().isEmpty());
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/natRule?networkDomainId=12345");
   }

   public void testGetNatRule() throws Exception {
      server.enqueue(jsonResponse("/natRule.json"));
      api.getNetworkApi().getNatRule("natRuleId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/natRule/natRuleId");
   }

   public void testGetNatRule_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().getNatRule("natRuleId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/natRule/natRuleId");
   }

   public void testListFirewallRules() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/firewallRules.json")));
      api.getNetworkApi().listFirewallRules("12345").concat().toList();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/firewallRule?networkDomainId=12345");
   }

   public void testListFirewallRules_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().listFirewallRules("12345").concat().toList();
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/firewallRule?networkDomainId=12345");
   }

   public void testListFirewallRulesWithPagination() throws Exception {
      server.enqueue(jsonResponse("/firewallRules-page1.json"));
      server.enqueue(jsonResponse("/firewallRules-page2.json"));
      Iterable<FirewallRule> firewallRules = api.getNetworkApi().listFirewallRules("12345").concat().toList();

      consumeIterableAndAssertAdditionalPagesRequested(firewallRules, 15, 0);

      assertSent(HttpMethod.GET, expectedListFirewallRulesUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListFirewallRulesUriBuilder(), 2).toString());
   }

   public void testDeleteFirewallRule() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"DELETE_FIREWALL_RULE\",\n" + "\"responseCode\": \"IN_PROGRESS\",\n"
                  + "\"message\": \"Delete Firewall Rule (Id:12623a68-ebdb-11e3-9153-001b21cfdbe0) complete\", "
                  + "\"info\": [],\n" + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api.getNetworkApi().deleteFirewallRule("firewallRuleId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteFirewallRule");
   }

   public void testDeleteFirewallRule_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().deleteFirewallRule("firewallRuleId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deleteFirewallRule");
   }

   public void testGetPortList() throws Exception {
      server.enqueue(new MockResponse().setBody(payloadFromResource("/portList.json")));
      api.getNetworkApi().getPortList("portListId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/portList/portListId");
   }

   public void testGetPortList_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().getPortList("portListId");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/portList/portListId");
   }

   public void testDeletePortList() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
            "{\n" + "\"operation\": \"DELETE_PORT_LIST\",\n" + "\"responseCode\": \"IN_PROGRESS\",\n"
                  + "\"message\": \"Port List with id portListId has been deleted.\", " + "\"info\": [],\n"
                  + "\"warning\": [],\n" + "\"error\": [],\n"
                  + "\"requestId\": \"NA9/2015-03-05T13:46:34.848-05:00/f8fdef24-8a12-45ea-a831-\n"
                  + "d5463212ef6a\" }"));
      api.getNetworkApi().deletePortList("portListId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deletePortList");
   }

   public void testDeletePortList_404() throws Exception {
      server.enqueue(response404());
      api.getNetworkApi().deletePortList("portListId");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/network/deletePortList");
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
}
