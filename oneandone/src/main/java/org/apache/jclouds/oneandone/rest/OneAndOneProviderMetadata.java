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
package org.apache.jclouds.oneandone.rest;

import com.google.auto.service.AutoService;
import java.net.URI;
import java.util.Properties;
import static org.apache.jclouds.oneandone.rest.config.OneAndOneProperties.POLL_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_MAX_RATE_LIMIT_WAIT;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.compute.config.ComputeServiceProperties.POLL_INITIAL_PERIOD;
import static org.jclouds.compute.config.ComputeServiceProperties.POLL_MAX_PERIOD;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

@AutoService(ProviderMetadata.class)
public class OneAndOneProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public OneAndOneProviderMetadata() {
      super(builder());
   }

   public OneAndOneProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = OneAndOneApiMetadata.defaultProperties();

//      properties.setProperty(PROPERTY_REGIONS, "de,us,es,gb");
      properties.put("jclouds.ssh.max-retries", "7");
      properties.put("jclouds.ssh.retry-auth", "true");

      long defaultTimeout = 60L * 60L; // 1 hour
      properties.put(POLL_TIMEOUT, defaultTimeout);
      properties.put(POLL_INITIAL_PERIOD, 5L * 10);
      properties.put(POLL_MAX_PERIOD, 7L * 10L);
      properties.put(PROPERTY_SO_TIMEOUT, 6000 * 5);
      properties.put(PROPERTY_CONNECTION_TIMEOUT, 60000 * 5);
      properties.put(PROPERTY_MAX_RATE_LIMIT_WAIT, 330000);

      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("oneandone")
                 .name("OneAndOne REST Compute")
                 .apiMetadata(new OneAndOneApiMetadata())
                 .homepage(URI.create("https://cloudpanel-api.1and1.com"))
                 .console(URI.create("https://account.1and1.com"))
                 .endpoint("https://cloudpanel-api.1and1.com/v1/")
                 .defaultProperties(OneAndOneProviderMetadata.defaultProperties());
      }

      @Override
      public OneAndOneProviderMetadata build() {
         return new OneAndOneProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
