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

import static org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import static org.jclouds.Fallbacks.NullOnNotFoundOr404;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.azurecompute.domain.Disk;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;
import org.jclouds.azurecompute.xml.ListDisksHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * The Service Management API includes operations for managing the disks in your subscription.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157188">docs</a>
 */
@Path("/services/disks")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_XML)
public interface DiskApi {

   /**
    * The List Disks operation retrieves a list of the disks in your image repository.
    */
   @Named("ListDisks")
   @GET
   @XMLResponseParser(ListDisksHandler.class)
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Disk> list();

   /**
    * The Delete Disk operation deletes the specified data or operating system disk from your image repository.
    *
    * @return request id or null, if not found
    */
   @Named("DeleteDisk")
   @DELETE
   @Path("/{diskName}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(@PathParam("diskName") String diskName);
}
