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
package org.jclouds.openstack.marconi.v1.binders;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkArgument;

@Singleton
public class BindIdsToQueryParam implements Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof Iterable<?>, "This binder is only valid for Iterable");
      Iterable<String> ids = (Iterable<String>) input;
      checkArgument(Iterables.size(ids) > 0, "You must specify at least one id");

      return (R) request.toBuilder().replaceQueryParam("ids", Joiner.on(',').join(ids)).build();
   }
}
