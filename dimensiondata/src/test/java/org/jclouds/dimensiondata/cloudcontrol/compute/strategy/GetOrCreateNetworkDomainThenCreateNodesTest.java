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
package org.jclouds.dimensiondata.cloudcontrol.compute.strategy;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.easymock.EasyMock;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.collect.PagedIterables;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.compute.options.DimensionDataCloudControlTemplateOptions;
import org.jclouds.dimensiondata.cloudcontrol.domain.IpRange;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.domain.Vlan;
import org.jclouds.dimensiondata.cloudcontrol.features.NetworkApi;
import org.jclouds.domain.Location;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Date;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMockSupport.injectMocks;
import static org.jclouds.dimensiondata.cloudcontrol.compute.options.DimensionDataCloudControlTemplateOptions.DEFAULT_NETWORK_DOMAIN_NAME;
import static org.jclouds.dimensiondata.cloudcontrol.compute.options.DimensionDataCloudControlTemplateOptions.DEFAULT_PRIVATE_IPV4_BASE_ADDRESS;
import static org.jclouds.dimensiondata.cloudcontrol.compute.options.DimensionDataCloudControlTemplateOptions.DEFAULT_PRIVATE_IPV4_PREFIX_SIZE;
import static org.jclouds.dimensiondata.cloudcontrol.compute.options.DimensionDataCloudControlTemplateOptions.DEFAULT_VLAN_NAME;
import static org.testng.AssertJUnit.assertEquals;

@Test(groups = "unit", testName = "GetOrCreateNetworkDomainThenCreateNodesTest")
public class GetOrCreateNetworkDomainThenCreateNodesTest {

   private GetOrCreateNetworkDomainThenCreateNodes getOrCreateNetworkDomainThenCreateNodes;
   private NetworkApi networkApi;
   private DimensionDataCloudControlApi api;
   private Template template;
   private DimensionDataCloudControlTemplateOptions templateOptions;
   private CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy;
   private ListNodesStrategy listNodesStrategy;
   private GroupNamingConvention.Factory namingConvention;
   private ListeningExecutorService userExecutor;
   private CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory;
   private org.jclouds.compute.reference.ComputeServiceConstants.Timeouts timeouts;
   private Location location;
   private String datacenterId;
   private NetworkDomain networkDomain;
   private Vlan vlan;

   @BeforeMethod
   public void setUp() throws Exception {
      networkApi = EasyMock.createMock(NetworkApi.class);
      api = EasyMock.createMock(DimensionDataCloudControlApi.class);
      template = EasyMock.createNiceMock(Template.class);
      addNodeWithGroupStrategy = EasyMock.createNiceMock(CreateNodeWithGroupEncodedIntoName.class);
      listNodesStrategy = EasyMock.createNiceMock(ListNodesStrategy.class);
      namingConvention = EasyMock.createNiceMock(GroupNamingConvention.Factory.class);
      userExecutor = EasyMock.createNiceMock(ListeningExecutorService.class);
      customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory = EasyMock
            .createNiceMock(CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory.class);
      location = createNiceMock(Location.class);
      templateOptions = new DimensionDataCloudControlTemplateOptions();
      templateOptions.nodeNames(Sets.newHashSet("node1"));
      datacenterId = "datacenterId";

      timeouts = new ComputeServiceConstants.Timeouts();

      final Predicate<String> alwaysTrue = new Predicate<String>() {
         @Override
         public boolean apply(final String input) {
            return true;
         }
      };

      getOrCreateNetworkDomainThenCreateNodes = new GetOrCreateNetworkDomainThenCreateNodes(addNodeWithGroupStrategy,
            listNodesStrategy, namingConvention, userExecutor,
            customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory, api, timeouts, alwaysTrue, alwaysTrue);

      networkDomain = NetworkDomain.builder().id("690de302-bb80-49c6-b401-8c02bbefb945")
            .name(DEFAULT_NETWORK_DOMAIN_NAME).build();
      vlan = Vlan.builder().networkDomain(networkDomain).id("vlanId").name(DEFAULT_VLAN_NAME).description("")
            .privateIpv4Range(IpRange.create("10.0.0.0", 24))
            .ipv6Range(IpRange.create("2607:f480:111:1575:0:0:0:0", 64)).ipv4GatewayAddress("10.0.0.1")
            .ipv6GatewayAddress("2607:f480:111:1575:0:0:0:1").createTime(new Date()).state(State.NORMAL)
            .datacenterId("NA9").build();

      injectMocks(api);
      injectMocks(template);
      injectMocks(networkApi);
      expect(template.getOptions()).andReturn(templateOptions).anyTimes();
      expect(template.getLocation()).andReturn(location);
      expect(location.getId()).andReturn(datacenterId);

      expect(api.getNetworkApi()).andReturn(networkApi).anyTimes();
   }

   @Test
   public void testExecute() throws Exception {
      expect(networkApi.listNetworkDomainsWithDatacenterIdAndName(datacenterId, DEFAULT_NETWORK_DOMAIN_NAME))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(networkDomain))));

      expect(networkApi.listVlans(networkDomain.id()))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(vlan))));

      replay(networkApi, api, template, location);

      executeAndAssert();
   }

   @Test(dependsOnMethods = "testExecute")
   public void testExecute_deployNetworkDomain_deployVlan() throws Exception {
      expect(networkApi.listNetworkDomainsWithDatacenterIdAndName(datacenterId, DEFAULT_NETWORK_DOMAIN_NAME))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.<NetworkDomain>newArrayList())));

      final String deployedNetworkDomainId = "deployedNetworkDomainId";
      final String networkDomainDescription = "network domain created by jclouds";

      final NetworkDomain deployedNetworkDomain = NetworkDomain.builder().id(deployedNetworkDomainId)
            .description(networkDomainDescription).name(DEFAULT_NETWORK_DOMAIN_NAME).state(State.NORMAL).build();
      expect(networkApi.deployNetworkDomain(datacenterId, DEFAULT_NETWORK_DOMAIN_NAME, networkDomainDescription,
            NetworkDomain.Type.ESSENTIALS.name())).andReturn(deployedNetworkDomainId);
      expect(networkApi.getNetworkDomain(deployedNetworkDomainId)).andReturn(deployedNetworkDomain);

      final String deployedVlanId = "deployedVlanId";
      final String deployedVlanDescription = "vlan created by jclouds";
      Vlan deployedVlan = Vlan.builder().networkDomain(deployedNetworkDomain).id("deployedVlanId")
            .name(DEFAULT_VLAN_NAME).description("").privateIpv4Range(IpRange.create("10.0.0.0", 24))
            .ipv6Range(IpRange.create("2607:f480:111:1575:0:0:0:0", 64)).ipv4GatewayAddress("10.0.0.1")
            .ipv6GatewayAddress("2607:f480:111:1575:0:0:0:1").createTime(new Date()).state(State.NORMAL)
            .datacenterId(datacenterId).build();
      expect(networkApi.listVlans(deployedNetworkDomain.id()))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.<Vlan>newArrayList())));
      expect(networkApi.deployVlan(deployedNetworkDomainId, DEFAULT_VLAN_NAME, deployedVlanDescription,
            DEFAULT_PRIVATE_IPV4_BASE_ADDRESS, DEFAULT_PRIVATE_IPV4_PREFIX_SIZE)).andReturn(deployedVlanId);
      expect(networkApi.getVlan(deployedVlanId)).andReturn(deployedVlan);

      replay(networkApi, api, template, location);

      executeAndAssert();
   }

   private void executeAndAssert() {
      getOrCreateNetworkDomainThenCreateNodes.execute("group", 0, template, Collections.<NodeMetadata>emptySet(),
            Collections.<NodeMetadata, Exception>emptyMap(),
            ArrayListMultimap.<NodeMetadata, CustomizationResponse>create());

      assertEquals(DEFAULT_NETWORK_DOMAIN_NAME, templateOptions.getNetworkDomainName());
      assertEquals(DEFAULT_VLAN_NAME, templateOptions.getNetworks().iterator().next());
   }
}
