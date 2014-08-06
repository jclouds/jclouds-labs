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

public class OperatingSystem implements Serializable {

   private static final long serialVersionUID = 5789055455232061970L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String family;
      private String name;
      private String arch;
      private String version;
      private String description;
      private boolean is64Bit;

      public Builder family(final String family) {
         this.family = family;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder arch(final String arch) {
         this.arch = arch;
         return this;
      }

      public Builder version(final String version) {
         this.version = version;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public Builder is64Bit(final boolean is64Bit) {
         this.is64Bit = is64Bit;
         return this;
      }

      public OperatingSystem build() {
         return new OperatingSystem(family, name, arch, version, description, is64Bit);
      }
   }

   private final String family;
   private final String name;
   private final String arch;
   private final String version;
   private final String description;
   private final boolean is64Bit;


   public OperatingSystem(String family, String name, String arch, String version, String description, boolean is64Bit) {
      this.family = family;
      this.name = name;
      this.arch = arch;
      this.version = version;
      this.description = description;
      this.is64Bit = is64Bit;
   }

   public String getFamily() {
      return family;
   }

   public String getName() {
      return name;
   }

   public String getArch() {
      return arch;
   }

   public String getVersion() {
      return version;
   }

   public String getDescription() {
      return description;
   }

   public boolean isIs64Bit() {
      return is64Bit;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(family, version, arch, is64Bit);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("family", family).add("name", name).add("arch", arch)
              .add("version", version).add("description", description).add("is64bit", is64Bit).toString();
   }
}
