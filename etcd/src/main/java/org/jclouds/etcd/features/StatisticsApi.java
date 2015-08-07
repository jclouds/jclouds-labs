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

package org.jclouds.etcd.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.etcd.domain.statistics.Leader;
import org.jclouds.etcd.domain.statistics.Self;
import org.jclouds.etcd.domain.statistics.Store;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/{jclouds.api-version}/stats")
public interface StatisticsApi {

   /**
    * @return information on leader and entire cluster but only if WE are the
    *         leader
    */
   @Named("statistics:leader")
   @Path("/leader")
   @GET
   Leader leader();

   /**
    * @return information on node we are currently pointing at
    */
   @Named("statistics:self")
   @Path("/self")
   @GET
   Self self();

   /**
    * @return information about operations this node has handled
    */
   @Named("statistics:store")
   @Path("/store")
   @GET
   Store store();
}
