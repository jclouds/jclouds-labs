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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.jclouds.Constants;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.compute.options.ECSServiceTemplateOptions;
import org.jclouds.aliyun.ecs.domain.IpProtocol;
import org.jclouds.aliyun.ecs.domain.KeyPair;
import org.jclouds.aliyun.ecs.domain.KeyPairRequest;
import org.jclouds.aliyun.ecs.domain.SecurityGroup;
import org.jclouds.aliyun.ecs.domain.SecurityGroupRequest;
import org.jclouds.aliyun.ecs.domain.Tag;
import org.jclouds.aliyun.ecs.domain.VPCRequest;
import org.jclouds.aliyun.ecs.domain.VSwitch;
import org.jclouds.aliyun.ecs.domain.VSwitchRequest;
import org.jclouds.aliyun.ecs.domain.Zone;
import org.jclouds.aliyun.ecs.domain.options.CreateSecurityGroupOptions;
import org.jclouds.aliyun.ecs.domain.options.CreateVPCOptions;
import org.jclouds.aliyun.ecs.domain.options.CreateVSwitchOptions;
import org.jclouds.aliyun.ecs.domain.options.ListVSwitchesOptions;
import org.jclouds.aliyun.ecs.domain.options.TagOptions;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshKeys;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.aliyun.ecs.domain.ResourceType.SECURITYGROUP;
import static org.jclouds.compute.util.ComputeServiceUtils.getPortRangesFromList;

@Singleton
public class CreateResourcesThenCreateNodes extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   public static final String INTERNET = "0.0.0.0/0";
   public static final String DEFAULT_CIDR_BLOCK = "172.16.1.0/24";
   public static final String JCLOUDS_KEYPAIR_IMPORTED = "jclouds-imported";
   public static final String PORT_RANGE_FORMAT = "%d/%d";
   protected static final String DEFAULT_DESCRIPTION_SUFFIX = "created by jclouds";
   protected static final String VSWITCH_PREFIX = "vswitch";
   protected static final String VPC_PREFIX = "vpc";

   private final ECSComputeServiceApi api;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   protected CreateResourcesThenCreateNodes(CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
                                          ListNodesStrategy listNodesStrategy, GroupNamingConvention.Factory namingConvention,
                                          @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                          CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
                                          ECSComputeServiceApi api) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
            customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = api;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template,
                                                 Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
                                                 Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      String regionId = template.getLocation().getId();
      ECSServiceTemplateOptions options = template.getOptions().as(ECSServiceTemplateOptions.class);

      Optional<SecurityGroup> securityGroupOptional = tryFindSecurityGroupInRegion(regionId, options.getGroups());

      String vpcIdFromSecurityGroup;
      String vpcId;

      if (securityGroupOptional.isPresent()) {
         vpcIdFromSecurityGroup = securityGroupOptional.get().vpcId();
         if (!Strings.isNullOrEmpty(options.getVSwitchId())) {
            validateVSwitchId(regionId, options.getVSwitchId(), securityGroupOptional.get().name(), vpcIdFromSecurityGroup);
         } else {
            String message = String.format("Security group (%s) belongs to VPC (%s). Please specify a vSwitch Id of that VPC (%s) using ECSServiceTemplateOptions.vSwitchId",
            securityGroupOptional.get().name(),
                    vpcIdFromSecurityGroup,
                    vpcIdFromSecurityGroup);
            throw new IllegalStateException(message);
         }
      } else {
         if (!Strings.isNullOrEmpty(options.getVSwitchId())) {
            VSwitch vSwitch = tryFindVSwitch(regionId, options.getVSwitchId());
            vpcId = vSwitch.vpcId();
         } else {
            vpcId = createDefaultVPC(regionId, group);
            String vSwitchId = createDefaultVSwitch(regionId, vpcId, group);
            options.vSwitchId(vSwitchId);
         }
         String createdSecurityGroupId = createSecurityGroupForOptions(group, regionId, vpcId, options);
         options.securityGroups(createdSecurityGroupId);
      }


      // If keys haven't been configured, generate a key pair
      if (Strings.isNullOrEmpty(options.getPublicKey()) &&
          Strings.isNullOrEmpty(options.getLoginPrivateKey())) {
         String uniqueNameForGroup = namingConvention.create().uniqueNameForGroup(group);
         KeyPairRequest keyPairRequest = generateKeyPair(regionId, uniqueNameForGroup);
         options.keyPairName(keyPairRequest.getKeyPairName());
         options.overrideLoginPrivateKey(keyPairRequest.getPrivateKeyBody());
      }

      // If there is a script to run in the node, make sure a private key has
      // been configured so jclouds will be able to access the node
      if (options.getRunScript() != null && Strings.isNullOrEmpty(options.getLoginPrivateKey())) {
         logger.warn(">> A runScript has been configured but no SSH key has been provided. Authentication will delegate to the ssh-agent");
      }

      // If there is a public key configured, then make sure there is a key pair for it
      if (!Strings.isNullOrEmpty(options.getPublicKey())) {
         KeyPair keyPair = getOrImportKeyPairForPublicKey(options, regionId);
         options.keyPairName(keyPair.name());
      }

      Map<?, ListenableFuture<Void>> responses = super.execute(group, count, template, goodNodes, badNodes, customizationResponses);

      // Key pairs are only required to create the devices.
      // Better to delete the auto-generated key pairs when they are mo more required
      registerAutoGeneratedKeyPairCleanupCallbacks(responses, regionId, options.getKeyPairName());

      return responses;
   }

   private void validateVSwitchId(String regionId,
                                  String vSwitchId,
                                  String securityGroupName,
                                  String vpcIdFromSecurityGroup) {
      Optional<VSwitch> optionalVSwitch = tryFindVSwitchInVPC(regionId, vpcIdFromSecurityGroup, vSwitchId);
      if (!optionalVSwitch.isPresent()) {
         String message = String.format("security group (%s) and vSwitch (%s) must be in the same VPC_PREFIX (%s)",
                 securityGroupName,
                 optionalVSwitch.get().name(),
                 vpcIdFromSecurityGroup);

         throw new IllegalStateException(message);
      }
   }

   private String createDefaultVPC(String regionId, String group) {
      String vpcName = String.format("%s-%s", VPC_PREFIX, group);
      VPCRequest vpcRequest = api.vpcApi().create(regionId, CreateVPCOptions.Builder.vpcName(vpcName).description(String.format("%s - %s", VPC_PREFIX, DEFAULT_DESCRIPTION_SUFFIX)));
      return vpcRequest.getVpcId();
   }

   private String createDefaultVSwitch(String regionId, String vpcId, String name) {
      String vSwitchName = String.format("%s-%s", VSWITCH_PREFIX, name);
      Zone zone = Iterables.getFirst(api.regionAndZoneApi().describeZones(regionId), null);
      VSwitchRequest vSwitchRequest = api.vSwitchApi().create(zone.id(), DEFAULT_CIDR_BLOCK, vpcId,
              CreateVSwitchOptions.Builder.vSwitchName(vSwitchName).description(String.format("%s - %s", vSwitchName, DEFAULT_DESCRIPTION_SUFFIX)));
      return vSwitchRequest.getVSwitchId();
   }

   private KeyPair getOrImportKeyPairForPublicKey(ECSServiceTemplateOptions options, String regionId) {
      logger.debug(">> checking if the key pair already exists...");
      PublicKey userKey = readPublicKey(options.getPublicKey());
      final String fingerprint = computeFingerprint(userKey);
      KeyPair keyPair;

      synchronized (CreateResourcesThenCreateNodes.class) {
         Optional<KeyPair> keyPairOptional = Iterables
               .tryFind(api.sshKeyPairApi().list(regionId).concat(), new Predicate<KeyPair>() {
                  @Override
                  public boolean apply(KeyPair input) {
                     return input.keyPairFingerPrint().equals(fingerprint.replace(":", ""));
                  }
               });
         if (!keyPairOptional.isPresent()) {
            logger.debug(">> key pair not found. Importing a new key pair %s ...", fingerprint);
            keyPair = api.sshKeyPairApi().importKeyPair(
                    regionId,
                    options.getPublicKey(),
                    namingConvention.create().uniqueNameForGroup(JCLOUDS_KEYPAIR_IMPORTED));
            logger.debug(">> key pair imported! %s", keyPair);
         } else {
            logger.debug(">> key pair found for key %s", fingerprint);
            keyPair = keyPairOptional.get();
         }
         return keyPair;
      }
   }

   private KeyPairRequest generateKeyPair(String regionId, String uniqueNameForGroup) {
      logger.debug(">> creating default keypair for node...");
      KeyPairRequest keyPairRequest = api.sshKeyPairApi().create(regionId, uniqueNameForGroup);
      logger.debug(">> keypair created! %s", keyPairRequest);
      return keyPairRequest;
   }

   private Optional<SecurityGroup> tryFindSecurityGroupInRegion(String regionId, final Set<String> securityGroups) {
      checkArgument(securityGroups.size() <= 1, "Only one security group can be configured for each network interface");
      final String securityGroupId = Iterables.get(securityGroups, 0, null);

      if (securityGroupId != null) {
         return api.securityGroupApi().list(regionId).concat().firstMatch(new Predicate<SecurityGroup>() {
            @Override
            public boolean apply(@Nullable SecurityGroup input) {
               return securityGroupId.equals(input.id());
            }
         });
      }
      return Optional.absent();
   }

   private VSwitch tryFindVSwitch(String regionId, String vSwitchId) {
      ListVSwitchesOptions listVSwitchesOptions = ListVSwitchesOptions.Builder.vSwitchId(vSwitchId);
      Optional<VSwitch> optionalVSwitch = api.vSwitchApi().list(regionId, listVSwitchesOptions).first();
      if (!optionalVSwitch.isPresent()) {
         String message = String.format("Cannot find a valid vSwitch with id (%s) within region (%s)",
                 vSwitchId,
                 regionId);
         throw new IllegalStateException(message);
      }
      return optionalVSwitch.get();
   }

   private Optional<VSwitch> tryFindVSwitchInVPC(String regionId, String vpcId, String vSwitchId) {
      ListVSwitchesOptions listVSwitchesOptions = ListVSwitchesOptions.Builder.vpcId(vpcId).vSwitchId(vSwitchId);
      return api.vSwitchApi().list(regionId, listVSwitchesOptions).first();
   }

   private String createSecurityGroupForOptions(String group, String regionId, String vpcId,
                                                ECSServiceTemplateOptions options) {
      String name = namingConvention.create().sharedNameForGroup(group);
      SecurityGroupRequest securityGroupRequest = api.securityGroupApi().create(regionId,
              CreateSecurityGroupOptions.Builder
                      .securityGroupName(name)
                      .vpcId(vpcId));
      // add rules
      Map<Integer, Integer> portRanges = getPortRangesFromList(options.getInboundPorts());
      for (Map.Entry<Integer, Integer> portRange : portRanges.entrySet()) {
         String range = String.format(PORT_RANGE_FORMAT, portRange.getKey(), portRange.getValue());
         // TODO makes protocol and source CIDR configurable?
         api.securityGroupApi().addInboundRule(
                 regionId,
                 securityGroupRequest.getSecurityGroupId(),
                 IpProtocol.TCP,
                 range,
                 INTERNET);
      }
      api.tagApi().add(regionId, securityGroupRequest.getSecurityGroupId(), SECURITYGROUP,
              TagOptions.Builder
                      .tag(1, Tag.DEFAULT_OWNER_KEY, Tag.DEFAULT_OWNER_VALUE)
                      .tag(2, Tag.GROUP, group));
      return securityGroupRequest.getSecurityGroupId();
   }

   private void registerAutoGeneratedKeyPairCleanupCallbacks(Map<?, ListenableFuture<Void>> responses,
         final String regionId, final String keyPairName) {
      // The Futures.allAsList fails immediately if some of the futures fail.
      // The Futures.successfulAsList, however,
      // returns a list containing the results or 'null' for those futures that
      // failed. We want to wait for all them
      // (even if they fail), so better use the latter form.
      ListenableFuture<List<Void>> aggregatedResponses = Futures.successfulAsList(responses.values());

      // Key pairs must be cleaned up after all futures completed (even if some
      // failed).
      Futures.addCallback(aggregatedResponses, new FutureCallback<List<Void>>() {
         @Override
         public void onSuccess(List<Void> result) {
            cleanupAutoGeneratedKeyPairs(keyPairName);
         }

         @Override
         public void onFailure(Throwable t) {
            cleanupAutoGeneratedKeyPairs(keyPairName);
         }

         private void cleanupAutoGeneratedKeyPairs(String keyPairName) {
            logger.debug(">> cleaning up auto-generated key pairs...");
            try {
               api.sshKeyPairApi().delete(regionId, keyPairName);
            } catch (Exception ex) {
               logger.warn(">> could not delete key pair %s: %s", keyPairName, ex.getMessage());
            }
         }
      }, userExecutor);
   }


   private static PublicKey readPublicKey(String publicKey) {
      Iterable<String> parts = Splitter.on(' ').split(publicKey);
      checkArgument(size(parts) >= 2, "bad format, should be: ssh-rsa AAAAB3...");
      String type = get(parts, 0);

      try {
         if ("ssh-rsa".equals(type)) {
            RSAPublicKeySpec spec = SshKeys.publicKeySpecFromOpenSSH(publicKey);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
         } else {
            throw new IllegalArgumentException("bad format, ssh-rsa is only supported");
         }
      } catch (InvalidKeySpecException ex) {
         throw new RuntimeException(ex);
      } catch (NoSuchAlgorithmException ex) {
         throw new RuntimeException(ex);
      }
   }

   private static String computeFingerprint(PublicKey key) {
      if (key instanceof RSAPublicKey) {
         RSAPublicKey rsaKey = (RSAPublicKey) key;
         return SshKeys.fingerprint(rsaKey.getPublicExponent(), rsaKey.getModulus());
      } else {
         throw new IllegalArgumentException("Only RSA keys are supported");
      }
   }
}
