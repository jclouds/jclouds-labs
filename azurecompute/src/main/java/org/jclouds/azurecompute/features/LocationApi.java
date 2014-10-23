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

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.xml.ListLocationsHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * The Service Management API includes operations for listing the available data center locations
 * for a cloud service in your subscription.
 * <p/>
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441299" />
 */
@Path("/locations")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_XML)
public interface LocationApi {

   /**
    * The List Locations operation lists all of the data center locations that are valid for your
    * subscription.
    */
   @Named("ListLocations")
   @GET
   @XMLResponseParser(ListLocationsHandler.class)
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Location> list();
}
