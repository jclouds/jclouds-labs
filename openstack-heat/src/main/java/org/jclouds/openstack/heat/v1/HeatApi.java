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
package org.jclouds.openstack.heat.v1;

import java.io.Closeable;
import java.util.Set;

import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.openstack.heat.v1.features.ResourceApi;
import org.jclouds.openstack.heat.v1.features.StackApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides access to the OpenStack Orchestration (Heat) API.
 *
 */
public interface HeatApi extends Closeable {

   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides access to Resource features.
    */
   @Delegate
   ResourceApi getResourceApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to Stack features.
    */
   @Delegate
   StackApi getStackApi(@EndpointParam(parser = RegionToEndpoint.class) String region);
}

