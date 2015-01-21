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
package org.jclouds.rackspace.autoscale.v1;

import java.io.Closeable;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.rackspace.autoscale.v1.features.GroupApi;
import org.jclouds.rackspace.autoscale.v1.features.PolicyApi;
import org.jclouds.rackspace.autoscale.v1.features.WebhookApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides access to Rackspace Auto Scale v1 API.
 *
 */
public interface AutoscaleApi extends Closeable {
   /**
    * Provides a set of all regions available.
    *
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides access to all scaling Group features.
    */
   @Delegate
   GroupApi getGroupApi(@EndpointParam(parser = RegionToEndpoint.class) String region);


   /**
    * Provides access to all policy features for scaling Groups.
    */
   @Delegate
   @Path("/groups/{groupId}")
   PolicyApi getPolicyApi(@EndpointParam(parser = RegionToEndpoint.class) String region,
         @PathParam("groupId") String groupId);

   /**
    * Provides access to webhook management features.
    */
   @Delegate
   @Path("/groups/{groupId}/policies/{policyId}")
   WebhookApi getWebhookApi(@EndpointParam(parser = RegionToEndpoint.class) String region,
         @PathParam("groupId") String groupId,
         @PathParam("policyId") String policyId);

   /**
    * Provides the Tenant.
    */
   @Provides
   Optional<Tenant> getCurrentTenantId();

   /**
    * @return the configured zone codes
    * @deprecated Please use {@link #getConfiguredRegions()} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Provides
   @Region
   Set<String> getConfiguredZones();

   /**
    * Provides access to all policy features for scaling Groups.
    * @deprecated Please use {@link #getPolicyApi(String, String)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/groups/{groupId}")
   PolicyApi getPolicyApiForGroup(@EndpointParam(parser = RegionToEndpoint.class) String region,
         @PathParam("groupId") String groupId);

   /**
    * Provides access to webhook management features.
    * @deprecated Please use {@link #getWebhookApi(String, String, String)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/groups/{groupId}/policies/{policyId}")
   WebhookApi getWebhookApiForGroupAndPolicy(@EndpointParam(parser = RegionToEndpoint.class) String region,
         @PathParam("groupId") String groupId,
         @PathParam("policyId") String policyId);

}
