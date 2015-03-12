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
package org.jclouds.azurecompute.binders;

import static com.google.common.base.Throwables.propagate;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.jamesmurty.utils.XMLBuilder;
import org.jclouds.azurecompute.domain.ProfileDefinitionEndpointParams;
import org.jclouds.azurecompute.domain.ProfileDefinitionParams;

public final class ProfileDefinitionParamsToXML implements Binder {

   @Override
   @SuppressWarnings("unchecked")
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      final ProfileDefinitionParams params = ProfileDefinitionParams.class.cast(input);
      try {
         XMLBuilder bld = XMLBuilder.create("Definition", "http://schemas.microsoft.com/windowsazure");
         bld.e("DnsOptions").e("TimeToLiveInSeconds").t(params.ttl().toString()).up().up();

         bld.e("Monitors").e("Monitor").e("IntervalInSeconds").t("30").up()
                 .e("TimeoutInSeconds").t("10").up()
                 .e("ToleratedNumberOfFailures").t("3").up()
                 .e("Protocol").t(params.protocol().name()).up()
                 .e("Port").t(params.port().toString()).up()
                 .e("HttpOptions")
                 .e("Verb").t("GET").up()
                 .e("RelativePath").t(params.path()).up()
                 .e("ExpectedStatusCode").t("200");

         bld = bld.e("Policy").e("LoadBalancingMethod").t(params.lb().getValue()).up()
                 .e("Endpoints");

         for (ProfileDefinitionEndpointParams endpoint : params.endpoints()) {
            bld = bld.e("Endpoint")
                    .e("DomainName").t(endpoint.domain()).up()
                    .e("Status").t(endpoint.status().getValue()).up();

            if (endpoint.type() != null) {
               bld = bld.e("Type").t(endpoint.type().getValue()).up();
            }
            if (endpoint.location() != null) {
               bld = bld.e("Location").t(endpoint.location()).up();
            }
            if (endpoint.min() != null) {
               bld = bld.e("MinChildEndpoints").t(endpoint.min().toString()).up();
            }
            if (endpoint.weight() != null) {
               bld = bld.e("Weight").t(endpoint.weight().toString()).up();
            }

            bld = bld.up();
         }

         bld.up().up();

         return (R) request.toBuilder().payload(bld.up().asString()).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }
}
