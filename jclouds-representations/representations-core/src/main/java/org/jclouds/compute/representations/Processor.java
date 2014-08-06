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
package org.jclouds.compute.representations;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public class Processor implements Serializable {

   private static final long serialVersionUID = -2621055948006358603L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private double cores;
      private double speed;

      public Builder cores(final double cores) {
         this.cores = cores;
         return this;
      }

      public Builder speed(final double speed) {
         this.speed = speed;
         return this;
      }

      public Processor build() {
         return new Processor(cores, speed);
      }

   }

   private final double cores;
   private final double speed;

   public Processor(double cores, double speed) {
      this.cores = cores;
      this.speed = speed;
   }

   public double getCores() {
      return cores;
   }

   public double getSpeed() {
      return speed;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(cores, speed);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("cores", cores).add("spped", speed).toString();
   }
}
