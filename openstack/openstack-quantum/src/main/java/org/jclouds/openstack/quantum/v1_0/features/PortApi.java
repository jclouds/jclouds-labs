/**
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
package org.jclouds.openstack.quantum.v1_0.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.quantum.v1_0.domain.Attachment;
import org.jclouds.openstack.quantum.v1_0.domain.Port;
import org.jclouds.openstack.quantum.v1_0.domain.PortDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Port operations on the openstack quantum API.
 * <p/>
 * A port represents a virtual switch port on a logical network switch where all the interfaces attached to a given network are connected.
 * <p/>
 * A port has an administrative state which is either 'DOWN' or 'ACTIVE'. Ports which are administratively down will not be able to receive/send traffic.
 *
 * @author Adam Lowe
 * @author Zack Shoylev
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-network/1.0/content/Ports.html">api doc</a>
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/ports")
public interface PortApi {
   /**
    * Returns the list of all ports currently defined in Quantum for the requested network
    */
   @GET
   @SelectJson("ports")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<? extends Reference> listReferences();

   /**
    * Returns the set of ports currently defined in Quantum for the requested network.
    */
   @GET
   @SelectJson("ports")
   @Path("/detail")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<? extends Port> list();

   /**
    * Returns a specific port.
    */
   @GET
   @SelectJson("port")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   Port get(@PathParam("id") String id);

   /**
    * Returns a specific port in detail.
    */
   @GET
   @SelectJson("port")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/{id}/detail")
   @Fallback(NullOnNotFoundOr404.class)
   PortDetails getDetails(@PathParam("id") String id);

   /**
    * Create a new port on the specified network
    */
   @POST
   @SelectJson("port")
   Reference create();

   /**
    * Create a new port on the specified network, with the requested state
    */
   @POST
   @SelectJson("port")
   @WrapWith("port")
   Reference create(@PayloadParam("state") Port.State state);

   /**
    * Updates the state of a port
    */
   @PUT
   @Path("/{id}")
   @WrapWith("port")
   boolean updateState(@PathParam("id") String id, @PayloadParam("state") Port.State state);

   /**
    * Deletes a port from a network
    */
   @DELETE
   @Path("/{id}")
   boolean delete(@PathParam("id") String id);

   /**
    * Returns the attachment for the specified port.
    */
   @GET
   @SelectJson("attachment")
   @Path("/{id}/attachment")
   @Fallback(NullOnNotFoundOr404.class)
   Attachment showAttachment(@PathParam("id") String portId);

   /**
    * Plugs an attachment into the specified port
    */
   @PUT
   @Path("/{id}/attachment")
   @WrapWith("attachment")
   boolean plugAttachment(@PathParam("id") String portId, @PayloadParam("id") String attachmentId);

   /**
    *  Unplugs the attachment currently plugged into the specified port
    */
   @DELETE
   @Path("{id}/attachment")
   boolean unplugAttachment(@PathParam("id") String portId);
}
