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
package org.jclouds.aliyun.ecs.compute.options;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jclouds.compute.options.TemplateOptions;

import static com.google.common.base.Objects.equal;

/**
 * Custom options for the Alibaba Elastic Compute Service API.
 */
public class ECSServiceTemplateOptions extends TemplateOptions implements Cloneable {

   private String keyPairName = "";
   private String vSwitchId = "";
   private String internetChargeType = "PayByTraffic";
   private String instanceChargeType = "PostPaid";
   private int internetMaxBandwidthOut = 5;

   public ECSServiceTemplateOptions keyPairName(String keyPairName) {
      this.keyPairName = keyPairName;
      return this;
   }

   public ECSServiceTemplateOptions vSwitchId(String vSwitchId) {
      this.vSwitchId = vSwitchId;
      return this;
   }

   public ECSServiceTemplateOptions internetChargeType(String internetChargeType) {
      this.internetChargeType = internetChargeType;
      return this;
   }

   public ECSServiceTemplateOptions instanceChargeType(String instanceChargeType) {
      this.instanceChargeType = instanceChargeType;
      return this;
   }

   public ECSServiceTemplateOptions internetMaxBandwidthOut(int internetMaxBandwidthOut) {
      this.internetMaxBandwidthOut = internetMaxBandwidthOut;
      return this;
   }

   public String getKeyPairName() {
      return keyPairName;
   }

   public String getVSwitchId() {
      return vSwitchId;
   }

   public String getInternetChargeType() {
      return internetChargeType;
   }

   public String getInstanceChargeType() {
      return instanceChargeType;
   }

   public int getInternetMaxBandwidthOut() {
      return internetMaxBandwidthOut;
   }

   @Override
   public ECSServiceTemplateOptions clone() {
      ECSServiceTemplateOptions options = new ECSServiceTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof ECSServiceTemplateOptions) {
         ECSServiceTemplateOptions eTo = ECSServiceTemplateOptions.class.cast(to);
         eTo.keyPairName(keyPairName);
         eTo.vSwitchId(vSwitchId);
         eTo.internetChargeType(internetChargeType);
         eTo.instanceChargeType(instanceChargeType);
         eTo.internetMaxBandwidthOut(internetMaxBandwidthOut);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), keyPairName, vSwitchId, internetChargeType, instanceChargeType, internetMaxBandwidthOut);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      ECSServiceTemplateOptions other = (ECSServiceTemplateOptions) obj;
      return super.equals(other) &&
              equal(this.keyPairName, other.keyPairName) &&
              equal(this.vSwitchId, other.vSwitchId) &&
              equal(this.internetChargeType, other.internetChargeType) &&
              equal(this.instanceChargeType, other.instanceChargeType) &&
              equal(this.internetMaxBandwidthOut, other.internetMaxBandwidthOut);
   }

   @Override
   public MoreObjects.ToStringHelper string() {
      MoreObjects.ToStringHelper toString = super.string().omitNullValues();
      toString.add("keyPairName", keyPairName);
      toString.add("vSwitchId", vSwitchId);
      toString.add("internetChargeType", internetChargeType);
      toString.add("instanceChargeType", instanceChargeType);
      toString.add("internetMaxBandwidthOut", internetMaxBandwidthOut);
      return toString;
   }

   public static class Builder {

      /**
       * @see ECSServiceTemplateOptions#keyPairName
       */
      public static ECSServiceTemplateOptions keyPairName(String keyPairName) {
         ECSServiceTemplateOptions options = new ECSServiceTemplateOptions();
         return options.keyPairName(keyPairName);
      }

      /**
       * @see ECSServiceTemplateOptions#vSwitchId
       */
      public static ECSServiceTemplateOptions vSwitchId(String vSwitchId) {
         ECSServiceTemplateOptions options = new ECSServiceTemplateOptions();
         return options.vSwitchId(vSwitchId);
      }

      /**
       * @see ECSServiceTemplateOptions#internetChargeType
       */
      public static ECSServiceTemplateOptions internetChargeType(String internetChargeType) {
         ECSServiceTemplateOptions options = new ECSServiceTemplateOptions();
         return options.internetChargeType(internetChargeType);
      }

      /**
       * @see ECSServiceTemplateOptions#instanceChargeType
       */
      public static ECSServiceTemplateOptions instanceChargeType(String instanceChargeType) {
         ECSServiceTemplateOptions options = new ECSServiceTemplateOptions();
         return options.instanceChargeType(instanceChargeType);
      }

      /**
       * @see ECSServiceTemplateOptions#internetMaxBandwidthOut
       */
      public static ECSServiceTemplateOptions internetMaxBandwidthOut(int internetMaxBandwidthOut) {
         ECSServiceTemplateOptions options = new ECSServiceTemplateOptions();
         return options.internetMaxBandwidthOut(internetMaxBandwidthOut);
      }

   }
}
