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
package org.jclouds.openstack.glance.v1_0;

import java.io.Closeable;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.openstack.glance.functions.ZoneToEndpointNegotiateVersion;
import org.jclouds.openstack.glance.v1_0.features.ImageApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides access to the OpenStack Image (Glance) v1 API.
 * <p/>
 *
 */
public interface GlanceApi extends Closeable {
   /**
    * Gets the configured zones.
    *
    * @return the zone codes currently configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides access to Image features.
    */
   @Delegate
   ImageApi getImageApiForZone(@EndpointParam(parser = ZoneToEndpointNegotiateVersion.class) @Nullable String zone);

}
