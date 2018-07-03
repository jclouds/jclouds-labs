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

public class CreateVSwitchOptions extends BaseHttpRequestOptions {
   public static final String VSWITCH_NAME_PARAM = "VSwitchName";
   public static final String DESCRIPTION_PARAM = "Description";
   public static final String CLIENT_TOKEN_PARAM = "ClientToken";

   /**
    * Configures the name of the VSwitch.
    */
   public CreateVSwitchOptions vSwitchName(String vSwitchName) {
      queryParameters.put(VSWITCH_NAME_PARAM, vSwitchName);
      return this;
   }

   /**
    * Configures the description of the VPC_PREFIX.
    */
   public CreateVSwitchOptions description(String description) {
      queryParameters.put(DESCRIPTION_PARAM, description);
      return this;
   }

   /**
    * Configures a client token used to guarantee the idempotence of request.
    */
   public CreateVSwitchOptions clientToken(String clientToken) {
      queryParameters.put(CLIENT_TOKEN_PARAM, clientToken);
      return this;
   }

   public static final class Builder {

      /**
       * @see {@link CreateVSwitchOptions#vSwitchName(String)}
       */
      public static CreateVSwitchOptions vSwitchName(String vSwitchName) {
         return new CreateVSwitchOptions().vSwitchName(vSwitchName);
      }

      /**
       * @see {@link CreateVSwitchOptions#description(String)}
       */
      public static CreateVSwitchOptions description(String description) {
         return new CreateVSwitchOptions().description(description);
      }

      /**
       * @see {@link CreateVSwitchOptions#clientToken(String)}
       */
      public static CreateVSwitchOptions clientToken(String clientToken) {
         return new CreateVSwitchOptions().clientToken(clientToken);
      }

   }
}
