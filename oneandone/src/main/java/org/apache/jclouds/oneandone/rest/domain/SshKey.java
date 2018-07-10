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
import org.apache.jclouds.oneandone.rest.domain.Types.GenericState;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class SshKey {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract GenericState state();

   @Nullable
   public abstract List<Server> servers();

   @Nullable
   public abstract String md5();

   @Nullable
   public abstract String publicKey();

   @Nullable
   public abstract String creationDate();

   @SerializedNames({"id", "name", "description", "state", "servers", "md5", "public_key", "creation_date"})
   public static SshKey create(String id, String name, String description, GenericState state, List<Server> servers, String md5, String publicKey, String creationDate) {
      return new AutoValue_SshKey(id, name, description, state, servers == null ? ImmutableList.<Server>of() : ImmutableList.copyOf(servers), md5, publicKey, creationDate);
   }

   @AutoValue
   public abstract static class CreateSshKey {

      public abstract String name();

      @Nullable
      public abstract String description();

      @Nullable
      public abstract String publicKey();

      @SerializedNames({"name", "description", "public_key"})
      public static CreateSshKey create(String name, String description, String publicKey) {
         return new AutoValue_SshKey_CreateSshKey(name, description, publicKey);
      }
   }

   @AutoValue
   public abstract static class UpdateSshKey {

      public abstract String name();

      @Nullable
      public abstract String description();

      @SerializedNames({"name", "description"})
      public static UpdateSshKey create(String name, String description) {
         return new AutoValue_SshKey_UpdateSshKey(name, description);
      }
   }

   @AutoValue
   public abstract static class Server {

      public abstract String id();

      public abstract String name();

      @SerializedNames({"id", "name"})
      public static Server create(String id, String name) {
         return new AutoValue_SshKey_Server(id, name);
      }
   }
}
