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

import com.jamesmurty.utils.XMLBuilder;
import org.jclouds.azurecompute.domain.StorageServiceKeys;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import static com.google.common.base.Throwables.propagate;

public class StorageServiceKeyTypeToXML implements Binder {

   @Override
   @SuppressWarnings("unchecked")
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      final StorageServiceKeys.KeyType params = StorageServiceKeys.KeyType.class.cast(input);

      try {
         final XMLBuilder builder = XMLBuilder.create(
                 "RegenerateKeys", "http://schemas.microsoft.com/windowsazure").
                 e("KeyType").t(params.name()).up();
         return (R) request.toBuilder().payload(builder.asString()).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }

}
