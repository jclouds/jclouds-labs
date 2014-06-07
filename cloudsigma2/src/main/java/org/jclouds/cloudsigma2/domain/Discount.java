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

public class Discount {

   private final String period;
   private final Double value;

   @ConstructorProperties({
         "period", "value"
   })
   public Discount(String period, Double value) {
      this.period = period;
      this.value = value;
   }

   /**
    * @return The minimum period for this discount
    */
   public String getPeriod() {
      return period;
   }

   /**
    * @return The value of the discount
    */
   public Double getValue() {
      return value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Discount)) return false;

      Discount discount = (Discount) o;

      if (period != null ? !period.equals(discount.period) : discount.period != null) return false;
      if (value != null ? !value.equals(discount.value) : discount.value != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = period != null ? period.hashCode() : 0;
      result = 31 * result + (value != null ? value.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "period='" + period + '\'' +
            ", value=" + value +
            "]";
   }
}
