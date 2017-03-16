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
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

/**
 *
 */
@AutoValue
public abstract class Status {

   public enum ResultType {
      ERROR, SUCCESS, WARNING
   }

   Status() {
   }

   public abstract ResultType result();

   public abstract String resultDetail();

   public abstract String resultCode();

   public abstract String operation();

   @Nullable
   public abstract List<AdditionalInformationType> additionalInformation();

   @SerializedNames({ "result", "resultDetail", "resultCode", "additionalInformation" })
   public static Status create(ResultType result, String resultDetail, String resultCode,
         List<AdditionalInformationType> additionalInformation, String operation) {
      return builder().result(result).resultDetail(resultDetail).resultCode(resultCode)
            .additionalInformation(additionalInformation).operation(operation).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder result(ResultType result);

      public abstract Builder resultDetail(String result);

      public abstract Builder resultCode(String resultCode);

      public abstract Builder additionalInformation(List<AdditionalInformationType> additionalInformation);

      public abstract Builder operation(String operation);

      abstract Status autoBuild();

      abstract List<AdditionalInformationType> additionalInformation();

      public Status build() {
         additionalInformation(additionalInformation() != null ? ImmutableList.copyOf(additionalInformation()) : null);
         return autoBuild();
      }
   }

   public static Builder builder() {
      return new AutoValue_Status.Builder();
   }
}
