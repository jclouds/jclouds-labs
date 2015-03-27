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
package org.jclouds.openstack.poppy.v1.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import org.jclouds.openstack.poppy.v1.domain.Service;
import org.jclouds.openstack.poppy.v1.domain.ServiceStatus;
import org.jclouds.openstack.poppy.v1.features.ServiceApi;
import com.google.common.base.Predicate;

public class ServicePredicates {
   public static Predicate<Service> awaitDeployed(ServiceApi serviceApi) {
      ServiceStatusPredicate statusPredicate = new ServiceStatusPredicate(serviceApi, ServiceStatus.DEPLOYED);
      return retry(statusPredicate, 1200, 15, 15, SECONDS);
   }

   private static class ServiceStatusPredicate implements Predicate<Service> {
      private ServiceApi serviceApi;
      private ServiceStatus status;

      public ServiceStatusPredicate(ServiceApi instanceApi, ServiceStatus status) {
         this.serviceApi = checkNotNull(instanceApi, "serviceApi must be defined");
         this.status = checkNotNull(status, "status must be defined");
      }

      /**
       * @return boolean Return true when the service reaches status, false otherwise.
       */
      @Override
      public boolean apply(Service service) {
         checkNotNull(service, "service must be defined");

         if (status.equals(service.getStatus())) {
            return true;
         }
         else {
            Service serviceUpdated = serviceApi.get(service.getId());
            checkNotNull(serviceUpdated, "service %s not found.", service.getId());

            return status.equals(serviceUpdated.getStatus());
         }
      }
   }
}
