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
package org.apache.jclouds.profitbricks.rest.domain;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import org.apache.jclouds.profitbricks.rest.util.Passwords;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import static com.google.common.base.Preconditions.checkArgument;

@AutoValue
public abstract class Volume {

    public abstract String id();
    
    @Nullable
    public abstract String dataCenterId();

    public abstract String type();

    public abstract String href();

    @Nullable
    public abstract Metadata metadata();

    @Nullable
    public abstract Properties properties();

    @SerializedNames({"id", "dataCenterId", "type", "href", "metadata", "properties"})
    public static Volume create(String id, String dataCenterId, String type, String href, Metadata metadata, Properties properties) {
        return new AutoValue_Volume(id, dataCenterId, type, href, metadata, properties);
    }

    @AutoValue
    public abstract static class Properties {

        public enum BusType {

            IDE, SCSI, VIRTIO, UNRECOGNIZED;

            public static BusType fromValue(String value) {
                return Enums.getIfPresent(BusType.class, value).or(UNRECOGNIZED);
            }
        }

        @Nullable
        public abstract String name();

        @Nullable
        public abstract String type();

        public abstract float size();

        @Nullable
        public abstract String image();

        @Nullable
        public abstract String imagePassword();
        
        @Nullable
        public abstract BusType bus();

        public abstract LicenceType licenceType();

        public abstract boolean cpuHotPlug();

        public abstract boolean cpuHotUnplug();

        public abstract boolean ramHotPlug();

        public abstract boolean ramHotUnplug();

        public abstract boolean nicHotPlug();

        public abstract boolean nicHotUnplug();

        public abstract boolean discVirtioHotPlug();

        public abstract boolean discVirtioHotUnplug();

        public abstract boolean discScsiHotPlug();

        public abstract boolean discScsiHotUnplug();

        @Nullable
        public abstract Integer deviceNumber();

        @SerializedNames({"name", "type", "size", "image", "imagePassword", "bus", "licenceType", "cpuHotPlug", "cpuHotUnplug", "ramHotPlug", "ramHotUnplug", "nicHotPlug", "nicHotUnplug", "discVirtioHotPlug", "discVirtioHotUnplug", "discScsiHotPlug", "discScsiHotUnplug", "deviceNumber"})
        public static Volume.Properties create(String name, String type, float size, String image, String imagePassword, BusType bus, LicenceType licenceType,
                boolean cpuHotPlug, boolean cpuHotUnplug, boolean ramHotPlug, boolean ramHotUnplug, boolean nicHotPlug, boolean nicHotUnplug, boolean discVirtioHotPlug,
                boolean discVirtioHotUnplug, boolean discScsiHotPlug, boolean discScsiHotUnplug, int deviceNumber) {

            return new AutoValue_Volume_Properties(name, type, size, image, imagePassword, bus, licenceType, cpuHotPlug, cpuHotUnplug, ramHotPlug, ramHotUnplug, nicHotPlug, nicHotUnplug, discVirtioHotPlug, discVirtioHotUnplug, discScsiHotPlug, discScsiHotUnplug, deviceNumber);

        }
    }

   
   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new AutoValue_Volume_Request_CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new AutoValue_Volume_Request_UpdatePayload.Builder();
      }
      
      public static CreateSnapshotPayload.Builder createSnapshotBuilder() {
         return new AutoValue_Volume_Request_CreateSnapshotPayload.Builder();
      }
      
      public static RestoreSnapshotPayload.Builder restoreSnapshotBuilder() {
         return new AutoValue_Volume_Request_RestoreSnapshotPayload.Builder();
      }
      
      @AutoValue
      public abstract static class CreatePayload {

         @Nullable
         public abstract String name();

         @Nullable
         public abstract String type();

         public abstract int size();

         @Nullable
         public abstract String image();

         @Nullable
         public abstract String imagePassword();

         @Nullable
         public abstract Properties.BusType bus();

         @Nullable
         public abstract LicenceType licenceType();
         
         public abstract String dataCenterId();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder name(String name);
            public abstract Builder type(String type);
            public abstract Builder size(int size);
            public abstract Builder image(String image);
            public abstract Builder imagePassword(String imagePassword);
            public abstract Builder bus(Properties.BusType bus);
            public abstract Builder licenceType(LicenceType licenceType);
            public abstract Builder dataCenterId(String dataCenterId);

            abstract CreatePayload autoBuild();

            public CreatePayload build() {
               CreatePayload payload = autoBuild();
               
               if (payload.imagePassword() != null)
                  checkArgument(Passwords.isValidPassword(payload.imagePassword()), "Password's format is not valid");
               
               checkArgument(
                  payload.image() != null || payload.licenceType() != null,
                  "Either image or licenceType need to be present"
               );
               
               return payload;
            }
         }

      }

      @AutoValue
      public abstract static class UpdatePayload {

         @Nullable
         public abstract String name();

         @Nullable
         public abstract Integer size();

         @Nullable
         public abstract Properties.BusType bus();
         
         public abstract String dataCenterId();
         public abstract String id();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder name(String name);
            public abstract Builder size(Integer size);
            public abstract Builder bus(Properties.BusType bus);
            public abstract Builder dataCenterId(String dataCenterId);
            public abstract Builder id(String id);

            abstract UpdatePayload autoBuild();

            public UpdatePayload build() {
               return autoBuild();
            }
         }
      }
      
      @AutoValue
      public abstract static class CreateSnapshotPayload {

         @Nullable
         public abstract String name();

         @Nullable
         public abstract String description();
         
         public abstract String dataCenterId();
         public abstract String volumeId();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder name(String name);
            public abstract Builder description(String description);
            public abstract Builder dataCenterId(String dataCenterId);
            public abstract Builder volumeId(String volumeId);

            abstract CreateSnapshotPayload autoBuild();

            public CreateSnapshotPayload build() {
               return autoBuild();
            }
         }

      }
      
      @AutoValue
      public abstract static class RestoreSnapshotPayload {

         public abstract String snapshotId();
         public abstract String dataCenterId();
         public abstract String volumeId();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder snapshotId(String snapshotId);
            public abstract Builder dataCenterId(String dataCenterId);
            public abstract Builder volumeId(String volumeId);

            abstract RestoreSnapshotPayload autoBuild();

            public RestoreSnapshotPayload build() {
               return autoBuild();
            }
         }

      }
      
   }
   
    
}
