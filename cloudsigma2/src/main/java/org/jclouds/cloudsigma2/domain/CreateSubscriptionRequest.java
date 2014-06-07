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

public class CreateSubscriptionRequest {

   public static class Builder {
      private String amount;
      private String period;
      private SubscriptionResource resource;

      /**
       * @param amount Subscription amount
       *               CreateSubscriptionRequest Builder
       */
      public Builder amount(String amount) {
         this.amount = amount;
         return this;
      }

      /**
       * @param period Duration of the subscription
       *               CreateSubscriptionRequest Builder
       */
      public Builder period(String period) {
         this.period = period;
         return this;
      }

      /**
       * @param resource Name of resource associated with the subscription
       *                 CreateSubscriptionRequest Builder
       */
      public Builder resource(SubscriptionResource resource) {
         this.resource = resource;
         return this;
      }

      public CreateSubscriptionRequest build() {
         return new CreateSubscriptionRequest(amount, period, resource);
      }
   }

   private final String amount;
   private final String period;
   private final SubscriptionResource resource;

   public CreateSubscriptionRequest(String amount, String period, SubscriptionResource resource) {
      this.amount = amount;
      this.period = period;
      this.resource = resource;
   }

   /**
    * @return Subscription amount
    */
   public String getAmount() {
      return amount;
   }

   /**
    * @return Duration of the subscription
    */
   public String getPeriod() {
      return period;
   }

   /**
    * @return Name of resource associated with the subscription
    */
   public SubscriptionResource getResource() {
      return resource;
   }

   @Override
   public String toString() {
      return "[" +
            "amount='" + amount + '\'' +
            ", period='" + period + '\'' +
            ", resource=" + resource +
            "]";
   }
}
