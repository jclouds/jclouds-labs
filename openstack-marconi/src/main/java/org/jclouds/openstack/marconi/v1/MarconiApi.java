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

import com.google.inject.Provides;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.marconi.v1.features.MessageApi;
import org.jclouds.openstack.marconi.v1.features.QueueApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.Closeable;
import java.util.Set;

/**
 * Marconi is a robust, web-scale message queuing service to support the distributed nature of large web applications.
 *
 * @author Everett Toews
 */
public interface MarconiApi extends Closeable {
   /**
    * @return The Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides access to Queue features.
    *
    * @param zone The zone where this queue will live.
    */
   @Delegate
   QueueApi getQueueApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Message features.
    *
    * @param zone The zone where this queue will live.
    * @param name Name of the queue.
    */
   @Delegate
   @Path("/queues/{name}")
   MessageApi getMessageApiForZoneAndQueue(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("name") String name);
}
