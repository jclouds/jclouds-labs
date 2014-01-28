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
package org.jclouds.rackspace.cloudbigdata.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;

import org.jclouds.rackspace.cloudbigdata.v1.features.ClusterApi;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Cloud Big Data CreateCluster.
 * This is used to describe how to create a Cloud Big Data cluster, a collection of machines that can be used to run distributed data processing.
 * This class implements the Builder pattern.
 * @see ClusterApi#create
 * @author Zack Shoylev
 */
public class CreateCluster implements Comparable<CreateCluster> {
   private final String name;
   private final String clusterType;
   private final String flavorId;
   private final int nodeCount;
   private final URI postInitScript;
   

   @ConstructorProperties({
      "name", "clusterType", "flavorId", "nodeCount", "postInitScript"
   })
   protected CreateCluster(String name, String clusterType, String flavorId, int nodeCount, URI postInitScript) {
      this.name = checkNotNull(name, "name required");
      this.clusterType = checkNotNull(clusterType, "clusterType required");
      this.flavorId = checkNotNull(flavorId, "flavorId required");
      this.nodeCount = nodeCount;
      this.postInitScript = postInitScript; // Not necessarily present in the response
   }

   /**
    * @return the name for this cluster
    * @see CreateCluster.Builder#name(String)
    */
   public String getName() {
      return name;
   }
   
   /**
    * @return the ClusterType for this cluster
    * @see CreateCluster.Builder#clusterType(String)
    */
   public String getClusterType() {
      return clusterType;
   }
   
   /**
    * @return the flavor id for this cluster. All cluster machines will use this flavor.
    * @see CreateCluster.Builder#flavorId(String)
    */
   public String getFlavorId() {
      return flavorId;
   }
   
   /**
    * @return the number of nodes on this cluster.
    * @see CreateCluster.Builder#nodeCount(int)
    */
   public int getNodeCount() {
      return nodeCount;
   }
   
   /**
    * @return the URI to the init script. Example: http://example.com/configure_cluster.sh
    * @see CreateCluster.Builder#postInitScript(URI)
    */
   public URI getPostInitScript() {
      return postInitScript;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, clusterType, flavorId, nodeCount, postInitScript);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      CreateCluster that = CreateCluster.class.cast(obj);
      return Objects.equal(this.name, that.name) && 
            Objects.equal(this.clusterType, that.clusterType) &&
            Objects.equal(this.flavorId, that.flavorId) &&
            Objects.equal(this.nodeCount, that.nodeCount) &&
            Objects.equal(this.postInitScript, that.postInitScript);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name)
            .add("clusterType", clusterType)
            .add("flavorId", flavorId)
            .add("nodeCount", nodeCount)
            .add("postInitScript", postInitScript);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * @return The builder object for this class.
    * This Builder is then used to create a CreateCluster object, which contains all information needed to configure and setup a cluster.
    */
   public static Builder builder() { 
      return new Builder();
   }

   /**
    * @return The Builder object. Extracts a Builder object that can be used to generate another CreateCluster object. 
    */
   public Builder toBuilder() { 
      return new Builder().fromCreateCluster(this);
   }

   /**
    * Implements the Builder pattern.
    */
   public static class Builder {
      protected String name;
      protected String clusterType;
      protected String flavorId;
      protected int nodeCount;
      protected URI postInitScript;

      /** 
       * @param name The name of this Cluster.
       * @return The builder object.
       * @see CreateCluster#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /** 
       * @param clusterType The type of this Cluster. Multiple types are supported. These types might change. Supported cluster types can be discovered.
       * @return The builder object.
       * @see CreateCluster#getClusterType()
       */
      public Builder clusterType(String clusterType) {
         this.clusterType = clusterType;
         return this;
      }
      
      /** 
       * @param flavorId The flavor id to be used for this cluster. This specifies an identifier for the type of machine to be used for each cluster node.
       * @return The builder object.
       * @see CreateCluster#getFlavorId()
       */
      public Builder flavorId(String flavorId) {
         this.flavorId = flavorId;
         return this;
      }
      
      /** 
       * @param nodeCount The size of this cluster. This size might be limited by your account quota. This is the number of nodes of the type specified by flavorId.
       * @return The builder object.
       * @see CreateCluster#getNodeCount()
       */
      public Builder nodeCount(int nodeCount) {
         this.nodeCount = nodeCount;
         return this;
      }
      
      /** 
       * @param postInitScript The URI to the init script. An init script can be executed on your cluster while configuring. This is used to specify the location of that script.
       * @return The builder object.
       * @see CreateCluster#getPostInitScript()
       */
      public Builder postInitScript(URI postInitScript) {
         this.postInitScript = postInitScript;
         return this;
      }

      /**
       * @return A new CreateCluster object.
       */
      public CreateCluster build() {
         return new CreateCluster(name, clusterType, flavorId, nodeCount, postInitScript);
      }

      /**
       * @param in The CreateCluster
       * @return The CreateCluster Builder
       */
      public Builder fromCreateCluster(CreateCluster in) {
         return this
               .name(in.getName())
               .clusterType(in.getClusterType())
               .flavorId(in.getFlavorId())
               .nodeCount(in.getNodeCount())
               .postInitScript(in.getPostInitScript());
      }        
   }

   @Override
   public int compareTo(CreateCluster that) {
      return this.getName().compareTo(that.getName());
   }

   /**
    * Enumerates different types of clusters.
    * This is just an example. Supported cluster types can be discovered through the API.
    */
   public static enum ClusterType {
      /**
       * Hadoop Hortonworks Data Platform 1.1
       * No longer supported.
       */
      HADOOP_HDP1_1,
      /**
       * Hadoop Hortonworks Data Platform 1.3
       */
      HADOOP_HDP1_3,
      /**
       * HBase Hortonworks Data Platform 1.1
       * Currently not enabled.
       */
      HBASE_HDP1_1,
      /**
       * Unrecognized value.
       */
      UNRECOGNIZED;

      @Override
      public String toString() {
         return name();
      }

      /**
       * @param type The string representation of a ClusterType
       * @return The corresponding ClusterType.
       */
      public static ClusterType fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }
}
