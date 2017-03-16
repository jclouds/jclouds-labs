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
package org.jclouds.dimensiondata.cloudcontrol.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Date;

@AutoValue
public abstract class Progress {

   @AutoValue
   public abstract static class Step {

      Step() {
      } // For AutoValue only!

      public abstract String name();

      public abstract int number();

      @SerializedNames({ "name", "number" })
      public static Step create(String name, int number) {
         return new AutoValue_Progress_Step(name, number);
      }
   }

   Progress() {
   }

   public abstract String action();

   public abstract String requestTime();

   public abstract String userName();

   @Nullable
   public abstract Integer numberOfSteps();

   @Nullable
   public abstract Date updateTime();

   @Nullable
   public abstract Step step();

   @SerializedNames({ "action", "requestTime", "userName", "numberOfSteps", "updateTime", "step" })
   public static Progress create(String action, String requestTime, String userName, Integer numberOfSteps,
         Date updateTime, Step step) {
      return builder().action(action).requestTime(requestTime).userName(userName).numberOfSteps(numberOfSteps)
            .updateTime(updateTime).step(step).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder action(String action);

      public abstract Builder requestTime(String requestTime);

      public abstract Builder userName(String userName);

      public abstract Builder numberOfSteps(Integer numberOfSteps);

      public abstract Builder updateTime(Date updateTime);

      public abstract Builder step(Step step);

      public abstract Progress build();
   }

   public static Builder builder() {
      return new AutoValue_Progress.Builder();
   }
}
