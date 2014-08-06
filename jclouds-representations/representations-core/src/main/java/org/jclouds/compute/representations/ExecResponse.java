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

public class ExecResponse implements Serializable {

   private static final long serialVersionUID = -3552310550261335525L;

   private final java.lang.String output;
   private final java.lang.String error;
   private final int exitStatus;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private java.lang.String output;
      private java.lang.String error;
      private int exitStatus;

      public Builder output(final String output) {
         this.output = output;
         return this;
      }

      public Builder error(final String error) {
         this.error = error;
         return this;
      }

      public Builder exitStatus(final int exitStatus) {
         this.exitStatus = exitStatus;
         return this;
      }

      public ExecResponse build() {
         return new ExecResponse(output, error, exitStatus);
      }
   }

   public ExecResponse(String output, String error, int exitStatus) {
      this.output = output;
      this.error = error;
      this.exitStatus = exitStatus;
   }

   public String getOutput() {
      return output;
   }

   public String getError() {
      return error;
   }

   public int getExitStatus() {
      return exitStatus;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(output, error, exitStatus);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("out", output).add("error", error).add("exitStatus", exitStatus).toString();
   }
}
