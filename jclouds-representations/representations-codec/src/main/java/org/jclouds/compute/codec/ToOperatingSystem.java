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
package org.jclouds.compute.codec;

import com.google.common.base.Function;
import org.jclouds.compute.representations.OperatingSystem;
import org.jclouds.javax.annotation.Nullable;

public enum ToOperatingSystem implements Function<org.jclouds.compute.domain.OperatingSystem, OperatingSystem> {

   INSTANCE;

   @Override
   public OperatingSystem apply(@Nullable org.jclouds.compute.domain.OperatingSystem input) {
      if (input == null) {
         return null;
      }
      return OperatingSystem.builder().family(input.getFamily().name()).version(input.getVersion()).name(input.getName())
                            .arch(input.getArch()).is64Bit(input.is64Bit()).build();
   }
}
