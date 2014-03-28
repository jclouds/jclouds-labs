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
import java.util.Date;

import org.jclouds.openstack.v2_0.domain.Link;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;

/**
 * Cloud Big Data Cluster.
 * Contains information about a Cloud Big Data cluster.
 * @see ClusterApi#create
 * @author Zack Shoylev
 */
public class Cluster extends CreateCluster {
   private final String id;
   private final Date created;
   private final Date updated;
   private final ScriptStatus postInitScriptStatus;
   private final float progress;
   private final Status status;
   private final ImmutableList<Link> links;

   @ConstructorProperties({
      "name", "clusterType", "flavorId", "nodeCount", "pointInitScript",
      "id", "created", "updated", "postInitScriptStatus", "progress", "status", "links"
   })
   protected Cluster(String name, String clusterType, String flavorId, int nodeCount, URI pointInitScript,
         String id, Date created, Date updated, ScriptStatus postInitScriptStatus, float progress, Status status, ImmutableList<Link> links) {
      super(name, clusterType, flavorId, nodeCount, pointInitScript);
      this.id = checkNotNull(id, "id must not be null");
      this.created = checkNotNull(created, "created must not be null");
      this.updated = checkNotNull(updated, "updated must not be null");
      this.postInitScriptStatus = postInitScriptStatus;
      this.progress = progress;
      this.status = checkNotNull(status, "status must not be null");
      this.links = checkNotNull(links, "links must not be null");
   }

   /**
    * @return the id for this cluster
    */
   public String getId() {
      return id;
   }

   /**
    * @return the timestamp when this cluster was created.
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return the timestamp when this cluster was updated. This can be empty.
    */
   public Date getUpdated() {
      return updated;
   }

   /**
    * @return The status of the execution of the init script.
    */
   public ScriptStatus getPostInitScriptStatus() {
      return postInitScriptStatus;
   }

   /**
    * @return The operation progress of this cluster (completion percent).
    * The way this completion is calculated is subject to change.
    * Currently this is calculated based on the number of nodes in the cluster and their progress through configuration:
    * <br>BUILDING: progress = 0.5 * configuring_count / len(self.nodes)
    * <br>CONFIGURING/RESIZING: progress = 0.5 + (0.5 * active_count / len(self.nodes))
    * <br>ACTIVE: progress = 1.0
    */
   public float getProgress() {
      return progress;
   }

   /**
    * @return the current status.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * @return the links for this cluster.
    */
   public ImmutableList<Link> getLinks() {
      return links;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), id, created, updated, postInitScriptStatus, progress, status, links);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Cluster that = Cluster.class.cast(obj);
      return Objects.equal(this.id, that.id) &&
            Objects.equal(this.created, that.created) &&
            Objects.equal(this.updated, that.updated) &&
            Objects.equal(this.postInitScriptStatus, that.postInitScriptStatus) &&
            Objects.equal(this.progress, that.progress) &&
            Objects.equal(this.status, that.status) &&
            Objects.equal(this.links, that.links) &&
            super.equals(obj);
   }

   protected ToStringHelper string() {
      return super.string()
            .add("id", id)
            .add("created", created)
            .add("updated", updated)
            .add("postInitScriptStatus", postInitScriptStatus)
            .add("progress", progress)
            .add("status", status)
            .add("links", links);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * Lists possible Cluster status.
    */
   public static enum Status {
      /**
       * The cluster is in the process of being created. Servers are being created and booted.
       */
      BUILDING,
      /**
       * The cluster is still in the process of being created. All servers are booted and are now being provisioned.
       */
      CONFIGURING,      
      /**
       * The cluster is operational. All nodes are provisioned and ready for use.
       */
      ACTIVE,
      /**
       * Cluster is changing configuration (resizing, etc) at customers request.
       */
      UPDATING,
      /**
       * Customer has requested an operation that failed but cluster is still operational. e.g. resize up but can't acquire more nodes
       */
      IMPAIRED,
      /**
       * Customer has requested the cluster be deleted but the operation hasn't yet completed.
       */
      DELETING,
      /**
       * The cluster is deleted.
       */
      DELETED,
      /**
       * A fatal error has occurred during cluster provisioning.
       */
      ERROR,
      /**
       * Unrecognized status response.
       */
      UNRECOGNIZED;

      @Override
      public String toString() {
         return name();
      }

      /**
       * @param status The string representation of a Status
       * @return The corresponding Status.
       */
      public static Status fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   /**
    * Lists possible Cluster script status.
    * This is the current status of the init script.
    */
   public static enum ScriptStatus {
      /**
       * The init script has failed.
       */
      FAILED,
      /**
       * The init script has not started executing.
       */
      PENDING,
      /**
       * The init script has been uploaded.
       */
      DELIVERED,
      /**
       * The init script is executing.
       */
      RUNNING,
      /**
       * The init script has finished successfully.
       */
      SUCCEEDED,
      /**
       * Unrecognized status response.
       */
      UNRECOGNIZED;

      @Override
      public String toString() {
         return name();
      }

      /**
       * @param scriptStatus The string representation of a ScriptStatus
       * @return The corresponding ScriptStatus.
       */
      public static ScriptStatus fromValue(String scriptStatus) {
         try {
            return valueOf(checkNotNull(scriptStatus, "scriptStatus"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }
}
