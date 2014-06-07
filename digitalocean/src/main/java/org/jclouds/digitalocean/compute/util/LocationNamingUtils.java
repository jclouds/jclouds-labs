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
package org.jclouds.digitalocean.compute.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.digitalocean.domain.Region;
import org.jclouds.domain.Location;

/**
 * Utility class to encode and decode the region id and name in a {@link Location}.
 */
public class LocationNamingUtils {

   /**
    * Extracts the region id for the given location.
    * 
    * @param location The location to extract the region id from.
    * @return The id of the region.
    */
   public static int extractRegionId(Location location) {
      checkNotNull(location, "location cannot be null");
      String regionIdAndName = location.getDescription();
      int index = regionIdAndName.indexOf('/');
      checkArgument(index >= 0, "location description should be in the form 'regionId/regionName' but was: %s",
            regionIdAndName);
      return Integer.parseInt(regionIdAndName.substring(0, index));
   }

   /**
    * Extracts the region name for the given location.
    * 
    * @param location The location to extract the region name from.
    * @return The name of the region.
    */
   public static String extractRegionName(Location location) {
      checkNotNull(location, "location cannot be null");
      String regionIdAndName = location.getDescription();
      int index = regionIdAndName.indexOf('/');
      checkArgument(index >= 0, "location description should be in the form 'regionId/regionName' but was: %s",
            regionIdAndName);
      return regionIdAndName.substring(index + 1);
   }

   /**
    * Encodes the id and name of the given region into a String so it can be populated in a {@link Location} object.
    * 
    * @param region The region to encode.
    * @return The encoded id and name for the given region.
    */
   public static String encodeRegionIdAndName(Region region) {
      checkNotNull(region, "region cannot be null");
      return region.getId() + "/" + region.getName();
   }
}
