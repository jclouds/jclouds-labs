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

import org.jclouds.javax.annotation.Nullable;

import java.util.Date;
import java.util.List;


public abstract class BaseImage {

   public String type;

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract Cluster cluster();

   /**
    * optional on CustomerImage, mandatory on OsImage
    */
   @Nullable
   public abstract Guest guest();

   public abstract String datacenterId();

   public abstract CPU cpu();

   public abstract int memoryGb();

   @Nullable
   public abstract List<ImageNic> nics();

   public abstract List<Disk> disks();

   public abstract List<String> softwareLabels();

   public abstract Date createTime();

}
