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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Date;

@AutoValue
public abstract class NetworkDomain {

   public enum Type {
      ESSENTIALS, ADVANCED
   }

   NetworkDomain() {
   }

   public static Builder builder() {
      return new AutoValue_NetworkDomain.Builder();
   }

   public abstract String id();

   @Nullable
   public abstract String datacenterId();

   public abstract String name();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract String state();

   @Nullable
   public abstract Type type();

   @Nullable
   public abstract String snatIpv4Address();

   @Nullable
   public abstract Date createTime();

   @SerializedNames({ "id", "datacenterId", "name", "description", "state", "type", "snatIpv4Address", "createTime" })
   public static NetworkDomain create(String id, String datacenterId, String name, String description, String state,
         Type type, String snatIpv4Address, Date createTime) {
      return builder().id(id).datacenterId(datacenterId).name(name).description(description).state(state).type(type)
            .snatIpv4Address(snatIpv4Address).createTime(createTime).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);

      public abstract Builder datacenterId(String datacenterId);

      public abstract Builder name(String name);

      public abstract Builder description(String description);

      public abstract Builder state(String state);

      public abstract Builder type(Type type);

      public abstract Builder snatIpv4Address(String snatIpv4Address);

      public abstract Builder createTime(Date createTime);

      public abstract NetworkDomain build();
   }

}
