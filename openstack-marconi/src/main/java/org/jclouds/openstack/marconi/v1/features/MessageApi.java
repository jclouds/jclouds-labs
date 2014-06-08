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
package org.jclouds.openstack.marconi.v1.features;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.marconi.v1.binders.BindIdsToQueryParam;
import org.jclouds.openstack.marconi.v1.domain.CreateMessage;
import org.jclouds.openstack.marconi.v1.domain.Message;
import org.jclouds.openstack.marconi.v1.domain.MessageStream;
import org.jclouds.openstack.marconi.v1.domain.MessagesCreated;
import org.jclouds.openstack.marconi.v1.functions.ParseMessage;
import org.jclouds.openstack.marconi.v1.functions.ParseMessagesCreated;
import org.jclouds.openstack.marconi.v1.functions.ParseMessagesToList;
import org.jclouds.openstack.marconi.v1.functions.ParseMessagesToStream;
import org.jclouds.openstack.marconi.v1.options.StreamMessagesOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import static org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;

/**
 * Provides access to Messages via their REST API.
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface MessageApi {
   /**
    * Create message(s) on a queue.
    *
    * @param messages The messages created on the queue. The number of messages allowed in one request are configurable
    *                 by your cloud provider. Consult your cloud provider documentation to learn the maximum.
    */
   @Named("message:create")
   @POST
   @Path("/messages")
   @ResponseParser(ParseMessagesCreated.class)
   @Fallback(NullOnNotFoundOr404.class)
   MessagesCreated create(@BinderParam(BindToJsonPayload.class) List<CreateMessage> messages);

   /**
    * Streams the messages off of a queue. In a very active queue it's possible that you could continuously stream
    * messages indefinitely.
    *
    * @param options  Options for streaming messages to your client.
    */
   @Named("message:stream")
   @GET
   @ResponseParser(ParseMessagesToStream.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/messages")
   MessageStream stream(StreamMessagesOptions... options);

   /**
    * Lists specific messages. Unlike the stream method, a client's own messages are always returned in this operation.
    *
    * @param ids      Specifies the IDs of the messages to list.
    */
   @Named("message:list")
   @GET
   @ResponseParser(ParseMessagesToList.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/messages")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Message> list(@BinderParam(BindIdsToQueryParam.class) Iterable<String> ids);

   /**
    * Gets a specific message. Unlike the stream method, a client's own messages are always returned in this operation.
    *
    * @param id       Specific ID of the message to get.
    */
   @Named("message:get")
   @GET
   @ResponseParser(ParseMessage.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/messages/{message_id}")
   @Fallback(NullOnNotFoundOr404.class)
   Message get(@PathParam("message_id") String id);

   /**
    * Deletes specific messages. If any of the message IDs are malformed or non-existent, they are ignored. The
    * remaining valid messages IDs are deleted.
    *
    * @param ids      Specifies the IDs of the messages to delete.
    */
   @Named("message:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/messages")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@BinderParam(BindIdsToQueryParam.class) Iterable<String> ids);

   /**
    * The claimId parameter specifies that the message is deleted only if it has the specified claim ID and that claim
    * has not expired. This specification is useful for ensuring only one worker processes any given message. When a
    * worker's claim expires before it can delete a message that it has processed, the worker must roll back any
    * actions it took based on that message because another worker can now claim and process the same message.
    *
    * @param id       Specific ID of the message to delete.
    * @param claimId  Specific claim ID of the message to delete.
    */
   @Named("message:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/messages/{message_id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteByClaim(@PathParam("message_id") String id,
                         @QueryParam("claim_id") String claimId);
}
