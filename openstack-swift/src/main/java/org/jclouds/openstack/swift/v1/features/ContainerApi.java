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
package org.jclouds.openstack.swift.v1.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.v1.binders.BindMetadataToHeaders.BindContainerMetadataToHeaders;
import org.jclouds.openstack.swift.v1.binders.BindMetadataToHeaders.BindRemoveContainerMetadataToHeaders;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.functions.FalseOnAccepted;
import org.jclouds.openstack.swift.v1.functions.ParseContainerFromHeaders;
import org.jclouds.rest.Binder;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.collect.FluentIterable;

/**
 * Storage Container Services
 * 
 * @author Adrian Cole
 * @author Zack Shoylev
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-object-storage/1.0/content/storage-container-services.html"
 *      >api doc</a>
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(APPLICATION_JSON)
public interface ContainerApi {

   /**
    * Lists up to 10,000 containers.
    * 
    * @return a list of existing storage containers ordered by name.
    */
   @Named("ListContainers")
   @GET
   @QueryParams(keys = "format", values = "json")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Path("/")
   FluentIterable<Container> listFirstPage();

   /**
    * Lists up to 10,000 containers, starting at {@code marker}
    * 
    * @param marker
    *           lexicographic position to start list.
    * 
    * @return a list of existing storage containers ordered by name.
    */
   @Named("ListContainers")
   @GET
   @QueryParams(keys = "format", values = "json")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Path("/")
   FluentIterable<Container> listAt(@QueryParam("marker") String marker);

   /**
    * Creates a container, if not already present.
    * 
    * @param containerName
    *           corresponds to {@link Container#name()}.
    * @param options
    *           configuration such as <a href=
    *           "http://docs.openstack.org/api/openstack-object-storage/1.0/content/special-metadata-acls.html"
    *           >public read access</a>.
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/create-container.html">
    *      Create Container API</a>
    * 
    * @return <code>false</code> if the container already existed.
    */
   @Named("CreateContainer")
   @PUT
   @ResponseParser(FalseOnAccepted.class)
   @Path("/{containerName}")
   boolean createIfAbsent(@PathParam("containerName") String containerName,
         @BinderParam(ContainerReadHeader.class) CreateContainerOptions options);

   static class ContainerReadHeader implements Binder {
      @SuppressWarnings("unchecked")
      @Override
      public <R extends HttpRequest> R bindToRequest(R request, Object input) {
         CreateContainerOptions options = CreateContainerOptions.class.cast(input);
         if (options.isPublicRead()) {
            return (R) request.toBuilder().addHeader("x-container-read", ".r:*,.rlistings").build();
         }
         return request;
      }
   }

   /**
    * Gets the {@link Container}.
    * 
    * @param containerName
    *           corresponds to {@link Container#name()}.
    * @return the Container or null, if not found.
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/retrieve-Container-metadata.html">
    *      Get Container Metadata API</a>
    */
   @Named("GetContainer")
   @HEAD
   @ResponseParser(ParseContainerFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{containerName}")
   @Nullable
   Container get(@PathParam("containerName") String containerName);

   /**
    * Creates or updates the Container metadata.
    * 
    * @param containerName
    *           corresponds to {@link Container#name()}.
    * @param metadata
    *           the Container metadata to create or update.
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/Update_Container_Metadata-d1e1900.html">
    *      Create or Update Container Metadata API</a>
    * 
    * @return <code>true</code> if the Container Metadata was successfully
    *         created or updated, false if not.
    */
   @Named("UpdateContainerMetadata")
   @POST
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{containerName}")
   boolean updateMetadata(@PathParam("containerName") String containerName,
         @BinderParam(BindContainerMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Deletes Container metadata.
    * 
    * @param containerName
    *           corresponds to {@link Container#name()}.
    * @param metadata
    *           the Container metadata to delete.
    * 
    * @return <code>true</code> if the Container Metadata was successfully
    *         deleted, false if not.
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/delete-container-metadata.html">
    *      Delete Container Metadata API</a>
    */
   @Named("DeleteContainerMetadata")
   @POST
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{containerName}")
   boolean deleteMetadata(@PathParam("containerName") String containerName,
         @BinderParam(BindRemoveContainerMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Deletes a container, if empty.
    * 
    * @param containerName
    *           corresponds to {@link Container#name()}.
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/delete-container.html">
    *      Delete Container API</a>
    * 
    * @return <code>false</code> if the container was not present.
    * @throws IllegalStateException
    *            when the container wasn't empty.
    */
   @Named("DeleteContainer")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{containerName}")
   boolean deleteIfEmpty(@PathParam("containerName") String containerName) throws IllegalStateException;
}
