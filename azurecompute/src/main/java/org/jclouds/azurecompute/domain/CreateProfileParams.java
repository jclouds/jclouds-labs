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
package org.jclouds.azurecompute.domain;

import com.google.auto.value.AutoValue;

/**
 * The Create Profile operation creates a new profile for a domain name, owned by the specified subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758254.aspx">docs</a>
 */
@AutoValue
public abstract class CreateProfileParams {

   CreateProfileParams() {
   } // For AutoValue only!

   /**
    * Specifies the name of the domain that the profile is being created for.
    *
    * A valid DNS name of the form &lt;subdomain name&gt;.trafficmanager.net, conforming to RFC 1123 specification.
    *
    * Total length of the domain name must be less than or equal to 253 characters. The &lt;subdomain name&gt; can
    * contain periods and each label within the subdomain must be less or equal to 63 characters.
    *
    * @return profile domain name.
    */
   public abstract String domain();

   /**
    * Specifies the name of the profile.
    *
    * The name must be composed of letters, numbers, and hyphens. The maximum length of the profile name is 256
    * characters. Hyphens cannot be the first or last character.
    *
    * @return profile name..
    */
   public abstract String name();

   public Builder toBuilder() {
      return builder().fromImageParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String domain;
      private String name;

      public Builder domain(final String domain) {
         this.domain = domain;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public CreateProfileParams build() {
         return CreateProfileParams.create(domain, name);
      }

      public Builder fromImageParams(final CreateProfileParams in) {
         return domain(in.domain()).name(in.name());
      }
   }

   private static CreateProfileParams create(
           final String domain,
           final String name) {
      return new AutoValue_CreateProfileParams(domain, name);
   }
}
