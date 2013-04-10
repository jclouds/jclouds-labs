/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rackspace.clouddns.v1.features;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates.jobCompleted;
import static org.jclouds.util.Predicates2.retry;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.domain.CreateDomain;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.UpdateDomain;

import com.google.common.util.concurrent.Atomics;

/**
 * A convenience class for working with Domains.
 * 
 * @author Everett Toews
 */
public class Domains {

   /**
    * As per {@link DomainApi#create(Iterable)} but waits for the job to complete.
    */
   public static Set<Domain> create(CloudDNSApi cloudDNSApi, Iterable<CreateDomain> createDomains)
         throws TimeoutException {
      AtomicReference<Job<Set<Domain>>> jobRef = Atomics.newReference(cloudDNSApi.getDomainApi().create(createDomains));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on create domain: " + jobRef.get());
      }

      return jobRef.get().getResource().get();
   }

   /**
    * As per {@link DomainApi#update(int, UpdateDomain)} but waits for the job to complete.
    */
   public static void update(CloudDNSApi cloudDNSApi, int id, UpdateDomain updateDomain) throws TimeoutException {
      AtomicReference<Job<Void>> jobRef = Atomics.newReference(cloudDNSApi.getDomainApi().update(id, updateDomain));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on update domain: " + jobRef.get());
      }
   }

   /**
    * As per {@link DomainApi#updateTTL(Iterable, int) but waits for the job to complete.
    */
   public static void updateTTL(CloudDNSApi cloudDNSApi, Iterable<Integer> ids, int ttl) throws TimeoutException {
      AtomicReference<Job<Void>> jobRef = Atomics.newReference(cloudDNSApi.getDomainApi().updateTTL(ids, ttl));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on update domain: " + jobRef.get());
      }
   }

   /**
    * As per {@link DomainApi#updateEmail(Iterable, String)} but waits for the job to complete.
    */
   public static void updateEmail(CloudDNSApi cloudDNSApi, Iterable<Integer> ids, String email) throws TimeoutException {
      AtomicReference<Job<Void>> jobRef = Atomics.newReference(cloudDNSApi.getDomainApi().updateEmail(ids, email));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on update domain: " + jobRef.get());
      }
   }

   /**
    * As per {@link DomainApi#delete(Iterable, boolean)} but waits for the job to complete.
    */
   public static void delete(CloudDNSApi cloudDNSApi, Iterable<Integer> domainIds) throws TimeoutException {
      AtomicReference<Job<Void>> jobRef = Atomics.newReference(cloudDNSApi.getDomainApi().delete(domainIds, true));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on delete domain: " + jobRef.get());
      }
   }

   /**
    * As per {@link DomainApi#exportFormat(int, Domain.Format)} but waits for the job to complete.
    */
   public static List<String> exportFormat(CloudDNSApi cloudDNSApi, int domainId, Domain.Format format)
         throws TimeoutException {
      AtomicReference<Job<List<String>>> jobRef = Atomics.newReference(cloudDNSApi.getDomainApi().exportFormat(
            domainId, format));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on export domain: " + jobRef.get());
      }

      return jobRef.get().getResource().get();
   }

   /**
    * As per {@link DomainApi#importFormat(List, Domain.Format)} but waits for the job to complete.
    */
   public static Domain importFormat(CloudDNSApi cloudDNSApi, List<String> contents, Domain.Format format)
         throws TimeoutException {
      AtomicReference<Job<Domain>> jobRef = Atomics.newReference(cloudDNSApi.getDomainApi().importFormat(contents,
            format));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on export domain: " + jobRef.get());
      }

      return jobRef.get().getResource().get();
   }
}
