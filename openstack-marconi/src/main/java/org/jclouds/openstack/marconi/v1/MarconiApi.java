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
package org.jclouds.openstack.marconi.v1;

import java.io.Closeable;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.openstack.marconi.v1.features.ClaimApi;
import org.jclouds.openstack.marconi.v1.features.MessageApi;
import org.jclouds.openstack.marconi.v1.features.QueueApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Marconi is a robust, web-scale message queuing service to support the distributed nature of large web applications.
 */
public interface MarconiApi extends Closeable {
   /**
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides access to Queue features.
    *
    * @param region   The region where this queue will live.
    * @param clientId A UUID for each client instance. The UUID must be submitted in its canonical form (for example,
    *                 3381af92-2b9e-11e3-b191-71861300734c). The client generates the Client-ID once. Client-ID
    *                 persists between restarts of the client so the client should reuse that same Client-ID. All
    *                 message-related operations require the use of Client-ID in the headers to ensure that messages
    *                 are not echoed back to the client that posted them, unless the client explicitly requests this.
    */
   @Delegate
   QueueApi getQueueApi(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
         @HeaderParam("Client-ID") UUID clientId);

   /**
    * Provides access to Message features.
    *
    * @param region   The region where this queue lives.
    * @param clientId A UUID for each client instance. The UUID must be submitted in its canonical form (for example,
    *                 3381af92-2b9e-11e3-b191-71861300734c). The client generates the Client-ID once. Client-ID
    *                 persists between restarts of the client so the client should reuse that same Client-ID. All
    *                 message-related operations require the use of Client-ID in the headers to ensure that messages
    *                 are not echoed back to the client that posted them, unless the client explicitly requests this.
    * @param name     Name of the queue.
    */
   @Delegate
   @Path("/queues/{name}")
   MessageApi getMessageApi(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
         @HeaderParam("Client-ID") UUID clientId,
         @PathParam("name") String name);

   /**
    * Provides access to Claim features.
    *
    * @param region   The region where this queue lives.
    * @param clientId A UUID for each client instance. The UUID must be submitted in its canonical form (for example,
    *                 3381af92-2b9e-11e3-b191-71861300734c). The client generates the Client-ID once. Client-ID
    *                 persists between restarts of the client so the client should reuse that same Client-ID. All
    *                 message-related operations require the use of Client-ID in the headers to ensure that messages
    *                 are not echoed back to the client that posted them, unless the client explicitly requests this.
    * @param name     Name of the queue.
    */
   @Delegate
   @Path("/queues/{name}")
   ClaimApi getClaimApi(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
         @HeaderParam("Client-ID") UUID clientId,
         @PathParam("name") String name);

   /**
    * @return the Zone codes configured
    * @deprecated Please use {@link #getConfiguredRegions()} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Provides
   @Region
   Set<String> getConfiguredZones();

   /**
    * Provides access to Queue features.
    *
    * @param zone     The zone where this queue will live.
    * @param clientId A UUID for each client instance. The UUID must be submitted in its canonical form (for example,
    *                 3381af92-2b9e-11e3-b191-71861300734c). The client generates the Client-ID once. Client-ID
    *                 persists between restarts of the client so the client should reuse that same Client-ID. All
    *                 message-related operations require the use of Client-ID in the headers to ensure that messages
    *                 are not echoed back to the client that posted them, unless the client explicitly requests this.
    * @deprecated Please use {@link #getQueueApi(String, UUID)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   QueueApi getQueueApiForZoneAndClient(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone,
         @HeaderParam("Client-ID") UUID clientId);

   /**
    * Provides access to Message features.
    *
    * @param zone     The zone where this queue lives.
    * @param clientId A UUID for each client instance. The UUID must be submitted in its canonical form (for example,
    *                 3381af92-2b9e-11e3-b191-71861300734c). The client generates the Client-ID once. Client-ID
    *                 persists between restarts of the client so the client should reuse that same Client-ID. All
    *                 message-related operations require the use of Client-ID in the headers to ensure that messages
    *                 are not echoed back to the client that posted them, unless the client explicitly requests this.
    * @param name     Name of the queue.
    * @deprecated Please use {@link #getMessageApi(String, UUID, String)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   @Path("/queues/{name}")
   MessageApi getMessageApiForZoneAndClientAndQueue(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone,
         @HeaderParam("Client-ID") UUID clientId,
         @PathParam("name") String name);

   /**
    * Provides access to Claim features.
    *
    * @param zone     The zone where this queue lives.
    * @param clientId A UUID for each client instance. The UUID must be submitted in its canonical form (for example,
    *                 3381af92-2b9e-11e3-b191-71861300734c). The client generates the Client-ID once. Client-ID
    *                 persists between restarts of the client so the client should reuse that same Client-ID. All
    *                 message-related operations require the use of Client-ID in the headers to ensure that messages
    *                 are not echoed back to the client that posted them, unless the client explicitly requests this.
    * @param name     Name of the queue.
    * @deprecated Please use {@link #getClaimApi(String, UUID, String)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   @Path("/queues/{name}")
   ClaimApi getClaimApiForZoneAndClientAndQueue(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone,
         @HeaderParam("Client-ID") UUID clientId,
         @PathParam("name") String name);
}
