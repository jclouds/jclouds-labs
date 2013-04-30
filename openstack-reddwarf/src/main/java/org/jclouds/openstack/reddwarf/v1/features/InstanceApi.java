/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.openstack.reddwarf.v1.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.redddwarf.v1.binders.BindCreateInstanceToJson;
import org.jclouds.openstack.reddwarf.v1.domain.Instance;
import org.jclouds.openstack.reddwarf.v1.functions.ParsePasswordFromRootedInstance;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import com.google.common.collect.FluentIterable;

/**
 * This API is for creating, listing, and deleting an Instance, and allows enabling a root user.
 * @see org.jclouds.openstack.reddwarf.v1.domain.Instance
 * Instance
 * 
 * @see <a href="http://sourceforge.net/apps/trac/reddwarf/">api doc</a>
 * @see <a
 *      href="https://github.com/reddwarf-nextgen/reddwarf">api
 *      src</a>
 *      
 * @author Zack Shoylev
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface InstanceApi {
    
   /**
    * Same as {@link #create(String, int, String)} but name is left empty
    * 
    * @see org.jclouds.openstack.reddwarf.v1.domain.Instance#create(String, int)
    */
   @Named("instance:create")
   @POST
   @Path("/instances")
   @SelectJson("instance")
   @Consumes(MediaType.APPLICATION_JSON)
   @MapBinder(BindCreateInstanceToJson.class)
   Instance create(@PayloadParam("flavorRef") String flavor, @PayloadParam("size") int volumeSize);

   /**
    * Create a database instance by flavor type and volume size
    *
    * @param flavor The flavor URL or flavor id
    * @param volumeSize The size in GB of the instance volume
    * @return The instance created.
    */
   @Named("instance:create")
   @POST
   @Path("/instances")
   @SelectJson("instance")
   @Consumes(MediaType.APPLICATION_JSON)
   @MapBinder(BindCreateInstanceToJson.class)
   Instance create(@PayloadParam("flavorRef") String flavor, @PayloadParam("size") int volumeSize, @PayloadParam("name") String name);
   
   /**
    * Deletes an Instance by id
    *
    * @return true if successful
    */
   @Named("instances:delete/{id}")
   @DELETE
   @Path("/instances/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String instanceId);
   
   /**
    * Enables root for an instance
    *
    * @return String password
    */
   @Named("instances/{id}/root")
   @POST
   @Path("/instances/{id}/root")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParsePasswordFromRootedInstance.class)
   @Fallback(NullOnNotFoundOr404.class)
   String enableRoot(@PathParam("id") String instanceId);
   
   /**
    * Checks to see if root is enabled for an instance
    *
    * @throws ResourceNotFoundException
    * @return boolean
    */
   @Named("instances/{id}/root")
   @GET
   @Path("/instances/{id}/root")
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("rootEnabled")
   boolean isRooted(@PathParam("id") String instanceId);
   
   /**
    * Returns a summary list of Instances.
    *
    * @return The list of Instances
    */
   @Named("instance:list")
   @GET
   @Path("/instances")
   @SelectJson("instances")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Instance> list();
      
   /**
    * Returns an Instance by id
    *
    * @return Instance or Null on not found
    */
   @Named("instances:get/{id}")
   @GET
   @Path("/instances/{id}")
   @SelectJson("instance")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Instance get(@PathParam("id") String instanceId);
}
