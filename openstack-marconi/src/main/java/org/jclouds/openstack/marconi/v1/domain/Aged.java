/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.marconi.v1.domain;

import com.google.common.base.Objects;

import java.util.Date;

/**
 * The age of messages in a queue.
 *
 * @author Everett Toews
 */
public class Aged {

   private int age;
   private Date created;

   protected Aged(int age, Date created) {
      this.age = age;
      this.created = created;
   }

   /**
    * @return Age of the oldest/newest message in seconds.
    */
   public int getAge() {
      return age;
   }

   /**
    * @return Date/Time of the oldest/newest message.
    */
   public Date getCreated() {
      return created;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(age, created);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Aged that = Aged.class.cast(obj);
      return Objects.equal(this.age, that.age) && Objects.equal(this.created, that.created);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("age", age).add("created", created);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromAged(this);
   }

   public static abstract class Builder {
      protected abstract Builder self();

      protected int age;
      protected Date created;

      /**
       * @see Aged#age
       */
      public Builder age(int age) {
         this.age = age;
         return self();
      }

      /**
       * @see Aged#created
       */
      public Builder created(Date created) {
         this.created = created;
         return self();
      }

      public Aged build() {
         return new Aged(age, created);
      }

      public Builder fromAged(Aged in) {
         return this.age(in.getAge()).created(in.getCreated());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
