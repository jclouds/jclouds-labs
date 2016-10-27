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
package org.apache.jclouds.profitbricks.rest.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.apache.jclouds.profitbricks.rest.domain.Trackable;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import com.google.inject.TypeLiteral;

/**
 * Get the response status URI, in case it is present.
 */
public class RequestStatusURIParser<T extends Trackable> extends ParseJson<T> {
   
   private final ParseRequestStatusURI parseRequestStatusURI;
   protected URI requestStatusURI;

   protected RequestStatusURIParser(Json json, TypeLiteral<T> type, ParseRequestStatusURI parseRequestStatusURI) {
      super(json, type);
      this.parseRequestStatusURI = checkNotNull(parseRequestStatusURI, "parseRequestStatusURI");
   }

   @Override
   public T apply(HttpResponse from) {
      T trackable = super.apply(from);
      trackable.setRequestStatusUri(parseRequestStatusURI.apply(from));
      return trackable;
   }
   
}
