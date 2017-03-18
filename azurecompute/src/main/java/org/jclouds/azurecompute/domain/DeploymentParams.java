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

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.SinceApiVersion;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * To create a new deployment/role
 *
 * Warning : the OSType must be the one of the source image used to create the VM
 */
// TODO: check which can be null.
@AutoValue
public abstract class DeploymentParams {

   @AutoValue
   public abstract static class ExternalEndpoint {

      public abstract String name();

      /**
       * Either {@code tcp} or {@code udp}.
       */
      public abstract String protocol();

      public abstract int port();

      public abstract int localPort();

      public static ExternalEndpoint inboundTcpToLocalPort(final int port, final int localPort) {
         return new AutoValue_DeploymentParams_ExternalEndpoint(
                 String.format("tcp_%s-%s", port, localPort), "tcp", port, localPort);
      }

      public static ExternalEndpoint inboundUdpToLocalPort(final int port, final int localPort) {
         return new AutoValue_DeploymentParams_ExternalEndpoint(
                 String.format("udp_%s-%s", port, localPort), "udp", port, localPort);
      }

      ExternalEndpoint() { // For AutoValue only!
      }
   }

   DeploymentParams() {
   } // For AutoValue only!

   /**
    * The user-supplied name for this deployment.
    */
   public abstract String name();

   /**
    * The size of the virtual machine to allocate. The default value is Small.
    */
   public abstract RoleSize.Type size();

   /**
    * Specifies the name of a user to be created in the sudoers group of the virtual machine. User names are ASCII
    * character strings 1 to 32 characters in length.
    */
   public abstract String username();

   /**
    * Specifies the associated password for the user name. Passwords are ASCII character strings 6 to 72 characters in
    * length.
    */
   public abstract String password();

   /**
    * {@link OSImage#name() name} of the user or platform image.
    */
   public abstract String sourceImageName();

   /**
    * Indicates the {@link OSImage#mediaLink() location} when {@link #sourceImageName() source} is a platform image.
    */
   public abstract URI mediaLink();

   /**
    * {@link OSImage#os() Os type} of the {@link #sourceImageName() source image}.
    */
   public abstract OSImage.Type os();

   public abstract Set<ExternalEndpoint> externalEndpoints();

   /**
    * {@link org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkSite#name}
    */
   @Nullable
   public abstract String virtualNetworkName();

   /**
    * Optional. Specifies the name of a reserved IP address that is to be assigned to the deployment. You must run
    * Create Reserved IP Address before you can assign the address to the deployment using this element.
    *
    * The ReservedIPName element is only available using version 2014-05-01 or higher.
    *
    * @return reserved IP.
    */
   @SinceApiVersion("2014-05-01")
   @Nullable
   public abstract String reservedIPName();

   public abstract List<String> subnetNames();

   /**
    * Optional. Indicates whether the VM Agent is installed on the Virtual
    * Machine. To run a resource extension in a Virtual Machine, this agent must
    * be installed.
    *
    * @return provisionGuestAgent true/false flag (or null)
    */
   @Nullable
   public abstract Boolean provisionGuestAgent();

   /**
    * Optional. Indicates whether Windows VM should be provisioned with Https WinRm listener.
    * By default it will use http listener.
    */
   @Nullable
   public abstract Boolean winrmUseHttps();

   public static Builder builder() {
      return new AutoValue_DeploymentParams.Builder()
              .externalEndpoints(ImmutableSet.<ExternalEndpoint> of())
              .subnetNames(ImmutableList.<String> of());
   }

   abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder name(String name);
      public abstract Builder size(RoleSize.Type roleSize);
      public abstract Builder username(String username);
      public abstract Builder password(String password);
      public abstract Builder winrmUseHttps(Boolean useHttps);
      public abstract Builder sourceImageName(String sourceImageName);
      public abstract Builder mediaLink(URI mediaLink);
      public abstract Builder os(OSImage.Type os);
      public abstract Builder externalEndpoints(Set<ExternalEndpoint> externalEndpoints);
      public abstract Builder virtualNetworkName(String virtualNetworkName);
      public abstract Builder reservedIPName(String reservedIPName);
      public abstract Builder subnetNames(List<String> subnetNames);
      public abstract Builder provisionGuestAgent(Boolean provisionGuestAgent);

      abstract Set<ExternalEndpoint> externalEndpoints();
      abstract List<String> subnetNames();

      abstract DeploymentParams autoBuild();

      public DeploymentParams build() {
         externalEndpoints(externalEndpoints() != null ? ImmutableSet.copyOf(externalEndpoints()) : null);
         subnetNames(subnetNames() != null ? ImmutableList.copyOf(subnetNames()) : null);
         return autoBuild();
      }
   }

   public static DeploymentParams create(String name, RoleSize.Type size, String username,
                                         String password, String sourceImageName, URI mediaLink,
                                         OSImage.Type os, Set<ExternalEndpoint> externalEndpoints,
                                         String virtualNetworkName, String reservedIPName,
                                         List<String> subnetNames, Boolean provisionGuestAgent) {
      return builder().name(name).size(size).username(username).password(password)
              .sourceImageName(sourceImageName).mediaLink(mediaLink).os(os)
              .externalEndpoints(externalEndpoints).virtualNetworkName(virtualNetworkName)
              .reservedIPName(reservedIPName).subnetNames(subnetNames)
              .provisionGuestAgent(provisionGuestAgent)
              .build();
   }
}
