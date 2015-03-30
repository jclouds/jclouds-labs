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
package org.jclouds.azurecompute.domain;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class NetworkSecurityGroup {

   public enum State {

      CREATED,
      CREATING,
      UPDATING,
      DELETING,
      UNAVAILABLE,
      UNRECOGNIZED;

      public static State fromString(final String text) {
         if (text != null) {
            for (State status : State.values()) {
               if (text.equalsIgnoreCase(status.name())) {
                  return status;
               }
            }
         }
         return UNRECOGNIZED;
      }
   }

   NetworkSecurityGroup() {
   } // For AutoValue only!

   public abstract String name();

   @Nullable
   public abstract String label();

   @Nullable
   public abstract String location();

   @Nullable
   public abstract State state();

   @Nullable
   public abstract List<Rule> rules();

   public static NetworkSecurityGroup create(final String name) {
      return new AutoValue_NetworkSecurityGroup(name, null, null, null, null);
   }

   public static NetworkSecurityGroup create(
           final String name, final String label, String location, final State state, final List<Rule> rules) {

      return new AutoValue_NetworkSecurityGroup(
              name,
              label,
              location,
              state,
              rules == null ? ImmutableList.<Rule>of() : ImmutableList.copyOf(rules));
   }
}
