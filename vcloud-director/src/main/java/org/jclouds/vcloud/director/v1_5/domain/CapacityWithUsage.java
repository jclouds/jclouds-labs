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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Represents a capacity and usage of a given resource.
 */
@XmlType(name = "CapacityWithUsage", propOrder = {
      "used",
      "overhead"
})
public class CapacityWithUsage extends Capacity<CapacityWithUsage> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromCapacityWithUsage(this);
   }

   public static class Builder extends Capacity.Builder<CapacityWithUsage> {

      private Long used;
      private Long overhead;

      /**
       * @see CapacityWithUsage#getUsed()
       */
      public Builder used(Long used) {
         this.used = used;
         return this;
      }

      /**
       * @see CapacityWithUsage#getOverhead()
       */
      public Builder overhead(Long overhead) {
         this.overhead = overhead;
         return this;
      }


      @Override
      public CapacityWithUsage build() {
         return new CapacityWithUsage(units, allocated, limit, used, overhead);
      }

      /**
       * @see Capacity#getUnits()
       */
      @Override
      public Builder units(String units) {
         this.units = units;
         return this;
      }

      /**
       * @see Capacity#getAllocated()
       */
      @Override
      public Builder allocated(Long allocated) {
         this.allocated = allocated;
         return this;
      }

      /**
       * @see Capacity#getLimit()
       */
      @Override
      public Builder limit(Long limit) {
         this.limit = limit;
         return this;
      }


      @Override
      public Builder fromCapacityType(Capacity<CapacityWithUsage> in) {
         return Builder.class.cast(super.fromCapacityType(in));
      }

      public Builder fromCapacityWithUsage(CapacityWithUsage in) {
         return fromCapacityType(in)
               .used(in.getUsed())
               .overhead(in.getOverhead());
      }
   }

   private CapacityWithUsage(String units, Long allocated, Long limit, Long used, Long overhead) {
      super(units, allocated, limit);
      this.used = used;
      this.overhead = overhead;
   }

   private CapacityWithUsage() {
      // for JAXB
   }

   @XmlElement(name = "Used")
   protected Long used;
   @XmlElement(name = "Overhead")
   protected Long overhead;

   /**
    * Gets the value of the used property.
    *
    * @return possible object is
    *         {@link Long }
    */
   public Long getUsed() {
      return used;
   }

   /**
    * Gets the value of the overhead property.
    *
    * @return possible object is
    *         {@link Long }
    */
   public Long getOverhead() {
      return overhead;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CapacityWithUsage that = CapacityWithUsage.class.cast(o);
      return equal(used, that.used) &&
            equal(overhead, that.overhead);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(used,
            overhead);
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper("")
            .add("used", used)
            .add("overhead", overhead).toString();
   }

}
