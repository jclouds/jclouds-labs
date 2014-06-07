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

import com.google.common.collect.ImmutableList;

import java.beans.ConstructorProperties;
import java.util.List;

public class Pricing {

   public static class Builder {
      private BurstLevel current;
      private BurstLevel next;
      private List<Price> priceList;

      public Builder current(BurstLevel current) {
         this.current = current;
         return this;
      }

      public Builder next(BurstLevel next) {
         this.next = next;
         return this;
      }

      public Builder priceList(List<Price> priceList) {
         this.priceList = ImmutableList.copyOf(priceList);
         return this;
      }

      public Pricing build() {
         return new Pricing(current, next, priceList);
      }
   }

   private final BurstLevel current;
   private final BurstLevel next;
   private final List<Price> priceList;

   @ConstructorProperties({
         "current", "next", "objects"
   })
   public Pricing(BurstLevel current, BurstLevel next, List<Price> priceList) {
      this.current = current;
      this.next = next;
      this.priceList = priceList;
   }

   public BurstLevel getCurrent() {
      return current;
   }

   public BurstLevel getNext() {
      return next;
   }

   public List<Price> getPriceList() {
      return priceList;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Pricing)) return false;

      Pricing pricing = (Pricing) o;

      if (current != null ? !current.equals(pricing.current) : pricing.current != null) return false;
      if (next != null ? !next.equals(pricing.next) : pricing.next != null) return false;
      if (priceList != null ? !priceList.equals(pricing.priceList) : pricing.priceList != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = current != null ? current.hashCode() : 0;
      result = 31 * result + (next != null ? next.hashCode() : 0);
      result = 31 * result + (priceList != null ? priceList.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "current=" + current +
            ", next=" + next +
            ", priceList=" + priceList +
            "]";
   }
}
