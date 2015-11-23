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

   public abstract String type();

   public abstract String href();

   @Nullable
   public abstract Metadata metadata();

   @Nullable
   public abstract Properties properties();

   @Nullable
   public abstract Entities entities();

   @SerializedNames({"id", "type", "href", "metadata", "properties", "entities"})
   public static Nic create(String id, String type, String href, Metadata metadata, Properties properties, Entities entities) {
      return new AutoValue_Nic(id, type, href, metadata, properties, entities);
   }

   @AutoValue
   public abstract static class Properties {

      @Nullable
      public abstract String name();

      public abstract String mac();

      public abstract List<String> ips();

      public abstract boolean dhcp();

      public abstract int lan();

      public abstract boolean firewallActive();

      @Nullable
      public abstract Entities entities();

      @SerializedNames({"name", "mac", "ips", "dhcp", "lan", "firewallActive", "entities"})
      public static Properties create(String name, String mac, List<String> ips, boolean dhcp, int lan, boolean firewallactive, Entities entities) {
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

}
