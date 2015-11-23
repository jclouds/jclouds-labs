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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Server {

   public abstract String id();

   public abstract String type();

   public abstract String href();

   @Nullable
   public abstract Metadata metadata();

   @Nullable
   public abstract Properties properties();

   @Nullable
   public abstract Entities entities();

   @SerializedNames({"id", "type", "href", "metadata", "properties", "entities"})
   public static Server create(String id, String type, String href, Metadata metadata, Properties properties, Entities entities) {
      return new AutoValue_Server(id, type, href, metadata, properties, entities);
   }

   @AutoValue
   public abstract static class Properties {

      public abstract String name();

      public abstract int cores();

      public abstract int ram();

      public abstract AvailabilityZone availabilityZone();

      public abstract VMState vmState();

      @Nullable
      public abstract Volume bootVolume();

      @Nullable
      public abstract Volume bootCdrom();

      @SerializedNames({"name", "cores", "ram", "availabilityZone", "vmState", "bootVolume", "bootCdrom"})
      public static Properties create(String name, int cores, int ram, AvailabilityZone availabilityZone, VMState vmState, Volume bootVolume, Volume bootCdrom) {
         return new AutoValue_Server_Properties(name, cores, ram, availabilityZone, vmState, bootVolume, bootCdrom);
      }

   }

   @AutoValue
   public abstract static class Entities {

      public abstract Volumes cdroms();

      public abstract Volumes volumes();

      public abstract Nics nics();

      @SerializedNames({"cdroms", "volumes", "nics"})
      public static Entities create(Volumes cdroms, Volumes volumes, Nics nics) {
	 return new AutoValue_Server_Entities(cdroms, volumes, nics);
      }

   }
}
