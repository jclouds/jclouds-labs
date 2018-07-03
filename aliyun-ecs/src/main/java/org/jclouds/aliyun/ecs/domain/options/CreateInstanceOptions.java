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

public class CreateInstanceOptions extends BaseHttpRequestOptions {
   public static final String VSWITCH_PARAM = "VSwitchId";
   public static final String INSTANCE_NAME_PARAM = "InstanceName";
   public static final String KEYPAIR_PARAM = "KeyPairName";
   public static final String INTERNET_CHARGE_TYPE_PARAM = "InternetChargeType";
   public static final String INTERNET_MAX_BANDWITH_IN_PARAM = "InternetMaxBandwidthIn";
   public static final String INTERNET_MAX_BANDWITH_OUT_PARAM = "InternetMaxBandwidthOut";
   public static final String INSTANCE_CHARGE_TYPE_PARAM = "InstanceChargeType";

   /**
    * Configures the name of the instance to be used.
    */
   public CreateInstanceOptions instanceName(String instanceName) {
      queryParameters.put(INSTANCE_NAME_PARAM, instanceName);
      return this;
   }

   /**
    * Configures the vSwitch Id to be used.
    */
   public CreateInstanceOptions vSwitchId(String vSwitchId) {
      queryParameters.put(VSWITCH_PARAM, vSwitchId);
      return this;
   }

   /**
    * Configures the keyPairName to be used.
    */
   public CreateInstanceOptions keyPairName(String keyPairName) {
      queryParameters.put(KEYPAIR_PARAM, keyPairName);
      return this;
   }

   /**
    * Configures the internet charge type of the instances to be used.
    */
   public CreateInstanceOptions internetChargeType(String internetChargeType) {
      queryParameters.put(INTERNET_CHARGE_TYPE_PARAM, internetChargeType);
      return this;
   }

   /**
    * Configures the internet max bandwidth in of the instances to be used.
    */
   public CreateInstanceOptions internetMaxBandwidthIn(int internetMaxBandwidthIn) {
      queryParameters.put(INTERNET_MAX_BANDWITH_IN_PARAM, String.valueOf(internetMaxBandwidthIn));
      return this;
   }

   /**
    * Configures the internet max bandwidth out of the instances to be used.
    */
   public CreateInstanceOptions internetMaxBandwidthOut(int internetMaxBandwidthOut) {
      queryParameters.put(INTERNET_MAX_BANDWITH_OUT_PARAM, String.valueOf(internetMaxBandwidthOut));
      return this;
   }

   /**
    * Configures the instance charge type of the instances to be used.
    */
   public CreateInstanceOptions instanceChargeType(String instanceChargeType) {
      queryParameters.put(INSTANCE_CHARGE_TYPE_PARAM, instanceChargeType);
      return this;
   }

   public CreateInstanceOptions paginationOptions(final PaginationOptions paginationOptions) {
      this.queryParameters.putAll(paginationOptions.buildQueryParameters());
      return this;
   }

   public CreateInstanceOptions tagOptions(final TagOptions tagOptions) {
      this.queryParameters.putAll(tagOptions.buildQueryParameters());
      return this;
   }

   public static final class Builder {

      /**
       * @see {@link CreateInstanceOptions#instanceName(String)}
       */
      public static CreateInstanceOptions instanceName(String instanceName) {
         return new CreateInstanceOptions().instanceName(instanceName);
      }

      /**
       * @see {@link CreateInstanceOptions#vSwitchId(String)}
       */
      public static CreateInstanceOptions vSwitchId(String vSwitchId) {
         return new CreateInstanceOptions().vSwitchId(vSwitchId);
      }

      /**
       * @see {@link CreateInstanceOptions#keyPairName(String)}
       */
      public static CreateInstanceOptions keyPairName(String keyPairName) {
         return new CreateInstanceOptions().keyPairName(keyPairName);
      }

      /**
       * @see {@link CreateInstanceOptions#internetChargeType(String)}
       */
      public static CreateInstanceOptions internetChargeType(String internetChargeType) {
         return new CreateInstanceOptions().internetChargeType(internetChargeType);
      }

      /**
       * @see {@link CreateInstanceOptions#internetMaxBandwidthIn(int)}
       */
      public static CreateInstanceOptions internetMaxBandwidthIn(int internetMaxBandwidthIn) {
         return new CreateInstanceOptions().internetMaxBandwidthIn(internetMaxBandwidthIn);
      }

      /**
       * @see {@link CreateInstanceOptions#internetMaxBandwidthOut(int)}
       */
      public static CreateInstanceOptions internetMaxBandwidthOut(int internetMaxBandwidthOut) {
         return new CreateInstanceOptions().internetMaxBandwidthOut(internetMaxBandwidthOut);
      }

      /**
       * @see {@link CreateInstanceOptions#instanceChargeType(String)}
       */
      public static CreateInstanceOptions instanceChargeType(String instanceChargeType) {
         return new CreateInstanceOptions().instanceChargeType(instanceChargeType);
      }

      /**
       * @see CreateInstanceOptions#paginationOptions(PaginationOptions)
       */
      public static CreateInstanceOptions paginationOptions(PaginationOptions paginationOptions) {
         return new CreateInstanceOptions().paginationOptions(paginationOptions);
      }

      /**
       * @see CreateInstanceOptions#tagOptions(TagOptions)
       */
      public static CreateInstanceOptions tagOptions(TagOptions tagOptions) {
         return new CreateInstanceOptions().tagOptions(tagOptions);
      }
   }
}
