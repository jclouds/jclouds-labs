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
package org.jclouds.azurecompute.options;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;

import com.google.common.base.Optional;

/**
 * Contains options supported by the null {@link org.jclouds.compute.ComputeService#createNodesInGroup(
 * String, int, org.jclouds.compute.options.TemplateOptions)} and null {@link org.jclouds.compute.ComputeService#createNodesInGroup(
 * String, int, org.jclouds.compute.options.TemplateOptions)} operations on the <em>gogrid</em> provider.
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

   private Optional<String> virtualNetworkName = Optional.absent();

   private Optional<String> addressSpaceAddressPrefix = Optional.absent();

   private Optional<String> subnetName = Optional.absent();

   private Optional<String> subnetAddressPrefix = Optional.absent();

   private Optional<String> storageAccountName = Optional.absent();

   private Optional<String> storageAccountType = Optional.absent();

   private Optional<String> networkSecurityGroupName = Optional.absent();

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
         if (virtualNetworkName.isPresent()) {
            eTo.virtualNetworkName(virtualNetworkName.get());
         }
         if (addressSpaceAddressPrefix.isPresent()) {
            eTo.addressSpaceAddressPrefix(addressSpaceAddressPrefix.get());
         }
         if (subnetName.isPresent()) {
            eTo.subnetName(subnetName.get());
         }
         if (networkSecurityGroupName.isPresent()) {
            eTo.subnetName(networkSecurityGroupName.get());
         }
         if (subnetAddressPrefix.isPresent()) {
            eTo.subnetAddressPrefix(subnetAddressPrefix.get());
         }
         if (storageAccountName.isPresent()) {
            eTo.storageAccountName(storageAccountName.get());
         }
         if (storageAccountType.isPresent()) {
            eTo.storageAccountType(storageAccountType.get());
         }
      }
   }

   public TemplateOptions virtualNetworkName(final String virtualNetworkName) {
      this.virtualNetworkName = Optional.of(virtualNetworkName);
      return this;
   }

   public TemplateOptions addressSpaceAddressPrefix(final String addressSpaceAddressPrefix) {
      this.addressSpaceAddressPrefix = Optional.of(addressSpaceAddressPrefix);
      return this;
   }

   public TemplateOptions subnetName(final String subnetName) {
      this.subnetName = Optional.of(subnetName);
      return this;
   }

   public TemplateOptions networkSecurityGroupName(final String networkSecurityGroupName) {
      this.networkSecurityGroupName = Optional.of(networkSecurityGroupName);
      return this;
   }

   public TemplateOptions subnetAddressPrefix(final String subnetAddressPrefix) {
      this.subnetAddressPrefix = Optional.of(subnetAddressPrefix);
      return this;
   }

   public TemplateOptions storageAccountName(final String storageAccountName) {
      this.storageAccountName = Optional.of(storageAccountName);
      return this;
   }

   public TemplateOptions storageAccountType(final String storageAccountType) {
      this.storageAccountType = Optional.of(storageAccountType);
      return this;
   }

   public Optional<String> getVirtualNetworkName() {
      return virtualNetworkName;
   }

   public Optional<String> getAddressSpaceAddressPrefix() {
      return addressSpaceAddressPrefix;
   }

   public Optional<String> getSubnetName() {
      return subnetName;
   }

   public Optional<String> getSubnetAddressPrefix() {
      return subnetAddressPrefix;
   }

   public Optional<String> getStorageAccountName() {
      return storageAccountName;
   }

   public Optional<String> getStorageAccountType() {
      return storageAccountType;
   }

   public Optional<String> getNetworkSecurityGroupName() {
      return networkSecurityGroupName;
   }

   public static class Builder {

      /**
       * @see #virtualNetworkName
       */
      public static AzureComputeTemplateOptions virtualNetworkName(final String virtualNetworkName) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.virtualNetworkName(virtualNetworkName));
      }

      /**
       * @see #addressSpaceAddressPrefix
       */
      public static AzureComputeTemplateOptions addressSpaceAddressPrefix(final String addressSpaceAddressPrefix) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.addressSpaceAddressPrefix(addressSpaceAddressPrefix));
      }

      /**
       * @see #subnetName
       */
      public static AzureComputeTemplateOptions subnetName(final String subnetName) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.subnetName(subnetName));
      }

      /**
       * @see #networkSecurityGroupName
       */
      public static AzureComputeTemplateOptions networkSecurityGroupName(final String networkSecurityGroupName) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.subnetName(networkSecurityGroupName));
      }

      /**
       * @see #subnetAddressPrefix
       */
      public static AzureComputeTemplateOptions subnetAddressPrefix(final String subnetAddressPrefix) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.subnetAddressPrefix(subnetAddressPrefix));
      }

      /**
       * @see #storageAccountName
       */
      public static AzureComputeTemplateOptions storageAccountName(final String storageAccountName) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.storageAccountName(storageAccountName));
      }

      /**
       * @see #storageAccountType
       */
      public static AzureComputeTemplateOptions storageAccountType(final String storageAccountType) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.storageAccountType(storageAccountType));
      }

      // methods that only facilitate returning the correct object type
      /**
       * @see org.jclouds.compute.options.TemplateOptions#inboundPorts(int...)
       */
      public static AzureComputeTemplateOptions inboundPorts(final int... ports) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#blockOnPort(int, int)
       */
      public static AzureComputeTemplateOptions blockOnPort(final int port, final int seconds) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#userMetadata(java.util.Map)
       */
      public static AzureComputeTemplateOptions userMetadata(final Map<String, String> userMetadata) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#userMetadata(String, String)
       */
      public static AzureComputeTemplateOptions userMetadata(final String key, final String value) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.userMetadata(key, value));
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#nodeNames(Iterable)
       */
      public static AzureComputeTemplateOptions nodeNames(final Iterable<String> nodeNames) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.nodeNames(nodeNames));
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#networks(Iterable)
       */
      public static AzureComputeTemplateOptions networks(final Iterable<String> networks) {
         final AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.networks(networks));
      }
   }

   // methods that only facilitate returning the correct object type
   /**
    * @see org.jclouds.compute.options.TemplateOptions#blockOnPort(int, int)
    */
   @Override
   public AzureComputeTemplateOptions blockOnPort(final int port, final int seconds) {
      return AzureComputeTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * @see org.jclouds.compute.options.TemplateOptions#inboundPorts(int...)
    */
   @Override
   public AzureComputeTemplateOptions inboundPorts(final int... ports) {
      return AzureComputeTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see org.jclouds.compute.options.TemplateOptions#authorizePublicKey(String)
    */
   @Override
   public AzureComputeTemplateOptions authorizePublicKey(final String publicKey) {
      return AzureComputeTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see org.jclouds.compute.options.TemplateOptions#installPrivateKey(String)
    */
   @Override
   public AzureComputeTemplateOptions installPrivateKey(final String privateKey) {
      return AzureComputeTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions userMetadata(final Map<String, String> userMetadata) {
      return AzureComputeTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions userMetadata(final String key, final String value) {
      return AzureComputeTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions nodeNames(final Iterable<String> nodeNames) {
      return AzureComputeTemplateOptions.class.cast(super.nodeNames(nodeNames));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeTemplateOptions networks(final Iterable<String> networks) {
      return AzureComputeTemplateOptions.class.cast(super.networks(networks));
   }
}
