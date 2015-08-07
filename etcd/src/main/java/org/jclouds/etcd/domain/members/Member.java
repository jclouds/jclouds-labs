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

package org.jclouds.etcd.domain.members;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class Member {

   public abstract String id();

   @Nullable
   public abstract String name();

   public abstract List<String> peerURLs();

   public abstract List<String> clientURLs();

   Member() {
   }

   @SerializedNames({ "id", "name", "peerURLs", "clientURLs" })
   private static Member create(String id, String name, List<String> peerURLs, List<String> clientURLs) {
      if (clientURLs == null)
         clientURLs = ImmutableList.of();
      return new AutoValue_Member(id, name, ImmutableList.copyOf(peerURLs), ImmutableList.copyOf(clientURLs));
   }
}
