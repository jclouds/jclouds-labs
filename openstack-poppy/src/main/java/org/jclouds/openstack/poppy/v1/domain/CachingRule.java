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

package org.jclouds.openstack.poppy.v1.domain;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CachingRule {
   /**
    * @see Builder#name(String)
    */
   public abstract String getName();

   /**
    * @see Builder#requestURL(String)
    */
   public abstract String getRequestURL();

   @SerializedNames({ "name", "request_url" })
   private static CachingRule create(String name, String requestUrl) {
      return builder().name(name).requestURL(requestUrl).build();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder()
            .name(getName())
            .requestURL(getRequestURL());
   }

   public static final class Builder {
      private String name;
      private String requestURL;
      Builder() {
      }
      Builder(CachingRule source) {
         name(source.getName());
         requestURL(source.getRequestURL());
      }

      /**
       * Required.
       * @param name Specifies the name of this rule. The minimum length for name is 1. The maximum length is 256.
       * @return The CachingRule builder.
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * Required.
       * @param requestURL Specifies the request URL that this rule should match for this TTL to be used.
       *                   The minimum length for request_url is 1. The maximum length is 1024. (Regex is supported.)
       * @return The CachingRule builder.
       */
      public Builder requestURL(String requestURL) {
         this.requestURL = requestURL;
         return this;
      }

      public CachingRule build() {
         String missing = "";
         if (name == null) {
            missing += " name";
         }
         if (requestURL == null) {
            missing += " requestURL";
         }
         if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required properties:" + missing);
         }
         CachingRule result = new AutoValue_CachingRule(
               this.name,
               this.requestURL);
         return result;
      }
   }
}
