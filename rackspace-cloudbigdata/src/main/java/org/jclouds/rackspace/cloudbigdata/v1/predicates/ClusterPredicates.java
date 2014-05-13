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

package org.jclouds.rackspace.cloudbigdata.v1.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import org.jclouds.rackspace.cloudbigdata.v1.domain.Cluster;
import org.jclouds.rackspace.cloudbigdata.v1.domain.Cluster.Status;
import org.jclouds.rackspace.cloudbigdata.v1.features.ClusterApi;

import com.google.common.base.Predicate;

/**
 * Tests to see if Cluster has reached status. This class is most useful when paired with a RetryablePredicate as
 * in the code below. This class can be used to block execution until the Cluster status has reached a desired state.
 * This is useful when your Cluster needs to be 100% ready before you can continue with execution.
 *
 * <pre>
 * {@code
 * Cluster Cluster = ClusterApi.create(100);
 * 
 * RetryablePredicate<String> awaitAvailable = RetryablePredicate.create(
 *    ClusterPredicates.available(ClusterApi), 600, 10, 10, TimeUnit.SECONDS);
 * 
 * if (!awaitAvailable.apply(Cluster.getId())) {
 *    throw new TimeoutException("Timeout on Cluster: " + Cluster); 
 * }    
 * }
 * </pre>
 * 
 * You can also use the static convenience methods as follows.
 * 
 * <pre>
 * {@code
 * Cluster Cluster = ClusterApi.create(100);
 * 
 * if (!ClusterPredicates.awaitAvailable(ClusterApi).apply(Cluster.getId())) {
 *    throw new TimeoutException("Timeout on Cluster: " + Cluster);     
 * }
 * }
 * </pre>
 * 
 * @author Zack Shoylev
 */
public class ClusterPredicates {
   /**
    * Wait until an Cluster is Available.
    * 
    * @param clusterApi The ClusterApi in the zone where your Cluster resides.
    * @return RetryablePredicate That will check the status every 5 seconds for a maxiumum of 10 minutes.
    */
   public static Predicate<Cluster> awaitAvailable(ClusterApi clusterApi) {
      StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(clusterApi, Cluster.Status.ACTIVE);
      return retry(statusPredicate, 600, 5, 5, SECONDS);
   }

   /**
    * Wait until an Cluster no longer exists.
    * 
    * @param clusterApi The ClusterApi in the zone where your Cluster resides.
    * @return RetryablePredicate That will check whether the Cluster exists.
    * every 5 seconds for a maximum of 10 minutes.
    */
   public static Predicate<Cluster> awaitDeleted(ClusterApi clusterApi) {
      DeletedPredicate deletedPredicate = new DeletedPredicate(clusterApi);
      return retry(deletedPredicate, 600, 5, 5, SECONDS);
   }
   
   /**
    * Wait until Cluster is in the status specified.
    * 
    * @param clusterApi The ClusterApi in the zone where your Cluster resides.
    * @param status Wait until Cluster in in this status.
    * @param maxWaitInSec Maximum time to wait.
    * @param periodInSec Interval between retries.
    * @return RetryablePredicate That will check whether the Cluster exists.
    */
   public static Predicate<Cluster> awaitStatus(
         ClusterApi clusterApi, Cluster.Status status, long maxWaitInSec, long periodInSec) {
      StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(clusterApi, status);
      return retry(statusPredicate, maxWaitInSec, periodInSec, periodInSec, SECONDS);
   }
   
   private static class StatusUpdatedPredicate implements Predicate<Cluster> {
      private ClusterApi clusterApi;
      private Status status;

      public StatusUpdatedPredicate(ClusterApi clusterApi, Cluster.Status status) {
         this.clusterApi = checkNotNull(clusterApi, "ClusterApi must be defined");
         this.status = checkNotNull(status, "status must be defined");
      }

      /**
       * @return boolean Return true when the Cluster reaches status, false otherwise.
       */
      @Override
      public boolean apply(Cluster cluster) {
         checkNotNull(cluster, "Cluster must be defined");
         
         if (status.equals(cluster.getStatus())) {
            return true;
         }
         else {
            Cluster ClusterUpdated = clusterApi.get(cluster.getId());
            checkNotNull(ClusterUpdated, "Cluster %s not found.", cluster.getId());
            
            return status.equals(ClusterUpdated.getStatus());
         }
      }
   }

   private static class DeletedPredicate implements Predicate<Cluster> {
      private ClusterApi clusterApi;

      public DeletedPredicate(ClusterApi clusterApi) {
         this.clusterApi = checkNotNull(clusterApi, "ClusterApi must be defined");
      }

      /**
       * @return boolean Return true when the snapshot is deleted, false otherwise.
       */
      @Override
      public boolean apply(Cluster cluster) {
         checkNotNull(cluster, "Cluster must be defined");

         return clusterApi.get(cluster.getId()) == null;
      }
   }
}
