/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.codec;

import com.google.common.base.Function;
import org.jclouds.representations.Context;
import org.jclouds.javax.annotation.Nullable;

public enum ToContext implements Function<org.jclouds.Context, Context> {

   INSTANCE;

   @Override
   public Context apply(@Nullable org.jclouds.Context input) {
      if (input == null) {
         return null;
      }
      return Context.builder().name(input.getName()).identity(input.getIdentity())
              .providerId(input.getProviderMetadata() != null ? input.getProviderMetadata().getId() : null).build();
   }
}
