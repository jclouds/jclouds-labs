/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.reddwarf.v1.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import org.jclouds.openstack.reddwarf.v1.domain.Instance;
import org.jclouds.openstack.reddwarf.v1.domain.Instance.Status;
import org.jclouds.openstack.reddwarf.v1.features.InstanceApi;

import com.google.common.base.Predicate;

/**
 * Tests to see if instance has reached status. This class is most useful when paired with a RetryablePredicate as
 * in the code below. This class can be used to block execution until the Instance status has reached a desired state.
 * This is useful when your Instance needs to be 100% ready before you can continue with execution.
 *
 * <pre>
 * {@code
 * Instance instance = instanceApi.create(100);
 * 
 * RetryablePredicate<String> awaitAvailable = RetryablePredicate.create(
 *    InstancePredicates.available(instanceApi), 600, 10, 10, TimeUnit.SECONDS);
 * 
 * if (!awaitAvailable.apply(instance.getId())) {
 *    throw new TimeoutException("Timeout on instance: " + instance); 
 * }    
 * }
 * </pre>
 * 
 * You can also use the static convenience methods as so.
 * 
 * <pre>
 * {@code
 * Instance instance = instanceApi.create(100);
 * 
 * if (!InstancePredicates.awaitAvailable(instanceApi).apply(instance.getId())) {
 *    throw new TimeoutException("Timeout on instance: " + instance);     
 * }
 * }
 * </pre>
 * 
 * @author Zack Shoylev
 */
public class InstancePredicates {
   /**
    * Wait until an Instance is Available.
    * 
    * @param instanceApi The InstanceApi in the zone where your Instance resides.
    * @return RetryablePredicate That will check the status every 5 seconds for a maxiumum of 10 minutes.
    */
   public static Predicate<Instance> awaitAvailable(InstanceApi instanceApi) {
      StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(instanceApi, Instance.Status.ACTIVE);
      return retry(statusPredicate, 600, 5, 5, SECONDS);
   }

   /**
    * Wait until an Instance no longer exists.
    * 
    * @param instanceApi The InstanceApi in the zone where your Instance resides.
    * @return RetryablePredicate That will check the whether the Instance exists 
    * every 5 seconds for a maxiumum of 10 minutes.
    */
   public static Predicate<Instance> awaitDeleted(InstanceApi instanceApi) {
      DeletedPredicate deletedPredicate = new DeletedPredicate(instanceApi);
      return retry(deletedPredicate, 600, 5, 5, SECONDS);
   }
   
   public static Predicate<Instance> awaitStatus(
         InstanceApi instanceApi, Instance.Status status, long maxWaitInSec, long periodInSec) {
      StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(instanceApi, status);
      return retry(statusPredicate, maxWaitInSec, periodInSec, periodInSec, SECONDS);
   }
   
   private static class StatusUpdatedPredicate implements Predicate<Instance> {
      private InstanceApi instanceApi;
      private Status status;

      public StatusUpdatedPredicate(InstanceApi instanceApi, Instance.Status status) {
         this.instanceApi = checkNotNull(instanceApi, "instanceApi must be defined");
         this.status = checkNotNull(status, "status must be defined");
      }

      /**
       * @return boolean Return true when the instance reaches status, false otherwise
       */
      @Override
      public boolean apply(Instance instance) {
         checkNotNull(instance, "instance must be defined");
         
         if (status.equals(instance.getStatus())) {
            return true;
         }
         else {
            Instance instanceUpdated = instanceApi.get(instance.getId());
            checkNotNull(instanceUpdated, "Instance %s not found.", instance.getId());
            
            return status.equals(instanceUpdated.getStatus());
         }
      }
   }

   private static class DeletedPredicate implements Predicate<Instance> {
      private InstanceApi instanceApi;

      public DeletedPredicate(InstanceApi instanceApi) {
         this.instanceApi = checkNotNull(instanceApi, "instanceApi must be defined");
      }

      /**
       * @return boolean Return true when the snapshot is deleted, false otherwise
       */
      @Override
      public boolean apply(Instance instance) {
         checkNotNull(instance, "instance must be defined");

         return instanceApi.get(instance.getId()) == null;
      }
   }
}
