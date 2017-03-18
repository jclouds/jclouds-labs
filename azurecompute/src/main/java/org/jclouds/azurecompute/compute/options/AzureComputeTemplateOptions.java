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
package org.jclouds.azurecompute.compute.options;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Contains options supported by the {@link org.jclouds.compute.ComputeService#createNodesInGroup(
 * String, int, org.jclouds.compute.options.TemplateOptions)} operation.
 *
 * <h2>Usage</h2> The recommended way to instantiate a {@link AzureComputeTemplateOptions} object is to statically
 * import {@code AzureComputeTemplateOptions.*} and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p>
 *
 * <pre>
 * import static org.jclouds.compute.options.AzureComputeTemplateOptions.Builder.*;
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set&lt;? extends NodeMetadata&gt; set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * </pre>
 *
 */
public class AzureComputeTemplateOptions extends TemplateOptions implements Cloneable {

   protected String virtualNetworkName;
   protected List<String> subnetNames = ImmutableList.of();
   protected String storageAccountName;
   protected String storageAccountType;
   protected String networkSecurityGroupName;
   protected String reservedIPName;
   protected Boolean provisionGuestAgent;
   protected Boolean winrmUseHttps;

   @Override
   public AzureComputeTemplateOptions clone() {
      final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(final TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof AzureComputeTemplateOptions) {
         final AzureComputeTemplateOptions eTo = AzureComputeTemplateOptions.class.cast(to);
         eTo.virtualNetworkName(virtualNetworkName);
         if (!subnetNames.isEmpty()) {
            eTo.subnetNames(subnetNames);
         }
         eTo.storageAccountName(storageAccountName);
         eTo.storageAccountType(storageAccountType);
         eTo.reservedIPName(reservedIPName);
         eTo.provisionGuestAgent(provisionGuestAgent);
         eTo.winrmUseHttps(winrmUseHttps);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof AzureComputeTemplateOptions)) return false;
      if (!super.equals(o)) return false;

      AzureComputeTemplateOptions that = (AzureComputeTemplateOptions) o;

      if (networkSecurityGroupName != null ? !networkSecurityGroupName.equals(that.networkSecurityGroupName) : that.networkSecurityGroupName != null)
         return false;
      if (reservedIPName != null ? !reservedIPName.equals(that.reservedIPName) : that.reservedIPName != null) return false;
      if (storageAccountName != null ? !storageAccountName.equals(that.storageAccountName) : that.storageAccountName != null) return false;
      if (storageAccountType != null ? !storageAccountType.equals(that.storageAccountType) : that.storageAccountType != null) return false;
      if (subnetNames != null ? !subnetNames.equals(that.subnetNames) : that.subnetNames != null) return false;
      if (virtualNetworkName != null ? !virtualNetworkName.equals(that.virtualNetworkName) : that.virtualNetworkName != null) return false;
      if (provisionGuestAgent != null ? !provisionGuestAgent.equals(that.provisionGuestAgent) : that.provisionGuestAgent != null) return false;
      if (winrmUseHttps != null ? !winrmUseHttps.equals(that.winrmUseHttps) : that.winrmUseHttps != null) return false;
      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (virtualNetworkName != null ? virtualNetworkName.hashCode() : 0);
      result = 31 * result + (subnetNames != null ? subnetNames.hashCode() : 0);
      result = 31 * result + (storageAccountName != null ? storageAccountName.hashCode() : 0);
      result = 31 * result + (storageAccountType != null ? storageAccountType.hashCode() : 0);
      result = 31 * result + (networkSecurityGroupName != null ? networkSecurityGroupName.hashCode() : 0);
      result = 31 * result + (reservedIPName != null ? reservedIPName.hashCode() : 0);
      result = 31 * result + (provisionGuestAgent != null ? provisionGuestAgent.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("virtualNetworkName", virtualNetworkName)
              .add("subnetNames", subnetNames)
              .add("storageAccountName", storageAccountName)
              .add("storageAccountType", storageAccountType)
              .add("networkSecurityGroupName", networkSecurityGroupName)
              .add("reservedIPName", reservedIPName)
              .add("provisionGuestAgent", provisionGuestAgent)
              .toString();
   }

   public AzureComputeTemplateOptions virtualNetworkName(@Nullable String virtualNetworkName) {
      this.virtualNetworkName = virtualNetworkName;
      return this;
   }

   public AzureComputeTemplateOptions subnetNames(Iterable<String> subnetNames) {
      this.subnetNames = ImmutableList.copyOf(checkNotNull(subnetNames, "subnetNames"));
      return this;
   }

   public AzureComputeTemplateOptions subnetNames(String...subnetNames) {
      return subnetNames(ImmutableList.copyOf(checkNotNull(subnetNames, "subnetNames")));
   }


   public AzureComputeTemplateOptions networkSecurityGroupName(@Nullable String networkSecurityGroupName) {
      this.networkSecurityGroupName = networkSecurityGroupName;
      return this;
   }

   public AzureComputeTemplateOptions storageAccountName(@Nullable String storageAccountName) {
      this.storageAccountName = storageAccountName;
      return this;
   }

   public AzureComputeTemplateOptions storageAccountType(@Nullable String storageAccountType) {
      this.storageAccountType = storageAccountType;
      return this;
   }

   public AzureComputeTemplateOptions reservedIPName(@Nullable String reservedIPName) {
      this.reservedIPName = reservedIPName;
      return this;
   }

   public AzureComputeTemplateOptions provisionGuestAgent(@Nullable Boolean provisionGuestAgent) {
      this.provisionGuestAgent = provisionGuestAgent;
      return this;
   }

   public AzureComputeTemplateOptions winrmUseHttps(@Nullable Boolean winrmUseHttps) {
      this.winrmUseHttps = winrmUseHttps;
      return this;
   }

   public String getVirtualNetworkName() {
      return virtualNetworkName;
   }

   public List<String> getSubnetNames() {
      return subnetNames;
   }

   public String getStorageAccountName() {
      return storageAccountName;
   }

   public String getStorageAccountType() {
      return storageAccountType;
   }

   public String getNetworkSecurityGroupName() {
      return networkSecurityGroupName;
   }

   public String getReservedIPName() {
      return reservedIPName;
   }

   public Boolean getProvisionGuestAgent() {
      return provisionGuestAgent;
   }

   public Boolean getWinrmUseHttps() {
      return winrmUseHttps;
   }

   public static class Builder {

      /**
       * @see #virtualNetworkName
       */
      public static AzureComputeTemplateOptions virtualNetworkName(final String virtualNetworkName) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.virtualNetworkName(virtualNetworkName);
      }

      /**
       * @see #subnetNames
       */
      public static AzureComputeTemplateOptions subnetNames(String...subnetNames) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.subnetNames(subnetNames);
      }

      /**
       * @see #subnetNames
       */
      public static AzureComputeTemplateOptions subnetNames(Iterable<String> subnetNames) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.subnetNames(subnetNames);
      }

      /**
       * @see #storageAccountName
       */
      public static AzureComputeTemplateOptions storageAccountName(final String storageAccountName) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.storageAccountName(storageAccountName);
      }

      /**
       * @see #storageAccountType
       */
      public static AzureComputeTemplateOptions storageAccountType(final String storageAccountType) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.storageAccountType(storageAccountType);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#inboundPorts(int...)
       */
      public static AzureComputeTemplateOptions inboundPorts(final int... ports) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.inboundPorts(ports);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#blockOnPort(int, int)
       */
      public static AzureComputeTemplateOptions blockOnPort(final int port, final int seconds) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.blockOnPort(port, seconds);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#userMetadata(java.util.Map)
       */
      public static AzureComputeTemplateOptions userMetadata(final Map<String, String> userMetadata) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.userMetadata(userMetadata);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#userMetadata(String, String)
       */
      public static AzureComputeTemplateOptions userMetadata(final String key, final String value) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.userMetadata(key, value);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#nodeNames(Iterable)
       */
      public static AzureComputeTemplateOptions nodeNames(final Iterable<String> nodeNames) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.nodeNames(nodeNames);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#networks(Iterable)
       */
      public static AzureComputeTemplateOptions networks(final Iterable<String> networks) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return options.networks(networks);
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions blockOnPort(int port, int seconds) {
      return AzureComputeTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions inboundPorts(int... ports) {
      return AzureComputeTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions authorizePublicKey(String publicKey) {
      return AzureComputeTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions installPrivateKey(String privateKey) {
      return AzureComputeTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return AzureComputeTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions dontAuthorizePublicKey() {
      return AzureComputeTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions nameTask(String name) {
      return AzureComputeTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions runAsRoot(boolean runAsRoot) {
      return AzureComputeTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions runScript(Statement script) {
      return AzureComputeTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return AzureComputeTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions overrideLoginPassword(String password) {
      return AzureComputeTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return AzureComputeTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions overrideLoginUser(String loginUser) {
      return AzureComputeTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return AzureComputeTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return AzureComputeTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions userMetadata(String key, String value) {
      return AzureComputeTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions nodeNames(Iterable<String> nodeNames) {
      return AzureComputeTemplateOptions.class.cast(super.nodeNames(nodeNames));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions networks(Iterable<String> networks) {
      return AzureComputeTemplateOptions.class.cast(super.networks(networks));
   }

}
