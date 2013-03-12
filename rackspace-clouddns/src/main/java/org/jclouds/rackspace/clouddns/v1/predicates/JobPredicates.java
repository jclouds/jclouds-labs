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
package org.jclouds.rackspace.clouddns.v1.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.CloudDNSExceptions;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.features.Domains;

import com.google.common.base.Predicate;

/**
 * Best to not use this class directly. See {@link Domains}
 * </p>
 * Tests to see if a Job has reached a status. This class is most useful when paired with a RetryablePredicate as
 * in the code below. This class can be used to block execution until the Job status has reached a desired state.
 * This is useful when your Job needs to be 100% ready before you can continue with execution.
 *
 * <pre>
 * {@code
 *    Job<Set<Domain>> job = cloudDNSApi.getDomainApi().create(createDomains);
 *    JobStatusPredicate<Set<Domain>> jobCompleted = new JobStatusPredicate<Set<Domain>>(cloudDNSApi, Job.Status.COMPLETED);
 *
 *    if (!retry(jobCompleted, 600, 2, 2, SECONDS).apply(job)) {
 *       throw new TimeoutException("Timeout on create domain: " + job);
 *    }
 *
 *    Set<Domain> domains = jobCompleted.getJob().getResource().get();
 * }
 * </pre>
 * 
 * @author Everett Toews
 */
public class JobPredicates {
   
   private JobPredicates() {
   }
   
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static Predicate<AtomicReference<? extends Job<?>>> jobCompleted(CloudDNSApi cloudDNSApi) {
      return new JobStatusPredicate(cloudDNSApi, Job.Status.COMPLETED);
   }
   
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static Predicate<AtomicReference<? extends Job<?>>> jobStatusEqualTo(CloudDNSApi cloudDNSApi, Job.Status status) {
      return new JobStatusPredicate(cloudDNSApi, status);
   }
   
   private static class JobStatusPredicate<T> implements Predicate<AtomicReference<Job<?>>> {
      private CloudDNSApi cloudDNSApi;
      private Job.Status status;

      private JobStatusPredicate(CloudDNSApi cloudDNSApi, Job.Status status) {
         this.cloudDNSApi = checkNotNull(cloudDNSApi, "domainApi must be defined");
         this.status = checkNotNull(status, "status must be defined");
      }
      
      /**
       * @return boolean Return true when the Job reaches status, false otherwise.
       */
      @Override
      public boolean apply(AtomicReference<Job<?>> jobRef) {
         checkNotNull(jobRef, "job must be defined");

         if (status.equals(jobRef.get().getStatus())) {
            return true;
         }
         else {
            jobRef.set(cloudDNSApi.getJob(jobRef.get().getId()));
            checkNotNull(jobRef.get(), "Job %s not found.", jobRef.get().getId());
            
            if (jobRef.get().getError().isPresent()) {
               throw new CloudDNSExceptions.JobErrorException(jobRef.get().getError().get());
            }
            
            return status.equals(jobRef.get().getStatus());
         }
      }
   }
}
