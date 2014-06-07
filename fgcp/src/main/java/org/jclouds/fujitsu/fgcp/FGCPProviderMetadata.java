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
package org.jclouds.fujitsu.fgcp;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;

import java.util.Properties;

import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Base implementation of {@link org.jclouds.providers.ProviderMetadata} for FGCP.
 */
public class FGCPProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   public FGCPProviderMetadata() {
      super(builder());
   }

   public FGCPProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(TEMPLATE, "osFamily=CENTOS,osVersionMatches=6.2,os64Bit=true");
      properties.setProperty(ComputeServiceProperties.POLL_INITIAL_PERIOD, 5 * 1000L + ""); // 5 sec.
      properties.setProperty(ComputeServiceProperties.POLL_MAX_PERIOD, 20 * 1000L + ""); // 20 sec.
      // (clean) node shutdown time depends on what's running inside, so give it enough time
      properties.setProperty(ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED, 6 * 60 * 1000L + ""); // 6 min.

      return properties;
   }
}
