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
package org.jclouds.fujitsu.fgcp;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.fujitsu.fgcp.compute.config.FGCPComputeServiceContextModule;
import org.jclouds.fujitsu.fgcp.config.FGCPHttpApiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Fujitsu's Global Cloud Platform (FGCP)
 */
public class FGCPApiMetadata extends BaseHttpApiMetadata<FGCPApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public FGCPApiMetadata() {
      this(new Builder());
   }

   protected FGCPApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      // enables peer verification using the CAs bundled with the JRE (or
      // value of javax.net.ssl.trustStore if set)
      properties.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "false");
      // create/delete operations on resources in the same system in FGCP are not
      // allowed to run simultaneously: the 2nd operation will get an error.
      // Tuning retry parameters accordingly:
      properties.setProperty(Constants.PROPERTY_RETRY_DELAY_START, "10000L"); // 10 sec.
      properties.setProperty(Constants.PROPERTY_MAX_RETRIES, "100"); // means 1000s (17min) timeout
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<FGCPApi, Builder> {

      protected Builder() {
         id("fgcp")
               .name("Fujitsu Global Cloud Platform (FGCP)")
               .identityName("user name (not used)")
               .credentialName("PEM converted from UserCert.p12")
               .documentation(
                     URI.create("https://globalcloud.fujitsu.com.au/portala/ctrl/aboutSopManual"))
               .version(FGCPApi.VERSION)
               .defaultEndpoint(
                     "https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint")
               .defaultProperties(FGCPApiMetadata.defaultProperties())
               .view(typeToken(ComputeServiceContext.class))
               .defaultModules(
                     ImmutableSet.<Class<? extends Module>> of(
                           FGCPComputeServiceContextModule.class,
                           FGCPHttpApiModule.class));
      }

      @Override
      public FGCPApiMetadata build() {
         return new FGCPApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
