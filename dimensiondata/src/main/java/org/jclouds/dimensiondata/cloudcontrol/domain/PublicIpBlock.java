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
package org.jclouds.dimensiondata.cloudcontrol.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

import java.util.Date;

@AutoValue
public abstract class PublicIpBlock {

   PublicIpBlock() {
   }

   public static Builder builder() {
      return new AutoValue_PublicIpBlock.Builder();
   }

   public abstract String id();

   public abstract String datacenterId();

   public abstract String state();

   public abstract Date createTime();

   public abstract String baseIp();

   public abstract int size();

   public abstract String networkDomainId();

   @SerializedNames({ "id", "datacenterId", "state", "createTime", "baseIp", "size", "networkDomainId" })
   public static PublicIpBlock create(String id, String datacenterId, String state, Date createTime, String baseIp,
         int size, String networkDomainId) {
      return builder().id(id).datacenterId(datacenterId).state(state).createTime(createTime).baseIp(baseIp).size(size)
            .networkDomainId(networkDomainId).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);

      public abstract Builder datacenterId(String datacenterId);

      public abstract Builder state(String state);

      public abstract Builder createTime(Date createTime);

      public abstract Builder baseIp(String ipv4GatewayAddress);

      public abstract Builder size(int size);

      public abstract Builder networkDomainId(String networkDomainId);

      public abstract PublicIpBlock build();
   }

}
