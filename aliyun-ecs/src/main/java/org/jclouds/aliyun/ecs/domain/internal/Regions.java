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
package org.jclouds.aliyun.ecs.domain.internal;

/**
 * Enumeration of region names
 */
public enum Regions {

   US_EAST_1("us-east-1", "US (Virginia)"),
   US_WEST_1("us-west-1", "US West (Silicon Valley)"),
   EU_CENTRAL_1("eu-central-1", "Germany (Frankfurt)"),
   AP_NORTHEAST_1("ap-northeast-1", "Japan (Tokyo)"),
   AP_SOUTH_1("ap-south-1", "India (Mumbai)"),
   AP_SOUTHEAST_1("ap-southeast-1", "Singapore"),
   AP_SOUTHEAST_2("ap-southeast-2", "Australia (Sydney)"),
   AP_SOUTHEAST_3("ap-southeast-3", "Malaysia (Kuala Lumpur)"),
   AP_SOUTHEAST_5("ap-southeast-5", "Indonesia (Jakarta)"),
   CN_NORTH_1("cn-qingdao", "China (Qingdao)"),
   CN_NORTH_2("cn-beijing", "China (Beijing)"),
   CN_NORTH_3("cn-zhangjiakou", "China (Zhangjiakou)"),
   CN_NORTH_5("cn-huhehaote", "China (Huhehaote)"),
   CN_EAST_1("cn-hangzhou", "China (Hangzou)"),
   CN_EAST_2("cn-shanghai", "China (Shanghai)"),
   CN_SOUTH_1("cn-shenzhen", "China (Shenzhen)"),
   CN_SOUTH_2("cn-hongkong", "China (Hongkong)"),
   ME_EAST_1("me-east-1", "UAE (Dubai)");

   private final String name;
   private final String description;

   Regions(String name, String description) {
      this.name = name;
      this.description = description;
   }

   /**
    * The name of this region, used in the regions.xml file to identify it.
    */
   public String getName() {
      return name;
   }

   /**
    * Descriptive readable name for this region.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Returns a region enum corresponding to the given region name.
    *
    * @param regionName
    *            The name of the region. Ex.: eu-west-1
    * @return Region enum representing the given region name.
    */
   public static Regions fromName(String regionName) {
      for (Regions region : Regions.values()) {
         if (region.getName().equals(regionName)) {
            return region;
         }
      }
      throw new IllegalArgumentException("Cannot create enum from " + regionName + " value!");
   }

}
