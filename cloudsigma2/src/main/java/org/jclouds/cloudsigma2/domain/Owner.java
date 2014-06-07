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
import java.net.URI;

public class Owner {
   public static class Builder {
      private String uuid;
      private URI resourceUri;
      private String email;

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder resourceUri(URI resourceUri) {
         this.resourceUri = resourceUri;
         return this;
      }

      public Builder email(String email) {
         this.email = email;
         return this;
      }

      public Owner build() {
         return new Owner(uuid, resourceUri, email);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
         result = prime * result + ((resourceUri == null) ? 0 : resourceUri.hashCode());
         result = prime * result + ((email == null) ? 0 : email.hashCode());
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
         Builder other = (Builder) obj;
         if (uuid == null) {
            if (other.uuid != null)
               return false;
         } else if (!uuid.equals(other.uuid))
            return false;
         if (resourceUri == null) {
            if (other.resourceUri != null)
               return false;
         } else if (!resourceUri.equals(other.resourceUri))
            return false;
         if (email == null) {
            if (other.email != null)
               return false;
         } else if (!email.equals(other.email))
            return false;
         return true;
      }
   }

   private String uuid;
   @Named("resoource_uri")
   private URI resourceUri;
   private String email;

   @ConstructorProperties({
         "uuid", "resource_uri", "email"
   })
   public Owner(String uuid, URI resourceUri, String email) {
      this.uuid = uuid;
      this.resourceUri = resourceUri;
      this.email = email;
   }

   /**
    * @return uuid of the owner.
    */
   public String getUuid() {
      return uuid;
   }

   /**
    * @return resource uri of the profile.
    */
   public URI getResourceUri() {
      return resourceUri;
   }

   public String getEmail() {
      return email;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
      result = prime * result + ((resourceUri == null) ? 0 : resourceUri.hashCode());
      result = prime * result + ((email == null) ? 0 : email.hashCode());
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

      Owner other = (Owner) obj;

      if (resourceUri == null) {
         if (other.resourceUri != null)
            return false;
      } else if (!resourceUri.equals(other.resourceUri))
         return false;

      if (uuid == null) {
         if (other.uuid != null)
            return false;
      } else if (!uuid.equals(other.uuid))
         return false;

      if (email == null) {
         if (other.email != null)
            return false;
      } else if (!email.equals(other.email))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", email=" + email + ", resourceUri=" + resourceUri + "]";
   }
}
