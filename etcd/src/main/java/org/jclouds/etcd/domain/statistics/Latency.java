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

package org.jclouds.etcd.domain.statistics;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Latency {

   public abstract double average();

   public abstract double current();

   public abstract double maximum();

   public abstract double minimum();

   public abstract double standardDeviation();

   Latency() {
   }

   @SerializedNames({ "average", "current", "maximum", "minimum", "standardDeviation" })
   public static Latency create(double average, double current, double maximum, double minimum,
         double standardDeviation) {
      return new AutoValue_Latency(average, current, maximum, minimum, standardDeviation);
   }
}
