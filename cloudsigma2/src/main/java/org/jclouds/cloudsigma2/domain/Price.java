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
import java.math.BigInteger;

public class Price {

   public static class Builder {
      private String currency;
      private String id;
      private Integer level;
      private BigInteger multiplier;
      private double price;
      private SubscriptionResource resource;
      private String unit;

      /**
       * @param currency The currency of the price
       *                 Price Builder
       */
      public Builder currency(String currency) {
         this.currency = currency;
         return this;
      }

      /**
       * @param id Unique identificator
       *           Price Builder
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @param level The burst level the price applies to
       *              Price Builder
       */
      public Builder level(int level) {
         this.level = level;
         return this;
      }

      /**
       * @param multiplier The multiplier applied to get the price of one unit per second, from the unit of the price
       *                   Price Builder
       */
      public Builder multiplier(BigInteger multiplier) {
         this.multiplier = multiplier;
         return this;
      }

      /**
       * @param price Price
       *              Price Builder
       */
      public Builder price(double price) {
         this.price = price;
         return this;
      }

      /**
       * @param resource The resource the price applies to
       *                 Price Builder
       */
      public Builder resource(SubscriptionResource resource) {
         this.resource = resource;
         return this;
      }

      /**
       * @param unit The unit of the price
       *             Price Builder
       */
      public Builder unit(String unit) {
         this.unit = unit;
         return this;
      }

      public Price build() {
         return new Price(currency, id, level, multiplier, price, resource, unit);
      }
   }

   private final String currency;
   private final String id;
   private final Integer level;
   private final BigInteger multiplier;
   private final double price;
   private final SubscriptionResource resource;
   private final String unit;

   @ConstructorProperties({
         "currency", "id", "level", "multiplier", "price", "resource", "unit"
   })
   public Price(String currency, String id, Integer level, BigInteger multiplier, double price,
                SubscriptionResource resource, String unit) {
      this.currency = currency;
      this.id = id;
      this.level = level;
      this.multiplier = multiplier;
      this.price = price;
      this.resource = resource;
      this.unit = unit;
   }

   /**
    * @return The currency of the price
    */
   public String getCurrency() {
      return currency;
   }

   /**
    * @return Unique identificator
    */
   public String getId() {
      return id;
   }

   /**
    * @return The burst level the price applies to
    */
   public Integer getLevel() {
      return level;
   }

   /**
    * @return The multiplier applied to get the price of one unit per second, from the unit of the price
    */
   public BigInteger getMultiplier() {
      return multiplier;
   }

   /**
    * @return Price
    */
   public double getPrice() {
      return price;
   }

   /**
    * @return The resource the price applies to
    */
   public SubscriptionResource getResource() {
      return resource;
   }

   /**
    * @return The unit of the price
    */
   public String getUnit() {
      return unit;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Price)) return false;

      Price price1 = (Price) o;

      if (Double.compare(price1.price, price) != 0) return false;
      if (currency != null ? !currency.equals(price1.currency) : price1.currency != null) return false;
      if (id != null ? !id.equals(price1.id) : price1.id != null) return false;
      if (level != null ? !level.equals(price1.level) : price1.level != null) return false;
      if (multiplier != null ? !multiplier.equals(price1.multiplier) : price1.multiplier != null) return false;
      if (resource != price1.resource) return false;
      if (unit != null ? !unit.equals(price1.unit) : price1.unit != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      result = currency != null ? currency.hashCode() : 0;
      result = 31 * result + (id != null ? id.hashCode() : 0);
      result = 31 * result + (level != null ? level.hashCode() : 0);
      result = 31 * result + (multiplier != null ? multiplier.hashCode() : 0);
      temp = Double.doubleToLongBits(price);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      result = 31 * result + (resource != null ? resource.hashCode() : 0);
      result = 31 * result + (unit != null ? unit.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "currency='" + currency + '\'' +
            ", id='" + id + '\'' +
            ", level=" + level +
            ", multiplier=" + multiplier +
            ", price='" + price + '\'' +
            ", resource=" + resource +
            ", unit='" + unit + '\'' +
            "]";
   }
}
