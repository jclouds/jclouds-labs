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
package org.jclouds.cloudsigma2.domain;

import javax.inject.Named;
import java.beans.ConstructorProperties;

public class ServerDrive {

   public static class Builder {
      private int bootOrder;
      private String deviceChannel;
      private DeviceEmulationType deviceEmulationType;
      private Drive drive;

      public Builder bootOrder(int bootOrder) {
         this.bootOrder = bootOrder;
         return this;
      }

      public Builder deviceChannel(String deviceChannel) {
         this.deviceChannel = deviceChannel;
         return this;
      }

      public Builder deviceEmulationType(DeviceEmulationType deviceEmulationType) {
         this.deviceEmulationType = deviceEmulationType;
         return this;
      }

      public Builder drive(Drive drive) {
         this.drive = drive;
         return this;
      }

      public ServerDrive build() {
         return new ServerDrive(bootOrder, deviceChannel, deviceEmulationType, drive);
      }
   }

   @Named("boot_order")
   private final int bootOrder;
   @Named("dev_channel")
   private final String deviceChannel;
   @Named("device")
   private final DeviceEmulationType deviceEmulationType;
   @Named("drive")
   private final Drive drive;
   private final String driveUuid;

   /**
    * @param bootOrder           drive boot order
    * @param deviceChannel       device channel in format {controller:unit} ex. 0:1, 0:2, etc.
    * @param deviceEmulationType device emulation type
    * @param drive               drive to attach. UUID Required.
    */
   @ConstructorProperties({
         "boot_order", "dev_channel", "device", "drive"
   })
   public ServerDrive(int bootOrder, String deviceChannel, DeviceEmulationType deviceEmulationType, Drive drive) {
      this.bootOrder = bootOrder;
      this.deviceChannel = deviceChannel;
      this.deviceEmulationType = deviceEmulationType;
      this.drive = drive;
      this.driveUuid = drive.getUuid();
   }

   /**
    * @param bootOrder           drive boot order
    * @param deviceChannel       device channel in format {controller:unit} ex. 0:1, 0:2, etc.
    * @param deviceEmulationType device emulation type
    * @param driveUuid           drive uuid.
    */
   public ServerDrive(int bootOrder, String deviceChannel, DeviceEmulationType deviceEmulationType, String driveUuid) {
      this.bootOrder = bootOrder;
      this.deviceChannel = deviceChannel;
      this.deviceEmulationType = deviceEmulationType;
      this.driveUuid = driveUuid;
      this.drive = null;
   }

   /**
    * @return drive boot order
    */
   public int getBootOrder() {
      return bootOrder;
   }

   /**
    * @return device channel in format {controller:unit} ex. 0:1, 0:2, etc.
    */
   public String getDeviceChannel() {
      return deviceChannel;
   }

   /**
    * @return device emulation type
    */
   public DeviceEmulationType getDeviceEmulationType() {
      return deviceEmulationType;
   }

   /**
    * @return drive
    */
   public Drive getDrive() {
      return drive;
   }

   /**
    * @return drive uuid
    */
   public String getDriveUuid() {
      return driveUuid;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ServerDrive)) return false;

      ServerDrive that = (ServerDrive) o;

      if (bootOrder != that.bootOrder) return false;
      if (deviceChannel != null ? !deviceChannel.equals(that.deviceChannel) : that.deviceChannel != null)
         return false;
      if (deviceEmulationType != that.deviceEmulationType) return false;
      if (drive != null ? !drive.equals(that.drive) : that.drive != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = bootOrder;
      result = 31 * result + (deviceChannel != null ? deviceChannel.hashCode() : 0);
      result = 31 * result + (deviceEmulationType != null ? deviceEmulationType.hashCode() : 0);
      result = 31 * result + (drive != null ? drive.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[bootOrder=" + bootOrder + ", deviceChannel=" + deviceChannel
            + ", deviceEmulationType=" + deviceEmulationType + ", drive=" + drive + "]";
   }
}
