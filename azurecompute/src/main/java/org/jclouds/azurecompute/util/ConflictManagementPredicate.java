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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.azurecompute.domain.Operation.Status.FAILED;

import com.google.common.base.Predicate;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.domain.Operation;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.util.Predicates2;

/**
 * Conflict errors (409 response status code) management predicate.
 */
public class ConflictManagementPredicate implements Predicate<String> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private AzureComputeApi api;

   private Predicate<String> operationSucceeded;

   private final long timeout = 600000;
   private final long interval = 15000;

   public ConflictManagementPredicate() {
      this(null);
   }

   /**
    * Constructor.
    *
    * @param api azure api.
    */
   public ConflictManagementPredicate(final AzureComputeApi api) {
      this(api, Predicates2.retry(new OperationSucceededPredicate(api), 600, 5, 5, SECONDS));
   }

   /**
    * Constructor.
    *
    * @param api azure api.
    * @param timeout predicate timeout.
    * @param period predicate period.
    * @param maxPeriod max period
    * @param unit timeout and period time unit.
    */
   public ConflictManagementPredicate(
           final AzureComputeApi api, long timeout, long period, long maxPeriod, TimeUnit unit) {
      this(api, Predicates2.retry(new OperationSucceededPredicate(api), timeout, period, maxPeriod, unit));
   }

   /**
    * Constructor.
    *
    * @param api azure api.
    * @param operationSucceeded predicate to be applied to the requestId.
    */
   public ConflictManagementPredicate(final AzureComputeApi api, final Predicate<String> operationSucceeded) {
      this.api = api;
      this.operationSucceeded = operationSucceeded;
   }

   /**
    * Operation to be executed.
    *
    * @return requestId.
    */
   protected String operation() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc }
    *
    * @param input interested object/operaton descripton or requestId.
    * @return predicate evaluation.
    */
   @Override
   public final boolean apply(final String input) {
      Operation operation = null;
      String requestId = null;

      boolean retry = true;

      long now = System.currentTimeMillis();
      long end = now + timeout;

      while (retry && now < end) {
         try {
            requestId = operation();
            logger.debug("Executed operation on %s", input);

            // If request id is not available let's assume operation succeeded.
            if (requestId == null) {
               logger.debug("No request id available. Assume operation succeeded.");
               return true;
            }

            operation = api.getOperationApi().get(requestId);
            logger.debug("Operation %s status: %s", operation.id(), operation.status().name());

            if (operation.status() == FAILED) {
               // rise an exception based on HTTP status code
               if (operation.httpStatusCode() == 409 || operation.httpStatusCode() == 500) {
                  logger.info("Retry operation %s with (code %d)", operation.id(), operation.httpStatusCode());
               } else {
                  logger.info("Not retriable operation %s (code %d)", operation.id(), operation.httpStatusCode());
                  retry = false;
               }
            } else {
               logger.debug("Tracking for operation %s ...", operation.id());
               retry = false;
            }
         } catch (UnsupportedOperationException e) {
            requestId = input;
            retry = false;
            logger.debug("Tracking for operation %s ...", input);
         } catch (RuntimeException e) {
            final HttpResponseException re = (e instanceof HttpResponseException)
                    ? HttpResponseException.class.cast(e) : (e.getCause() instanceof HttpResponseException)
                            ? HttpResponseException.class.cast(e.getCause())
                            : null;
            if (re == null) {
               throw e;
            } else {
               final HttpResponse res = re.getResponse();
               logger.info("[%s (%d)] Performing operation on %s", res.getStatusLine(), res.getStatusCode(), input);
               if (res.getStatusCode() == 409 || res.getStatusCode() == 500) {
                  logger.info("Retry operation %s", operation == null ? "" : operation.id(), res.getStatusCode());
               } else {
                  throw re;
               }
            }
         }

         if (retry) {
            try {
               Thread.sleep(interval);
            } catch (InterruptedException ex) {
               // ignore
            }

            now = System.currentTimeMillis();
         }
      }

      if (now >= end) {
         throw new RuntimeException(new TimeoutException(requestId));
      }

      return operationSucceeded.apply(requestId);
   }

   private static class OperationSucceededPredicate implements Predicate<String> {

      private final AzureComputeApi api;

      public OperationSucceededPredicate(final AzureComputeApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(final String input) {
         final Operation operation = api.getOperationApi().get(input);
         switch (operation.status()) {
            case SUCCEEDED:
               return true;

            case IN_PROGRESS:
            case UNRECOGNIZED:
               return false;

            case FAILED:
               throw new RuntimeException(new CancellationException(input));

            default:
               throw new IllegalStateException("Operation is in invalid status: " + operation.status().name());
         }
      }
   }
}
