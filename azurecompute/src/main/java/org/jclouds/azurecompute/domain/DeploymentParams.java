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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.jclouds.azurecompute.domain.Image.OSType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * To create a new deployment/role
 *
 * Warning : the OSType must be the one of the source image used to create the VM
 */
// TODO: check which can be null.
public final class DeploymentParams {

   public static final class ExternalEndpoint {

      public String name() {
         return name;
      }

      /** Either {@code tcp} or {@code udp}. */
      public String protocol() {
         return protocol;
      }

      public int port() {
         return port;
      }

      public int localPort() {
         return localPort;
      }

      public static ExternalEndpoint inboundTcpToLocalPort(int port, int localPort) {
         return new ExternalEndpoint(String.format("tcp %s:%s", port, localPort), "tcp", port, localPort);
      }

      public static ExternalEndpoint inboundUdpToLocalPort(int port, int localPort) {
         return new ExternalEndpoint(String.format("udp %s:%s", port, localPort), "udp", port, localPort);
      }

      // TODO: Remove from here down with @AutoValue.
      private ExternalEndpoint(String name, String protocol, int port, int localPort) {
         this.name = checkNotNull(name, "name");
         this.protocol = checkNotNull(protocol, "protocol");
         this.port = port;
         this.localPort = localPort;
      }

      private final String name;
      private final String protocol;
      private final int port;
      private final int localPort;

      @Override public int hashCode() {
         return Objects.hashCode(name, protocol, localPort, port);
      }

      @Override public boolean equals(Object object) {
         if (this == object) {
            return true;
         }
         if (object instanceof ExternalEndpoint) {
            ExternalEndpoint that = ExternalEndpoint.class.cast(object);
            return equal(name, that.name)
                  && equal(protocol, that.protocol)
                  && equal(localPort, that.localPort)
                  && equal(port, that.port);
         } else {
            return false;
         }
      }

      @Override public String toString() {
         return toStringHelper(this)
               .add("name", name)
               .add("protocol", protocol)
               .add("port", port)
               .add("localPort", localPort).toString();
      }
   }

   /**
    * Specifies the name of a user to be created in the sudoers group of the
    * virtual machine. User names are ASCII character strings 1 to 32
    * characters in length.
    */
   public String username() {
      return username;
   }

   /** The size of the virtual machine to allocate. The default value is Small. */
   public RoleSize size() {
      return size;
   }

   /**
    * Specifies the associated password for the user name.
    * Passwords are ASCII character strings 6 to 72 characters in
    * length.
    */
   public String password() {
      return password;
   }

   /** {@link Image#name() name} of the user or platform image. */
   public String sourceImageName() {
      return sourceImageName;
   }

   /** Indicates the {@link Image#mediaLink() location} when {@link #sourceImageName() source} is a platform image. */
   public URI mediaLink() {
      return mediaLink;
   }

   /** {@link Image#os() Os type} of the {@link #sourceImageName() source image}. */
   public OSType os() {
      return os;
   }

   public List<ExternalEndpoint> externalEndpoints() {
      return externalEndpoints;
   }

   public Builder toBuilder() {
      return builder().fromDeploymentParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {
      private RoleSize size = RoleSize.SMALL;
      private String username;
      private String password;
      private String sourceImageName;
      private URI mediaLink;
      private OSType os;
      private List<ExternalEndpoint> externalEndpoints = Lists.newArrayList();

      public Builder size(RoleSize size) {
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

      public Builder os(OSType os) {
         this.os = os;
         return this;
      }

      public Builder externalEndpoint(ExternalEndpoint endpoint) {
         externalEndpoints.add(endpoint);
         return this;
      }

      public Builder externalEndpoints(Collection<ExternalEndpoint> externalEndpoints) {
         externalEndpoints.addAll(externalEndpoints);
         return this;
      }

      public DeploymentParams build() {
         return DeploymentParams.create(size, username, password, sourceImageName, mediaLink, os,
               ImmutableList.copyOf(externalEndpoints));
      }

      public Builder fromDeploymentParams(DeploymentParams in) {
         return size(in.size())
               .username(in.username())
               .password(in.password())
               .sourceImageName(in.sourceImageName())
               .mediaLink(in.mediaLink())
               .os(in.os())
               .externalEndpoints(in.externalEndpoints());
      }
   }

   private static DeploymentParams create(RoleSize size, String username, String password, String sourceImageName,
         URI mediaLink, OSType os, List<ExternalEndpoint> externalEndpoints) {
      return new DeploymentParams(size, username, password, sourceImageName, mediaLink, os, externalEndpoints);
   }

   // TODO: Remove from here down with @AutoValue.
   private DeploymentParams(RoleSize size, String username, String password, String sourceImageName, URI mediaLink,
         OSType os, List<ExternalEndpoint> externalEndpoints) {
      this.size = checkNotNull(size, "size");
      this.username = checkNotNull(username, "username");
      this.password = checkNotNull(password, "password");
      this.sourceImageName = checkNotNull(sourceImageName, "sourceImageName");
      this.mediaLink = checkNotNull(mediaLink, "mediaLink");
      this.os = checkNotNull(os, "os");
      this.externalEndpoints = checkNotNull(externalEndpoints, "externalEndpoints");
   }

   private final RoleSize size;
   private final String username;
   private final String password;
   private final String sourceImageName;
   private final URI mediaLink;
   private final OSType os;
   private final List<ExternalEndpoint> externalEndpoints;

   @Override
   public int hashCode() {
      return Objects.hashCode(sourceImageName, username, password, mediaLink, size, os, externalEndpoints);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof DeploymentParams) {
         DeploymentParams that = DeploymentParams.class.cast(object);
         return equal(size, that.size)
               && equal(username, that.username)
               && equal(password, that.password)
               && equal(sourceImageName, that.sourceImageName)
               && equal(mediaLink, that.mediaLink)
               && equal(os, that.os)
               && equal(externalEndpoints, that.externalEndpoints);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return toStringHelper(this)
            .add("size", size)
            .add("username", username)
            .add("password", password)
            .add("sourceImageName", sourceImageName)
            .add("mediaLink", mediaLink)
            .add("os", os)
            .add("externalEndpoints", externalEndpoints).toString();
   }
}
