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
package org.jclouds.aliyun.ecs.compute.strategy;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.easymock.EasyMock;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.compute.options.ECSServiceTemplateOptions;
import org.jclouds.aliyun.ecs.domain.IpProtocol;
import org.jclouds.aliyun.ecs.domain.KeyPairRequest;
import org.jclouds.aliyun.ecs.domain.Permission;
import org.jclouds.aliyun.ecs.domain.Request;
import org.jclouds.aliyun.ecs.domain.ResourceInfo;
import org.jclouds.aliyun.ecs.domain.ResourceType;
import org.jclouds.aliyun.ecs.domain.SecurityGroup;
import org.jclouds.aliyun.ecs.domain.SecurityGroupRequest;
import org.jclouds.aliyun.ecs.domain.Tag;
import org.jclouds.aliyun.ecs.domain.VPCRequest;
import org.jclouds.aliyun.ecs.domain.VSwitch;
import org.jclouds.aliyun.ecs.domain.VSwitchRequest;
import org.jclouds.aliyun.ecs.domain.Zone;
import org.jclouds.aliyun.ecs.domain.internal.PaginatedCollection;
import org.jclouds.aliyun.ecs.domain.internal.Regions;
import org.jclouds.aliyun.ecs.domain.options.CreateSecurityGroupOptions;
import org.jclouds.aliyun.ecs.domain.options.CreateVPCOptions;
import org.jclouds.aliyun.ecs.domain.options.CreateVSwitchOptions;
import org.jclouds.aliyun.ecs.domain.options.ListVSwitchesOptions;
import org.jclouds.aliyun.ecs.domain.options.TagOptions;
import org.jclouds.aliyun.ecs.features.RegionAndZoneApi;
import org.jclouds.aliyun.ecs.features.SecurityGroupApi;
import org.jclouds.aliyun.ecs.features.SshKeyPairApi;
import org.jclouds.aliyun.ecs.features.TagApi;
import org.jclouds.aliyun.ecs.features.VPCApi;
import org.jclouds.aliyun.ecs.features.VSwitchApi;
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
import org.jclouds.domain.Location;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMockSupport.injectMocks;
import static org.jclouds.aliyun.ecs.compute.strategy.CreateResourcesThenCreateNodes.DEFAULT_CIDR_BLOCK;
import static org.jclouds.aliyun.ecs.compute.strategy.CreateResourcesThenCreateNodes.DEFAULT_DESCRIPTION_SUFFIX;
import static org.jclouds.aliyun.ecs.compute.strategy.CreateResourcesThenCreateNodes.VSWITCH_PREFIX;
import static org.testng.AssertJUnit.assertEquals;

/**
 *
 * User can specify security group and vSwitch.
 * 1. security group and vSwitch
 * 2. only security group  -> impossible to determine which vSwitch the user wants to use or create
 * 3. only vswitch ID -> create a securitygroup in the same vpc
 * 4. none of them -> create vpc, vswitch and securitygroup
 *
 * Case 1 is tested with testExecuteWithSecurityGroupsVSwitchId
 * Case 2 testExecuteOnlySecurityGroup
 * Case 3 is tested with testExecuteOnlyVSwitchId
 * Case 4 is tested with testExecuteNoSecurityGroupsVSwitchId
 */
@Test(groups = "unit", testName = "CreateResourcesThenCreateNodesTest")
public class CreateResourcesThenCreateNodesTest {

   private CreateResourcesThenCreateNodes createResourcesThenCreateNodes;
   private SecurityGroupApi securityGroupApi;
   private VSwitchApi vSwitchApi;
   private TagApi tagApi;
   private SshKeyPairApi sshKeyPairApi;
   private VPCApi vpcApi;
   private RegionAndZoneApi regionAndZoneApi;
   private ECSComputeServiceApi api;
   private Template template;
   private ECSServiceTemplateOptions templateOptions;
   private CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy;
   private ListNodesStrategy listNodesStrategy;
   private GroupNamingConvention.Factory factory;
   private GroupNamingConvention namingConvention;
   private ListeningExecutorService userExecutor;
   private CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory;
   private ComputeServiceConstants.Timeouts timeouts;
   private Location location;
   private String regionId;
   private SecurityGroup securityGroup;
   private Permission permission;
   private VSwitch vswitch;
   private Zone zone;

   @BeforeMethod
   public void setUp() {
      securityGroupApi = EasyMock.createMock(SecurityGroupApi.class);
      vSwitchApi = EasyMock.createMock(VSwitchApi.class);
      tagApi = EasyMock.createMock(TagApi.class);
      sshKeyPairApi = EasyMock.createMock(SshKeyPairApi.class);
      vpcApi = EasyMock.createMock(VPCApi.class);
      regionAndZoneApi = EasyMock.createMock(RegionAndZoneApi.class);
      api = EasyMock.createMock(ECSComputeServiceApi.class);
      template = EasyMock.createNiceMock(Template.class);
      addNodeWithGroupStrategy = EasyMock.createNiceMock(CreateNodeWithGroupEncodedIntoName.class);
      listNodesStrategy = EasyMock.createNiceMock(ListNodesStrategy.class);
      factory = EasyMock.createNiceMock(GroupNamingConvention.Factory.class);
      namingConvention = EasyMock.createNiceMock(GroupNamingConvention.class);

      userExecutor = EasyMock.createNiceMock(ListeningExecutorService.class);
      customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory = EasyMock
            .createNiceMock(CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory.class);
      location = createNiceMock(Location.class);
      templateOptions = new ECSServiceTemplateOptions();
      regionId = Regions.EU_CENTRAL_1.getName();

      timeouts = new ComputeServiceConstants.Timeouts();

      createResourcesThenCreateNodes = new CreateResourcesThenCreateNodes(addNodeWithGroupStrategy,
            listNodesStrategy, factory, userExecutor,
            customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory, api);

      permission = Permission.create(
              "",
              "",
              "",
              Permission.NicType.INTERNET,
              "",
              "",
              "",
              Permission.Direction.ALL,
              "",
              IpProtocol.ALL,
              "",
         Permission.Policy.ACCEPT,
              new Date(),
              "",
              "",
              ""
              );

      zone = Zone.create("id",
              "localName",
              ImmutableMap.<String, List<Object>>of(),
              ImmutableMap.<String, List<String>>of(),
              ImmutableMap.<String, List<String>>of(),
              ImmutableMap.<String, List<ResourceInfo>>of(),
              ImmutableMap.<String, List<String>>of(),
              ImmutableMap.<String, List<String>>of(),
              ImmutableMap.<String, List<String>>of()
              );

      injectMocks(api);
      injectMocks(template);
      injectMocks(securityGroupApi);
      injectMocks(vSwitchApi);
      injectMocks(tagApi);
      injectMocks(sshKeyPairApi);
      injectMocks(vpcApi);
      injectMocks(regionAndZoneApi);

      expect(template.getLocation()).andReturn(location).anyTimes();
      expect(location.getId()).andReturn(regionId).anyTimes();

      expect(api.securityGroupApi()).andReturn(securityGroupApi).anyTimes();
      expect(api.vSwitchApi()).andReturn(vSwitchApi).anyTimes();
      expect(api.tagApi()).andReturn(tagApi).anyTimes();
      expect(api.sshKeyPairApi()).andReturn(sshKeyPairApi).anyTimes();
      expect(api.vpcApi()).andReturn(vpcApi).anyTimes();
      expect(api.regionAndZoneApi()).andReturn(regionAndZoneApi).anyTimes();
   }


   @Test
   public void testExecuteWithSecurityGroupsVSwitchId() {
      String vpcId = "vpc-1";
      String vSwitchId = "vs-1";
      String securityGroupId = "sg-1";
      securityGroup = createSecurityGroup(securityGroupId, vpcId);
      vswitch = createVSwitch(vSwitchId, vpcId);

      templateOptions.vSwitchId(vSwitchId).securityGroups(securityGroupId);

      expect(template.getOptions()).andReturn(templateOptions).anyTimes();

      expect(securityGroupApi.list(regionId))
              .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(securityGroup))));

      expect(securityGroupApi.get(regionId, securityGroupId))
              .andReturn(Lists.newArrayList(permission));

      // found VSwitch specified by user in VPC_PREFIX
      expect(vSwitchApi.list(regionId, ListVSwitchesOptions.Builder.vSwitchId(vSwitchId).vpcId(vpcId)))
              .andReturn(new PaginatedCollection(ImmutableMap.<String, Iterable<VSwitch>>of("VSwitch", Lists.<VSwitch> newArrayList(vswitch)), 1, 1, 1, regionId, "requestId"));

      expect(factory.create()).andReturn(namingConvention).anyTimes();
      expect(namingConvention.sharedNameForGroup(anyString())).andReturn("group").anyTimes();
      expect(namingConvention.uniqueNameForGroup(anyString())).andReturn("prefix").anyTimes();

      expect(securityGroupApi.create(regionId, CreateSecurityGroupOptions.Builder.securityGroupName("group").vpcId(vpcId)))
              .andReturn(new SecurityGroupRequest("requestId", securityGroupId)).anyTimes();
      expect(securityGroupApi.addInboundRule(regionId, securityGroupId, IpProtocol.TCP, "22/22", "0.0.0.0/0"))
              .andReturn(new Request("requestId")).anyTimes();
      expect(tagApi.add(regionId, securityGroupId, ResourceType.SECURITYGROUP,
              TagOptions.Builder.tag(1,  Tag.DEFAULT_OWNER_KEY, Tag.DEFAULT_OWNER_VALUE).tag(2, Tag.GROUP, "group"))
      ).andReturn(new Request("requestId")).anyTimes();
      expect(sshKeyPairApi.create(regionId, "prefix"))
              .andReturn(new KeyPairRequest("requestId", "name", "fingerPrint", "body")).anyTimes();

      replay(securityGroupApi, vSwitchApi, tagApi, sshKeyPairApi, vpcApi, regionAndZoneApi, api, factory, namingConvention, template, location);

      executeAndAssert(vSwitchId, securityGroupId);
   }

   @Test(dependsOnMethods = "testExecuteWithSecurityGroupsVSwitchId")
   public void testExecuteNoSecurityGroupsNoVSwitchId() {
      String vpcId = "vpc-1";
      String vSwitchId = "vs-1";
      String securityGroupId = "sg-1";

      vswitch = createVSwitch(vSwitchId, vpcId);

      expect(template.getOptions()).andReturn(templateOptions).anyTimes();

      expect(securityGroupApi.list(regionId))
      .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.<SecurityGroup>newArrayList())));

      expect(vSwitchApi.list(regionId))
              .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.<VSwitch>newArrayList())));

      expect(regionAndZoneApi.describeZones(regionId)).andReturn(ImmutableList.of(zone));
      expect(vpcApi.create(regionId, CreateVPCOptions.Builder.vpcName(anyString()).description(anyString()))).andReturn(new VPCRequest("reqId", "routeId", "vRoutId", vpcId));

      String vSwitchName = String.format("%s-%s", VSWITCH_PREFIX, "group");
      String vSwitchDescription = String.format("%s - %s", vSwitchName, DEFAULT_DESCRIPTION_SUFFIX);
      expect(vSwitchApi.create(zone.id(), DEFAULT_CIDR_BLOCK, vpcId,
              CreateVSwitchOptions.Builder.vSwitchName(vSwitchName).description(vSwitchDescription))).andReturn(new VSwitchRequest("reqId", vSwitchId));

      expect(factory.create()).andReturn(namingConvention).anyTimes();
      expect(namingConvention.sharedNameForGroup(anyString())).andReturn("group").anyTimes();
      expect(namingConvention.uniqueNameForGroup(anyString())).andReturn("prefix").anyTimes();

      expect(securityGroupApi.create(regionId, CreateSecurityGroupOptions.Builder.securityGroupName("group").vpcId(vpcId)))
              .andReturn(new SecurityGroupRequest("requestId", securityGroupId)).anyTimes();
      expect(securityGroupApi.addInboundRule(regionId, securityGroupId, IpProtocol.TCP, "22/22", "0.0.0.0/0")).andReturn(new Request("requestId")).anyTimes();
      expect(tagApi.add(regionId, securityGroupId, ResourceType.SECURITYGROUP,
              TagOptions.Builder.tag(1,  Tag.DEFAULT_OWNER_KEY, Tag.DEFAULT_OWNER_VALUE).tag(2, Tag.GROUP, "group"))
      ).andReturn(new Request("requestId")).anyTimes();
      expect(sshKeyPairApi.create(regionId, "prefix")).andReturn(new KeyPairRequest("requestId", "name", "fingerPrint", "body")).anyTimes();

      replay(securityGroupApi, vSwitchApi, tagApi, sshKeyPairApi, vpcApi, regionAndZoneApi, api, factory, namingConvention, template, location);

      executeAndAssert(vSwitchId, securityGroupId);
   }

   @Test(dependsOnMethods = "testExecuteNoSecurityGroupsNoVSwitchId", expectedExceptions = IllegalStateException.class)
   public void testExecuteOnlySecurityGroup() {
      String vpcId = "vpc-2";
      String vSwitchId = "";
      String securityGroupId = "sg-2";
      templateOptions.vSwitchId(vSwitchId).securityGroups(securityGroupId);
      securityGroup = createSecurityGroup(securityGroupId, vpcId);
      vswitch = createVSwitch(vSwitchId, vpcId);

      expect(template.getOptions()).andReturn(templateOptions).anyTimes();

      expect(securityGroupApi.list(regionId))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(securityGroup))));

      expect(securityGroupApi.get(regionId, securityGroupId))
            .andReturn(Lists.newArrayList(permission));

      // at least a VSwitch is available in regionId
      expect(vSwitchApi.list(regionId, ListVSwitchesOptions.Builder.vpcId(vpcId)))
              .andReturn(new PaginatedCollection(ImmutableMap.of("VSwitch", Lists.newArrayList(vswitch)), 1, 1, 1, regionId, "requestId"));

      expect(factory.create()).andReturn(namingConvention).anyTimes();
      expect(namingConvention.sharedNameForGroup(anyString())).andReturn("group").anyTimes();
      expect(namingConvention.uniqueNameForGroup(anyString())).andReturn("prefix").anyTimes();

      expect(securityGroupApi.create(regionId, CreateSecurityGroupOptions.Builder.securityGroupName("group").vpcId(vpcId)))
              .andReturn(new SecurityGroupRequest("requestId", securityGroupId)).anyTimes();
      expect(securityGroupApi.addInboundRule(regionId, securityGroupId, IpProtocol.TCP, "22/22", "0.0.0.0/0"))
              .andReturn(new Request("requestId")).anyTimes();
      expect(tagApi.add(regionId, securityGroupId, ResourceType.SECURITYGROUP,
              TagOptions.Builder.tag(1,  Tag.DEFAULT_OWNER_KEY, Tag.DEFAULT_OWNER_VALUE).tag(2, Tag.GROUP, "group"))
      ).andReturn(new Request("requestId")).anyTimes();
      expect(sshKeyPairApi.create(regionId, "prefix"))
              .andReturn(new KeyPairRequest("requestId", "name", "fingerPrint", "body")).anyTimes();

      replay(securityGroupApi, vSwitchApi, tagApi, sshKeyPairApi, vpcApi, regionAndZoneApi, api, factory, namingConvention, template, location);

      executeAndAssert(vSwitchId, securityGroupId);
   }

   @Test(dependsOnMethods = "testExecuteOnlySecurityGroup")
   public void testExecuteOnlyVSwitchId() {
      String vpcId = "vpc-3";
      String vSwitchId = "vs-3";
      String securityGroupId = "sg-3";
      vswitch = createVSwitch(vSwitchId, vpcId);

      templateOptions.vSwitchId(vSwitchId).securityGroups(securityGroupId);

      expect(template.getOptions()).andReturn(templateOptions).anyTimes();

      expect(securityGroupApi.list(regionId))
              .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.<SecurityGroup> newArrayList())));

      expect(securityGroupApi.get(regionId, securityGroupId))
              .andReturn(Lists.newArrayList(permission));

      // found VSwitch specified by user in VPC_PREFIX
      expect(vSwitchApi.list(regionId, ListVSwitchesOptions.Builder.vSwitchId(vSwitchId)))
              .andReturn(new PaginatedCollection(ImmutableMap.<String, Iterable<VSwitch>>of("VSwitch", Lists.<VSwitch> newArrayList(vswitch)), 1, 1, 1, regionId, "requestId"));

      expect(factory.create()).andReturn(namingConvention).anyTimes();
      expect(namingConvention.sharedNameForGroup(anyString())).andReturn("group").anyTimes();
      expect(namingConvention.uniqueNameForGroup(anyString())).andReturn("prefix").anyTimes();

      expect(securityGroupApi.create(regionId, CreateSecurityGroupOptions.Builder.securityGroupName("group").vpcId(vpcId)))
              .andReturn(new SecurityGroupRequest("requestId", securityGroupId)).anyTimes();
      expect(securityGroupApi.addInboundRule(regionId, securityGroupId, IpProtocol.TCP, "22/22", "0.0.0.0/0"))
              .andReturn(new Request("requestId")).anyTimes();
      expect(tagApi.add(regionId, securityGroupId, ResourceType.SECURITYGROUP,
              TagOptions.Builder.tag(1,  Tag.DEFAULT_OWNER_KEY, Tag.DEFAULT_OWNER_VALUE).tag(2, Tag.GROUP, "group"))
      ).andReturn(new Request("requestId")).anyTimes();
      expect(sshKeyPairApi.create(regionId, "prefix"))
              .andReturn(new KeyPairRequest("requestId", "name", "fingerPrint", "body")).anyTimes();

      replay(securityGroupApi, vSwitchApi, tagApi, sshKeyPairApi, vpcApi, regionAndZoneApi, api, factory, namingConvention, template, location);

      executeAndAssert(vSwitchId, securityGroupId);
   }

   private void executeAndAssert(String vSwitchId, String securityGroupId) {
      createResourcesThenCreateNodes.execute("group", 0, template, Collections.<NodeMetadata>emptySet(),
              Collections.<NodeMetadata, Exception>emptyMap(),
              ArrayListMultimap.<NodeMetadata, CustomizationResponse>create());

      assertEquals(vSwitchId, templateOptions.getVSwitchId());
      assertEquals(securityGroupId, templateOptions.getGroups().iterator().next());
   }

   private SecurityGroup createSecurityGroup(String securityGroupId, String vpcId) {
      return SecurityGroup.create(
              securityGroupId,
              "",
              "securityGroupName",
              vpcId,
              ImmutableMap.<String, List<Tag>>of()
      );
   }

   private VSwitch createVSwitch(String vSwitchId, String vpcId) {
      return VSwitch.create(
              "",
              new Date(),
              "vSwitch",
              "",
              VSwitch.Status.AVAILABLE,
              1,
              vpcId,
              vSwitchId,
              ""
      );
   }

}
