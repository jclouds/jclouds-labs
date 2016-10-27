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
package org.apache.jclouds.profitbricks.rest.domain;

import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class RequestStatus {

   public static enum Status {
      DONE, FAILED, RUNNING, UNRECOGNIZED;

      public static Status fromValue(String value) {
         return Enums.getIfPresent(Status.class, value).or(UNRECOGNIZED);
      }
   }
   
   public abstract String id();
   public abstract String type();
   public abstract String href();
   public abstract Metadata metadata();
   
   RequestStatus() { }
   
   @SerializedNames({"id", "type", "href", "metadata"})
   public static RequestStatus create(String id, String type, String href, Metadata metadata) {
      return new AutoValue_RequestStatus(id, type, href, metadata);
   }
   
   @AutoValue
   public abstract static class Metadata {
      public abstract Status status();
      public abstract String message();
      public abstract String etag();
      public abstract List<TargetEntity> targets();
      
      Metadata() { }
      
      @SerializedNames({"status", "message", "etag", "targets"})
      public static Metadata create(Status status, String message, String etag, List<TargetEntity> targets) {
         return new AutoValue_RequestStatus_Metadata(status, message, etag, ImmutableList.copyOf(targets));
      }
      
      @AutoValue
      public abstract static class TargetEntity {
         public abstract Status status();
         public abstract Target target();
         
         TargetEntity() { }
         
         @SerializedNames({"status", "target"})
         public static TargetEntity create(Status status, Target target) {
            return new AutoValue_RequestStatus_Metadata_TargetEntity(status, target);
         }
         
         @AutoValue
         public abstract static class Target {
            public abstract String id();
            public abstract String type();
            public abstract String href();
            
            Target() { }
            
            @SerializedNames({"id", "type", "href"})
            public static Target create(String id, String type, String href) {
               return new AutoValue_RequestStatus_Metadata_TargetEntity_Target(id, type, href);
            }
         }
      }
   }
   
}
