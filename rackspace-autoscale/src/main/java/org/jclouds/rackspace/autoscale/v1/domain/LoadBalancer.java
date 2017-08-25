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
package org.jclouds.rackspace.autoscale.v1.domain;

import static com.google.common.base.Preconditions.checkArgument;

import java.beans.ConstructorProperties;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.gson.annotations.SerializedName;

/**
 * Autoscale LoadBalancer. Part of the launch configuration.
 *
 * @see LaunchConfiguration#getLoadBalancers()
 */
public class LoadBalancer {
   private final int port;
   @SerializedName("loadBalancerId")
   private final int id;

   @ConstructorProperties({ "port", "loadBalancerId" })
   protected LoadBalancer(int port, int id) {
      checkArgument(port >= 0, "port should be non-negative");
      this.port = port;
      checkArgument(id >= 0, "id should be non-negative");
      this.id = id;
   }

   /**
    * @return the socket port number of this LoadBalancer.
    * @see LoadBalancer.Builder#port(int)
    */
   public int getPort() {
      return this.port;
   }

   /**
    * @return the id for this LoadBalancer.
    * @see LoadBalancer.Builder#id(int)
    */
   public int getId() {
      return this.id;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(port, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      LoadBalancer that = LoadBalancer.class.cast(obj);
      return Objects.equal(this.port, that.port) && Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return MoreObjects.toStringHelper(this).add("port", port).add("id", id);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromLoadBalancer(this);
   }

   public static class Builder {
      protected int port;
      protected int id;

      /**
       * Required.
       * The port number of the service (on the new servers) to use for this particular load balancer. In most cases,
       * this port number is 80. NOTE that when using RackConnectV3, instead of a cloud load balancer, leave this
       * parameter empty.
       *
       * @param port The port of this LoadBalancer.
       * @return The builder object.
       * @see LoadBalancer#getPort()
       */
      public Builder port(int port) {
         this.port = port;
         return this;
      }

      /**
       * Required.
       * The ID of the cloud load balancer, or RackConnectV3 load balancer pool, to which new servers are added. For
       * cloud load balancers set the ID as an integer, for RackConnectV3 set the UUID as a string. NOTE that when
       * using RackConnectV3, this value is supplied to you by Rackspace Support after they configure your load
       * balancer pool.
       *
       * @param id The id of this LoadBalancer.
       * @return The builder object.
       * @see LoadBalancer#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @return A new LoadBalancer object.
       */
      public LoadBalancer build() {
         return new LoadBalancer(port, id);
      }

      public Builder fromLoadBalancer(LoadBalancer in) {
         return this.port(in.getPort()).id(in.getId());
      }
   }
}
