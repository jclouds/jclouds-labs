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

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.util.Date;

public class Transaction {

   public static class Builder {
      private double amount;
      private long billingCycle;
      private double end;
      private String id;
      private double initial;
      private String reason;
      private Date time;

      public Builder amount(double amount) {
         this.amount = amount;
         return this;
      }

      public Builder billingCycle(long billingCycle) {
         this.billingCycle = billingCycle;
         return this;
      }

      public Builder end(double end) {
         this.end = end;
         return this;
      }

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder initial(double initial) {
         this.initial = initial;
         return this;
      }

      public Builder reason(String reason) {
         this.reason = reason;
         return this;
      }

      public Builder time(Date time) {
         this.time = time;
         return this;
      }

      public Transaction build() {
         return new Transaction(amount, billingCycle, end, id, initial, reason, time);
      }
   }

   private final double amount;
   @Named("billing_cycle")
   private final long billingCycle;
   private final double end;
   private final String id;
   private final double initial;
   private final String reason;
   private final Date time;

   @ConstructorProperties({
         "amount", "billing_cycle", "end", "id", "initial", "reason", "time"
   })
   public Transaction(double amount, long billingCycle, double end, String id, double initial, String reason,
                      Date time) {
      this.amount = amount;
      this.billingCycle = billingCycle;
      this.end = end;
      this.id = id;
      this.initial = initial;
      this.reason = reason;
      this.time = time;
   }

   /**
    * @return Amount of the operation, positive for debits, negative for credits
    */
   public double getAmount() {
      return amount;
   }

   /**
    * @return Billing cycle that generated this charge
    */
   public long getBillingCycle() {
      return billingCycle;
   }

   /**
    * @return Amount of money after the operation
    */
   public double getEnd() {
      return end;
   }

   /**
    * @return Unique id
    */
   public String getId() {
      return id;
   }

   /**
    * @return Amount of money before the operation
    */
   public double getInitial() {
      return initial;
   }

   /**
    * @return Description of the operation
    */
   public String getReason() {
      return reason;
   }

   /**
    * @return date & time
    */
   public Date getTime() {
      return time;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Transaction)) return false;

      Transaction that = (Transaction) o;

      if (Double.compare(that.amount, amount) != 0) return false;
      if (billingCycle != that.billingCycle) return false;
      if (Double.compare(that.end, end) != 0) return false;
      if (Double.compare(that.initial, initial) != 0) return false;
      if (id != null ? !id.equals(that.id) : that.id != null) return false;
      if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
      if (time != null ? !time.equals(that.time) : that.time != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      temp = Double.doubleToLongBits(amount);
      result = (int) (temp ^ (temp >>> 32));
      result = 31 * result + (int) (billingCycle ^ (billingCycle >>> 32));
      temp = Double.doubleToLongBits(end);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      result = 31 * result + (id != null ? id.hashCode() : 0);
      temp = Double.doubleToLongBits(initial);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      result = 31 * result + (reason != null ? reason.hashCode() : 0);
      result = 31 * result + (time != null ? time.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "amount=" + amount +
            ", billingCycle=" + billingCycle +
            ", end=" + end +
            ", id='" + id + '\'' +
            ", initial=" + initial +
            ", reason='" + reason + '\'' +
            ", time=" + time +
            "]";
   }
}
