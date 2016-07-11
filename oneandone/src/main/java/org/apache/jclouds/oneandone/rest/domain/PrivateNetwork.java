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
import org.apache.jclouds.oneandone.rest.domain.Types.GenericState;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class PrivateNetwork {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract String networkAddress();

   @Nullable
   public abstract String subnetMask();

   @Nullable
   public abstract GenericState state();

   @Nullable
   public abstract String creationDate();

   @Nullable
   public abstract List<Server> servers();

   @Nullable
   public abstract String cloudpanelId();

   @Nullable
   public abstract DataCenter datacenter();

   @SerializedNames({"id", "name", "description", "network_address", "subnet_mask", "state", "creation_date", "servers", "cloudpanel_id", "datacenter"})
   public static PrivateNetwork create(String id, String name, String description, String networkAddress, String subnetMask, GenericState state, String creationDate, List<Server> servers, String cloudpanelId, DataCenter datacenter) {
      return new AutoValue_PrivateNetwork(id, name, description, networkAddress, subnetMask, state, creationDate, servers == null ? ImmutableList.<Server>of() : servers, cloudpanelId, datacenter);
   }
}
