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
package org.jclouds.dimensiondata.cloudcontrol.config;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.dimensiondata.cloudcontrol.compute.functions.BaseImageToImage;
import org.jclouds.dimensiondata.cloudcontrol.compute.functions.OperatingSystemToOsFamily;
import org.jclouds.dimensiondata.cloudcontrol.domain.BaseImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.domain.internal.ServerWithExternalIp;

public class DimensionDataCloudControlComputeServiceContextModule
      extends ComputeServiceAdapterContextModule<ServerWithExternalIp, BaseImage, BaseImage, Datacenter> {

   @Override
   protected void configure() {
      super.configure();

      bind(new TypeLiteral<Function<BaseImage, Image>>() {
      }).to(BaseImageToImage.class);
      bind(new TypeLiteral<Function<OperatingSystem, OsFamily>>() {
      }).to(OperatingSystemToOsFamily.class);

   }

}
