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
package org.jclouds.dimensiondata.cloudcontrol.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class FirewallRuleTarget {

   @AutoValue
   public abstract static class Port {

      Port() {
      } // For AutoValue only!

      public abstract int begin();

      @Nullable
      public abstract Integer end();

      @SerializedNames({ "begin", "end" })
      public static Port create(int begin, Integer end) {
         return new AutoValue_FirewallRuleTarget_Port(begin, end);
      }
   }

   @AutoValue
   public abstract static class PortList {

      PortList() {
      } // For AutoValue only!

      public abstract String id();

      @Nullable
      public abstract String name();

      @Nullable
      public abstract String description();

      @Nullable
      public abstract List<Port> port();

      @Nullable
      public abstract List<String> childPortList();

      @SerializedNames({ "id", "name", "description", "port", "childPortList" })
      public static PortList create(String id, String name, String description, List<Port> port,
            List<String> childPortList) {
         return new AutoValue_FirewallRuleTarget_PortList(id, name, description, port, childPortList);
      }
   }

   public static Builder builder() {
      return new AutoValue_FirewallRuleTarget.Builder();
   }

   FirewallRuleTarget() {
   } // For AutoValue only!

   @Nullable
   public abstract IpRange ip();

   @Nullable
   public abstract Port port();

   @Nullable
   public abstract String portListId();

   @Nullable
   public abstract PortList portList();

   @SerializedNames({ "ip", "port", "portListId", "portList" })
   public static FirewallRuleTarget create(IpRange ip, Port port, String portListId, PortList portList) {
      return builder().ip(ip).port(port).portListId(portListId).portList(portList).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder ip(IpRange ip);

      public abstract Builder port(Port port);

      public abstract Builder portListId(String portListId);

      public abstract Builder portList(PortList portList);

      public abstract FirewallRuleTarget build();
   }

}
