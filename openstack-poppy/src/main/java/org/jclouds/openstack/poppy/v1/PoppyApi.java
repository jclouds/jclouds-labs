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
package org.jclouds.openstack.poppy.v1;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.auth.filters.AuthenticateRequest;
import org.jclouds.openstack.poppy.v1.config.CDN;
import org.jclouds.openstack.poppy.v1.fallbacks.PoppyFallbacks.FalseOn500or503;
import org.jclouds.openstack.poppy.v1.features.FlavorApi;
import org.jclouds.openstack.poppy.v1.features.ServiceApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import com.google.common.annotations.Beta;

/**
 * Provides access to the OpenStack CDN (Poppy) API.
 *
 */
@Beta
public interface PoppyApi extends Closeable {

   /**
    * Pings the server to ensure it is available.
    *
    * @return true if the server is responding, false otherwise
    */
   @Named("poppy:ping")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Endpoint(CDN.class)
   @RequestFilters(AuthenticateRequest.class)
   @Fallback(FalseOn500or503.class)
   @Path("/ping")
   boolean ping();

   /**
    * Provides access to Flavor features.
    */
   @Delegate
   FlavorApi getFlavorApi();

   /**
    * Provides access to Service features.
    */
   @Delegate
   ServiceApi getServiceApi();
}
