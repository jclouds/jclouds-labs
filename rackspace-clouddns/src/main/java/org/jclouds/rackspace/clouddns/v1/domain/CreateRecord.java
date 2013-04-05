/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rackspace.clouddns.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * @author Everett Toews
 */
public class CreateRecord {
   private final String name;
   private final String type;
   private final Optional<Integer> ttl;
   private final String data;
   private final Optional<Integer> priority;
   private final Optional<String> comment;

   protected CreateRecord(String name, String type, Optional<Integer> ttl, String data, Optional<Integer> priority,
         Optional<String> comment) {
      this.name = checkNotNull(name, "name required");
      this.type = checkNotNull(type, "type required");
      this.ttl = ttl;
      this.data = data;
      this.priority = priority;
      this.comment = comment;
   }

   /**
    * @see Builder#name(String)
    */
   public String getName() {
      return name;
   }

   /**
    * @see Builder#type(String)
    */
   public String getType() {
      return type;
   }

   /**
    * @see Builder#ttl(Integer)
    */
   public Optional<Integer> getTTL() {
      return ttl;
   }

   /**
    * @see Builder#data(String)
    */
   public String getData() {
      return data;
   }

   /**
    * @see Builder#priority(Integer)
    */
   public Optional<Integer> getPriority() {
      return priority;
   }

   /**
    * @see Builder#comment(String)
    */
   public Optional<String> getComment() {
      return comment;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, type, data, ttl.orNull(), priority.orNull(), comment.orNull());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      CreateRecord that = CreateRecord.class.cast(obj);

      return Objects.equal(this.name, that.name) && Objects.equal(this.type, that.type)
            && Objects.equal(this.data, that.data) && Objects.equal(this.ttl.orNull(), that.ttl.orNull())
            && Objects.equal(this.priority.orNull(), that.priority.orNull())
            && Objects.equal(this.comment.orNull(), that.comment.orNull());
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("type", type).add("ttl", ttl.orNull())
            .add("data", data).add("priority", priority.orNull()).add("comment", comment.orNull());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static class Builder {
      private String name;
      private String type;
      private Optional<Integer> ttl = Optional.absent();
      private String data;
      private Optional<Integer> priority = Optional.absent();
      private Optional<String> comment = Optional.absent();

      /**
       * The name for the domain or subdomain. Must be a fully qualified domain name (FQDN) that doesn't end in a '.'.
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * The record type to add.
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * The duration in seconds that the record may be cached by clients. If specified, must be greater than 300. The
       * default value, if not specified, is 3600.
       */
      public Builder ttl(Integer ttl) {
         this.ttl = Optional.fromNullable(ttl);
         return this;
      }

      /**
       * The data field for PTR, A, and AAAA records must be a valid IPv4 or IPv6 IP address.
       */
      public Builder data(String data) {
         this.data = data;
         return this;
      }

      /**
       * Required for MX and SRV records, but forbidden for other record types. If specified, must be an integer from 0
       * to 65535.
       */
      public Builder priority(Integer priority) {
         this.priority = Optional.fromNullable(priority);
         return this;
      }

      /**
       * If included, its length must be less than or equal to 160 characters.
       */
      public Builder comment(String comment) {
         this.comment = Optional.fromNullable(comment);
         return this;
      }

      public CreateRecord build() {
         return new CreateRecord(name, type, ttl, data, priority, comment);
      }

      public Builder from(CreateRecord in) {
         return this.name(in.getName()).type(in.getType()).ttl(in.getTTL().orNull()).data(in.getData())
               .priority(in.getPriority().orNull()).comment(in.getComment().orNull());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
