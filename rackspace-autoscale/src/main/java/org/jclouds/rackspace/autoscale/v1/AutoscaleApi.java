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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.rackspace.autoscale.v1.features.GroupApi;
import org.jclouds.rackspace.autoscale.v1.features.PolicyApi;
import org.jclouds.rackspace.autoscale.v1.features.WebhookApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides access to Rackspace Autoscale.
 *  
 * @see <a href="https://rackspace-autoscale.readthedocs.org">API Doc</a>
 * @see <a href="http://docs.autoscale.apiary.io/">Apiary API Doc</a>
 * @author Zack Shoylev
 */
public interface AutoscaleApi extends Closeable{
   /**
    * Provides a set of all zones available.
    * 
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides access to all scaling Group features.
    */
   @Delegate
   GroupApi getGroupApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to all policy features for scaling Groups.
    */
   @Delegate
   @Path("/groups/{groupId}")
   PolicyApi getPolicyApiForZoneAndGroup(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone,
         @PathParam("groupId") String groupId);

   /**
    * Provides access to webhook management features.
    */
   @Delegate
   @Path("/groups/{groupId}/policies/{policyId}")
   WebhookApi getWebhookApiForZoneAndGroupAndPolicy(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone,
         @PathParam("groupId") String groupId,
         @PathParam("policyId") String policyId);

   /**
    * Provides the Tenant.
    */
   @Provides 
   Optional<Tenant> getCurrentTenantId();
}
