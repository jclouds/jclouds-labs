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
package org.jclouds.openstack.poppy.v1.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.poppy.v1.domain.Service;

import com.google.common.base.Function;
import com.google.common.net.HttpHeaders;

/**
 * Parses the {@link Service} URI from the Location header of the HTTP Response.
 */
@Singleton
public class ParseServiceURIFromHeaders implements Function<HttpResponse, URI> {

	@Override
   public URI apply(HttpResponse response) {
     String locationUri =  checkNotNull(response.getFirstHeaderOrNull(HttpHeaders.LOCATION),
           HttpHeaders.LOCATION);
     return URI.create(locationUri);
    }
}
