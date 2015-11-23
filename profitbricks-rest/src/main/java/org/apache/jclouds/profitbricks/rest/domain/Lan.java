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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Lan {

   public abstract String id();

   public abstract String type();

   public abstract String href();

   @Nullable
   public abstract Metadata metadata();

   @Nullable
   public abstract Properties properties();

   @Nullable
   public abstract Entities entities();

   @SerializedNames({"id", "type", "href", "metadata", "properties", "entities"})
   public static Lan create(String id, String type, String href, Metadata metadata, Properties properties, Entities entities) {
      return new AutoValue_Lan(id, type, href, metadata, properties, entities);
   }

   @AutoValue
   public abstract static class Properties {
      @Nullable
      public abstract String name();
      
      public abstract boolean isPublic();
      
      @SerializedNames({"name", "public"})
      public static Properties create(String name, boolean isPublic) {
         return new AutoValue_Lan_Properties(name, isPublic);
      }
   }

   @AutoValue
   public abstract static class Entities {

      public abstract Nics nics();

      @SerializedNames({"nics"})
      public static Entities create(Nics nics) {
         return new AutoValue_Lan_Entities(nics);
      }

   }
}
