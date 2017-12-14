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
package org.jclouds.dimensiondata.cloudcontrol;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule;
import org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlHttpApiModule;
import org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlParserModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataProperties.OPERATION_TIMEOUT;
import static org.jclouds.reflect.Reflection2.typeToken;

public class DimensionDataCloudControlApiMetadata extends BaseHttpApiMetadata<DimensionDataCloudControlApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public DimensionDataCloudControlApiMetadata() {
      this(new Builder());
   }

   protected DimensionDataCloudControlApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<DimensionDataCloudControlApi, Builder> {

      protected Builder() {
         final Properties defaultProperties = DimensionDataCloudControlApiMetadata.defaultProperties();
         defaultProperties.put(OPERATION_TIMEOUT, 30 * 60 * 1000);
         id("dimensiondata-cloudcontrol").name("DimensionData CloudControl API").identityName("user name")
               .credentialName("user password")
               .documentation(URI.create("http://www.dimensiondata.com/en-US/Solutions/Cloud"))
               .defaultEndpoint("https://api-REGION.dimensiondata.com/caas").version("2.4")
               .defaultProperties(defaultProperties).view(typeToken(ComputeServiceContext.class)).defaultModules(
               ImmutableSet.<Class<? extends Module>>builder().add(DimensionDataCloudControlHttpApiModule.class)
                     .add(DimensionDataCloudControlParserModule.class)
                     .add(DimensionDataCloudControlComputeServiceContextModule.class).build());
      }

      @Override
      public DimensionDataCloudControlApiMetadata build() {
         return new DimensionDataCloudControlApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         return this;
      }
   }
}
