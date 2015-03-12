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

import org.jclouds.azurecompute.domain.CreateAffinityGroupParams;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

public final class CreateAffinityGroupParamsToXML implements Binder {

   @Override
   @SuppressWarnings("unchecked")
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      final CreateAffinityGroupParams params = CreateAffinityGroupParams.class.cast(input);

      try {
         XMLBuilder builder = XMLBuilder.create("CreateAffinityGroup", "http://schemas.microsoft.com/windowsazure")
                 .e("Name").t(params.name()).up()
                 .e("Label").t(BaseEncoding.base64().encode(params.label().getBytes(Charsets.UTF_8))).up();
         if (params.description() != null) {
            builder.e("Description").t(params.description()).up();
         }
         String xml = builder.e("Location").t(params.location()).up().asString();
         return (R) request.toBuilder().payload(xml).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }
}
