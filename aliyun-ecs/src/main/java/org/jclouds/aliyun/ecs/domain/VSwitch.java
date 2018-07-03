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
import org.jclouds.json.SerializedNames;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

@AutoValue
public abstract class VSwitch {

   public enum Status {
      AVAILABLE, UNAVAILABLE, PENDING;

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

   VSwitch() {}

   @SerializedNames({"CidrBlock", "CreationTime", "Description", "ZoneId", "Status",
           "AvailableIpAddressCount", "VpcId", "VSwitchId", "VSwitchName" })
   public static VSwitch create(String cidrBlock, Date creationTime, String description, String zoneId, Status status,
                                int availableIpAddressCount,
                                String vpcId, String id, String name) {
      return builder().cidrBlock(cidrBlock).creationTime(creationTime).description(description).zoneId(zoneId).status(status)
              .availableIpAddressCount(availableIpAddressCount).vpcId(vpcId).id(id).name(name).build();
   }

   public abstract String cidrBlock();

   public abstract Date creationTime();

   public abstract String description();

   public abstract String zoneId();

   public abstract Status status();

   public abstract int availableIpAddressCount();

   public abstract String vpcId();

   public abstract String id();

   public abstract String name();

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_VSwitch.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder cidrBlock(String cidrBlock);

      public abstract Builder creationTime(Date creationTime);

      public abstract Builder description(String description);

      public abstract Builder zoneId(String regionId);

      public abstract Builder status(Status status);

      public abstract Builder availableIpAddressCount(int availableIpAddressCount);

      public abstract Builder vpcId(String vpcId);

      public abstract Builder id(String id);

      public abstract Builder name(String name);

      abstract VSwitch build();
   }


}
