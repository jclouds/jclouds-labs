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
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class InstanceType {

   InstanceType() {
   }

   @SerializedNames(
           {"InstanceTypeId", "InstanceTypeFamily", "EniQuantity",
            "GPUSpec", "CpuCoreCount", "MemorySize", "GPUAmount", "LocalStorageCategory" })
   public static InstanceType create(String id, String instanceTypeFamily, Integer eniQuantity,
                                     String gpuSpec, Integer cpuCoreCount, Double memorySize, Double gpuAmount, String localStorageCategory) {
      return builder()
              .id(id).instanceTypeFamily(instanceTypeFamily).eniQuantity(eniQuantity)
              .gpuSpec(gpuSpec).cpuCoreCount(cpuCoreCount).memorySize(memorySize)
              .gpuAmount(gpuAmount).localStorageCategory(localStorageCategory)
              .build();
   }

   public abstract String id();

   public abstract Integer cpuCoreCount();

   public abstract String instanceTypeFamily();

   public abstract Integer eniQuantity();

   public abstract String gpuSpec();

   public abstract Double memorySize();

   public abstract Double gpuAmount();

   public abstract String localStorageCategory();

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_InstanceType.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);

      public abstract Builder cpuCoreCount(Integer cpuCoreCount);

      public abstract Builder instanceTypeFamily(String instanceTypeFamily);

      public abstract Builder eniQuantity(Integer eniQuantity);

      public abstract Builder gpuSpec(String gpuSpec);

      public abstract Builder memorySize(Double memorySize);

      public abstract Builder gpuAmount(Double gpuAmount);

      public abstract Builder localStorageCategory(String localStorageCategory);

      public abstract InstanceType build();

   }

}
