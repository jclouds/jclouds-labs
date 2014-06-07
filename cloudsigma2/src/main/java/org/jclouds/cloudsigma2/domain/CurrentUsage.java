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

public class CurrentUsage {

   public static class Builder {
      private AccountBalance balance;
      private AccountUsage usage;

      public Builder balance(AccountBalance balance) {
         this.balance = balance;
         return this;
      }

      public Builder usage(AccountUsage usage) {
         this.usage = usage;
         return this;
      }

      public CurrentUsage build() {
         return new CurrentUsage(balance, usage);
      }
   }

   private final AccountBalance balance;
   private final AccountUsage usage;

   @ConstructorProperties({
         "balance", "usage"
   })
   public CurrentUsage(AccountBalance balance, AccountUsage usage) {
      this.balance = balance;
      this.usage = usage;
   }

   public AccountBalance getBalance() {
      return balance;
   }

   public AccountUsage getUsage() {
      return usage;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CurrentUsage)) return false;

      CurrentUsage that = (CurrentUsage) o;

      if (balance != null ? !balance.equals(that.balance) : that.balance != null) return false;
      if (usage != null ? !usage.equals(that.usage) : that.usage != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = balance != null ? balance.hashCode() : 0;
      result = 31 * result + (usage != null ? usage.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "balance=" + balance +
            ", usage=" + usage +
            "]";
   }
}
