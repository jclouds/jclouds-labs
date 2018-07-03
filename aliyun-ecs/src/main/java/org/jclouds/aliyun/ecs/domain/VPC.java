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
public abstract class VPC {

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

   VPC() {}

   @SerializedNames({"CidrBlock", "CreationTime", "Description", "RegionId", "Status",
           "UserCidrs", "VRouterId", "VSwitchIds", "VpcId", "VpcName" })
   public static VPC create(String cidrBlock, Date creationTime, String description, String regionId, Status status,
                            Map<String, List<UserCidr>> userCidrs,
                            String vRouterId,
                            Map<String, List<String>> vSwitchIds,
                            String id, String name) {
      return builder().cidrBlock(cidrBlock).creationTime(creationTime).description(description).regionId(regionId)
              .status(status).userCidrs(userCidrs).vRouterId(vRouterId).vSwitchIds(vSwitchIds)
              .id(id).name(name).build();
   }

   public abstract String cidrBlock();

   public abstract Date creationTime();

   public abstract String description();

   public abstract String regionId();

   public abstract Status status();

   public abstract Map<String, List<UserCidr>> userCidrs();

   public abstract String vRouterId();

   public abstract Map<String, List<String>> vSwitchIds();

   public abstract String id();

   public abstract String name();

   public abstract VPC.Builder toBuilder();

   public static VPC.Builder builder() {
      return new AutoValue_VPC.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder cidrBlock(String cidrBlock);

      public abstract Builder creationTime(Date creationTime);

      public abstract Builder description(String description);

      public abstract Builder regionId(String regionId);

      public abstract Builder status(Status status);

      public abstract Builder userCidrs(Map<String, List<UserCidr>> userCidrs);

      public abstract Builder vRouterId(String vRouterId);

      public abstract Builder vSwitchIds(Map<String, List<String>> vSwitchIds);

      public abstract Builder id(String id);

      public abstract Builder name(String name);

      abstract VPC autoBuild();

      abstract Map<String, List<UserCidr>> userCidrs();

      abstract Map<String, List<String>> vSwitchIds();

      public VPC build() {
         userCidrs(userCidrs() == null ? ImmutableMap.<String, List<UserCidr>>of() : ImmutableMap.copyOf(userCidrs()));
         vSwitchIds(vSwitchIds() == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(vSwitchIds()));
         return autoBuild();
      }
   }

}
