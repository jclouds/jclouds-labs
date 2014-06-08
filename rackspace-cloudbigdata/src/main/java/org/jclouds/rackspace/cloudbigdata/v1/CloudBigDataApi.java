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
package org.jclouds.rackspace.cloudbigdata.v1;

import java.io.Closeable;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.rackspace.cloudbigdata.v1.features.ClusterApi;
import org.jclouds.rackspace.cloudbigdata.v1.features.ProfileApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides access to Rackspace Cloud Big Data.
 * Rackspace Cloud Big Data is an on-demand Apache Hadoop service on the Rackspace open cloud. The service supports a RESTful API and alleviates the pain associated with deploying, managing, and scaling Hadoop clusters.
 * @see <a href="http://docs.rackspace.com/cbd/api/v1.0/cbd-devguide/content/overview.html">API Doc</a>
 */
public interface CloudBigDataApi extends Closeable{
   /**
    * Provides a set of all zones available.
    * 
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides access to all Profile features.
    * @param zone The zone (region) for the profile API.
    * @return A profile API context.
    */
   @Delegate
   ProfileApi getProfileApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to all Cluster features.
    * @param zone The zone (region) for the profile API.
    * @return A cluster API context.
    */
   @Delegate
   ClusterApi getClusterApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
}
