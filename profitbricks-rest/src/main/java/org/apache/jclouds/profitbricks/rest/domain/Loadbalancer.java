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
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Loadbalancer {

   public abstract String id();

   public abstract String type();

   public abstract String href();

   public abstract Metadata metadata();

   public abstract Properties properties();

   public abstract Entities entities();

   @SerializedNames({"id", "type", "href", "metadata", "properties", "entities"})
   public static Loadbalancer create(String id, String type, String href, Metadata metadata, Properties properties, Entities entities) {
      return new AutoValue_Loadbalancer(id, type, href, metadata, properties, entities);
   }

   @AutoValue
   public abstract static class Properties {

      public abstract String name();

      public abstract String ip();

      public abstract boolean dhcp();

      @SerializedNames({"name", "ip", "dhcp"})
      public static Properties create(String name, String ip, boolean dhcp) {
         return new AutoValue_Loadbalancer_Properties(name, ip, dhcp);
      }
   }

   @AutoValue
   public abstract static class Entities {
      
      public abstract Nics balancednics();
      
      @SerializedNames({"balancednics"})
      public static Entities create(Nics balancednics) {
         return new AutoValue_Loadbalancer_Entities(balancednics);
      }
   }
}
