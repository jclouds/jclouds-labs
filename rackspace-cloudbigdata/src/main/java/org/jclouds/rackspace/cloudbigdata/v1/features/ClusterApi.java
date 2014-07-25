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
package org.jclouds.rackspace.cloudbigdata.v1.features;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.cloudbigdata.v1.domain.Cluster;
import org.jclouds.rackspace.cloudbigdata.v1.domain.CreateCluster;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.collect.FluentIterable;

/**
 * The API for controlling clusters.
 * A cluster is a group of servers (nodes). In Cloud Big Data, the servers are virtual.
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
@Path("/clusters")
public interface ClusterApi extends Closeable {

   /**
    * Create a Cluster.
    * Before creating a cluster, a profile has to be created.
    * @param cluster An object containing information about the cluster to be created.
    * @return Cluster The cluster created by this call.
    * @see Cluster
    * @see CreateCluster
    */
   @Named("cluster:create")
   @POST
   @SelectJson("cluster")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Cluster create(@WrapWith("cluster") CreateCluster cluster);

   /**
    * List all clusters.
    * @return A list containing information about all the clusters.
    * @see Cluster
    */
   @Named("cluster:list")
   @GET
   @SelectJson("clusters")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Cluster> list();

   /**
    * Get information about a specific cluster.
    * @param clusterId The id of the cluster queried.
    * @return Detailed information about a specific cluster.
    * @see Cluster
    */
   @Named("cluster:get")
   @GET
   @Path("/{clusterId}")
   @SelectJson("cluster")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Cluster get(@PathParam("clusterId") String clusterId);

   /**
    * Delete a cluster.
    * @param clusterId The id of the cluster to be deleted.
    * @return Detailed information about the cluster to be deleted.
    * @see Cluster
    */
   @Named("cluster:delete")
   @DELETE
   @Path("/{clusterId}")
   @SelectJson("cluster")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Cluster delete(@PathParam("clusterId") String clusterId);

   /**
    * Resize a cluster. Changes the number of nodes for this cluster.
    * @param clusterId The id of the cluster to be deleted.
    * @param nodeCount The target number of cluster nodes.
    * @return Detailed information about the cluster to be resized.
    * @see Cluster
    */
   @Named("cluster:resize")
   @POST
   @Path("/{clusterId}/action")
   @WrapWith("resize")
   @SelectJson("cluster")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Cluster resize(@PathParam("clusterId") String clusterId, @PayloadParam("nodeCount") int nodeCount);
}
