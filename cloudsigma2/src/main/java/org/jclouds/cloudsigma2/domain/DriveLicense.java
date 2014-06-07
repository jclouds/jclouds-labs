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
package org.jclouds.cloudsigma2.domain;

import java.beans.ConstructorProperties;

public class DriveLicense {

   public static class Builder {
      private int amount;
      private License license;
      private Owner user;

      public Builder amount(int amount) {
         this.amount = amount;
         return this;
      }

      public Builder license(License license) {
         this.license = license;
         return this;
      }

      public Builder user(Owner user) {
         this.user = user;
         return this;
      }

      public DriveLicense build() {
         return new DriveLicense(amount, license, user);
      }
   }

   private final int amount;
   private final License license;
   private final Owner user;

   @ConstructorProperties({
         "amount", "license", "user"
   })
   public DriveLicense(int amount, License license, Owner user) {
      this.amount = amount;
      this.license = license;
      this.user = user;
   }

   public int getAmount() {
      return amount;
   }

   public License getLicense() {
      return license;
   }

   public Owner getUser() {
      return user;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof DriveLicense)) return false;

      DriveLicense that = (DriveLicense) o;

      if (amount != that.amount) return false;
      if (license != null ? !license.equals(that.license) : that.license != null) return false;
      if (user != null ? !user.equals(that.user) : that.user != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = amount;
      result = 31 * result + (license != null ? license.hashCode() : 0);
      result = 31 * result + (user != null ? user.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "amount=" + amount +
            ", license='" + license + '\'' +
            ", user=" + user +
            "]";
   }
}
