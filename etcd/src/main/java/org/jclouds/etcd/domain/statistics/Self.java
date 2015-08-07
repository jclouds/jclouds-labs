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
public abstract class Self {

   public abstract String id();

   public abstract LeaderInfo leaderInfo();

   public abstract String name();

   public abstract double recvAppendRequestCnt();

   public abstract double sendAppendRequestCnt();

   public abstract double sendBandwidthRate();

   public abstract double sendPkgRate();

   public abstract String startTime();

   public abstract String state();

   Self() {
   }

   @SerializedNames({ "id", "leaderInfo", "name", "recvAppendRequestCnt", "sendAppendRequestCnt", "sendBandwidthRate",
         "sendPkgRate", "startTime", "state" })
   private static Self create(String id, LeaderInfo leaderInfo, String name, double recvAppendRequestCnt,
         double sendAppendRequestCnt, double sendBandwidthRate, double sendPkgRate, String startTime, String state) {
      return new AutoValue_Self(id, leaderInfo, name, recvAppendRequestCnt, sendAppendRequestCnt, sendBandwidthRate,
            sendPkgRate, startTime, state);
   }
}
