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

import javax.inject.Named;

/**
 * A Size.
 */
public class Size {

   private final int id;
   private final String name;
   private final String slug;
   private final int memory;
   private final int cpu;
   private final int disk;
   @Named("cost_per_hour")
   private final String costPerHour;
   @Named("cost_per_month")
   private final String costPerMonth;

   @ConstructorProperties({ "id", "name", "slug", "memory", "cpu", "disk", "cost_per_hour", "cost_per_month" })
   public Size(int id, String name, String slug, int memory, int cpu, int disk, String costPerHour, String costPerMonth) {
      this.id = id;
      this.name = checkNotNull(name, "name cannot be null");
      this.slug = checkNotNull(slug, "slug");
      this.memory = memory;
      this.cpu = cpu;
      this.disk = disk;
      this.costPerHour = checkNotNull(costPerHour, "costPerHour cannot be null");
      this.costPerMonth = checkNotNull(costPerMonth, "costPerMonth cannot be null");
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

   public int getMemory() {
      return memory;
   }

   public int getCpu() {
      return cpu;
   }

   public int getDisk() {
      return disk;
   }

   public String getCostPerHour() {
      return costPerHour;
   }

   public String getCostPerMonth() {
      return costPerMonth;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = prime * result + (costPerHour == null ? 0 : costPerHour.hashCode());
      result = prime * result + (costPerMonth == null ? 0 : costPerMonth.hashCode());
      result = prime * result + cpu;
      result = prime * result + disk;
      result = prime * result + id;
      result = prime * result + memory;
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
      Size other = (Size) obj;
      if (costPerHour == null) {
         if (other.costPerHour != null) {
            return false;
         }
      } else if (!costPerHour.equals(other.costPerHour)) {
         return false;
      }
      if (costPerMonth == null) {
         if (other.costPerMonth != null) {
            return false;
         }
      } else if (!costPerMonth.equals(other.costPerMonth)) {
         return false;
      }
      if (cpu != other.cpu) {
         return false;
      }
      if (disk != other.disk) {
         return false;
      }
      if (id != other.id) {
         return false;
      }
      if (memory != other.memory) {
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
      return "Size [id=" + id + ", name=" + name + ", slug=" + slug + ", memory=" + memory + ", cpu=" + cpu + ", disk="
            + disk + ", costPerHour=" + costPerHour + ", costPerMonth=" + costPerMonth + "]";
   }

}
