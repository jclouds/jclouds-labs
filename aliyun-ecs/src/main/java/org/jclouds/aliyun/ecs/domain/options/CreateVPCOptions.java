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

public class CreateVPCOptions extends BaseHttpRequestOptions {
   public static final String CIDR_BLOCK_PARAM = "CidrBlock";
   public static final String VPC_NAME_PARAM = "VpcName";
   public static final String DESCRIPTION_PARAM = "Description";
   public static final String CLIENT_TOKEN_PARAM = "ClientToken";
   public static final String USER_CIDR_PARAM = "UserCidr";

   /**
    * Configures the IP address range of the VPC_PREFIX in the CIDR block form.
    */
   public CreateVPCOptions cidrBlock(String cidrBlock) {
      queryParameters.put(CIDR_BLOCK_PARAM, cidrBlock);
      return this;
   }

   /**
    * Configures the name of the VPC_PREFIX.
    */
   public CreateVPCOptions vpcName(String vpcName) {
      queryParameters.put(VPC_NAME_PARAM, vpcName);
      return this;
   }

   /**
    * Configures the description of the VPC_PREFIX.
    */
   public CreateVPCOptions description(String description) {
      queryParameters.put(DESCRIPTION_PARAM, description);
      return this;
   }

   /**
    * Configures a client token used to guarantee the idempotence of request.
    */
   public CreateVPCOptions clientToken(String clientToken) {
      queryParameters.put(CLIENT_TOKEN_PARAM, clientToken);
      return this;
   }

   /**
    * Configures the user CIDR.
    */
   public CreateVPCOptions userCidr(String userCidr) {
      queryParameters.put(USER_CIDR_PARAM, userCidr);
      return this;
   }

   public static final class Builder {

      /**
       * @see {@link CreateVPCOptions#cidrBlock(String)}
       */
      public static CreateVPCOptions cidrBlock(String cidrBlock) {
         return new CreateVPCOptions().cidrBlock(cidrBlock);
      }

      /**
       * @see {@link CreateVPCOptions#vpcName(String)}
       */
      public static CreateVPCOptions vpcName(String vpcName) {
         return new CreateVPCOptions().vpcName(vpcName);
      }

      /**
       * @see {@link CreateVPCOptions#description(String)}
       */
      public static CreateVPCOptions description(String description) {
         return new CreateVPCOptions().description(description);
      }

      /**
       * @see {@link CreateVPCOptions#clientToken(String)}
       */
      public static CreateVPCOptions clientToken(String clientToken) {
         return new CreateVPCOptions().clientToken(clientToken);
      }

      /**
       * @see {@link CreateVPCOptions#userCidr(String)}
       */
      public static CreateVPCOptions userCidr(String userCidr) {
         return new CreateVPCOptions().userCidr(userCidr);
      }

   }
}
