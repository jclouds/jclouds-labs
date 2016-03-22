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
import org.jclouds.json.SerializedNames;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.copyOf;
import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class Nic {

   public abstract String id();
   
   @Nullable
   public abstract String dataCenterId();
   
   @Nullable
   public abstract String serverId();   

   public abstract String type();

   public abstract String href();

   @Nullable
   public abstract Metadata metadata();

   @Nullable
   public abstract Properties properties();

   @Nullable
   public abstract Entities entities();

   @SerializedNames({"id", "dataCenterId", "serverId", "type", "href", "metadata", "properties", "entities"})
   public static Nic create(String id, String dataCenterId, String serverId, String type, String href, Metadata metadata, Properties properties, Entities entities) {
      return new AutoValue_Nic(id, dataCenterId, serverId, type, href, metadata, properties, entities);
   }

   @AutoValue
   public abstract static class Properties {

      @Nullable
      public abstract String name();

      @Nullable
      public abstract String mac();

      public abstract List<String> ips();

      @Nullable
      public abstract Boolean dhcp();

      public abstract int lan();

      @Nullable
      public abstract Boolean firewallActive();

      @Nullable
      public abstract Entities entities();

      @SerializedNames({"name", "mac", "ips", "dhcp", "lan", "firewallActive", "entities"})
      public static Properties create(String name, String mac, List<String> ips, Boolean dhcp, int lan, Boolean firewallactive, Entities entities) {
	 return new AutoValue_Nic_Properties(name, mac, ips == null ? ImmutableList.<String>of() : copyOf(ips), dhcp, lan, firewallactive, entities);
      }
   }

   @AutoValue
   public abstract static class Entities {

      public abstract FirewallRules firewallrules();

      @SerializedNames({"firewallrules"})
      public static Entities create(FirewallRules firewallrules) {
	 return new AutoValue_Nic_Entities(firewallrules);
      }
   }
   
   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new AutoValue_Nic_Request_CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new AutoValue_Nic_Request_UpdatePayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload {

         @Nullable
         public abstract String name();

         @Nullable
         public abstract List<String> ips();

         @Nullable
         public abstract Boolean dhcp();

         public abstract int lan();
         
         @Nullable
         public abstract Boolean firewallActive();
         
         @Nullable
         public abstract List<FirewallRule> firewallrules();

         public abstract String dataCenterId();
         public abstract String serverId();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder name(String name);
            public abstract Builder ips(List<String> ips);
            public abstract Builder dhcp(Boolean dhcp);
            public abstract Builder lan(int lan);
            public abstract Builder firewallActive(Boolean firewallActive);
            public abstract Builder firewallrules(List<FirewallRule> firewallrules);
            public abstract Builder dataCenterId(String dataCenterId);
            public abstract Builder serverId(String serverId);
           
            abstract CreatePayload autoBuild();

            public CreatePayload build() {
               return autoBuild();
            }
         }
      }

      @AutoValue
      public abstract static class UpdatePayload {

         @Nullable
         public abstract String name();

         @Nullable
         public abstract List<String> ips();

         @Nullable
         public abstract Boolean dhcp();

         @Nullable
         public abstract Integer lan();
         
         public abstract String dataCenterId();
         public abstract String serverId();
         public abstract String id();

         @AutoValue.Builder
         public abstract static class Builder {
            public abstract Builder name(String name);
            public abstract Builder ips(List<String> ips);
            public abstract Builder dhcp(Boolean dhcp);
            public abstract Builder lan(Integer lan);
            public abstract Builder dataCenterId(String dataCenterId);
            public abstract Builder serverId(String serverId);
            public abstract Builder id(String id);
           
            abstract UpdatePayload autoBuild();

            public UpdatePayload build() {
               return autoBuild();
            }
         }
      }
      
   }
   

}

