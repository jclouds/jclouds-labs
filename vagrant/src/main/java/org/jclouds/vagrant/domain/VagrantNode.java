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
package org.jclouds.vagrant.domain;

import java.io.File;
import java.util.Collection;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata.Status;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class VagrantNode {

   private volatile Status machineState = Status.PENDING;

   public abstract File path();

   public abstract String id();

   public abstract String group();

   public abstract String name();

   public abstract Image image();

   public abstract Collection<String> networks();

   public abstract String hostname();

   public static Builder builder() {
      return new AutoValue_VagrantNode.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder setPath(File path);
      public abstract Builder setId(String id);
      public abstract Builder setGroup(String group);
      public abstract Builder setName(String name);
      public abstract Builder setImage(Image image);
      public abstract Builder setNetworks(Collection<String> networks);
      public abstract Builder setHostname(String hostname);
      public abstract VagrantNode build();
   }

   public Status machineState() {
      return machineState;
   }

   public void setMachineState(Status machineState) {
      this.machineState = machineState;
   }

}
