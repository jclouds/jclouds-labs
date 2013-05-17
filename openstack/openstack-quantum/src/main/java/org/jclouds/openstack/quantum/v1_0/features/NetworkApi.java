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
package org.jclouds.openstack.quantum.v1_0.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.quantum.v1_0.domain.Network;
import org.jclouds.openstack.quantum.v1_0.domain.NetworkDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Network operations on the openstack quantum API.
 * <p/>
 * Each tenant can define one or more networks. A network is a virtual isolated layer-2 broadcast domain reserved to the
 * tenant. A tenant can create several ports for a network, and plug virtual interfaces into these ports.
 *
 * @author Adam Lowe
 * @author Zack Shoylev
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-network/1.0/content/Networks.html">api doc</a>
 */

@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/networks")
public interface NetworkApi {

   /**
    * Returns the list of all networks currently defined in Quantum for the current tenant. The list provides the unique
    * identifier of each network configured for the tenant.
    */
   @GET
   @SelectJson("networks")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<? extends Reference> listReferences();

   /**
    * Returns all networks currently defined in Quantum for the current tenant.
    */
   @GET
   @SelectJson("networks")
   @Path("/detail")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<? extends Network> list();

   /**
    * Returns the specific network.
    */
   @GET
   @SelectJson("network")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   Network get(@PathParam("id") String id);

   /**
    * Returns the details of the specific network.
    */
   @GET
   @SelectJson("network")
   @Path("/{id}/detail")
   @Fallback(NullOnNotFoundOr404.class)
   NetworkDetails getDetails(@PathParam("id") String id);

   /**
    * Create a new network with the specified symbolic name
    */
   @POST
   @SelectJson("network")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("network")
   Reference create(@PayloadParam("name") String name);

   /**
    * Adjusts the symbolic name of a network
    *
    * @param id   the id of the Network to modify
    * @param name the new name for the Network
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @WrapWith("network")
   boolean rename(@PathParam("id") String id, @PayloadParam("name") String name);

   /**
    * Deletes the specified network
    */
   @DELETE
   @Path("/{id}")
   boolean delete(@PathParam("id") String id);
}
