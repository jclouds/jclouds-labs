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
package org.jclouds.rackspace.autoscale.v1.features;


import java.io.Closeable;
import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.autoscale.v1.binders.BindScalingPoliciesToJson;
import org.jclouds.rackspace.autoscale.v1.binders.BindScalingPolicyToJson;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy;
import org.jclouds.rackspace.autoscale.v1.functions.ParseScalingPoliciesResponse;
import org.jclouds.rackspace.autoscale.v1.functions.ParseScalingPolicyResponse;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.collect.FluentIterable;

/**
 * The API for controlling the configuration of scaling groups.
 * A scaling group is a high-level autoscaling concept that encompasses a group configuration, a launch configuration, and a set of scaling policies.
 * @author Zack Shoylev
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface PolicyApi extends Closeable {
   /**
    * Create a scaling policy.
    * @param scalingPolicies The list of scaling policies.
    * @return List of the created scaling policies
    * @see CreateScalingPolicy
    * @see ScalingPolicy
    */
   @Named("Policy:create")
   @POST
   @Path("/policies")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @MapBinder(BindScalingPoliciesToJson.class)
   @ResponseParser(ParseScalingPoliciesResponse.class)
   FluentIterable<ScalingPolicy> create(@PayloadParam("scalingPolicies") List<CreateScalingPolicy> scalingPolicies);

   /**
    * This operation lists all scaling policies.
    * @return A list of scaling policy responses.
    * @see ScalingPolicy
    */
   @Named("Policy:list")
   @GET
   @Path("/policies")
   @ResponseParser(ParseScalingPoliciesResponse.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<ScalingPolicy> list();

   /**
    * This operation returns the details for a single scaling policy.
    * @return Existing scaling policy details
    * @see ScalingPolicy
    */
   @Named("Policy:get")
   @GET
   @Path("/policies/{scalingPolicyId}")
   @ResponseParser(ParseScalingPolicyResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   ScalingPolicy get(@PathParam("scalingPolicyId") String scalingPolicyId);

   /**
    * This operation updates a specific scaling policy.
    * @return true if successful.
    * @see CreateScalingPolicy
    */
   @Named("Policy:update")
   @PUT
   @Path("/policies/{scalingPolicyId}")
   @MapBinder(BindScalingPolicyToJson.class)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean update(@PathParam("scalingPolicyId") String scalingPolicyId, @PayloadParam("scalingPolicy") CreateScalingPolicy scalingPolicy);

   /**
    * This operation deletes a specific scaling policy.
    * @return true if successful.
    * @see CreateScalingPolicy
    */
   @Named("Policy:delete")
   @DELETE
   @Path("/policies/{scalingPolicyId}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("scalingPolicyId") String scalingPolicyId);

   /**
    * This operation executes a specific scaling policy.
    * @return true if successful.
    * @see CreateScalingPolicy
    */
   @Named("Policy:execute")
   @POST
   @Path("/policies/{scalingPolicyId}/execute")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean execute(@PathParam("scalingPolicyId") String scalingPolicyId);
}
