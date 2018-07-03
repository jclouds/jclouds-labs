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

public class ListVPCsOptions extends BaseHttpRequestOptions {
   public static final String VPC_ID_PARAM = "VpcId";
   public static final String IS_DEFAULT_PARAM = "IsDefault";

   public ListVPCsOptions vpcId(String vpcId) {
      queryParameters.put(VPC_ID_PARAM, vpcId);
      return this;
   }

   public ListVPCsOptions isDefault(Boolean isDefault) {
      queryParameters.put(IS_DEFAULT_PARAM, isDefault.toString());
      return this;
   }

   public ListVPCsOptions paginationOptions(final PaginationOptions paginationOptions) {
      this.queryParameters.putAll(paginationOptions.buildQueryParameters());
      return this;
   }

   public static final class Builder {

      /**
       * @see {@link ListVSwitchesOptions#vpcId(String)}
       */
      public static ListVPCsOptions vpcId(String vpcId) {
         return new ListVPCsOptions().vpcId(vpcId);
      }

      /**
       * @see {@link ListVPCsOptions#isDefault(Boolean)}
       */
      public static ListVPCsOptions isDefault(Boolean isDefault) {
         return new ListVPCsOptions().isDefault(isDefault);
      }

      /**
       * @see ListVPCsOptions#paginationOptions(PaginationOptions)
       */
      public static ListVPCsOptions paginationOptions(PaginationOptions paginationOptions) {
         return new ListVPCsOptions().paginationOptions(paginationOptions);
      }
   }
}
