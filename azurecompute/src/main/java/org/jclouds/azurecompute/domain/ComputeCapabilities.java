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
package org.jclouds.azurecompute.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Shared by {@link Location} and {@link AffinityGroup}.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/gg441293#bk_computecapabilities">docs</a>
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/ee460797.aspx#bk_computecapabilities">docs</a>
 */
@AutoValue
public abstract class ComputeCapabilities {

   ComputeCapabilities() {
   } // For AutoValue only!

   /**
    * Specifies the role size that is available for the type of deployment.
    *
    * @return the role size that is available for the type of deployment
    */
   public abstract List<RoleSize.Type> virtualMachineRoleSizes();

   /**
    * Specifies the role size that is available for the type of deployment.
    *
    * @return the role size that is available for the type of deployment
    */
   public abstract List<RoleSize.Type> webWorkerRoleSizes();

   public static ComputeCapabilities create(
           final List<RoleSize.Type> virtualMachineRoleSizes, final List<RoleSize.Type> webWorkerRoleSizes) {

      return new AutoValue_ComputeCapabilities(
              ImmutableList.copyOf(virtualMachineRoleSizes), ImmutableList.copyOf(webWorkerRoleSizes));
   }

}
