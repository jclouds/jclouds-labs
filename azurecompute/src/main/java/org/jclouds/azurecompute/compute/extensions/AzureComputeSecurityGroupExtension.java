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
package org.jclouds.azurecompute.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azurecompute.compute.AzureComputeServiceAdapter.generateIllegalStateExceptionMessage;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkSite;
import org.jclouds.azurecompute.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.domain.Rule;
import org.jclouds.azurecompute.util.ConflictManagementPredicate;
import org.jclouds.azurecompute.util.NetworkSecurityGroups;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * An extension to compute service to allow for the manipulation of {@link org.jclouds.compute.domain.SecurityGroup}s.
 * Implementation is optional by providers.
 *
 * It considers only the custom rules added by the user and ignores the default rules created by Azure
 */
public class AzureComputeSecurityGroupExtension implements SecurityGroupExtension {

   protected final AzureComputeApi api;

   private final Predicate<String> operationSucceededPredicate;

   private final AzureComputeConstants azureComputeConstants;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   AzureComputeSecurityGroupExtension(final AzureComputeApi api,
           final Predicate<String> operationSucceededPredicate, final AzureComputeConstants azureComputeConstants) {

      this.api = api;
      this.operationSucceededPredicate = operationSucceededPredicate;
      this.azureComputeConstants = azureComputeConstants;
   }

   @Override
   public Set<SecurityGroup> listSecurityGroups() {
      return FluentIterable.from(api.getNetworkSecurityGroupApi().list())
              .transform(new NetworkSecurityGroupSecurityGroupFunction())
              .toSet();
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsInLocation(final Location location) {
      return FluentIterable.from(api.getNetworkSecurityGroupApi().list())
              .transform(new NetworkSecurityGroupSecurityGroupFunction())
              .toSet();
   }

   /**
    * @param name it represents both cloudservice and deployment name
    * @return Set&lt;SecurityGroup&gt;
    */
   @Override
   public Set<SecurityGroup> listSecurityGroupsForNode(final String name) {
      checkNotNull(name, "name");

      final Deployment deployment = api.getDeploymentApiForService(name).get(name);
      final String virtualNetworkName = deployment.virtualNetworkName();

      final List<String> subnetNames = FluentIterable.from(deployment.roleList())
              .transformAndConcat(new Function<Role, Iterable<Role.ConfigurationSet>>() {
                 @Override
                 public Iterable<Role.ConfigurationSet> apply(final Role input) {
                    return input.configurationSets();
                 }
              })
              .transformAndConcat(new Function<Role.ConfigurationSet, Iterable<Role.ConfigurationSet.SubnetName>>() {
                 @Override
                 public Iterable<Role.ConfigurationSet.SubnetName> apply(final Role.ConfigurationSet input) {
                    return input.subnetNames();
                 }
              })
              .transform(new Function<Role.ConfigurationSet.SubnetName, String>() {
                 @Override
                 public String apply(final Role.ConfigurationSet.SubnetName input) {
                    return input.name();
                 }
              })
              .toList();

      return FluentIterable.from(subnetNames)
              .transform(new Function<String, NetworkSecurityGroup>() {
                 @Override
                 public NetworkSecurityGroup apply(final String input) {
                    return api.getNetworkSecurityGroupApi().
                    getNetworkSecurityGroupAppliedToSubnet(virtualNetworkName, input);
                 }
              })
              .transform(new NetworkSecurityGroupSecurityGroupFunction())
              .toSet();
   }

   @Override
   public SecurityGroup getSecurityGroupById(final String id) {
      return transformNetworkSecurityGroupToSecurityGroup(id);
   }

   @Override
   public SecurityGroup createSecurityGroup(final String name, final Location location) {
      checkNotNull(name, "name");
      checkNotNull(location, "location");

      final NetworkSecurityGroup networkSecurityGroup = NetworkSecurityGroup.create(
              name, name, location.getId(), null, null);
      final String createNSGRequestId = api.getNetworkSecurityGroupApi().create(networkSecurityGroup);
      if (!operationSucceededPredicate.apply(createNSGRequestId)) {
         final String message = generateIllegalStateExceptionMessage("Create NSG" + name,
                 createNSGRequestId, azureComputeConstants.operationTimeout());
         logger.warn(message);
         throw new IllegalStateException(message);
      }
      return transformNetworkSecurityGroupToSecurityGroup(name);
   }

   private SecurityGroup transformNetworkSecurityGroupToSecurityGroup(final String name) {
      final NetworkSecurityGroup fullDetails = api.getNetworkSecurityGroupApi().getFullDetails(name);
      return fullDetails == null
              ? null
              : new NetworkSecurityGroupSecurityGroupFunction().apply(fullDetails);
   }

   @Override
   public boolean removeSecurityGroup(final String id) {
      final NetworkConfiguration networkConfiguration = api.getVirtualNetworkApi().getNetworkConfiguration();
      if (networkConfiguration != null) {
         for (VirtualNetworkSite virtualNetworkSite
                 : networkConfiguration.virtualNetworkConfiguration().virtualNetworkSites()) {

            for (NetworkConfiguration.Subnet subnet : virtualNetworkSite.subnets()) {
               final String virtualNetworkName = virtualNetworkSite.name();
               final String subnetName = subnet.name();
               if (virtualNetworkName != null && subnetName != null) {
                  final NetworkSecurityGroup networkSecurityGroupAppliedToSubnet = api.getNetworkSecurityGroupApi()
                          .getNetworkSecurityGroupAppliedToSubnet(virtualNetworkName, subnetName);
                  if (networkSecurityGroupAppliedToSubnet != null
                          && networkSecurityGroupAppliedToSubnet.name().equals(id)) {
                     logger.debug("Removing a networkSecurityGroup %s is already applied to subnet '%s' ...",
                             id, subnetName);

                     // remove existing nsg from subnet
                     if (!new ConflictManagementPredicate(api, operationSucceededPredicate) {
                        @Override
                        protected String operation() {
                           return api.getNetworkSecurityGroupApi().removeFromSubnet(
                                   virtualNetworkName, subnetName, id);
                        }
                     }.apply(id)) {
                        final String message = generateIllegalStateExceptionMessage("Remove NSG" + id + " from subnet " + subnetName,
                                "Remove security group from subnet", azureComputeConstants.operationTimeout());
                        logger.warn(message);
                        throw new IllegalStateException(message);
                     }
                  }
               }
            }
         }
      }
      String deleteRequestId = api.getNetworkSecurityGroupApi().delete(id);
      return operationSucceededPredicate.apply(deleteRequestId);
   }

   @Override
   public SecurityGroup addIpPermission(final IpPermission ipPermission, final SecurityGroup group) {
      checkNotNull(group, "group");
      checkNotNull(ipPermission, "ipPermission");

      final String id = checkNotNull(group.getId(), "group.getId()");

      final int priority = NetworkSecurityGroups.getFirstAvailablePriority(
              NetworkSecurityGroups.getCustomRules(api.getNetworkSecurityGroupApi().getFullDetails(group.getName())));

      final String ruleName = NetworkSecurityGroups.createRuleName(
              azureComputeConstants.tcpRuleFormat(), ipPermission.getFromPort(), ipPermission.getToPort());

      // add rule to NSG
      addRuleToNetworkSecurityGroup(id, ruleName, priority, ipPermission);

      return transformNetworkSecurityGroupToSecurityGroup(id);
   }

   @Override
   public SecurityGroup addIpPermission(
           final IpProtocol protocol,
           final int startPort,
           final int endPort,
           final Multimap<String, String> tenantIdGroupNamePairs,
           final Iterable<String> ipRanges,
           final Iterable<String> groupIds,
           final SecurityGroup group) {

      final IpPermission.Builder permBuilder = IpPermission.builder();
      permBuilder.ipProtocol(protocol);
      permBuilder.fromPort(startPort);
      permBuilder.toPort(endPort);
      permBuilder.tenantIdGroupNamePairs(tenantIdGroupNamePairs);
      permBuilder.cidrBlocks(ipRanges);
      permBuilder.groupIds(groupIds);

      return addIpPermission(permBuilder.build(), group);
   }

   @Override
   public SecurityGroup removeIpPermission(final IpPermission ipPermission, final SecurityGroup group) {
      checkNotNull(group, "group");
      checkNotNull(ipPermission, "ipPermission");

      final String id = checkNotNull(group.getId(), "group.getId()");

      final String ruleName = NetworkSecurityGroups.createRuleName(
              azureComputeConstants.tcpRuleFormat(), ipPermission.getFromPort(), ipPermission.getToPort());

      // remove rule to NSG
      removeRuleFromNetworkSecurityGroup(id, ruleName);

      return transformNetworkSecurityGroupToSecurityGroup(id);
   }

   @Override
   public SecurityGroup removeIpPermission(
           final IpProtocol protocol,
           final int startPort,
           final int endPort,
           final Multimap<String, String> tenantIdGroupNamePairs,
           final Iterable<String> ipRanges,
           final Iterable<String> groupIds,
           final SecurityGroup group) {

      final IpPermission.Builder permBuilder = IpPermission.builder();
      permBuilder.ipProtocol(protocol);
      permBuilder.fromPort(startPort);
      permBuilder.toPort(endPort);
      permBuilder.tenantIdGroupNamePairs(tenantIdGroupNamePairs);
      permBuilder.cidrBlocks(ipRanges);
      permBuilder.groupIds(groupIds);

      return removeIpPermission(permBuilder.build(), group);
   }

   @Override
   public boolean supportsTenantIdGroupNamePairs() {
      return false;
   }

   @Override
   public boolean supportsTenantIdGroupIdPairs() {
      return false;
   }

   @Override
   public boolean supportsGroupIds() {
      return false;
   }

   @Override
   public boolean supportsPortRangesForGroups() {
      return false;
   }

   @Override
   public boolean supportsExclusionCidrBlocks() {
      return false;
   }

   private class RuleToIpPermission implements Function<Rule, IpPermission> {

      @Override
      public IpPermission apply(final Rule rule) {
         final IpPermission.Builder builder = IpPermission.builder();
         if (rule.name().matches(azureComputeConstants.tcpRuleRegexp())) {
            builder.fromPort(extractPort(rule.name(), 0))
                    .toPort(extractPort(rule.name(), 1));
         }
         builder.ipProtocol(rule.protocol().equals(Rule.Protocol.ALL)
                 ? IpProtocol.ALL : IpProtocol.valueOf(rule.protocol().getValue()));
         if (rule.destinationAddressPrefix().equals("*")) {
            builder.cidrBlock("0.0.0.0/0");
         } else {
            builder.cidrBlock(rule.destinationAddressPrefix());
         }
         return builder.build();
      }

      private int extractPort(String ruleName, int position) {
         return Integer.parseInt(Iterables.get(Splitter.on("-").omitEmptyStrings().
                 split(ruleName.substring(4, ruleName.length())), position));
      }
   }

   private class NetworkSecurityGroupSecurityGroupFunction implements Function<NetworkSecurityGroup, SecurityGroup> {

      @Override
      public SecurityGroup apply(final NetworkSecurityGroup networkSecurityGroup) {
         final SecurityGroupBuilder securityGroupBuilder = new SecurityGroupBuilder()
                 .id(networkSecurityGroup.name())
                 .providerId(networkSecurityGroup.label())
                 .name(networkSecurityGroup.name());
         if (networkSecurityGroup.rules() != null) {
            final List<Rule> filteredRules = NetworkSecurityGroups.getCustomRules(networkSecurityGroup);

            final Iterable<IpPermission> permissions = Iterables.transform(filteredRules, new RuleToIpPermission());
            securityGroupBuilder.ipPermissions(permissions);
         }
         return securityGroupBuilder.build();
      }
   }

   private void addRuleToNetworkSecurityGroup(final String networkSecurityGroupId, final String ruleName,
           final int priority, final IpPermission ipPermission) {

      final String protocol = ipPermission.getIpProtocol().name();
      final String destinationPortRange = ipPermission.getFromPort() == ipPermission.getToPort()
              ? String.valueOf(ipPermission.getToPort())
              : String.format("%s-%s", ipPermission.getFromPort(), ipPermission.getToPort());
      final String destinationAddressPrefix = ipPermission.getCidrBlocks().isEmpty()
              || Iterables.get(ipPermission.getCidrBlocks(), 0).equals("0.0.0.0/0")
                      ? "*"
                      : Iterables.get(ipPermission.getCidrBlocks(), 0);
      final String setRuleToNSGRequestId = api.getNetworkSecurityGroupApi().
              setRule(networkSecurityGroupId, ruleName, Rule.create(ruleName, // name
                              Rule.Type.Inbound, // type
                              String.valueOf(priority), // priority
                              Rule.Action.Allow, // action
                              "INTERNET", // sourceAddressPrefix
                              "*", // sourcePortRange
                              destinationAddressPrefix, // destinationAddressPrefix
                              destinationPortRange, // destinationPortRange
                              Rule.Protocol.fromString(protocol)));
      if (!operationSucceededPredicate.apply(setRuleToNSGRequestId)) {
         final String message = generateIllegalStateExceptionMessage("Add rule " + ruleName,
                 setRuleToNSGRequestId, azureComputeConstants.operationTimeout());
         logger.warn(message);
         throw new IllegalStateException(message);
      }
   }

   private void removeRuleFromNetworkSecurityGroup(final String id, final String ruleName) {
      String setRuleToNSGRequestId = api.getNetworkSecurityGroupApi().deleteRule(id, ruleName);
      if (!operationSucceededPredicate.apply(setRuleToNSGRequestId)) {
         final String message = generateIllegalStateExceptionMessage("Remove rule " + ruleName,
                 setRuleToNSGRequestId, azureComputeConstants.operationTimeout());
         logger.warn(message);
         throw new IllegalStateException(message);
      }
   }

}
