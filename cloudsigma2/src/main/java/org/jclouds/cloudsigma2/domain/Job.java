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

import java.beans.ConstructorProperties;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Named;

public class Job {

   public static class Builder {
      private String resourceUri;
      private String uuid;

      public Builder resourceUri(String resourceUri) {
         this.resourceUri = resourceUri;
         return this;
      }

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Job build() {
         return new Job(resourceUri, uuid);
      }
   }
   
   @Named("resource_uri")
   private final String resourceUri;
   @Named("uuid")
   private final String uuid;

   @ConstructorProperties({
      "resource_uri", "uuid"
   })
   public Job(String resourceUri, String uuid) {
      this.resourceUri = checkNotNull(resourceUri, "resourceUri");
      this.uuid = checkNotNull(uuid, "uuid");
   }

   public String getResourceUri() {
      return resourceUri;
   }

   public String getUuid() {
      return uuid;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((resourceUri == null) ? 0 : resourceUri.hashCode());
      result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (!(obj instanceof Job)) return false;
      Job other = (Job) obj;
      if (resourceUri == null) {
         if (other.resourceUri != null) return false;
      } else if (!resourceUri.equals(other.resourceUri))
         return false;
      if (uuid == null) {
         if (other.uuid != null) return false;
      } else if (!uuid.equals(other.uuid))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Job [resourceUri=" + resourceUri + ", uuid=" + uuid + "]";
   }
}
