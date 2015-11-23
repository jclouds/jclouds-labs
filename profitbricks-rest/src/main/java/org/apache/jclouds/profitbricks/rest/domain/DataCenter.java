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
public abstract class DataCenter {

   public abstract String id();

   public abstract String type();

   public abstract String href();

   public abstract Metadata metadata();

   public abstract Properties properties();

   @Nullable
   public abstract Entities entities();

   @SerializedNames({"id", "type", "href", "metadata", "properties", "entities"})
   public static DataCenter create(String id, String type, String href, Metadata metadata, DataCenter.Properties properties, DataCenter.Entities entities) {
      return new AutoValue_DataCenter(id, type, href, metadata, properties, entities);
   }

   @AutoValue
   public abstract static class Properties {

      public abstract String name();

      @Nullable
      public abstract String description();

      public abstract Location location();

      public abstract int version();

      @SerializedNames({"name", "description", "location", "version"})
      public static Properties create(String name, String description, Location location, int version) {
         return new AutoValue_DataCenter_Properties(name, description, location, version);
      }

   }

   @AutoValue
   public abstract static class Entities {

      public abstract Servers servers();

      public abstract Volumes volumes();

      public abstract Loadbalancers loadbalancers();

      public abstract Lans lans();

      @SerializedNames({"servers", "volumes", "loadbalancers", "lans"})
      public static Entities create(Servers servers, Volumes volumes, Loadbalancers loadbalancers, Lans lans) {
         return new AutoValue_DataCenter_Entities(servers, volumes, loadbalancers, lans);
      }

   }

}
