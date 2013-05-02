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
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
import org.jclouds.View;
import org.jclouds.representations.ApiMetadata;
import org.jclouds.javax.annotation.Nullable;

import static com.google.common.collect.Iterables.transform;

public enum ToApiMetadata implements Function<org.jclouds.apis.ApiMetadata, ApiMetadata> {

   INSTANCE;

   @Override
   public ApiMetadata apply(@Nullable org.jclouds.apis.ApiMetadata input) {
      if (input == null) {
         return null;
      }
      return ApiMetadata.builder().id(input.getId()).name(input.getName()).endpointName(input.getEndpointName())
                         .identityName(input.getIdentityName()).credentialName(input.getCredentialName().orNull())
                         .version(input.getVersion())
                         .defaultEndpoint(input.getDefaultEndpoint().orNull())
                         .defaultIdentity(input.getDefaultIdentity().orNull())
                         .defaultCredential(input.getDefaultCredential().orNull())
                         .defaultProperties(input.getDefaultProperties())
                         .documentation(input.getDocumentation())
                         .context(input.getContext().getType().toString())

                         .defaultModules(ImmutableSet.<String>builder().addAll(transform(input.getDefaultModules(), new Function<Class<? extends Module>, String>() {
                           @Override
                           public String apply(@Nullable Class<? extends Module> input) {
                              return input.getName();
                           }
                         })).build())

                         .views(ImmutableSet.<String>builder().addAll(transform(input.getViews(), new Function<TypeToken<? extends View>, String>() {
                           @Override
                           public String apply(@Nullable TypeToken<? extends View> input) {
                              return input.getRawType().getName();
                           }
                         })).build())

                         .build();
   }
}
