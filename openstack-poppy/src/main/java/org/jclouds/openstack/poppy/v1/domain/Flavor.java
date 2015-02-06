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
package org.jclouds.openstack.poppy.v1.domain;

import java.util.List;
import java.util.Set;

import org.jclouds.json.SerializedNames;
import org.jclouds.openstack.v2_0.domain.Link;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Representation of an OpenStack Poppy CDN Flavor.
 */
@AutoValue
public abstract class Flavor {
   /**
    * @return Specifies the name of the flavor. The name must not exceed 64 bytes in length and is limited to Unicode,
    * digits, underscores, and hyphens.
    */
   public abstract String getId();

   /**
    * @return Specifies the list of providers mapped to this flavor.
    */
   public abstract List<Provider> getProviders();

   /**
    * @return Specifies the self-navigating JSON document paths.
    */
   public abstract Set<Link> getLinks();

   @SerializedNames({ "id", "providers", "links" })
   private static Flavor create(String id, List<Provider> providers, Set<Link> links) {
      return new AutoValue_Flavor(id, ImmutableList.copyOf(providers), ImmutableSet.copyOf(links));
   }
}
