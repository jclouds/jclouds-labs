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

public class CreateSecurityGroupOptions extends BaseHttpRequestOptions {
   public static final String SECURITY_GROUP_NAME_PARAM = "SecurityGroupName";
   public static final String DESCRIPTION_PARAM = "Description";
   public static final String VPC_ID_PARAM = "VpcId";
   public static final String CLIENT_TOKEN_PARAM = "ClientToken";

   public CreateSecurityGroupOptions securityGroupName(String securityGroupName) {
      queryParameters.put(SECURITY_GROUP_NAME_PARAM, securityGroupName);
      return this;
   }

   public CreateSecurityGroupOptions description(String description) {
      queryParameters.put(DESCRIPTION_PARAM, description);
      return this;
   }

   public CreateSecurityGroupOptions vpcId(String vpcId) {
      queryParameters.put(VPC_ID_PARAM, vpcId);
      return this;
   }

   public CreateSecurityGroupOptions clientToken(String clientToken) {
      queryParameters.put(CLIENT_TOKEN_PARAM, clientToken);
      return this;
   }

   public static final class Builder {

      /**
       * @see {@link CreateSecurityGroupOptions#securityGroupName(String)}
       */
      public static CreateSecurityGroupOptions securityGroupName(String securityGroupName) {
         return new CreateSecurityGroupOptions().securityGroupName(securityGroupName);
      }

      /**
       * @see {@link CreateSecurityGroupOptions#description(String)}
       */
      public static CreateSecurityGroupOptions description(String description) {
         return new CreateSecurityGroupOptions().description(description);
      }

      /**
       * @see {@link CreateSecurityGroupOptions#vpcId(String)}
       */
      public static CreateSecurityGroupOptions vpcId(String vpcId) {
         return new CreateSecurityGroupOptions().vpcId(vpcId);
      }

      /**
       * @see {@link CreateSecurityGroupOptions#clientToken(String)}
       */
      public static CreateSecurityGroupOptions clientToken(String clientToken) {
         return new CreateSecurityGroupOptions().clientToken(clientToken);
      }
   }

}
