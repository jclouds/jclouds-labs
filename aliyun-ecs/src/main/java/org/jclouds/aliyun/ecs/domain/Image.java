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
import com.google.common.collect.ImmutableMap;
import org.jclouds.json.SerializedNames;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoValue
public abstract class Image {

   Image() {}

   @SerializedNames({"ImageId", "Description", "ProductCode", "OSType", "Architecture", "OSName", "DiskDeviceMappings",
           "ImageOwnerAlias", "Progress", "IsSupportCloudinit", "Usage", "CreationTime", "Tags",
           "ImageVersion", "Status", "ImageName", "IsSupportIoOptimized", "IsSelfShared", "IsCopied",
           "IsSubscribed", "Platform", "Size"})
   public static Image create(String imageId, String description, String productCode, String osType,
                              String architecture, String osName, Map<String, List<DiskDeviceMapping>> diskDeviceMappings,
                              String imageOwnerAlias, String progress, Boolean isSupportCloudinit, String usage, Date creationTime,
                              Map<String, List<Tag>> tags, String imageVersion, String status, String imageName,
                              Boolean isSupportIoOptimized, Boolean isSelfShared, Boolean isCopied, Boolean isSubscribed, String platform,
                              String size) {
      return new AutoValue_Image(imageId, description, productCode, osType, architecture, osName,
              diskDeviceMappings == null ?
                      ImmutableMap.<String, List<DiskDeviceMapping>>of() :
                      ImmutableMap.copyOf(diskDeviceMappings), imageOwnerAlias, progress, isSupportCloudinit, usage,
              creationTime, tags == null ? ImmutableMap.<String, List<Tag>>of() : ImmutableMap.copyOf(tags), imageVersion,
              status, imageName, isSupportIoOptimized, isSelfShared, isCopied, isSubscribed, platform, size);
   }

   public abstract String imageId();

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

   public abstract String status();

   public abstract String imageName();

   public abstract Boolean isSupportIoOptimizeds();

   public abstract Boolean isSelfShared();

   public abstract Boolean isCopied();

   public abstract Boolean isSubscribed();

   public abstract String platform();

   public abstract String size();

}
