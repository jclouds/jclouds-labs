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
package org.jclouds.rackspace.clouddns.v1;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.features.DomainApi;
import org.jclouds.rackspace.clouddns.v1.features.Domains;
import org.jclouds.rackspace.clouddns.v1.features.LimitApi;
import org.jclouds.rackspace.clouddns.v1.features.RecordApi;
import org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides access to the Rackspace Cloud DNS API.
 * <p/>
 * See <a href="http://docs.rackspace.com/cdns/api/v1.0/cdns-devguide/content/index.html">Cloud DNS Developer Guide</a>
 *  
 * @see CloudDNSAsyncApi
 * @author Everett Toews
 */
public interface CloudDNSApi {
   /**
    * Returns the current status of a job.
    * </p>
    * Operations that create, update, or delete resources may take some time to process. Therefore they return 
    * a Job containing information, which allows the status and response information of the job to be 
    * retrieved at a later point in time.
    * </p>
    * You likely won't need to use this method directly. Use {@link Domains} or see {@link JobPredicates.JobStatusPredicate}. 
    *
    * @return null, if not found.
    */
   @Nullable
   <T> Job<T> getJob(String jobId);
   
   /**
    * Provides synchronous access to Limit features.
    */
   @Delegate
   LimitApi getLimitApi();

   /**
    * Provides synchronous access to Domain features.
    */
   @Delegate
   DomainApi getDomainApi();

   /**
    * Provides synchronous access to Record features.
    */
   @Delegate
   RecordApi getRecordApi();
}
