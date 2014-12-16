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
 * Contains options supported by the
 * {@link org.jclouds.compute.ComputeService#createNodesInGroup(String, int, org.jclouds.compute.options.TemplateOptions)} and
 * {@link org.jclouds.compute.ComputeService#createNodesInGroup(String, int, org.jclouds.compute.options.TemplateOptions)}
 * operations on the <em>gogrid</em> provider.
 *
 * <h2>Usage</h2> The recommended way to instantiate a
 * {@link AzureComputeTemplateOptions} object is to statically import
 * {@code AzureComputeTemplateOptions.*} and invoke a static creation method
 * followed by an instance mutator (if needed):
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
      AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof AzureComputeTemplateOptions) {
         AzureComputeTemplateOptions eTo = AzureComputeTemplateOptions.class.cast(to);
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

   public TemplateOptions virtualNetworkName(String virtualNetworkName) {
      this.virtualNetworkName = Optional.of(virtualNetworkName);
      return this;
   }

   public TemplateOptions addressSpaceAddressPrefix(String addressSpaceAddressPrefix) {
      this.addressSpaceAddressPrefix = Optional.of(addressSpaceAddressPrefix);
      return this;
   }

   public TemplateOptions subnetName(String subnetName) {
      this.subnetName = Optional.of(subnetName);
      return this;
   }

   public TemplateOptions networkSecurityGroupName(String networkSecurityGroupName) {
      this.networkSecurityGroupName = Optional.of(networkSecurityGroupName);
      return this;
   }

   public TemplateOptions subnetAddressPrefix(String subnetAddressPrefix) {
      this.subnetAddressPrefix = Optional.of(subnetAddressPrefix);
      return this;
   }

   public TemplateOptions storageAccountName(String storageAccountName) {
      this.storageAccountName = Optional.of(storageAccountName);
      return this;
   }

   public TemplateOptions storageAccountType(String storageAccountType) {
      this.storageAccountType = Optional.of(storageAccountType);
      return this;
   }

   public Optional<String> getVirtualNetworkName() {
      return virtualNetworkName;
   }

   public Optional<String> getAddressSpaceAddressPrefix() {
      return addressSpaceAddressPrefix;
   }

   public Optional<String> getSubnetName() { return subnetName; }

   public Optional<String> getSubnetAddressPrefix() { return subnetAddressPrefix; }

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
      public static AzureComputeTemplateOptions virtualNetworkName(String virtualNetworkName) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.virtualNetworkName(virtualNetworkName));
      }

      /**
       * @see #addressSpaceAddressPrefix
       */
      public static AzureComputeTemplateOptions addressSpaceAddressPrefix(String addressSpaceAddressPrefix) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.addressSpaceAddressPrefix(addressSpaceAddressPrefix));
      }

      /**
       * @see #subnetName
       */
      public static AzureComputeTemplateOptions subnetName(String subnetName) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.subnetName(subnetName));
      }

      /**
       * @see #networkSecurityGroupName
       */
      public static AzureComputeTemplateOptions networkSecurityGroupName(String networkSecurityGroupName) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.subnetName(networkSecurityGroupName));
      }

      /**
       * @see #subnetAddressPrefix
       */
      public static AzureComputeTemplateOptions subnetAddressPrefix(String subnetAddressPrefix) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.subnetAddressPrefix(subnetAddressPrefix));
      }

      /**
       * @see #storageAccountName
       */
      public static AzureComputeTemplateOptions storageAccountName(String storageAccountName) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.storageAccountName(storageAccountName));
      }

      /**
       * @see #storageAccountType
       */
      public static AzureComputeTemplateOptions storageAccountType(String storageAccountType) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.storageAccountType(storageAccountType));
      }

      // methods that only facilitate returning the correct object type

      /**
       * @see org.jclouds.compute.options.TemplateOptions#inboundPorts(int...)
       */
      public static AzureComputeTemplateOptions inboundPorts(int... ports) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#blockOnPort(int, int)
       */
      public static AzureComputeTemplateOptions blockOnPort(int port, int seconds) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#userMetadata(java.util.Map)
       */
      public static AzureComputeTemplateOptions userMetadata(Map<String, String> userMetadata) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#userMetadata(String, String)
       */
      public static AzureComputeTemplateOptions userMetadata(String key, String value) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.userMetadata(key, value));
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#nodeNames(Iterable)
       */
      public static AzureComputeTemplateOptions nodeNames(Iterable<String> nodeNames) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.nodeNames(nodeNames));
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#networks(Iterable)
       */
      public static AzureComputeTemplateOptions networks(Iterable<String> networks) {
         AzureComputeTemplateOptions options = new AzureComputeTemplateOptions();
         return AzureComputeTemplateOptions.class.cast(options.networks(networks));
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * @see org.jclouds.compute.options.TemplateOptions#blockOnPort(int, int)
    */
   @Override
   public AzureComputeTemplateOptions blockOnPort(int port, int seconds) {
      return AzureComputeTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * @see org.jclouds.compute.options.TemplateOptions#inboundPorts(int...)
    */
   @Override
   public AzureComputeTemplateOptions inboundPorts(int... ports) {
      return AzureComputeTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see org.jclouds.compute.options.TemplateOptions#authorizePublicKey(String)
    */
   @Override
   public AzureComputeTemplateOptions authorizePublicKey(String publicKey) {
      return AzureComputeTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see org.jclouds.compute.options.TemplateOptions#installPrivateKey(String)
    */
   @Override
   public AzureComputeTemplateOptions installPrivateKey(String privateKey) {
      return AzureComputeTemplateOptions.class.cast(super.installPrivateKey(privateKey));
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
