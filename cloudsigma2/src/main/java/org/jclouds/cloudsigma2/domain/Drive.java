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

import com.google.common.base.Objects;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.net.URI;

public class Drive extends Item {

   public static class Builder extends Item.Builder {
      protected Owner owner;
      protected DriveStatus status = DriveStatus.UNMOUNTED;

      /**
       * @param owner Owner of the drive
       * @return Drive Builder
       */
      public Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @param status Status of the drive
       * @return Drive Builder
       */
      public Builder status(DriveStatus status) {
         this.status = status;
         return this;
      }

      /**
       * @param uuid UUID of the drive
       * @return Drive Builder
       */
      @Override
      public Builder uuid(String uuid) {
         return Builder.class.cast(super.uuid(uuid));
      }

      /**
       * @param name Human readable name of the drive
       * @return Drive Builder
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * @return Drive Builder
       */
      @Override
      public Builder resourceUri(URI resourceUri) {
         return Builder.class.cast(super.resourceUri(resourceUri));
      }

      public Drive build() {
         return new Drive(uuid, name, resourceUri, owner, status);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         result = prime * result + ((owner == null) ? 0 : owner.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         Drive other = (Drive) obj;
         if (owner != other.owner)
            return false;
         if (!Objects.equal(status, other.status))
            return false;
         if (!Objects.equal(name, other.name))
            return false;
         return true;
      }
   }

   protected final Owner owner;
   protected final DriveStatus status;

   @ConstructorProperties({
         "uuid", "name", "resource_uri", "owner", "status"
   })
   public Drive(@Nullable String uuid, String name, @Nullable URI resourceUri, @Nullable Owner owner,
                DriveStatus status) {
      super(uuid, name, resourceUri);

      this.owner = owner;
      this.status = status;
   }

   /**
    * @return Owner of the drive
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * @return Status of the drive
    */
   public DriveStatus getStatus() {
      return status;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Drive)) return false;
      if (!super.equals(o)) return false;

      Drive drive = (Drive) o;

      if (owner != null ? !owner.equals(drive.owner) : drive.owner != null) return false;
      if (status != drive.status) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (owner != null ? owner.hashCode() : 0);
      result = 31 * result + (status != null ? status.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", name=" + name + ", owner=" + owner + ", status=" + status + "]";
   }

   /**
    * Creates drive for attaching to server
    *
    * @param bootOrder           drive boot order
    * @param deviceChannel       device channel in format {controller:unit} ex. 0:1, 0:2, etc.
    * @param deviceEmulationType device emulation type
    */
   public ServerDrive toServerDrive(int bootOrder, String deviceChannel, DeviceEmulationType deviceEmulationType) {
      return new ServerDrive(bootOrder, deviceChannel, deviceEmulationType, this.uuid);
   }
}
