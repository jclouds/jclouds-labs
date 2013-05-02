/*
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

package org.jclouds.management;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import org.jclouds.ContextBuilder;
import org.jclouds.apis.Apis;
import org.jclouds.codec.ToApiMetadata;
import org.jclouds.codec.ToContext;
import org.jclouds.codec.ToProvider;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.management.config.ManagementLifecycle;
import org.jclouds.management.internal.BaseManagementContext;
import org.jclouds.providers.Providers;
import org.jclouds.representations.ApiMetadata;
import org.jclouds.representations.Context;
import org.jclouds.representations.ProviderMetadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.google.common.collect.Iterables.transform;

/**
 * Core Jclouds MBean for displaying available {@link org.jclouds.representations.ApiMetadata}, {@link org.jclouds.representations.ProviderMetadata} and {@link org.jclouds.representations.Context}s.
 * Also useful for creating contexts.
 */
public class JcloudsManagement implements JcloudsManagementMBean, ManagedBean {

   private final ManagementContext managementContext;

   public JcloudsManagement() {
      this(BaseManagementContext.INSTANCE);
   }

   public JcloudsManagement(ManagementContext managementContext) {
      this.managementContext = managementContext;
   }

   @Override
   public Iterable<ApiMetadata> getApis() {
      return ImmutableSet.<ApiMetadata>builder()
              .addAll(transform(Apis.all(), ToApiMetadata.INSTANCE))
              .build();
   }

   @Override
   public ApiMetadata findApiById(String id) {
      return ToApiMetadata.INSTANCE.apply(Apis.withId(id));
   }

   @Override
   public Iterable<ProviderMetadata> getProviders() {
      return ImmutableSet.<ProviderMetadata>builder()
              .addAll(transform(Providers.all(), ToProvider.INSTANCE))
              .build();
   }

   @Override
   public ProviderMetadata findProviderById(String id) {
      return ToProvider.INSTANCE.apply(Providers.withId(id));
   }

   @Override
   public Iterable<Context> getContexts() {
      return ImmutableSet.<Context>builder()
              .addAll(transform(managementContext.listContexts(), ToContext.INSTANCE))
              .build();
   }

   @Override
   public Context createContext(String id, String name, String identity, String credential, String endpoint, String overrides) throws IOException {
      return ToContext.INSTANCE.apply(
              ContextBuilder.newBuilder(id).name(name).credentials(identity, credential).endpoint(endpoint)
                      .modules(ImmutableSet.of(new ManagementLifecycle(BaseManagementContext.INSTANCE)))
                      .overrides(stringToProperties(overrides))
                      .build()
      );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return "management";
   }

   /**
    * Parses the String into a {@link Properties} object.
    * The String is expected to separated key from valus using the '=' sign and key/value pairs with a new line.
    * @param input
    * @return
    * @throws IOException
    */
   private static Properties stringToProperties(@Nullable String input) throws IOException {
      Properties properties = new Properties();
      if (!Strings.isNullOrEmpty(input)) {
         ByteArrayInputStream bis = null;
         try {
            bis = new ByteArrayInputStream(input.getBytes(Charsets.UTF_8));
            properties.load(bis);
         } finally {
            Closeables.close(bis, true);
         }
      }
      return properties;
   }
}
