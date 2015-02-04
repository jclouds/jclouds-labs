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

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Rule {

   public abstract String name();

   public abstract String type();

   public abstract String priority();

   public abstract String action();

   public abstract String sourceAddressPrefix();

   public abstract String sourcePortRange();

   public abstract String destinationAddressPrefix();

   public abstract String destinationPortRange();

   public abstract String protocol();

   public abstract String state();

   @Nullable public abstract Boolean isDefault();

   Rule() {} // For AutoValue only!

   public static Rule create(String name, String type, String priority, String action, String sourceAddressPrefix,
                             String sourcePortRange, String destinationAddressPrefix, String destinationPortRange,
                             String protocol, String state, Boolean isDefault) {
      return new AutoValue_Rule(name, type, priority, action, sourceAddressPrefix, sourcePortRange,
              destinationAddressPrefix, destinationPortRange, protocol, state, isDefault);
   }

}
