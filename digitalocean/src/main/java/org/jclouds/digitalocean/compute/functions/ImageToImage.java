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
package org.jclouds.digitalocean.compute.functions;

import static org.jclouds.compute.domain.OperatingSystem.builder;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.OperatingSystem;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * Transforms an {@link Image} to the jclouds portable model.
 */
@Singleton
public class ImageToImage implements Function<Image, org.jclouds.compute.domain.Image> {

   @Override
   public org.jclouds.compute.domain.Image apply(final Image input) {
      ImageBuilder builder = new ImageBuilder();
      // Private images don't have a slug
      builder.id(input.getSlug() != null ? input.getSlug() : String.valueOf(input.getId()));
      builder.providerId(String.valueOf(input.getId()));
      builder.name(input.getName());
      builder.description(input.getName());
      builder.status(Status.AVAILABLE);

      OperatingSystem os = input.getOs();

      builder.operatingSystem(builder() 
            .name(input.getName()) 
            .family(os.getDistribution().getOsFamily()) 
            .description(input.getName()) 
            .arch(os.getArch()) 
            .version(os.getVersion()) 
            .is64Bit(os.is64bit()) 
            .build());

      ImmutableMap.Builder<String, String> metadata = ImmutableMap.builder();
      metadata.put("publicImage", String.valueOf(input.isPublicImage()));
      builder.userMetadata(metadata.build());

      return builder.build();
   }

}
