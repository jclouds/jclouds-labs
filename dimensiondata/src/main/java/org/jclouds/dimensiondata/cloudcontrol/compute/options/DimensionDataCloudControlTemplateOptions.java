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
package org.jclouds.dimensiondata.cloudcontrol.compute.options;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.javax.annotation.Nullable;

public class DimensionDataCloudControlTemplateOptions extends TemplateOptions implements Cloneable {

   public static final String DEFAULT_NETWORK_DOMAIN_NAME = "JCLOUDS_NETWORK_DOMAIN";
   public static final String DEFAULT_VLAN_NAME = "JCLOUDS_VLAN";
   public static final String DEFAULT_PRIVATE_IPV4_BASE_ADDRESS = "10.0.0.0";
   public static final Integer DEFAULT_PRIVATE_IPV4_PREFIX_SIZE = 24;

   private String networkDomainName;
   private String defaultPrivateIPv4BaseAddress;
   private Integer defaultPrivateIPv4PrefixSize;

   public String getNetworkDomainName() {
      return networkDomainName;
   }

   public String getDefaultPrivateIPv4BaseAddress() {
      return defaultPrivateIPv4BaseAddress;
   }

   public Integer getDefaultPrivateIPv4PrefixSize() {
      return defaultPrivateIPv4PrefixSize;
   }

   public DimensionDataCloudControlTemplateOptions networkDomainName(@Nullable String networkDomainName) {
      this.networkDomainName = networkDomainName;
      return this;
   }

   public DimensionDataCloudControlTemplateOptions defaultPrivateIPv4BaseAddress(
         @Nullable String defaultPrivateIPv4BaseAddress) {
      this.defaultPrivateIPv4BaseAddress = defaultPrivateIPv4BaseAddress;
      return this;
   }

   public DimensionDataCloudControlTemplateOptions defaultPrivateIPv4PrefixSize(
         @Nullable Integer defaultPrivateIPv4PrefixSize) {
      this.defaultPrivateIPv4PrefixSize = defaultPrivateIPv4PrefixSize;
      return this;
   }

   @Override
   public DimensionDataCloudControlTemplateOptions clone() {
      final DimensionDataCloudControlTemplateOptions options = new DimensionDataCloudControlTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof DimensionDataCloudControlTemplateOptions)) {
         return false;
      }
      if (!super.equals(o)) {
         return false;
      }

      DimensionDataCloudControlTemplateOptions that = (DimensionDataCloudControlTemplateOptions) o;

      if (networkDomainName != null ?
            !networkDomainName.equals(that.networkDomainName) :
            that.networkDomainName != null) {
         return false;
      }

      if (defaultPrivateIPv4BaseAddress != null ?
            !defaultPrivateIPv4BaseAddress.equals(that.defaultPrivateIPv4BaseAddress) :
            that.defaultPrivateIPv4BaseAddress != null) {
         return false;
      }
      return defaultPrivateIPv4PrefixSize != null ?
            defaultPrivateIPv4PrefixSize.equals(that.defaultPrivateIPv4PrefixSize) :
            that.defaultPrivateIPv4PrefixSize == null;

   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (networkDomainName != null ? networkDomainName.hashCode() : 0);
      result = 31 * result + (defaultPrivateIPv4BaseAddress != null ? defaultPrivateIPv4BaseAddress.hashCode() : 0);
      result = 31 * result + (defaultPrivateIPv4PrefixSize != null ? defaultPrivateIPv4PrefixSize.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "DimensionDataCloudControlTemplateOptions{ networkDomainName='" + networkDomainName
            + "', defaultPrivateIPv4BaseAddress='" + defaultPrivateIPv4BaseAddress + "', defaultPrivateIPv4PrefixSize='"
            + defaultPrivateIPv4PrefixSize + "'}";
   }

   public static class Builder {

      /**
       * @see #networkDomainName
       */
      public static DimensionDataCloudControlTemplateOptions networkDomainName(final String networkDomainName) {
         final DimensionDataCloudControlTemplateOptions options = new DimensionDataCloudControlTemplateOptions();
         return options.networkDomainName(networkDomainName);
      }

      /**
       * @see #defaultPrivateIPv4BaseAddress
       */
      public static DimensionDataCloudControlTemplateOptions defaultPrivateIPv4BaseAddress(
            final String defaultPrivateIPv4BaseAddress) {
         final DimensionDataCloudControlTemplateOptions options = new DimensionDataCloudControlTemplateOptions();
         return options.defaultPrivateIPv4BaseAddress(defaultPrivateIPv4BaseAddress);
      }

      /**
       * @see #defaultPrivateIPv4PrefixSize
       */
      public static DimensionDataCloudControlTemplateOptions defaultPrivateIPv4PrefixSize(
            final Integer defaultPrivateIPv4PrefixSize) {
         final DimensionDataCloudControlTemplateOptions options = new DimensionDataCloudControlTemplateOptions();
         return options.defaultPrivateIPv4PrefixSize(defaultPrivateIPv4PrefixSize);
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DimensionDataCloudControlTemplateOptions nodeNames(Iterable<String> nodeNames) {
      return DimensionDataCloudControlTemplateOptions.class.cast(super.nodeNames(nodeNames));
   }
}
