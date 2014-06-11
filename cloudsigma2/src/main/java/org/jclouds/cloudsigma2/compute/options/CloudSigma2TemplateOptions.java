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
package org.jclouds.cloudsigma2.compute.options;

import com.google.common.base.Objects.ToStringHelper;
import org.jclouds.cloudsigma2.domain.DeviceEmulationType;
import org.jclouds.cloudsigma2.domain.Model;
import org.jclouds.compute.options.TemplateOptions;

public class CloudSigma2TemplateOptions extends TemplateOptions {

   private DeviceEmulationType deviceEmulationType = DeviceEmulationType.VIRTIO;
   private Model nicModel = Model.VIRTIO;
   private String vncPassword;

   /**
    * Configures the device emulation type.
    */
   public CloudSigma2TemplateOptions deviceEmulationType(DeviceEmulationType deviceEmulationType) {
      this.deviceEmulationType = deviceEmulationType;
      return this;
   }

   /**
    * Configures the type of NICs to create.
    */
   public CloudSigma2TemplateOptions nicModel(Model nicModel) {
      this.nicModel = nicModel;
      return this;
   }

   /**
    * Configures the vnc password.
    */
   public CloudSigma2TemplateOptions vncPassword(String vncPassword) {
      this.vncPassword = vncPassword;
      return this;
   }

   public DeviceEmulationType getDeviceEmulationType() {
      return deviceEmulationType;
   }

   public Model getNicModel() {
      return nicModel;
   }

   public String getVncPassword() {
      return vncPassword;
   }

   @Override
   public TemplateOptions clone() {
      CloudSigma2TemplateOptions options = new CloudSigma2TemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof CloudSigma2TemplateOptions) {
         CloudSigma2TemplateOptions eTo = CloudSigma2TemplateOptions.class.cast(to);
         eTo.deviceEmulationType(deviceEmulationType);
         eTo.nicModel(nicModel);
         eTo.vncPassword(vncPassword);
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((deviceEmulationType == null) ? 0 : deviceEmulationType.hashCode());
      result = prime * result + ((nicModel == null) ? 0 : nicModel.hashCode());
      result = prime * result + ((vncPassword == null) ? 0 : vncPassword.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      CloudSigma2TemplateOptions other = (CloudSigma2TemplateOptions) obj;
      if (deviceEmulationType != other.deviceEmulationType)
         return false;
      if (nicModel != other.nicModel)
         return false;
      if (vncPassword == null) {
         if (other.vncPassword != null)
            return false;
      } else if (!vncPassword.equals(other.vncPassword))
         return false;
      return true;
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper toString = super.string().omitNullValues();
      toString.add("deviceEmulationType", deviceEmulationType);
      toString.add("nicModel", nicModel);
      toString.add("vncPassword", vncPassword);
      return toString;
   }

   public static class Builder {

      /**
       * @see CloudSigma2TemplateOptions#deviceEmulationType(DeviceEmulationType)
       */
      public CloudSigma2TemplateOptions deviceEmulationType(DeviceEmulationType deviceEmulationType) {
         CloudSigma2TemplateOptions options = new CloudSigma2TemplateOptions();
         options.deviceEmulationType(deviceEmulationType);
         return options;
      }

      /**
       * @see CloudSigma2TemplateOptions#nicModel(Model)
       */
      public CloudSigma2TemplateOptions nicModel(Model nicModel) {
         CloudSigma2TemplateOptions options = new CloudSigma2TemplateOptions();
         options.nicModel(nicModel);
         return options;
      }

      /**
       * @see CloudSigma2TemplateOptions#vncPassword(String)
       */
      public CloudSigma2TemplateOptions vncPassword(String vncPassword) {
         CloudSigma2TemplateOptions options = new CloudSigma2TemplateOptions();
         options.vncPassword(vncPassword);
         return options;
      }
   }
}
