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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.marconi.v1.domain.Message;
import org.jclouds.openstack.marconi.v1.domain.MessageStream;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;

import javax.inject.Inject;
import java.beans.ConstructorProperties;
import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.http.utils.Queries.queryParser;

/**
 * @author Everett Toews
 */
public class ParseMessagesToStream implements Function<HttpResponse, MessageStream> {

   private final ParseJson<MessagesWithHref> json;

   @Inject
   ParseMessagesToStream(ParseJson<MessagesWithHref> json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public MessageStream apply(HttpResponse response) {
      // An empty message stream has a 204 response code
      if (response.getStatusCode() == 204) {
         return new Messages(ImmutableSet.<Message> of(), ImmutableSet.<Link> of());
      }

      MessagesWithHref messagesWithHref = json.apply(response);
      Iterable<Message> messages = transform(messagesWithHref, TO_MESSAGE);

      return new Messages(messages, messagesWithHref.getLinks());
   }

   /**
    * Strip off everything but the message id.
    */
   private static String getIdFromHref(String rawMessageHref) {
      int indexOfQuestionMark = rawMessageHref.indexOf('?');
      int lastIndexOfSlash = rawMessageHref.lastIndexOf('/') + 1;

      if (indexOfQuestionMark > lastIndexOfSlash) {
         return rawMessageHref.substring(lastIndexOfSlash, indexOfQuestionMark);
      }
      else {
         return rawMessageHref.substring(lastIndexOfSlash);
      }
   }

   private static String getClaimIdFromHref(String rawMessageHref) {
      try {
         String query = new URI(rawMessageHref).getQuery();

         if (query != null) {
            Multimap<String, String> queryParams = queryParser().apply(query);
            return getOnlyElement(queryParams.get("claim_id"), null);
         }
         else {
            return null;
         }
      }
      catch (URISyntaxException e) {
         return null;
      }
   }

   protected static final Function<MessageWithHref, Message> TO_MESSAGE = new Function<MessageWithHref, Message>() {
      @Override
      public Message apply(MessageWithHref messageWithHref) {
         return messageWithHref.toBuilder()
               .id(getIdFromHref(messageWithHref.getId()))
               .claimId(getClaimIdFromHref(messageWithHref.getId()))
               .build();
      }
   };

   protected static final Function<String, String> TO_ID_FROM_HREF = new Function<String, String>() {
      @Override
      public String apply(String messageIdWithHref) {
         return getIdFromHref(messageIdWithHref);
      }
   };

   private static class Messages extends MessageStream {

      @ConstructorProperties({ "messages", "links" })
      protected Messages(Iterable<Message> messages, Iterable<Link> links) {
         super(messages, links);
      }
   }

   private static class MessagesWithHref extends PaginatedCollection<MessageWithHref> {

      @ConstructorProperties({ "messages", "links" })
      protected MessagesWithHref(Iterable<MessageWithHref> messagesWithHref, Iterable<Link> links) {
         super(messagesWithHref, links);
      }
   }

   protected static class MessageWithHref extends Message {

      @ConstructorProperties({ "href", "ttl", "body", "age" })
      protected MessageWithHref(String href, int ttl, String body, int age) {
         super(href, ttl, body, age, null);
      }
   }
}
