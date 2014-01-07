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
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.OBJECT_COPY_FROM;

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

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnContainerNotFound;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.v1.CopyObjectException;
import org.jclouds.openstack.swift.v1.binders.BindMetadataToHeaders.BindObjectMetadataToHeaders;
import org.jclouds.openstack.swift.v1.binders.BindMetadataToHeaders.BindRemoveObjectMetadataToHeaders;
import org.jclouds.openstack.swift.v1.binders.SetPayload;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.functions.ETagHeader;
import org.jclouds.openstack.swift.v1.functions.ParseObjectFromResponse;
import org.jclouds.openstack.swift.v1.functions.ParseObjectListFromResponse;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-object-storage/1.0/content/storage-object-services.html"
 *      >api doc</a>
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(APPLICATION_JSON)
public interface ObjectApi {

   /**
    * Lists up to 10,000 objects.
    * 
    * @param options
    *          options to control the output list.
    *          
    * @return an {@link ObjectList} of {@link SwiftObject} ordered by name or null.
    */
   @Named("ListObjects")
   @GET
   @QueryParams(keys = "format", values = "json")
   @ResponseParser(ParseObjectListFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/")
   @Nullable
   ObjectList list(ListContainerOptions options);

   /**
    * Creates or updates an object.
    * 
    * @param objectName
    *           corresponds to {@link SwiftObject#name()}.
    * @param payload
    *           corresponds to {@link SwiftObject#payload()}.
    * @param metadata
    *           corresponds to {@link SwiftObject#metadata()}.
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/create-update-object.html">
    *      Create or Update Object API</a>
    * 
    * @return {@link SwiftObject#etag()} of the object.
    */
   @Named("CreateOrUpdateObject")
   @PUT
   @ResponseParser(ETagHeader.class)
   @Path("/{objectName}")
   String replace(@PathParam("objectName") String objectName, @BinderParam(SetPayload.class) Payload payload,
         @BinderParam(BindObjectMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Gets the {@link SwiftObject} metadata without its
    * {@link Payload#getInput() body}.
    * 
    * @param objectName
    *           corresponds to {@link SwiftObject#name()}.
    * @return the {@link SwiftObject} or null, if not found.
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/retrieve-object-metadata.html">
    *      Get Object Metadata API</a>
    */
   @Named("GetObjectMetadata")
   @HEAD
   @ResponseParser(ParseObjectFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{objectName}")
   @Nullable
   SwiftObject head(@PathParam("objectName") String objectName);

   /**
    * Gets the {@link SwiftObject} including its {@link Payload#getInput() body}.
    * 
    * @param objectName
    *           corresponds to {@link SwiftObject#name()}.
    * @param options
    *           options to control the download.
    * 
    * @return the Object or null, if not found.
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/retrieve-object.html">
    *      Get Object API</a>
    */
   @Named("GetObject")
   @GET
   @ResponseParser(ParseObjectFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{objectName}")
   @Nullable
   SwiftObject get(@PathParam("objectName") String objectName, GetOptions options);

   /**
    * Creates or updates the metadata for a {@link SwiftObject}.
    * 
    * @param objectName
    *           corresponds to {@link SwiftObject#name()}.
    * @param metadata
    *           the metadata to create or update.
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/Update_Container_Metadata-d1e1900.html">
    *      Create or Update Object Metadata API</a>
    * 
    * @return {@code true} if the metadata was successfully created
    *         or updated, false if not.
    */
   @Named("UpdateObjectMetadata")
   @POST
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{objectName}")
   boolean updateMetadata(@PathParam("objectName") String objectName,
         @BinderParam(BindObjectMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Deletes the metadata from a {@link SwiftObject}.
    * 
    * @param objectName
    *           corresponds to {@link SwiftObject#name()}.
    * @param metadata
    *           corresponds to {@link SwiftObject#metadata()}.
    * 
    * @return {@code true} if the metadata was successfully deleted, false if not.
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/delete-object-metadata.html">
    *      Delete Object Metadata API</a>
    */
   @Named("DeleteObjectMetadata")
   @POST
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{objectName}")
   boolean deleteMetadata(@PathParam("objectName") String objectName,
         @BinderParam(BindRemoveObjectMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Deletes an object, if present.
    * 
    * @param objectName
    *           corresponds to {@link SwiftObject#name()}.
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/delete-object.html">
    *      Delete Object API</a>
    */
   @Named("DeleteObject")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/{objectName}")
   void delete(@PathParam("objectName") String objectName);

   /**
    * Copies an object from one container to another. Please note that this 
    * is a server side copy.
    * 
    * @param destinationObject
    *           the destination object name.
    * @param sourceContainer
    *           the source container name.
    * @param sourceObject
    *           the source object name.
    * @return {@code true} if the object was successfully copied, false if not.
    * 
    * @throws CopyObjectException if the source or destination container do not exist
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/copy-object.html">
    *      Copy Object API</a>
    */
   @Named("CopyObject")
   @PUT
   @Path("/{destinationObject}")
   @Headers(keys = OBJECT_COPY_FROM, values = "/{sourceContainer}/{sourceObject}")
   @Fallback(FalseOnContainerNotFound.class)
   boolean copy(@PathParam("destinationObject") String destinationObject,
                @PathParam("sourceContainer") String sourceContainer,
                @PathParam("sourceObject") String sourceObject);

}
