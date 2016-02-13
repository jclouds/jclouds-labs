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

package org.jclouds.etcd.domain.keys;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class Node {

   public abstract int createdIndex();

   public abstract boolean dir();

   public abstract List<Node> nodes();

   @Nullable
   public abstract String expiration();

   @Nullable
   public abstract String key();

   public abstract int modifiedIndex();

   public abstract int ttl();

   @Nullable
   public abstract String value();

   Node() {
   }

   @SerializedNames({ "createdIndex", "dir", "nodes", "expiration", "key", "modifiedIndex", "ttl", "value" })
   public static Node create(int createdIndex, boolean dir, List<Node> nodes, String expiration, String key,
         int modifiedIndex, int ttl, String value) {
      return new AutoValue_Node(createdIndex, dir,
            nodes != null ? ImmutableList.copyOf(nodes) : ImmutableList.<Node> of(), expiration, key, modifiedIndex,
            ttl, value);
   }
}
