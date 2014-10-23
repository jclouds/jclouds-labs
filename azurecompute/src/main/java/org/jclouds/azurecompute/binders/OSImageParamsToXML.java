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
import static org.jclouds.azurecompute.domain.OSImage.Type.LINUX;

import org.jclouds.azurecompute.domain.OSImageParams;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.jamesmurty.utils.XMLBuilder;

public final class OSImageParamsToXML implements Binder {
   @Override public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      OSImageParams params = OSImageParams.class.cast(input);
      try {
         String xml = XMLBuilder.create("OSImage", "http://schemas.microsoft.com/windowsazure")
                                .e("Label").t(params.label()).up()
                                .e("MediaLink").t(params.mediaLink().toASCIIString()).up()
                                .e("Name").t(params.name()).up()
                                .e("OS").t(params.os() == LINUX ? "Linux" : "Windows").up()
                                .up().asString();
         return (R) request.toBuilder().payload(xml).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }
}
