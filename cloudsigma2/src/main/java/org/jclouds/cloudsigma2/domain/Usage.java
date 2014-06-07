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

public class Usage {

   public static class Builder {
      private BigInteger burst;
      private BigInteger subscribed;
      private BigInteger using;

      public Builder subscribed(BigInteger subscribed) {
         this.subscribed = subscribed;
         return this;
      }

      public Builder burst(BigInteger burst) {
         this.burst = burst;
         return this;
      }

      public Builder using(BigInteger using) {
         this.using = using;
         return this;
      }

      public Usage build() {
         return new Usage(burst, subscribed, using);
      }
   }

   private final BigInteger burst;
   private final BigInteger subscribed;
   private final BigInteger using;

   @ConstructorProperties({
         "burst", "subscribed", "using"
   })
   public Usage(BigInteger burst, BigInteger subscribed, BigInteger using) {
      this.burst = burst;
      this.subscribed = subscribed;
      this.using = using;
   }

   public BigInteger getUsing() {
      return using;
   }

   public BigInteger getBurst() {
      return burst;
   }

   public BigInteger getSubscribed() {
      return subscribed;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Usage)) return false;

      Usage usage = (Usage) o;

      if (burst != null ? !burst.equals(usage.burst) : usage.burst != null) return false;
      if (subscribed != null ? !subscribed.equals(usage.subscribed) : usage.subscribed != null) return false;
      if (using != null ? !using.equals(usage.using) : usage.using != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = burst != null ? burst.hashCode() : 0;
      result = 31 * result + (subscribed != null ? subscribed.hashCode() : 0);
      result = 31 * result + (using != null ? using.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "burst=" + burst +
            ", subscribed=" + subscribed +
            ", using=" + using +
            "]";
   }
}
