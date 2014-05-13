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

package org.jclouds.openstack.marconi.v1.domain;

import com.google.common.base.Objects;

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The age of messages in a queue.
 *
 * @author Everett Toews
 */
public class Aged {

   private final int age;
   private final Date created;
   @Named("href")
   private final String id;

   @ConstructorProperties({
         "age", "created", "href"
   })
   protected Aged(int age, Date created, String id) {
      this.age = age;
      this.created = checkNotNull(created, "created required");
      this.id = checkNotNull(id, "id required");
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

   /**
    * @return Id of the oldest/newest message.
    */
   public String getId() {
      return id;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(age, created, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Aged that = Aged.class.cast(obj);
      return Objects.equal(this.age, that.age) && Objects.equal(this.created, that.created)
            && Objects.equal(this.id, that.id);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("age", age).add("created", created).add("id", id);
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

   public abstract static class Builder {
      protected abstract Builder self();

      protected int age;
      protected Date created;
      protected String id;

      /**
       * @see Aged#getAge()
       */
      public Builder age(int age) {
         this.age = age;
         return self();
      }

      /**
       * @see Aged#getCreated()
       */
      public Builder created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see Aged#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return self();
      }

      public Aged build() {
         return new Aged(age, created, id);
      }

      public Builder fromAged(Aged in) {
         return this.age(in.getAge()).created(in.getCreated()).id(in.getId());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
