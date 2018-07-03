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
package org.jclouds.aliyun.ecs.domain;

import com.google.auto.value.AutoValue;
import com.google.common.base.CaseFormat;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jclouds.json.SerializedNames;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@AutoValue
public abstract class Image {

   public enum Status {
      AVAILABLE, UNAVAILABLE;


      public static Status fromValue(String value) {
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         checkArgument(status.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(Status.values()), value);
         return status.get();
      }

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }
   }

   Image() {}

   @SerializedNames({"ImageId", "Description", "ProductCode", "OSType", "Architecture", "OSName", "DiskDeviceMappings",
           "ImageOwnerAlias", "Progress", "IsSupportCloudinit", "Usage", "CreationTime", "Tags",
           "ImageVersion", "Status", "ImageName", "IsSupportIoOptimized", "IsSelfShared", "IsCopied",
           "IsSubscribed", "Platform", "Size"})
   public static Image create(String id, String description, String productCode, String osType,
                              String architecture, String osName, Map<String, List<DiskDeviceMapping>> diskDeviceMappings,
                              String imageOwnerAlias, String progress, Boolean isSupportCloudinit, String usage, Date creationTime,
                              Map<String, List<Tag>> tags, String imageVersion, Status status, String name,
                              Boolean isSupportIoOptimized, Boolean isSelfShared, Boolean isCopied, Boolean isSubscribed, String platform,
                              int size) {
      return builder().id(id).description(description).productCode(productCode).osType(osType)
              .architecture(architecture).osName(osName).diskDeviceMappings(diskDeviceMappings).imageOwnerAlias(imageOwnerAlias)
              .progress(progress).isSupportCloudinit(isSupportCloudinit).usage(usage).creationTime(creationTime)
              .tags(tags).imageVersion(imageVersion).status(status).name(name).isSupportIoOptimizeds(isSupportIoOptimized)
              .isSelfShared(isSelfShared).isCopied(isCopied).isSubscribed(isSubscribed).platform(platform).size(size)
              .build();
   }

   public abstract String id();

   public abstract String description();

   public abstract String productCode();

   public abstract String osType();

   public abstract String architecture();

   public abstract String osName();

   public abstract Map<String, List<DiskDeviceMapping>> diskDeviceMappings();

   public abstract String imageOwnerAlias();

   public abstract String progress();

   public abstract Boolean isSupportCloudinit();

   public abstract String usage();

   public abstract Date creationTime();

   public abstract Map<String, List<Tag>> tags();

   public abstract String imageVersion();

   public abstract Status status();

   public abstract String name();

   public abstract Boolean isSupportIoOptimizeds();

   public abstract Boolean isSelfShared();

   public abstract Boolean isCopied();

   public abstract Boolean isSubscribed();

   public abstract String platform();

   public abstract int size();

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_Image.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);

      public abstract Builder description(String description);

      public abstract Builder productCode(String productCode);

      public abstract Builder osType(String osType);

      public abstract Builder architecture(String architecture);

      public abstract Builder osName(String osName);

      public abstract Builder diskDeviceMappings(Map<String, List<DiskDeviceMapping>> diskDeviceMappings);

      public abstract Builder imageOwnerAlias(String imageOwnerAlias);

      public abstract Builder progress(String progress);

      public abstract Builder isSupportCloudinit(Boolean isSupportCloudinit);

      public abstract Builder usage(String usage);

      public abstract Builder creationTime(Date creationTime);

      public abstract Builder tags(Map<String, List<Tag>> tags);

      public abstract Builder imageVersion(String imageVersion);

      public abstract Builder status(Status status);

      public abstract Builder name(String name);

      public abstract Builder isSupportIoOptimizeds(Boolean isSupportIoOptimizeds);

      public abstract Builder isSelfShared(Boolean isSelfShared);

      public abstract Builder isCopied(Boolean isCopied);

      public abstract Builder isSubscribed(Boolean isSubscribed);

      public abstract Builder platform(String platform);

      public abstract Builder size(int size);

      abstract Image autoBuild();

      abstract Map<String, List<DiskDeviceMapping>> diskDeviceMappings();

      abstract Map<String, List<Tag>> tags();

      public Image build() {
         diskDeviceMappings(diskDeviceMappings() != null ? ImmutableMap.copyOf(diskDeviceMappings()) : ImmutableMap.<String, List<DiskDeviceMapping>>of());
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : ImmutableMap.<String, List<Tag>>of());
         return autoBuild();
      }
   }

}
