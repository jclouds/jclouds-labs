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
package org.jclouds.azurecompute.util;

import static org.jclouds.azurecompute.compute.AzureComputeServiceAdapter.generateIllegalStateExceptionMessage;

import com.google.common.base.Predicate;
import javax.annotation.Resource;
import javax.inject.Named;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;

/**
 * Conflict errors (409 response status code) management predicate.
 */
public abstract class ConflictManagementPredicate implements Predicate<String> {

   protected final Predicate<String> operationSucceeded;

   private Long timeout = null;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private boolean raiseException = false;

   /**
    * Constructor.
    *
    * @param operationSucceeded predicate to be applied to the requestId.
    */
   public ConflictManagementPredicate(final Predicate<String> operationSucceeded) {
      this.operationSucceeded = operationSucceeded;
   }

   /**
    * Constructor to be used to raise an IllegalStateException in case of the given predicate evaluation is false.
    *
    * @param operationSucceeded predicate to be applied to the requestId.
    * @param timeout timeout of the predicate.
    */
   public ConflictManagementPredicate(
           final Predicate<String> operationSucceeded,
           final Long timeout) {

      this.operationSucceeded = operationSucceeded;
      this.timeout = timeout;
      this.raiseException = true;
   }

   /**
    * Operation to be executed.
    *
    * @return requestId.
    */
   protected abstract String operation();

   /**
    * {@inheritDoc }
    *
    * @param name interested object/operaton descripton.
    * @return predicate evaluation.
    */
   @Override
   public final boolean apply(final String name) {
      try {
         final String requestId = operation();
         logger.info("Executed operation on {0}", name);

         if (requestId == null) {
            return true;
         }

         final boolean res = operationSucceeded.apply(requestId);
         if (!res && raiseException) {
            final String message = generateIllegalStateExceptionMessage(requestId, timeout);
            logger.warn(message);
            throw new IllegalStateException(message);
         } else {
            return res;
         }
      } catch (RuntimeException e) {
         final HttpResponseException re = (e instanceof HttpResponseException)
                 ? HttpResponseException.class.cast(e) : (e.getCause() instanceof HttpResponseException)
                         ? HttpResponseException.class.cast(e.getCause())
                         : null;
         if (re == null) {
            throw e;
         } else {
            final HttpResponse res = re.getResponse();
            logger.info("[{0} (core: {1})] while performing operation on {2}",
                    new Object[]{res.getStatusLine(), res.getStatusCode(), name});
            if (res.getStatusCode() == 409) {
               logger.info("[{0}] Retry operation on {1}", new Object[]{e.getMessage(), name});
               return false;
            } else {
               throw re;
            }
         }
      }
   }
}
