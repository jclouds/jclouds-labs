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
package org.jclouds.azurecompute.features;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import static org.jclouds.Fallbacks.NullOnNotFoundOr404;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.azurecompute.binders.CreateAffinityGroupParamsToXML;
import org.jclouds.azurecompute.binders.UpdateAffinityGroupParamsToXML;
import org.jclouds.azurecompute.domain.AffinityGroup;
import org.jclouds.azurecompute.domain.CreateAffinityGroupParams;
import org.jclouds.azurecompute.domain.UpdateAffinityGroupParams;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;
import org.jclouds.azurecompute.xml.AffinityGroupHandler;
import org.jclouds.azurecompute.xml.ListAffinityGroupsHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * The Service Management API includes operations for managing affinity groups in your subscription.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/azure/ee460798">docs</a>
 */
@Path("/affinitygroups")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(APPLICATION_XML)
@Produces(APPLICATION_XML)
public interface AffinityGroupApi {

   /**
    * The List Affinity Groups operation lists the affinity groups that are associated with the specified subscription.
    *
    * @return the affinity groups that are associated with the specified subscription
    */
   @Named("ListAffinityGroups")
   @GET
   @XMLResponseParser(ListAffinityGroupsHandler.class)
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<AffinityGroup> list();

   /**
    * The Get Affinity Group Properties operation returns the system properties that are associated with the specified
    * affinity group.
    *
    * @param name name of the affinity group
    * @return the system properties that are associated with the specified affinity group
    */
   @Named("GetAffinityGroup")
   @GET
   @Path("/{name}")
   @XMLResponseParser(AffinityGroupHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   AffinityGroup get(@PathParam("name") String name);

   /**
    * The Create Affinity Group operation creates a new affinity group for the specified subscription.
    *
    * @param params the affinity group to be created
    * @return a value that uniquely identifies a request made against the management service
    */
   @Named("AddAffinityGroup")
   @POST
   @ResponseParser(ParseRequestIdHeader.class)
   String add(@BinderParam(CreateAffinityGroupParamsToXML.class) CreateAffinityGroupParams params);

   /**
    * The Update Affinity Group operation updates the label or the description for an affinity group in the specified
    * subscription.
    *
    * @param params the affinity group to be created
    * @return a value that uniquely identifies a request made against the management service
    */
   @Named("UpdateAffinityGroup")
   @PUT
   @Path("/{name}")
   @ResponseParser(ParseRequestIdHeader.class)
   String update(@PathParam("name") String name,
           @BinderParam(UpdateAffinityGroupParamsToXML.class) UpdateAffinityGroupParams params);

   /**
    * The Delete Affinity Group operation deletes an affinity group in the specified subscription.
    *
    * @param name name of the affinity group
    * @return a value that uniquely identifies a request made against the management service
    */
   @Named("DeleteAffinityGroup")
   @DELETE
   @Path("/{name}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(@PathParam("name") String name);
}
