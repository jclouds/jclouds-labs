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
package org.jclouds.codec;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
import org.jclouds.View;
import org.jclouds.representations.ProviderMetadata;
import org.jclouds.javax.annotation.Nullable;

import java.util.Map;
import java.util.Properties;

import static com.google.common.collect.Iterables.transform;

public enum ToProvider implements Function<org.jclouds.providers.ProviderMetadata, ProviderMetadata> {

   INSTANCE;

   public ProviderMetadata apply(@Nullable org.jclouds.providers.ProviderMetadata input) {
      if (input == null) {
         return null;
      }




      return ProviderMetadata.builder().id(input.getId()).name(input.getName())
                             .endpoint(input.getEndpoint())
                             .console(input.getConsole().orNull()).homePage(input.getHomepage().orNull())
                             .linkedServices(input.getLinkedServices()).iso3166Codes(input.getIso3166Codes())
                             .identityName(input.getApiMetadata().getIdentityName())
                             .credentialName(input.getApiMetadata().getCredentialName().orNull())
                             .endpointName(input.getApiMetadata().getEndpointName())
                             .documentation(input.getApiMetadata().getDocumentation().toString())
                             .defaultProperties(fromProperties(input.getApiMetadata().getDefaultProperties(), input.getDefaultProperties()))

                             .defaultModules(ImmutableSet.<String>builder().addAll(transform(input.getApiMetadata().getDefaultModules(), new Function<Class<? extends Module>, String>() {
                                @Override
                                public String apply(@Nullable Class<? extends Module> input) {
                                   return input.getName();
                                }
                             })).build())

                             .views(ImmutableSet.<String>builder().addAll(transform(input.getApiMetadata().getViews(), new Function<TypeToken<? extends View>, String>() {
                                @Override
                                public String apply(@Nullable TypeToken<? extends View> input) {
                                   return input.getRawType().getName();
                                }
                             })).build())

                             .build();
   }

   /**
    * Merges multiple {@link Properties} into a {@link Map}.
    * In case of duplicate keys, the latest value will be kept.
    * This utility is mostly needed because the map builder can't handle duplicates.
    * @param properties
    * @return
    */
   private static Map<String, String> fromProperties(Properties ... properties) {
      Map<String, String> map = Maps.newHashMap();
      for (Properties p : properties) {
         map.putAll(Maps.fromProperties(p));
      }
      return map;
   }
}
