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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class BlockStorage {

   public abstract String id();

   public abstract int size();

   @Nullable
   public abstract String state();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract Datacenter datacenter();

   public abstract String name();

   @Nullable
   public abstract String creationDate();

   @Nullable
   public abstract Server server();

   @SerializedNames({"id", "size", "state", "description", "datacenter", "name", "creation_date", "server"})
   public static BlockStorage create(String id, int size, String state, String description, Datacenter datacenter, String name, String creationDate, Server server) {
      return new AutoValue_BlockStorage(id, size, state, description, datacenter, name, creationDate, server);
   }

   @AutoValue
   public abstract static class CreateBlockStorage {

      public abstract String name();

      public abstract int size();

      @Nullable
      public abstract String description();

      @Nullable
      public abstract String datacenterId();

      @Nullable
      public abstract String server();

      @SerializedNames({"name", "size", "description", "datacenter_id", "server"})
      public static CreateBlockStorage create(final String name, final int size, final String description,
              final String datacenterId, final String server) {
         return builder()
                 .name(name)
                 .size(size)
                 .description(description)
                 .datacenterId(datacenterId)
                 .server(server)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_BlockStorage_CreateBlockStorage.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder description(String description);

         public abstract Builder size(int size);

         public abstract Builder datacenterId(String datacenterId);

         public abstract Builder server(String server);

         public abstract CreateBlockStorage build();
      }
   }

   @AutoValue
   public abstract static class UpdateBlockStorage {

      @Nullable
      public abstract String name();

      @Nullable
      public abstract String description();

      @SerializedNames({"name", "description"})
      public static UpdateBlockStorage create(final String name, final String description) {
         return new AutoValue_BlockStorage_UpdateBlockStorage(name, description);
      }
   }

   @AutoValue
   public abstract static class Datacenter {

      public abstract String id();

      public abstract String location();

      public abstract String countryCode();

      @SerializedNames({"id", "location", "country_code"})
      public static Datacenter create(String id, String location, String countryCode) {
         return new AutoValue_BlockStorage_Datacenter(id, location, countryCode);
      }
   }

   @AutoValue
   public abstract static class Server {

      public abstract String id();

      public abstract String name();

      @SerializedNames({"id", "name"})
      public static Server create(String id, String name) {
         return new AutoValue_BlockStorage_Server(id, name);
      }

      @AutoValue
      public abstract static class AttachServer {

         public abstract String serverId();

         @SerializedNames({"server_id"})
         public static AttachServer create(final String serverId) {
            return new AutoValue_BlockStorage_Server_AttachServer(serverId);
         }
      }
   }

}
