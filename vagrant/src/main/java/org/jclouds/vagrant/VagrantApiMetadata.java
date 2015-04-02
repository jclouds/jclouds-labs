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
package org.jclouds.vagrant;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.vagrant.config.VagrantComputeServiceContextModule;
import org.jclouds.vagrant.reference.VagrantConstants;

import com.google.auto.service.AutoService;

@AutoService(ApiMetadata.class)
public class VagrantApiMetadata extends BaseApiMetadata {

   public VagrantApiMetadata() {
      this(new Builder());
   }

   protected VagrantApiMetadata(Builder builder) {
      super(builder);
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public static class Builder extends BaseApiMetadata.Builder<Builder> {

      protected Builder() {
         id("vagrant")
         .name("Vagrant API")
         .identityName("User")
         .credentialName("Password")
         .defaultEndpoint("https://atlas.hashicorp.com/")
         .documentation(URI.create("https://www.vagrantup.com/docs"))
         .view(ComputeServiceContext.class)
         .defaultIdentity("guest")
         .defaultCredential("guest")
         .defaultProperties(defaultProperties())
         .defaultModule(VagrantComputeServiceContextModule.class);
      }

      private Properties defaultProperties() {
         Properties defaultProperties = BaseApiMetadata.defaultProperties();
         defaultProperties.setProperty(VagrantConstants.JCLOUDS_VAGRANT_HOME, VagrantConstants.JCLOUDS_VAGRANT_HOME_DEFAULT);
         defaultProperties.put(ComputeServiceProperties.TEMPLATE, "osFamily=UBUNTU");
         return defaultProperties;
      }

      @Override
      public ApiMetadata build() {
         return new VagrantApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }

   }
}
