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
import org.jclouds.azurecompute.domain.StorageServiceParams;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.jamesmurty.utils.XMLBuilder;

public final class StorageServiceParamsToXML implements Binder {

   @Override public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      StorageServiceParams params = StorageServiceParams.class.cast(input);

      try {
         XMLBuilder builder = XMLBuilder.create("CreateStorageServiceInput", "http://schemas.microsoft.com/windowsazure")
            .e("ServiceName").t(params.name()).up()
            //.e("Description").up()
            .e("Label").t(BaseEncoding.base64().encode(params.label().getBytes(Charsets.UTF_16))).up()
            .e("Location").t(params.location()).up()
            //.e("GeoReplicationEnabled").up()
            //.e("ExtendedProperties").up()
            //.e("SecondaryReadEnabled").up()
            .e("AccountType").t(params.accountType().name()).up();
         return (R) request.toBuilder().payload(builder.asString()).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }

}
