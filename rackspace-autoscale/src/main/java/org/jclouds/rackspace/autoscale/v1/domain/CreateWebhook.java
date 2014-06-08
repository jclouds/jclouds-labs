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
package org.jclouds.rackspace.autoscale.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

/**
 * An Autoscale Webhook for a specific group and policy.
 * 
 * @see Group
 * @see CreateScalingPolicy
 */
public class CreateWebhook {
   private final String name;
   private final ImmutableMap<String, Object> metadata;

   @ConstructorProperties({ "name", "metadata" })
   protected CreateWebhook(String name, @Nullable Map<String, Object> metadata) {
      this.name = checkNotNull(name, "name should not be null");
      if (metadata == null) {
         this.metadata = ImmutableMap.of();
      } else {
         this.metadata = ImmutableMap.copyOf(metadata);
      }
   }

   /**
    * @return the name of this Webhook.
    * @see CreateWebhook.Builder#name(String)
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the metadata for this Webhook.
    * @see CreateWebhook.Builder#metadata(Map)
    */
   public ImmutableMap<String, Object> getMetadata() {
      return this.metadata;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, metadata);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      CreateWebhook that = CreateWebhook.class.cast(obj);
      return Objects.equal(this.name, that.name) && Objects.equal(this.metadata, that.metadata);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("name", name).add("metadata", metadata);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromWebhook(this);
   }

   public static class Builder {
      protected String name;
      protected Map<String, Object> metadata;

      /**
       * @param name The name of this Webhook.
       * @return The builder object.
       * @see CreateWebhook#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @param metadata The metadata of this Webhook.
       * @return The builder object.
       * @see CreateWebhook#getMetadata()
       */
      public Builder metadata(Map<String, Object> metadata) {
         this.metadata = metadata;
         return this;
      }

      /**
       * @return A new Webhook object.
       */
      public CreateWebhook build() {
         return new CreateWebhook(name, metadata);
      }

      public Builder fromWebhook(CreateWebhook in) {
         return this.name(in.getName()).metadata(in.getMetadata());
      }
   }
}
