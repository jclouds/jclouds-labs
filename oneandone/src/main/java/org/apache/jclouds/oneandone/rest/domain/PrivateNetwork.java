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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class PrivateNetwork {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract String networkAddress();

   @Nullable
   public abstract String subnetMask();

   @Nullable
   public abstract GenericState state();

   @Nullable
   public abstract String creationDate();

   @Nullable
   public abstract List<Server> servers();

   @Nullable
   public abstract String cloudpanelId();

   @Nullable
   public abstract DataCenter datacenter();

   @SerializedNames({"id", "name", "description", "network_address", "subnet_mask", "state", "creation_date", "servers", "cloudpanel_id", "datacenter"})
   public static PrivateNetwork create(String id, String name, String description, String networkAddress, String subnetMask, GenericState state, String creationDate, List<Server> servers, String cloudpanelId, DataCenter datacenter) {
      return new AutoValue_PrivateNetwork(id, name, description, networkAddress, subnetMask, state, creationDate, servers == null ? ImmutableList.<Server>of() : servers, cloudpanelId, datacenter);
   }

   @AutoValue
   public abstract static class CreatePrivateNetwork {

      public abstract String name();

      @Nullable
      public abstract String description();

      @Nullable
      public abstract String networkAddress();

      @Nullable
      public abstract String subnetMask();

      @Nullable
      public abstract String datacenterId();

      @SerializedNames({"name", "description", "network_address", "subnet_mask", "datacenter_id"})
      public static CreatePrivateNetwork create(String name, String description, String networkAddress, String subnetMask, String datacenterId) {
         return builder()
                 .name(name)
                 .description(description)
                 .subnetMask(subnetMask)
                 .networkAddress(networkAddress)
                 .datacenterId(datacenterId)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_PrivateNetwork_CreatePrivateNetwork.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder description(String description);

         public abstract Builder networkAddress(String networkAddress);

         public abstract Builder subnetMask(String subnetMask);

         public abstract Builder datacenterId(String datacenterId);

         public abstract CreatePrivateNetwork build();
      }
   }

   @AutoValue
   public abstract static class UpdatePrivateNetwork {

      public abstract String name();

      @Nullable
      public abstract String description();

      @Nullable
      public abstract String networkAddress();

      @Nullable
      public abstract String subnetMask();

      @SerializedNames({"name", "description", "network_address", "subnet_mask"})
      public static UpdatePrivateNetwork create(String name, String description, String networkAddress, String subnetMask) {
         return builder()
                 .name(name)
                 .description(description)
                 .subnetMask(subnetMask)
                 .networkAddress(networkAddress)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_PrivateNetwork_UpdatePrivateNetwork.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder description(String description);

         public abstract Builder networkAddress(String networkAddress);

         public abstract Builder subnetMask(String subnetMask);

         public abstract UpdatePrivateNetwork build();
      }
   }

   @AutoValue
   public abstract static class Server {

      public abstract String id();

      public abstract String name();

      @SerializedNames({"id", "name"})
      public static Server create(String id, String name) {
         return new AutoValue_PrivateNetwork_Server(id, name);
      }

      @AutoValue
      public abstract static class CreateServer {

         public abstract List<String> servers();

         @SerializedNames({"servers"})
         public static CreateServer create(List<String> servers) {
            return new AutoValue_PrivateNetwork_Server_CreateServer(servers == null ? ImmutableList.<String>of() : ImmutableList.copyOf(servers));
         }
      }
   }
}
