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
package org.jclouds.openstack.marconi.v1.functions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.marconi.v1.domain.Message;

import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.openstack.marconi.v1.functions.ParseMessagesToStream.MessageWithHref;
import static org.jclouds.openstack.marconi.v1.functions.ParseMessagesToStream.TO_MESSAGE;

public class ParseMessagesToList implements Function<HttpResponse, List<Message>> {

   private final ParseJson<List<MessageWithHref>> json;

   @Inject
   ParseMessagesToList(ParseJson<List<MessageWithHref>> json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public List<Message> apply(HttpResponse response) {
      // An empty message stream has a 204 response code
      if (response.getStatusCode() == 204) {
         return ImmutableList.of();
      }

      List<MessageWithHref> messagesWithHref = json.apply(response);
      return ImmutableList.copyOf(transform(messagesWithHref, TO_MESSAGE));
   }
}
