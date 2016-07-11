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
import org.apache.jclouds.oneandone.rest.domain.Types.IPType;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class ServerIp {

   public abstract String id();

   public abstract String ip();

   public abstract List<ServerLoadBalancer> loadBalancers();

   public abstract List<ServerFirewallPolicy> firewallPolicy();

   @Nullable
   public abstract String reverseDns();

   @Nullable
   public abstract IPType type();

   @SerializedNames({"id", "ip", "loadBalancers", "firewallPolicy", "reverseDns", "type"})
   public static ServerIp create(String id, String ip, List<ServerLoadBalancer> loadBalancers, List<ServerFirewallPolicy> firewallPolicy, String reverseDns, IPType type) {
      return new AutoValue_ServerIp(id, ip, loadBalancers == null ? ImmutableList.<ServerLoadBalancer>of() : loadBalancers,
              firewallPolicy == null ? ImmutableList.<ServerFirewallPolicy>of() : firewallPolicy, reverseDns, type);
   }
}
