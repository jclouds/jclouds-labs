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
package org.jclouds.aliyun.ecs.domain.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

public class ListVSwitchesOptions extends BaseHttpRequestOptions {
   public static final String VPC_ID_PARAM = "VpcId";
   public static final String VSWITCH_ID_PARAM = "VSwitchId";
   public static final String ZONE_ID_PARAM = "ZoneId";
   public static final String IS_DEFAULT_PARAM = "IsDefault";

   public ListVSwitchesOptions vpcId(String vpcId) {
      queryParameters.put(VPC_ID_PARAM, vpcId);
      return this;
   }

   public ListVSwitchesOptions vSwitchId(String vSwitchId) {
      queryParameters.put(VSWITCH_ID_PARAM, vSwitchId);
      return this;
   }

   public ListVSwitchesOptions zoneId(String zoneId) {
      queryParameters.put(ZONE_ID_PARAM, zoneId);
      return this;
   }

   public ListVSwitchesOptions isDefault(Boolean isDefault) {
      queryParameters.put(IS_DEFAULT_PARAM, isDefault.toString());
      return this;
   }

   public ListVSwitchesOptions paginationOptions(final PaginationOptions paginationOptions) {
      this.queryParameters.putAll(paginationOptions.buildQueryParameters());
      return this;
   }

   public static final class Builder {

      /**
       * @see {@link ListVSwitchesOptions#vpcId(String)}
       */
      public static ListVSwitchesOptions vpcId(String vpcId) {
         return new ListVSwitchesOptions().vpcId(vpcId);
      }

      /**
       * @see {@link ListVSwitchesOptions#vSwitchId(String)}
       */
      public static ListVSwitchesOptions vSwitchId(String vSwitchId) {
         return new ListVSwitchesOptions().vSwitchId(vSwitchId);
      }

      /**
       * @see {@link ListVSwitchesOptions#zoneId(String)}
       */
      public static ListVSwitchesOptions zoneId(String zoneId) {
         return new ListVSwitchesOptions().zoneId(zoneId);
      }

      /**
       * @see {@link ListVSwitchesOptions#isDefault(Boolean)}
       */
      public static ListVSwitchesOptions isDefault(Boolean isDefault) {
         return new ListVSwitchesOptions().isDefault(isDefault);
      }

      /**
       * @see ListVSwitchesOptions#paginationOptions(PaginationOptions)
       */
      public static ListVSwitchesOptions paginationOptions(PaginationOptions paginationOptions) {
         return new ListVSwitchesOptions().paginationOptions(paginationOptions);
      }
   }
}
