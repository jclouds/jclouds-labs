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
import static org.apache.jclouds.profitbricks.rest.util.Preconditions.checkCores;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Server {
   
   public abstract String id();
   
   @Nullable
   public abstract String dataCenterId();

   public abstract String type();

   public abstract String href();
   
   @Nullable
   public abstract Metadata metadata();

   @Nullable
   public abstract Properties properties();

   @Nullable
   public abstract Entities entities();

   @SerializedNames({"id", "dataCenterId", "type", "href", "metadata", "properties", "entities"})
   public static Server create(String id, String dataCenterId, String type, String href, Metadata metadata, Properties properties, Entities entities) {
      return new AutoValue_Server(id, dataCenterId, type, href, metadata, properties, entities);
   }

   @AutoValue
   public abstract static class Properties {

      public abstract String name();

      public abstract int cores();

      public abstract int ram();

      @Nullable
      public abstract AvailabilityZone availabilityZone();
      
      @Nullable
      public abstract Server.Status vmState();
      
      @Nullable
      public abstract LicenceType licenceType();

      @Nullable
      public abstract Volume bootVolume();

      @Nullable
      public abstract Volume bootCdrom();

      @SerializedNames({"name", "cores", "ram", "availabilityZone", "vmState", "licenceType", "bootVolume", "bootCdrom"})
      public static Properties create(String name, int cores, int ram, AvailabilityZone availabilityZone, Server.Status vmState, LicenceType licenceType, Volume bootVolume, Volume bootCdrom) {
         return new AutoValue_Server_Properties(name, cores, ram, availabilityZone, vmState, licenceType, bootVolume, bootCdrom);
      }

   }

   @AutoValue
   public abstract static class Entities {

      @Nullable
      public abstract Images cdroms();
      
      @Nullable
      public abstract Volumes volumes();

      @Nullable
      public abstract Nics nics();

      @SerializedNames({"cdroms", "volumes", "nics"})
      public static Entities create(Images cdroms, Volumes volumes, Nics nics) {
         return new AutoValue_Server_Entities(cdroms, volumes, nics);
      }

   }
   
   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new AutoValue_Server_Request_CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new AutoValue_Server_Request_UpdatePayload.Builder();
      }
      
      public static AttachCdromPayload.Builder attachCdromBuilder() {
         return new AutoValue_Server_Request_AttachCdromPayload.Builder();
      }
      
      public static AttachVolumePayload.Builder attachVolumeBuilder() {
         return new AutoValue_Server_Request_AttachVolumePayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract String name();
         
         public abstract int cores();

         public abstract int ram();

         public abstract String dataCenterId();

         @Nullable
         public abstract Volume bootVolume();

         @Nullable
         public abstract Volume bootCdrom();

         @Nullable
         public abstract AvailabilityZone availabilityZone();
         
         @Nullable
         public abstract LicenceType licenceType();
        
         @Nullable
         public abstract Entities entities();
                  
         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder name(String name);

            public abstract Builder cores(int cores);

            public abstract Builder ram(int ram);

            public abstract Builder dataCenterId(String dataCenterId);

            public abstract Builder bootVolume(Volume bootVolume);

            public abstract Builder bootCdrom(Volume bootCdrom);

            public abstract Builder availabilityZone(AvailabilityZone availabilityZone);
            
            public abstract Builder licenceType(LicenceType licenceType);
           
            public abstract Builder entities(Entities entities);
            
            abstract CreatePayload autoBuild();

            public CreatePayload build() {
               CreatePayload payload = autoBuild();
               checkCores(payload.cores());
               return payload;
            }
         }

      }

      @AutoValue
      public abstract static class UpdatePayload {

         public abstract String id();
         
         public abstract String dataCenterId();

         @Nullable
         public abstract String name();
         
         @Nullable
         public abstract Integer cores();

         @Nullable
         public abstract Integer ram();

         @Nullable
         public abstract Volume bootVolume();

         @Nullable
         public abstract Volume bootCdrom();

         @Nullable
         public abstract AvailabilityZone availabilityZone();
         
         @Nullable
         public abstract LicenceType licenceType();

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder id(String id);
            
            public abstract Builder dataCenterId(String dataCenterId);
            
            public abstract Builder name(String name);

            public abstract Builder cores(Integer cores);

            public abstract Builder ram(Integer ram);

            public abstract Builder bootVolume(Volume bootVolume);

            public abstract Builder bootCdrom(Volume bootCdrom);

            public abstract Builder availabilityZone(AvailabilityZone availabilityZone);
            
            public abstract Builder licenceType(LicenceType licenceType);

            abstract UpdatePayload autoBuild();

            public UpdatePayload build() {
               UpdatePayload payload = autoBuild();
               if (payload.cores() != null)
                  checkCores(payload.cores());
               return payload;
            }
         }
      }
      
      @AutoValue
      public abstract static class AttachCdromPayload {

         public abstract String imageId();
         public abstract String dataCenterId();
         public abstract String serverId();
         
         @AutoValue.Builder
         public abstract static class Builder {
            
            public abstract Builder imageId(String imageId);
            public abstract Builder dataCenterId(String dataCenterId);
            public abstract Builder serverId(String serverId);

            abstract AttachCdromPayload autoBuild();

            public AttachCdromPayload build() {
               return autoBuild();
            }
         }
      }
      
      @AutoValue 
      public abstract static class AttachVolumePayload {

         public abstract String volumeId();
         public abstract String dataCenterId();
         public abstract String serverId();
         
         @AutoValue.Builder
         public abstract static class Builder {
            
            public abstract Builder volumeId(String volumeId);
            public abstract Builder dataCenterId(String dataCenterId);
            public abstract Builder serverId(String serverId);

            abstract AttachVolumePayload autoBuild();

            public AttachVolumePayload build() {
               return autoBuild();
            }
         }
      }
   }
   
   public enum Status {

      NOSTATE, RUNNING, BLOCKED, PAUSED, SHUTDOWN, SHUTOFF, CRASHED, UNRECOGNIZED;

      public String value() {
         return name();
      }

      public static Status fromValue(String v) {
         return Enums.getIfPresent(Status.class, v).or(UNRECOGNIZED);
      }
   }

}
