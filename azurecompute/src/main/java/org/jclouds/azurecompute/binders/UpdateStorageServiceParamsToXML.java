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

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.jamesmurty.utils.XMLBuilder;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.azurecompute.domain.UpdateStorageServiceParams;

public final class UpdateStorageServiceParamsToXML implements Binder {

   @Override
   @SuppressWarnings("unchecked")
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      final UpdateStorageServiceParams params = UpdateStorageServiceParams.class.cast(input);

      try {
         final XMLBuilder builder = XMLBuilder.create(
                 "UpdateStorageServiceInput", "http://schemas.microsoft.com/windowsazure");

         if (params.description() != null) {
            builder.e("Description").t(params.description()).up();
         }

         if (params.label() != null) {
            builder.e("Label").t(BaseEncoding.base64().encode(params.label().getBytes(Charsets.UTF_8))).up();
         }

         if (params.geoReplicationEnabled() != null) {
            builder.e("geoReplicationEnabled").t(params.geoReplicationEnabled().toString()).up();
         }

         if (params.extendedProperties() != null) {
            final XMLBuilder extProps = builder.e("ExtendedProperties");
            for (Map.Entry<String, String> entry : params.extendedProperties().entrySet()) {
               final XMLBuilder extProp = extProps.e("ExtendedProperty");
               extProp.e("Name").t(entry.getKey()).up();
               extProp.e("Value").t(entry.getValue()).up();
               extProp.up();
            }
            extProps.up();
         }

         if (params.customDomains() != null) {
            final XMLBuilder custDomains = builder.e("CustomDomains");
            for (UpdateStorageServiceParams.CustomDomain domain : params.customDomains()) {
               final XMLBuilder custDomain = custDomains.e("CustomDomain");
               custDomain.e("Name").t(domain.name()).up();
               custDomain.e("UseSubDomainName").t(domain.useSubDomainName().toString()).up();
               custDomain.up();
            }
            custDomains.up();
         }

         if (params.accountType() != null) {
            builder.e("AccountType").t(params.accountType().name()).up();
         }

         return (R) request.toBuilder().payload(builder.asString()).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }

}
