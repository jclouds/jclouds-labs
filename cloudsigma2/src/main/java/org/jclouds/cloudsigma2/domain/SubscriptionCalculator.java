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
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SubscriptionCalculator {
   private final double price;
   @Named("objects")
   private final List<CalcSubscription> subscriptions;

   @ConstructorProperties({"price", "objects"})
   public SubscriptionCalculator(double price, List<CalcSubscription> subscriptions) {
      this.price = checkNotNull(price, "price");
      this.subscriptions = checkNotNull(subscriptions, "subscriptions");
   }

   /**
    * @return total price for all requested subscriptions
    */
   public double getPrice() {
      return price;
   }

   /**
    * @return list of all subscriptions requested for calculation
    */
   public List<CalcSubscription> getSubscriptions() {
      return subscriptions;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof SubscriptionCalculator)) return false;

      SubscriptionCalculator that = (SubscriptionCalculator) o;

      if (Double.compare(that.price, price) != 0) return false;
      if (subscriptions != null ? !subscriptions.equals(that.subscriptions) : that.subscriptions != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      temp = Double.doubleToLongBits(price);
      result = (int) (temp ^ (temp >>> 32));
      result = 31 * result + (subscriptions != null ? subscriptions.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "SubscriptionCalculator{" +
            "price=" + price +
            ", subscriptions=" + subscriptions +
            "}";
   }
}
