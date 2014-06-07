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

public class AccountBalance {

   private final double balance;
   private final String currency;

   @ConstructorProperties({
         "balance", "currency"
   })
   public AccountBalance(double balance, String currency) {
      this.balance = balance;
      this.currency = currency;
   }

   /**
    * @return Amount of money in account
    */
   public double getBalance() {
      return balance;
   }

   /**
    * @return Currency of the account
    */
   public String getCurrency() {
      return currency;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof AccountBalance)) return false;

      AccountBalance that = (AccountBalance) o;

      if (Double.compare(that.balance, balance) != 0) return false;
      if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      temp = Double.doubleToLongBits(balance);
      result = (int) (temp ^ (temp >>> 32));
      result = 31 * result + (currency != null ? currency.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "balance=" + balance +
            ", currency='" + currency + '\'' +
            "]";
   }
}
