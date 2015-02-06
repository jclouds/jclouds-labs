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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Representation of an OpenStack Poppy Domain.
 */
@AutoValue
public abstract class Domain {
   /**
    * @see Builder#domain(String)
    */
   public abstract String getDomain();

   /**
    * @see Builder#protocol(Protocol)
    */
   @Nullable public abstract Protocol getProtocol();

   @SerializedNames({ "domain", "protocol" })
   private static Domain create(String domain, Protocol protocol) {
      return builder().domain(domain).protocol(protocol).build();
   }

   public static Builder builder() {
      return new AutoValue_Domain.Builder().protocol(null);
   }
   public Builder toBuilder() {
      return builder()
            .domain(getDomain())
            .protocol(getProtocol());
   }

   public static final class Builder {
      private String domain;
      private Protocol protocol;
      Builder() {
      }
      Builder(Domain source) {
         domain(source.getDomain());
         protocol(source.getProtocol());
      }

      /**
       * Required.
       * @param domain Specifies the domain used to access the assets on their website, for which a CNAME is given to
       *             the CDN provider.
       * @return The Domain builder.
       */
      public Domain.Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      /**
       * Optional.
       * @param protocol Specifies the protocol used to access the assets on this domain. Only http or https are
       *                 currently allowed. protocol defaults to http.
       * @return The Domain builder.
       */
      public Domain.Builder protocol(Protocol protocol) {
         this.protocol = protocol;
         return this;
      }

      public Domain build() {
         String missing = "";
         if (domain == null) {
            missing += " domain";
         }
         if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required properties:" + missing);
         }
         Domain result = new AutoValue_Domain(
               this.domain,
               this.protocol);
         return result;
      }
   }
}
