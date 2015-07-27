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
public abstract class Store {

   public abstract int compareAndSwapFail();

   public abstract int compareAndSwapSuccess();

   public abstract int createFail();

   public abstract int createSuccess();

   public abstract int deleteFail();

   public abstract int deleteSuccess();

   public abstract int expireCount();

   public abstract int getsFail();

   public abstract int getsSuccess();

   public abstract int setsFail();

   public abstract int setsSuccess();

   public abstract int updateFail();

   public abstract int updateSuccess();

   public abstract int watchers();

   Store() {
   }

   @SerializedNames({ "compareAndSwapFail", "compareAndSwapSuccess", "createFail", "createSuccess", "deleteFail",
         "deleteSuccess", "expireCount", "getsFail", "getsSuccess", "setsFail", "setsSuccess", "updateFail",
         "updateSuccess", "watchers" })
   public static Store create(int compareAndSwapFail, int compareAndSwapSuccess, int createFail, int createSuccess,
         int deleteFail, int deleteSuccess, int expireCount, int getsFail, int getsSuccess, int setsFail,
         int setsSuccess, int updateFail, int updateSuccess, int watchers) {
      return new AutoValue_Store(compareAndSwapFail, compareAndSwapSuccess, createFail, createSuccess, deleteFail,
            deleteSuccess, expireCount, getsFail, getsSuccess, setsFail, setsSuccess, updateFail, updateSuccess,
            watchers);
   }
}
