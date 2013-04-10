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
package org.jclouds.rackspace.clouddns.v1.features;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates.jobCompleted;
import static org.jclouds.util.Predicates2.retry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Atomics;

/**
 * A convenience class for working with Records.
 * 
 * @author Everett Toews
 */
public class Records {

   /**
    * As per {@link RecordApi#create(Iterable)} but waits for the job to complete.
    */
   public static Set<RecordDetail> create(CloudDNSApi cloudDNSApi, int domainId, Iterable<Record> createRecords)
         throws TimeoutException {
      AtomicReference<Job<Set<RecordDetail>>> jobRef = Atomics.newReference(cloudDNSApi
            .getRecordApiForDomain(domainId).create(createRecords));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on create record: " + jobRef.get());
      }

      return jobRef.get().getResource().get();
   }

   /**
    * As per {@link RecordApi#update(String, Record)} but waits for the job to complete.
    */
   public static void update(CloudDNSApi cloudDNSApi, int domainId, String recordId, Record record)
         throws TimeoutException {
      AtomicReference<Job<Void>> jobRef = Atomics.newReference(cloudDNSApi.getRecordApiForDomain(domainId).update(
            recordId, record));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on update record: " + jobRef.get());
      }
   }

   /**
    * As per {@link RecordApi#update(Map)} but waits for the job to complete.
    */
   public static void update(CloudDNSApi cloudDNSApi, int domainId, Map<String, Record> updateRecords)
         throws TimeoutException {
      AtomicReference<Job<Void>> jobRef = Atomics.newReference(cloudDNSApi.getRecordApiForDomain(domainId).update(
            updateRecords));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on update records: " + jobRef.get());
      }
   }

   /**
    * As per {@link RecordApi#delete(String) but waits for the job to complete.
    */
   public static void delete(CloudDNSApi cloudDNSApi, int domainId, String recordId) throws TimeoutException {
      delete(cloudDNSApi, domainId, ImmutableList.<String> of(recordId));
   }

   /**
    * As per {@link RecordApi#delete(Iterable) but waits for the job to complete.
    */
   public static void delete(CloudDNSApi cloudDNSApi, int domainId, Iterable<String> recordIds) throws TimeoutException {
      AtomicReference<Job<Void>> jobRef = Atomics.newReference(cloudDNSApi.getRecordApiForDomain(domainId).delete(
            recordIds));

      if (!retry(jobCompleted(cloudDNSApi), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on delete records: " + jobRef.get());
      }
   }
}
