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

import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.rackspace.cloudbigdata.v1.features.ClusterApi;
import org.jclouds.rackspace.cloudbigdata.v1.features.ProfileApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides access to the Rackspace Cloud Big Data v1 API.
 *
 * Rackspace Cloud Big Data is an on-demand Apache Hadoop service on the Rackspace open cloud. The service
 * supports a RESTful API and alleviates the pain associated with deploying, managing, and scaling Hadoop clusters.
 */
public interface CloudBigDataApi extends Closeable {
   /**
    * Provides a set of all regions available.
    *
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides access to all Profile features.
    * @param region The region for the profile API.
    * @return A profile API context.
    */
   @Delegate
   ProfileApi getProfileApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to all Cluster features.
    * @param region The region for the profile API.
    * @return A cluster API context.
    */
   @Delegate
   ClusterApi getClusterApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * @return the Zone codes configured
    * @deprecated Please use {@link #getConfiguredRegions()} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Provides
   @Region
   Set<String> getConfiguredZones();

   /**
    * Provides access to all Profile features.
    * @param zone The zone (region) for the profile API.
    * @return A profile API context.
    * @deprecated Please use {@link #getProfileApi(String)} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   ProfileApi getProfileApiForZone(@EndpointParam(parser = RegionToEndpoint.class) String zone);

   /**
    * Provides access to all Cluster features.
    * @param zone The zone (region) for the profile API.
    * @return A cluster API context.
    * @deprecated Please use {@link #getClusterApi(String)} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   ClusterApi getClusterApiForZone(@EndpointParam(parser = RegionToEndpoint.class) String zone);

}
