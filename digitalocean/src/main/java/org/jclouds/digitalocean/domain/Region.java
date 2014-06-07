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
package org.jclouds.digitalocean.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

/**
 * A Region.
 */
public class Region {

   private final int id;
   private final String name;
   private final String slug;

   @ConstructorProperties({ "id", "name", "slug" })
   public Region(int id, String name, String slug) {
      this.id = id;
      this.name = checkNotNull(name, "name cannot be null");
      this.slug = checkNotNull(slug, "slug cannot be null");
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getSlug() {
      return slug;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (slug == null ? 0 : slug.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Region other = (Region) obj;
      if (id != other.id) {
         return false;
      }
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (slug == null) {
         if (other.slug != null) {
            return false;
         }
      } else if (!slug.equals(other.slug)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "Region [id=" + id + ", name=" + name + ", slug=" + slug + "]";
   }

}
