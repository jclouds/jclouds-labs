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

public class TagResource {

   public static class Builder {
      private Owner owner;
      private URI resourceUri;
      private String uuid;
      private TagResourceType resourceType;

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder resourceUri(URI resourceUri) {
         this.resourceUri = resourceUri;
         return this;
      }

      public Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      public Builder resourceType(TagResourceType resourceType) {
         this.resourceType = resourceType;
         return this;
      }

      public TagResource build() {
         return new TagResource(uuid, resourceType, owner, resourceUri);
      }
   }

   private final Owner owner;
   @Named("resource_uri")
   private final URI resourceUri;
   private final String uuid;
   @Named("resource_type")
   private final TagResourceType resourceType;

   @ConstructorProperties({
         "uuid", "resource_type", "owner", "resource_uri"
   })
   public TagResource(String uuid, TagResourceType resourceType, Owner owner, URI resourceUri) {
      this.owner = owner;
      this.resourceUri = resourceUri;
      this.uuid = uuid;
      this.resourceType = resourceType;
   }

   /**
    * @return resource owner
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * @return resource type
    */
   public TagResourceType getResourceType() {
      return resourceType;
   }

   /**
    * @return resource uri
    */
   public URI getResourceUri() {
      return resourceUri;
   }

   /**
    * @return resource uuid
    */
   public String getUuid() {
      return uuid;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TagResource other = (TagResource) obj;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
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
      if (resourceType == null) {
         if (other.resourceType != null)
            return false;
      } else if (!resourceType.equals(other.resourceType))
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((resourceUri == null) ? 0 : resourceUri.hashCode());
      result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
      return result;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", owner=" + owner
            + ", resourceUri=" + resourceUri + ", resourceType=" + resourceType + "]";
   }
}
