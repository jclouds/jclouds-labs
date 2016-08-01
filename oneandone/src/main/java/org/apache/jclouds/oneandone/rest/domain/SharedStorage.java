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
import org.apache.jclouds.oneandone.rest.domain.Types.StorageServerRights;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class SharedStorage {

   public abstract String id();

   public abstract int size();

   @Nullable
   public abstract String state();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract String cloudpanelId();

   public abstract int sizeUsed();

   @Nullable
   public abstract String cifsPath();

   @Nullable
   public abstract String nfsPath();

   public abstract String name();

   @Nullable

   public abstract String creationDate();

   @Nullable
   public abstract List<Server> servers();

   @SerializedNames({"id", "size", "state", "description", "cloudpanel_id", "size_used", "cifs_path", "nfs_path", "name", "creation_date", "servers"})
   public static SharedStorage create(String id, int size, String state, String description, String cloudpanelId, int sizeUsed, String cifsPath, String nfsPath, String name, String creationDate, List<Server> servers) {
      return new AutoValue_SharedStorage(id, size, state, description, cloudpanelId, sizeUsed, cifsPath, nfsPath, name, creationDate, servers == null ? ImmutableList.<Server>of() : ImmutableList.copyOf(servers));
   }

   @AutoValue
   public abstract static class CreateSharedStorage {

      public abstract String name();

      @Nullable
      public abstract String description();

      public abstract int size();

      @Nullable
      public abstract String datacenterId();

      @SerializedNames({"name", "description", "size", "datacenter_id"})
      public static CreateSharedStorage create(final String name, final String description, final int size, final String dataCenterId) {
         return builder()
                 .name(name)
                 .description(description)
                 .datacenterId(dataCenterId)
                 .size(size)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_SharedStorage_CreateSharedStorage.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder description(String description);

         public abstract Builder size(int size);

         public abstract Builder datacenterId(String datacenterId);

         public abstract CreateSharedStorage build();
      }
   }

   @AutoValue
   public abstract static class UpdateSharedStorage {

      @Nullable
      public abstract String name();

      @Nullable
      public abstract String description();

      @Nullable
      public abstract Integer size();

      @SerializedNames({"name", "description", "size"})
      public static UpdateSharedStorage create(final String name, final String description, final Integer size) {
         return new AutoValue_SharedStorage_UpdateSharedStorage(name, description, size);
      }
   }

   @AutoValue
   public abstract static class Server {

      public abstract String id();

      public abstract String name();

      public abstract StorageServerRights rights();

      @SerializedNames({"id", "name", "rights"})
      public static Server create(String id, String name, StorageServerRights rights) {
         return new AutoValue_SharedStorage_Server(id, name, rights);
      }

      @AutoValue
      public abstract static class CreateServer {

         public abstract List<ServerPayload> servers();

         @SerializedNames({"servers"})
         public static CreateServer create(final List<ServerPayload> servers) {
            return new AutoValue_SharedStorage_Server_CreateServer(servers);
         }

         @AutoValue
         public abstract static class ServerPayload {

            public abstract String id();

            public abstract StorageServerRights rights();

            @SerializedNames({"id", "rights"})
            public static ServerPayload create(final String id, StorageServerRights rights) {
               return new AutoValue_SharedStorage_Server_CreateServer_ServerPayload(id, rights);
            }
         }
      }
   }
}
