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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Hardware implements Serializable {

   private static final long serialVersionUID = -5052972144323758255L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String name;
      private Set<String> tags = ImmutableSet.of();
      private List<Processor> processors = ImmutableList.of();
      private int ram;
      private List<Volume> volumes = ImmutableList.of();
      private String hypervisor;

      public Builder id(final String id) {
         this.id = id;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder tags(final Set<String> tags) {
         this.tags = ImmutableSet.copyOf(tags);
         return this;
      }

      public Builder processors(final List<Processor> processors) {
         this.processors = ImmutableList.copyOf(processors);
         return this;
      }

      public Builder ram(final int ram) {
         this.ram = ram;
         return this;
      }

      public Builder volumes(final List<Volume> volumes) {
         this.volumes = ImmutableList.copyOf(volumes);
         return this;
      }

      public Builder hypervisor(final String hypervisor) {
         this.hypervisor = hypervisor;
         return this;
      }

      public Hardware build() {
         return new Hardware(id, name, tags, processors, ram, volumes, hypervisor);
      }
   }

   private final String id;
   private final String name;
   private final Set<String> tags;
   private final List<Processor> processors;
   private final int ram;
   private final List<Volume> volumes;
   private final String hypervisor;


   public Hardware(String id, String name, Set<String> tags, List<Processor> processors, int ram, List<Volume> volumes, String hypervisor) {
      this.id = id;
      this.name = name;
      this.tags = tags;
      this.processors = processors;
      this.ram = ram;
      this.volumes = volumes;
      this.hypervisor = hypervisor;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public Set<String> getTags() {
      return tags;
   }

   public List<Processor> getProcessors() {
      return processors;
   }

   public int getRam() {
      return ram;
   }

   public List<Volume> getVolumes() {
      return volumes;
   }

   public String getHypervisor() {
      return hypervisor;
   }


   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("id", id).add("name", name)
              .add("processors", processors).add("ram", ram).add("volums", volumes).add("hypervisor", hypervisor)
              .toString();
   }
}

