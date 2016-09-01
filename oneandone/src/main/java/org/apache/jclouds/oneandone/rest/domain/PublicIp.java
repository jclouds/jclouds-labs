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
package org.apache.jclouds.oneandone.rest.domain;

import com.google.auto.value.AutoValue;
import org.apache.jclouds.oneandone.rest.domain.Types.IPOwner;
import org.apache.jclouds.oneandone.rest.domain.Types.IPType;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class PublicIp {

   public abstract String id();

   public abstract String ip();

   public abstract IPType type();

   @Nullable
   public abstract AssignedTo assignedTo();

   @Nullable
   public abstract String reverseDns();

   public abstract boolean isDhcp();

   @Nullable
   public abstract String state();

   @Nullable
   public abstract String creationDate();

   @Nullable
   public abstract DataCenter datacenter();

   @AutoValue
   public abstract static class AssignedTo {

      public abstract String id();

      public abstract String name();

      public abstract IPOwner type();

      @SerializedNames({"id", "name", "type"})
      public static AssignedTo create(String id, String name, IPOwner type) {
         return new AutoValue_PublicIp_AssignedTo(id, name, type);
      }
   }

   @SerializedNames({"id", "ip", "type", "assigned_to", "reverse_dns", "is_dhcp", "state", "creation_date", "datacenter"})
   public static PublicIp create(String id, String ip, IPType type, AssignedTo assignedTo, String reverseDns, boolean isDhcp, String state, String creationDate, DataCenter datacenter) {
      return new AutoValue_PublicIp(id, ip, type, assignedTo, reverseDns, isDhcp, state, creationDate, datacenter);
   }

   @AutoValue
   public abstract static class CreatePublicIp {

      @Nullable
      public abstract String reverseDns();

      @Nullable
      public abstract String dataCenterId();

      @Nullable
      public abstract IPType type();

      @SerializedNames({"reverse_dns", "datacenter_id", "type"})
      public static CreatePublicIp create(final String reverseDns, final String dataCenterId, IPType type) {
         return new AutoValue_PublicIp_CreatePublicIp(reverseDns, dataCenterId, type);
      }
   }

   @AutoValue
   public abstract static class UpdatePublicIp {

      @Nullable
      public abstract String reverseDns();

      @SerializedNames({"reverse_dns"})
      public static UpdatePublicIp create(final String reverseDns) {
         return new AutoValue_PublicIp_UpdatePublicIp(reverseDns);
      }
   }
}
