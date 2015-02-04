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

import static com.google.common.collect.ImmutableList.copyOf;
import java.util.List;

import com.google.auto.value.AutoValue;

/** A data center location that is valid for your subscription. */
@AutoValue
public abstract class Location {

   Location() {} // For AutoValue only!

   /** The name of the data center location. Ex. {@code West Europe}. */
   public abstract String name();

   /** The localized name of the data center location. */
   public abstract String displayName();

   /** Indicates the services available at this location. Ex. {@code Compute}. */
   public abstract List<String> availableServices();

   public static Location create(String name, String displayName, List<String> availableServices) {
      return new AutoValue_Location(name, displayName, copyOf(availableServices));
   }
}
