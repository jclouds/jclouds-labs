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
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class EipAddress {

   public enum InternetChargeType {
      ECS_INSTANCE("EcsInstance"),
      SLB_INSTANCE ("SlbInstance"),
      NAT ("Nat"),
      HA_VIP("HaVip"),
      DEFAULT("");

      private final String internetChargeType;

      InternetChargeType(String internetChargeType) {
         this.internetChargeType = internetChargeType;
      }

      public static InternetChargeType fromValue(String value) {
         return Enums.getIfPresent(InternetChargeType.class, CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, value)).or(InternetChargeType.DEFAULT);
      }

      public String internetChargeType() {
         return internetChargeType;
      }

      @Override
      public String toString() {
         return internetChargeType();
      }
   }

   EipAddress() {}

   @SerializedNames({ "IpAddress", "AllocationId", "InternetChargeType" })
   public static EipAddress create(String ipAddress, String allocationId, InternetChargeType internetChargeType) {
      return new AutoValue_EipAddress(ipAddress, allocationId, internetChargeType);
   }

   public abstract String ipAddress();

   public abstract String allocationId();

   public abstract InternetChargeType internetChargeType();

}
