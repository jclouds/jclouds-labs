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
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.marconi.v1.domain.Message;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.marconi.v1.functions.ParseMessagesToStream.MessageWithHref;
import static org.jclouds.openstack.marconi.v1.functions.ParseMessagesToStream.TO_MESSAGE;

/**
 * @author Everett Toews
 */
public class ParseMessage implements Function<HttpResponse, Message> {

   private final ParseJson<MessageWithHref> json;

   @Inject
   ParseMessage(ParseJson<MessageWithHref> json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public Message apply(HttpResponse response) {
      MessageWithHref messagesWithHref = json.apply(response);

      return TO_MESSAGE.apply(messagesWithHref);
   }
}
