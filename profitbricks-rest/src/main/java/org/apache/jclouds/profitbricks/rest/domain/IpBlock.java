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
package org.apache.jclouds.profitbricks.rest.domain;

import com.google.auto.value.AutoValue;
import java.util.List;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class IpBlock {

   public abstract String id();

   public abstract String type();

   public abstract String href();

   @Nullable
   public abstract Metadata metadata();

   @Nullable
   public abstract Properties properties();

   @SerializedNames({"id", "type", "href", "metadata", "properties"})
   public static IpBlock create(String id, String type, String href, Metadata metadata, Properties properties) {
      return new AutoValue_IpBlock(id, type, href, metadata, properties);
   }

   @AutoValue
   public abstract static class Properties {

      public abstract String name();

      @Nullable
      public abstract List<String> ips();

      public abstract String location();

      public abstract int size();

      @SerializedNames({"name", "ips", "location", "size"})
      public static Properties create(String name, List<String> ips, String location, int size) {
         return new AutoValue_IpBlock_Properties(name, ips, location, size);
      }
   }

   @AutoValue
   public abstract static class PropertiesRequest {

      @Nullable
      public abstract String name();

      public abstract String location();

      public abstract int size();

      @SerializedNames({"name", "location", "size"})
      public static PropertiesRequest create(String name, String location, int size) {
         return new AutoValue_IpBlock_PropertiesRequest(name, location, size);
      }
   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new AutoValue_IpBlock_Request_CreatePayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract PropertiesRequest properties();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder properties(PropertiesRequest properties);

            abstract CreatePayload autoBuild();

            public CreatePayload build() {
               return autoBuild();
            }
         }

      }
   }

}
