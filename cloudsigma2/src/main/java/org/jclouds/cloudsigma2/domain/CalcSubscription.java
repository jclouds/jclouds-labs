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

import org.jclouds.javax.annotation.Nullable;

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class CalcSubscription {

   public static class Builder {
      private Double amount;
      private Double discountAmount;
      private Double discountPercent;
      private String period;
      private Double price;
      private SubscriptionResource resource;
      private Date startTime;
      private Date endTime;

      /**
       * @param amount
       * @return CalcSubscription Builder
       */
      public Builder amount(double amount) {
         this.amount = amount;
         return this;
      }

      /**
       * @param discountAmount Amount of discount
       * @return CalcSubscription Builder
       */
      public Builder discountAmount(double discountAmount) {
         this.discountAmount = discountAmount;
         return this;
      }

      /**
       * @param discountPercent Percent of discount
       * @return CalcSubscription Builder
       */
      public Builder discountPercent(double discountPercent) {
         this.discountPercent = discountPercent;
         return this;
      }

      /**
       * @param period Duration of the subscription
       * @return CalcSubscription Builder
       */
      public Builder period(String period) {
         this.period = period;
         return this;
      }

      /**
       * @param price Subscription price
       * @return CalcSubscription Builder
       */
      public Builder price(double price) {
         this.price = price;
         return this;
      }

      /**
       * @param resource Name of resource associated with the subscription
       * @return CalcSubscription Builder
       */
      public Builder resource(SubscriptionResource resource) {
         this.resource = resource;
         return this;
      }

      /**
       * @param startTime Start time of subscription
       * @return CalcSubscription Builder
       */
      public Builder startTime(Date startTime) {
         this.startTime = startTime;
         return this;
      }

      /**
       * @param endTime End time of subscription
       * @return CalcSubscription Builder
       */
      public Builder endTime(Date endTime) {
         this.endTime = endTime;
         return this;
      }

      public CalcSubscription build() {
         return new CalcSubscription(amount, discountAmount, discountPercent, period, price, resource,
               startTime, endTime);
      }
   }

   private final Double amount;
   @Named("discount_amount")
   private final Double discountAmount;
   @Named("discount_percent")
   private final Double discountPercent;
   private final String period;
   private final Double price;
   private final SubscriptionResource resource;
   @Named("start_time")
   private final Date startTime;
   @Named("end_time")
   private final Date endTime;

   @ConstructorProperties({
         "amount", "discount_amount", "discount_percent", "period", "price", "resource", "start_time", "end_time"
   })
   public CalcSubscription(@Nullable Double amount, @Nullable Double discountAmount, @Nullable Double discountPercent,
                           @Nullable String period, @Nullable Double price, SubscriptionResource resource,
                           @Nullable Date startTime, @Nullable Date endTime) {
      checkArgument(!(endTime == null && period == null),
            "Subscription period should be configured with endTime or period");
      this.amount = amount;
      this.discountAmount = discountAmount;
      this.discountPercent = discountPercent;
      this.period = period;
      this.price = price;
      this.resource = checkNotNull(resource, "resource");
      this.startTime = startTime;
      this.endTime = endTime;
   }

   /**
    * @return Subscription amount
    */
   public double getAmount() {
      return amount;
   }
   /**
    * @return Amount of discount
    */
   public double getDiscountAmount() {
      return discountAmount;
   }

   /**
    * @return Percent of discount
    */
   public double getDiscountPercent() {
      return discountPercent;
   }

   /**
    * @return Duration of the subscription
    */
   public String getPeriod() {
      return period;
   }

   /**
    * @return Subscription price
    */
   public double getPrice() {
      return price;
   }
   /**
    * @return Name of resource associated with the subscription
    */
   public SubscriptionResource getResource() {
      return resource;
   }

   /**
    * @return Start time of subscription
    */
   public Date getStartTime() {
      return startTime;
   }

   /**
    * @return End time of subscription
    */
   public Date getEndTime() {
      return endTime;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CalcSubscription)) return false;

      CalcSubscription that = (CalcSubscription) o;

      if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
      if (discountAmount != null ? !discountAmount.equals(that.discountAmount) : that.discountAmount != null)
          return false;
      if (discountPercent != null ? !discountPercent.equals(that.discountPercent) : that.discountPercent != null)
          return false;
      if (price != null ? !price.equals(that.price) : that.price != null) return false;
      if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
      if (period != null ? !period.equals(that.period) : that.period != null) return false;
      if (resource != that.resource) return false;
      if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      temp = Double.doubleToLongBits(amount);
      result = (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(discountAmount);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(discountPercent);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
      result = 31 * result + (period != null ? period.hashCode() : 0);
      temp = Double.doubleToLongBits(price);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      result = 31 * result + (resource != null ? resource.hashCode() : 0);
      result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "CalcSubscription{" +
            "amount='" + amount + '\'' +
            ", discountAmount=" + discountAmount +
            ", discountPercent=" + discountPercent +
            ", endTime=" + endTime +
            ", period='" + period + '\'' +
            ", price=" + price +
            ", resource=" + resource +
            ", startTime=" + startTime +
            "}";
   }
}
