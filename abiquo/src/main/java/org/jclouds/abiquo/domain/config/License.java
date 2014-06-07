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
package org.jclouds.abiquo.domain.config;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;

import org.jclouds.rest.ApiContext;

import com.abiquo.server.core.config.LicenseDto;

/**
 * Adds high level functionality to {@link LicenseDto}.
 */
public class License extends DomainWrapper<LicenseDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected License(final ApiContext<AbiquoApi> context, final LicenseDto target) {
      super(context, target);
   }

   // Domain operations

   public void remove() {
      context.getApi().getConfigApi().removeLicense(target);
      target = null;
   }

   public void add() {
      target = context.getApi().getConfigApi().addLicense(target);
   }

   // Builder

   public static Builder builder(final ApiContext<AbiquoApi> context, final String code) {
      return new Builder(context, code);
   }

   public static class Builder {
      private ApiContext<AbiquoApi> context;

      private String code;

      public Builder(final ApiContext<AbiquoApi> context, final String code) {
         super();
         this.context = context;
         this.code = code;
      }

      public Builder code(final String code) {
         this.code = code;
         return this;
      }

      public License build() {
         LicenseDto dto = new LicenseDto();
         dto.setCode(code);

         License license = new License(context, dto);
         return license;
      }

      public static Builder fromLicense(final License in) {
         return License.builder(in.context, in.getCode());
      }
   }

   // Delegate methods

   public String getCode() {
      return target.getCode();
   }

   public String getExpiration() {
      return target.getExpiration();
   }

   public Integer getId() {
      return target.getId();
   }

   public Integer getNumCores() {
      return target.getNumcores();
   }

   @Override
   public String toString() {
      return "License [id=" + getId() + ", code=" + getCode() + ", expiration=" + getExpiration()
          + ", numCores=" + getNumCores() + "]";
   }

}
