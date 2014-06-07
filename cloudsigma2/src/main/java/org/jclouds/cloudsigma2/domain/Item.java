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

import org.jclouds.javax.annotation.Nullable;

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.net.URI;

public class Item {
   public static class Builder {
      protected String uuid;
      protected String name;
      protected URI resourceUri;

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder resourceUri(URI resourceUri) {
         this.resourceUri = resourceUri;
         return this;
      }

      public Item build() {
         return new Item(uuid, name, resourceUri);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
         result = prime * result + ((resourceUri == null) ? 0 : resourceUri.hashCode());
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
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
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
         return true;
      }
   }

   @Nullable
   protected final String uuid;
   @Nullable
   protected final String name;
   @Nullable
   @Named("resource_uri")
   protected final URI resourceUri;

   @ConstructorProperties({
         "uuid", "name", "resource_uri"
   })
   public Item(@Nullable String uuid, @Nullable String name, @Nullable URI resourceUri) {
      this.uuid = uuid;
      this.name = name;
      this.resourceUri = resourceUri;
   }

   /**
    * @return uuid of the item.
    */
   @Nullable
   public String getUuid() {
      return uuid;
   }

   /**
    * @return Human readable name
    */
   public String getName() {
      return name;
   }

   /**
    * @return Unicode string data.
    */
   @Nullable
   public URI getResourceUri() {
      return resourceUri;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      Item other = (Item) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
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

      return true;
   }

   @Override

   public String toString() {
      return "[uuid=" + uuid + ", name=" + name + ", resourceUri=" + resourceUri + "]";
   }
}
