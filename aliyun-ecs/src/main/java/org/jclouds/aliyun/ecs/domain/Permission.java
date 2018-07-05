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
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import org.jclouds.json.SerializedNames;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

@AutoValue
public abstract class Permission {

   public enum NicType {
      INTERNET, INTRANET;

      public static NicType fromValue(String value) {
         Optional<NicType> nicType = Enums.getIfPresent(NicType.class, value.toUpperCase());
         checkArgument(nicType.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(NicType.values()), value);
         return nicType.get();
      }
   }

   public enum Policy {
      ACCEPT, DROP;

      public static Policy fromValue(String value) {
         Optional<Policy> policy = Enums.getIfPresent(Policy.class, value.toUpperCase());
         checkArgument(policy.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(Policy.values()), value);
         return policy.get();
      }
   }

   public enum Direction {
      EGRESS, ALL;

      public static Direction fromValue(String value) {
         Optional<Direction> direction = Enums.getIfPresent(Direction.class, value.toUpperCase());
         checkArgument(direction.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(Direction.values()), value);
         return direction.get();
      }
   }

   Permission() {}

   @SerializedNames({"SourceCidrIp", "DestCidrIp", "Description", "NicType",
                   "DestGroupName", "PortRange", "DestGroupId", "Direction", "Priority",
                   "IpProtocol", "SourceGroupOwnerAccount", "Policy", "CreateTime",
                   "SourceGroupId", "DestGroupOwnerAccount", "SourceGroupName"})
   public static Permission create(String sourceCidrIp, String destCidrIp, String description, NicType nicType,
                                   String destGroupName, String portRange, String destGroupId, Direction direction,
                                   String priority,
                                   IpProtocol ipProtocol, String sourceGroupOwnerAccount, Policy policy,
                                   Date createTime, String sourceGroupId, String destGroupOwnerAccount, String sourceGroupName) {
      return new AutoValue_Permission(sourceCidrIp, destCidrIp, description, nicType, destGroupName, portRange,
              destGroupId, direction, priority, ipProtocol, sourceGroupOwnerAccount, policy, createTime, sourceGroupId,
              destGroupOwnerAccount, sourceGroupName);
   }

   public abstract String sourceCidrIp();

   public abstract String destCidrIp();

   public abstract String description();

   public abstract NicType nicType();

   public abstract String destGroupName();

   public abstract String portRange();

   public abstract String destGroupId();

   public abstract Direction direction();

   public abstract String priority();

   public abstract IpProtocol ipProtocol();

   public abstract String sourceGroupOwnerAccount();

   public abstract Policy policy();

   public abstract Date createTime();

   public abstract String sourceGroupId();

   public abstract String destGroupOwnerAccount();

   public abstract String sourceGroupName();

}
