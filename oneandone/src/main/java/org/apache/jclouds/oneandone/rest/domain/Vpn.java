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
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.Types.GenericState;
import org.apache.jclouds.oneandone.rest.domain.Types.VPNType;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Vpn {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract GenericState state();

   @Nullable
   public abstract DataCenter datacenter();

   @Nullable
   public abstract VPNType type();

   @Nullable
   public abstract String creationDate();

   @Nullable
   public abstract String cloudpanelId();

   @Nullable
   public abstract List<String> ips();

   @SerializedNames({"id", "name", "description", "state", "datacenter", "type", "creation_date", "cloudpanel_id", "ips"})
   public static Vpn create(String id, String name, String description, GenericState state, DataCenter datacenter, VPNType type, String creationDate, String cloudpanelId, List<String> ips) {
      return new AutoValue_Vpn(id, name, description, state, datacenter, type, creationDate, cloudpanelId, ips == null ? ImmutableList.<String>of() : ImmutableList.copyOf(ips));
   }

   @AutoValue
   public abstract static class CreateVpn {

      public abstract String name();

      @Nullable
      public abstract String description();

      @Nullable
      public abstract String datacenterId();

      @SerializedNames({"name", "description", "datacenter_id"})
      public static CreateVpn create(String name, String description, String datacenterId) {
         return new AutoValue_Vpn_CreateVpn(name, description, datacenterId);
      }
   }

   @AutoValue
   public abstract static class UpdateVpn {

      public abstract String name();

      @Nullable
      public abstract String description();

      @SerializedNames({"name", "description"})
      public static UpdateVpn create(String name, String description) {
         return new AutoValue_Vpn_UpdateVpn(name, description);
      }
   }
}
