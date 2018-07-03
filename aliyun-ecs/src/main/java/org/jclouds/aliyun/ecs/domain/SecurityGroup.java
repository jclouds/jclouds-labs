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
package org.jclouds.aliyun.ecs.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import org.jclouds.json.SerializedNames;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class SecurityGroup {

   SecurityGroup() {
   }

   @SerializedNames({ "SecurityGroupId", "Description", "SecurityGroupName", "VpcId", "Tags" })
   public static SecurityGroup create(String id, String description, String name,
                                      String vpcId, Map<String, List<Tag>> tags) {
      return new AutoValue_SecurityGroup(id, description, name, vpcId, tags == null ?
              ImmutableMap.<String, List<Tag>>of() :
              ImmutableMap.copyOf(tags));
   }

   public abstract String id();

   public abstract String description();

   public abstract String name();

   public abstract String vpcId();

   public abstract Map<String, List<Tag>> tags();

}
