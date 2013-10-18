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
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.marconi.v1.domain.MessagesCreated;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.marconi.v1.functions.ParseMessages.TO_MESSAGE_ID;

/**
 * This parses the messages created on a queue.
 * 
 * @author Everett Toews
 */
public class ParseMessagesCreated implements Function<HttpResponse, MessagesCreated> {

   private final ParseJson<MessagesCreated> json;

   @Inject
   ParseMessagesCreated(ParseJson<MessagesCreated> json) {
      this.json = checkNotNull(json, "json");
   }

   public MessagesCreated apply(HttpResponse from) {
      MessagesCreated rawMessagesCreated = json.apply(from);
      List<String> messageIds = Lists.transform(rawMessagesCreated.getMessageIds(), TO_MESSAGE_ID);

      MessagesCreated messagesCreated = MessagesCreated.builder()
            .messageIds(messageIds)
            .build();

      return messagesCreated;
   }
}
