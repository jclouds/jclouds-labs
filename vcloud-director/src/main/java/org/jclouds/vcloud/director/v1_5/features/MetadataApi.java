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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA_VALUE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.vcloud.director.v1_5.binders.BindMapAsMetadata;
import org.jclouds.vcloud.director.v1_5.binders.BindStringAsMetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.RegexValueParser;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface MetadataApi {
   /**
    * Retrieves an list of metadata
    * 
    * @return a list of metadata
    */
   @GET
   @Path("/metadata")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Metadata get();

   /**
    * Retrieves a metadata value
    * 
    * @return the metadata value, or null if not found
    */
   @GET
   @Path("/metadata/{key}")
   @Consumes
   @ResponseParser(RegexValueParser.class)
   @Fallback(NullOnNotFoundOr404.class)
   String get(@PathParam("key") String key);

   /**
    * Merges the metadata for a media with the information provided.
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned task status in order to
    *         check when it is completed.
    */
   @POST
   @Path("/metadata")
   @Consumes(TASK)
   @Produces(METADATA)
   @JAXBResponseParser
   Task putAll(@BinderParam(BindMapAsMetadata.class) Map<String, String> metadata);

   /**
    * Sets the metadata for the particular key for the media to the value provided. Note: this will replace any existing
    * metadata information
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned task status in order to
    *         check when it is completed.
    */
   @PUT
   @Path("/metadata/{key}")
   @Consumes(TASK)
   @Produces(METADATA_VALUE)
   @JAXBResponseParser
   Task put(@PathParam("key") String key, @BinderParam(BindStringAsMetadataValue.class) String metadataValue);

   /**
    * Deletes a metadata entry.
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned task status in order to
    *         check when it is completed.
    */
   @DELETE
   @Path("/metadata/{key}")
   @Consumes(TASK)
   @JAXBResponseParser
   Task remove(@PathParam("key") String key);
}
