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
import java.util.Collection;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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

      /** Either {@code tcp} or {@code udp}. */
      public abstract String protocol();

      public abstract int port();

      public abstract int localPort();

      public static ExternalEndpoint inboundTcpToLocalPort(int port, int localPort) {
         return new AutoValue_DeploymentParams_ExternalEndpoint(String.format("tcp_%s-%s", port, localPort), "tcp",
               port, localPort);
      }

      public static ExternalEndpoint inboundUdpToLocalPort(int port, int localPort) {
         return new AutoValue_DeploymentParams_ExternalEndpoint(String.format("udp_%s-%s", port, localPort), "udp",
               port, localPort);
      }

      ExternalEndpoint() { // For AutoValue only!
      }
   }

   DeploymentParams() {} // For AutoValue only!

   /** The user-supplied name for this deployment. */
   public abstract String name();

   /** The size of the virtual machine to allocate. The default value is Small. */
   public abstract RoleSize.Type size();

   /**
    * Specifies the name of a user to be created in the sudoers group of the
    * virtual machine. User names are ASCII character strings 1 to 32
    * characters in length.
    */
   public abstract String username();

   /**
    * Specifies the associated password for the user name.
    * Passwords are ASCII character strings 6 to 72 characters in
    * length.
    */
   public abstract String password();

   /** {@link OSImage#name() name} of the user or platform image. */
   public abstract String sourceImageName();

   /** Indicates the {@link OSImage#mediaLink() location} when {@link #sourceImageName() source} is a platform image. */
   public abstract URI mediaLink();

   /** {@link OSImage#os() Os type} of the {@link #sourceImageName() source image}. */
   public abstract OSImage.Type os();

   public abstract List<ExternalEndpoint> externalEndpoints();

   /** {@link org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkSite#name} */
   @Nullable public abstract String virtualNetworkName();

   public abstract List<String> subnetNames();

   public Builder toBuilder() {
      return builder().fromDeploymentParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {
      private String name;
      private RoleSize.Type size;
      private String username;
      private String password;
      private String sourceImageName;
      private URI mediaLink;
      private OSImage.Type os;
      private List<ExternalEndpoint> externalEndpoints = Lists.newArrayList();
      private String virtualNetworkName;
      private List<String> subnetNames = Lists.newArrayList();

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder size(RoleSize.Type size) {
         this.size = size;
         return this;
      }

      public Builder username(String username) {
         this.username = username;
         return this;
      }

      public Builder password(String password) {
         this.password = password;
         return this;
      }

      public Builder sourceImageName(String sourceImageName) {
         this.sourceImageName = sourceImageName;
         return this;
      }

      public Builder mediaLink(URI mediaLink) {
         this.mediaLink = mediaLink;
         return this;
      }

      public Builder os(OSImage.Type os) {
         this.os = os;
         return this;
      }

      public Builder externalEndpoint(ExternalEndpoint endpoint) {
         externalEndpoints.add(endpoint);
         return this;
      }

      public Builder externalEndpoints(Collection<ExternalEndpoint> externalEndpoints) {
         this.externalEndpoints.addAll(externalEndpoints);
         return this;
      }

      public Builder virtualNetworkName(String virtualNetworkName) {
         this.virtualNetworkName = virtualNetworkName;
         return this;
      }

      public Builder subnetName(String subnetName) {
         subnetNames.add(subnetName);
         return this;
      }

      public Builder subnetNames(Collection<String> subnetNames) {
         this.subnetNames.addAll(subnetNames);
         return this;
      }

      public DeploymentParams build() {
         return DeploymentParams.create(name, size, username, password, sourceImageName, mediaLink, os,
               ImmutableList.copyOf(externalEndpoints), virtualNetworkName, ImmutableList.copyOf(subnetNames));
      }

      public Builder fromDeploymentParams(DeploymentParams in) {
         return name(in.name())
               .size(in.size())
               .username(in.username())
               .password(in.password())
               .sourceImageName(in.sourceImageName())
               .mediaLink(in.mediaLink())
               .os(in.os())
               .externalEndpoints(in.externalEndpoints())
               .subnetNames(in.subnetNames());
      }
   }

   private static DeploymentParams create(String name, RoleSize.Type size, String username, String password, String sourceImageName,
                                          URI mediaLink, OSImage.Type os, List<ExternalEndpoint> externalEndpoints,
                                          String virtualNetworkName, List<String> subnetNames) {
      return new AutoValue_DeploymentParams(name, size, username, password, sourceImageName, mediaLink, os,
              copyOf(externalEndpoints), virtualNetworkName, copyOf(subnetNames));
   }
}
