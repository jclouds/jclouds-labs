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
import org.jclouds.azurecompute.domain.ReservedIPAddressParams;

public final class ReservedIPAddressParamsToXML implements Binder {

   @Override
   @SuppressWarnings("unchecked")
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      final ReservedIPAddressParams params = ReservedIPAddressParams.class.cast(input);
      try {
         final XMLBuilder bld = XMLBuilder.create("ReservedIP", "http://schemas.microsoft.com/windowsazure")
                 .e("Name").t(params.name()).up();

         if (params.label() != null) {
            bld.e("Label").t(params.label()).up();
         }

         bld.e("Location").t(params.location()).up();

         return (R) request.toBuilder().payload(bld.up().asString()).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }
}
