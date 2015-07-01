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
package org.jclouds.azurecompute.domain;

import static com.google.common.collect.ImmutableList.copyOf;
import java.net.URI;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/jj157193.aspx">Role</a>
 */
@AutoValue
public abstract class Role {

   @AutoValue
   public abstract static class ConfigurationSet {

      @AutoValue
      public abstract static class InputEndpoint {

         @AutoValue
         public abstract static class LoadBalancerProbe {

            public abstract String path();

            public abstract int port();

            public abstract String protocol();

            LoadBalancerProbe() { // For AutoValue only!
            }

            public static LoadBalancerProbe create(final String path, final int port, final String protocol) {
               return new AutoValue_Role_ConfigurationSet_InputEndpoint_LoadBalancerProbe(path, port, protocol);
            }
         }

         public abstract int localPort();

         public abstract String name();

         public abstract Integer port();

         public abstract String protocol();

         @Nullable
         public abstract String vip();

         public abstract Boolean enableDirectServerReturn();

         @Nullable
         public abstract String loadBalancerName();

         @Nullable
         public abstract LoadBalancerProbe loadBalancerProbe();

         @Nullable
         public abstract Integer idleTimeoutInMinutes();

         InputEndpoint() { // For AutoValue only!
         }

         public static InputEndpoint create(
                 final String name, final String protocol, final int localPort, final int port,
                 final String vip, final boolean enableDirectServerReturn, final String loadBalancerName,
                 final LoadBalancerProbe loadBalancerProbe, final Integer idleTimeoutInMinutes) {

            return new AutoValue_Role_ConfigurationSet_InputEndpoint(localPort, name, port, protocol, vip,
                    enableDirectServerReturn, loadBalancerName, loadBalancerProbe, idleTimeoutInMinutes);
         }
      }

      @AutoValue
      public abstract static class SubnetName {

         @Nullable
         public abstract String name();

         SubnetName() { // For AutoValue only!
         }

         public static SubnetName create(final String name) {
            return new AutoValue_Role_ConfigurationSet_SubnetName(name);
         }
      }

      @AutoValue
      public abstract static class PublicIP {

         public abstract String name();

         public abstract int idleTimeoutInMinutes();

         PublicIP() { // For AutoValue only!
         }

         public static PublicIP create(final String name, final int idleTimeoutInMinutes) {
            return new AutoValue_Role_ConfigurationSet_PublicIP(name, idleTimeoutInMinutes);
         }
      }

      public abstract String configurationSetType();

      public abstract List<InputEndpoint> inputEndpoints();

      @Nullable
      public abstract List<SubnetName> subnetNames();

      @Nullable
      public abstract String staticVirtualNetworkIPAddress();

      @Nullable
      public abstract List<PublicIP> publicIPs();

      @Nullable
      public abstract String networkSecurityGroup();

      ConfigurationSet() { // For AutoValue only!
      }

      public static ConfigurationSet create(
              final String configurationSetType, final List<InputEndpoint> inputEndpoints,
              final List<SubnetName> subnetNames, final String staticVirtualNetworkIPAddress,
              final List<PublicIP> publicIPs, final String networkSecurityGroup) {

         return new AutoValue_Role_ConfigurationSet(configurationSetType, inputEndpoints, subnetNames,
                 staticVirtualNetworkIPAddress, publicIPs, networkSecurityGroup);
      }
   }

   @AutoValue
   public abstract static class ResourceExtensionReference {

      @AutoValue
      public abstract static class ResourceExtensionParameterValue {

         public abstract String key();

         public abstract String value();

         public abstract String type();

         ResourceExtensionParameterValue() { // For AutoValue only!
         }

         public static ResourceExtensionParameterValue create(
                 final String key, final String value, final String type) {

            return new AutoValue_Role_ResourceExtensionReference_ResourceExtensionParameterValue(key, value, type);
         }
      }

      @Nullable public abstract String referenceName();

      @Nullable public abstract String publisher();

      @Nullable public abstract String name();

      @Nullable public abstract String version();

      @Nullable public abstract List<ResourceExtensionParameterValue> resourceExtensionParameterValues();

      @Nullable public abstract String state();

      ResourceExtensionReference() { // For AutoValue only!
      }

      public static ResourceExtensionReference create(
              final String referenceName, final String publisher, final String name, final String version,
              final List<ResourceExtensionParameterValue> resourceExtensionParameterValues, final String state) {

         return new AutoValue_Role_ResourceExtensionReference(referenceName, publisher, name, version,
                 resourceExtensionParameterValues, state);
      }
   }

   @AutoValue
   public abstract static class OSVirtualHardDisk {

      public abstract String hostCaching();

      public abstract String diskName();

      @Nullable
      public abstract Integer lun();

      @Nullable
      public abstract Integer logicalDiskSizeInGB();

      public abstract URI mediaLink();

      public abstract String sourceImageName();

      public abstract OSImage.Type os();

      OSVirtualHardDisk() { // For AutoValue only!
      }

      public static OSVirtualHardDisk create(final String hostCaching, final String diskName, final Integer lun,
              final Integer logicalDiskSizeInGB, final URI mediaLink, final String sourceImageName,
              final OSImage.Type os) {

         return new AutoValue_Role_OSVirtualHardDisk(hostCaching, diskName, lun, logicalDiskSizeInGB, mediaLink,
                 sourceImageName, os);
      }
   }

   /**
    * Represents the name of the Virtual Machine.
    */
   public abstract String roleName();

   /**
    * Specifies the type of role that is used. For Virtual Machines, this must be PersistentVMRole.
    */
   public abstract String roleType();

   /**
    * Specifies the name of the VM Image that was used to create the Virtual Machine.
    */
   @Nullable
   public abstract String vmImage();

   /**
    * Specifies the path to the VHD files that are associated with the VM Image.
    */
   @Nullable
   public abstract String mediaLocation();

   /**
    * Contains a collection of configuration sets that define system and application settings.
    */
   public abstract List<ConfigurationSet> configurationSets();

   /**
    * Optional. Contains a collection of resource extensions that are installed on the Virtual Machine. This element is
    * used if ProvisionGuestAgent is set to true.
    */
   @Nullable
   public abstract List<ResourceExtensionReference> resourceExtensionReferences();

   /**
    * Specifies the name of a collection of Virtual Machines. Virtual Machines specified in the same availability set
    * are allocated to different nodes to maximize availability.
    */
   @Nullable
   public abstract String availabilitySetName();

   /**
    * Contains the parameters that were used to add a data disk to a Virtual Machine.
    */
   @Nullable
   public abstract List<DataVirtualHardDisk> dataVirtualHardDisks();

   /**
    * Contains the parameters that were used to create the operating system disk for a Virtual Machine.
    */
   public abstract OSVirtualHardDisk osVirtualHardDisk();

   /**
    * Specifies the size of the Virtual Machine.
    */
   public abstract RoleSize.Type roleSize();

   /**
    * Optional. Indicates whether the VM Agent is installed on the Virtual Machine. To run a resource extension in a
    * Virtual Machine, this service must be installed.
    *
    * @return true or false
    */
   @Nullable
   public abstract Boolean provisionGuestAgent();

   /**
    * Specifies the read-only thumbprint of the certificate that is used with the HTTPS listener for WinRM.
    */
   @Nullable
   public abstract String defaultWinRmCertificateThumbprint();

   public static Role create(final String roleName, final String roleType, final String vmImage,
           final String mediaLocation, final List<ConfigurationSet> configurationSets,
           final List<ResourceExtensionReference> resourceExtensionReferences,
           final String availabilitySetName, final List<DataVirtualHardDisk> dataVirtualHardDisks,
           final OSVirtualHardDisk osVirtualHardDisk, final RoleSize.Type roleSize, final Boolean provisionGuestAgent,
           final String defaultWinRmCertificateThumbprint) {

      return new AutoValue_Role(roleName, roleType, vmImage, mediaLocation, copyOf(configurationSets),
              copyOf(resourceExtensionReferences), availabilitySetName, copyOf(dataVirtualHardDisks),
              osVirtualHardDisk, roleSize, provisionGuestAgent, defaultWinRmCertificateThumbprint);
   }
}
