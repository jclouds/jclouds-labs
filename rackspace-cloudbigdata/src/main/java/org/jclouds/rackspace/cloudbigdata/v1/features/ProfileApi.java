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
package org.jclouds.rackspace.cloudbigdata.v1.features;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.cloudbigdata.v1.domain.CreateProfile;
import org.jclouds.rackspace.cloudbigdata.v1.domain.Profile;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

/**
 * The API for controlling profiles.
 * Your Cloud Big Data profile is different from your cloud account. Your profile has the following characteristics and requirements:
 * A profile is the configuration for the administration and login account for the cluster.
 * Only one profile is allowed for each user or account.
 * Any updates or additions override the existing profile.
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
@Path("/profile")
public interface ProfileApi extends Closeable {

   /**
    * Create a Profile.
    * Before creating a cluster, a profile has to be created.
    * @param profile A CreateProfile object containing information about the profile to be created.
    * @return Profile The profile created by this call.
    * @see Profile
    * @see CreateProfile
    */
   @Named("profile:create")
   @POST
   @SelectJson("profile")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Profile create(@WrapWith("profile") CreateProfile profile);

   /**
    * This operation returns detailed profile information for the current user.
    * Before creating a cluster, a profile has to be created.
    * @return Detailed profile information for the current user.
    * @see Profile
    * @see CreateProfile
    */
   @Named("profile:get")
   @GET
   @SelectJson("profile")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Profile get();
}
