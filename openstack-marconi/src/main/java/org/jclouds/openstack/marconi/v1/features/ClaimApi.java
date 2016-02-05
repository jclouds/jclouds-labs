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

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.marconi.v1.domain.Claim;
import org.jclouds.openstack.marconi.v1.domain.Message;
import org.jclouds.openstack.marconi.v1.functions.ParseClaim;
import org.jclouds.openstack.marconi.v1.functions.ParseMessagesToList;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

/**
 * Provides access to Messages via their REST API.
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/claims")
public interface ClaimApi {
   /**
    * This operation claims a set of messages (up to the value of the limit parameter) from oldest to newest and skips
    * any messages that are already claimed. If no unclaimed messages are available, an empty List is returned.
    * </p>
    * When a client (worker) finishes processing a message, it should delete the message before the claim expires to
    * ensure that the message is processed only once. As part of the delete operation, workers should specify the claim
    * ID. If workers perform these actions and a claim simply expires, the server can return an error and notify the
    * worker of the race condition. This action gives the worker a chance to roll back its own processing of the given
    * message because another worker can claim the message and process it.
    * </p>
    * The age given for a claim is relative to the server's clock. The claim's age is useful for determining how
    * quickly messages are getting processed and whether a given message's claim is about to expire.
    * </p>
    * When a claim expires, it is released. If the original worker failed to process the message, another client worker
    * can then claim the message.
    * </p>
    * Note that claim creation is best-effort, meaning the worker may claim and return less than the requested number
    * of messages.
    * </p>
    * To deal with workers that have stopped responding (for up to 1209600 seconds or 14 days, including claim
    * lifetime), the server extends the lifetime of claimed messages to be at least as long as the lifetime of the
    * claim itself, plus the specified grace period. If a claimed message would normally live longer than the grace
    * period, its expiration is not adjusted.
    *
    * @param ttl   The TTL attribute specifies how long the server waits before releasing the claim. The ttl value
    *              must be between 60 and 43200 seconds (12 hours). You must include a value for this attribute in
    *              your request.
    * @param grace The grace value specifies the message grace period in seconds. The value of grace value must
    *              be between 60 and 43200 seconds (12 hours). You must include a value for this attribute in your
    *              request.
    * @param limit Specifies the number of messages to return, up to 20 messages.
    */
   @Named("claim:claim")
   @POST
   @Payload("%7B\"ttl\":{ttl},\"grace\":{grace}%7D")
   @ResponseParser(ParseMessagesToList.class)
   List<Message> claim(@PayloadParam("ttl") int ttl, @PayloadParam("grace") int grace, @QueryParam("limit") int limit);

   /**
    * Gets a specific claim and the associated messages.
    *
    * @param claimId Specific claim ID of the message to get.
    */
   @Named("claim:get")
   @GET
   @Path("/{claim_id}")
   @ResponseParser(ParseClaim.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Claim get(@PathParam("claim_id") String claimId);

   /**
    * Clients should periodically renew claims during long-running batches of work to avoid losing a claim while
    * processing a message. The client can renew a claim by including a new TTL for the claim (which can be different
    * from the original TTL). The server resets the age of the claim and applies the new TTL.
    *
    * @param claimId Specific claim ID of the message to get.
    * @param ttl     The ttl attribute specifies how long the server waits before releasing the claim. The ttl value
    *                must be between 60 and 43200 seconds (12 hours). You must include a value for this attribute in
    *                your request.
    */
   @Named("claim:update")
   @PATCH
   @Path("/{claim_id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"ttl\":{ttl}%7D")
   void update(@PathParam("claim_id") String claimId, @PayloadParam("ttl") int ttl);

   /**
    * This operation immediately releases a claim, making any remaining, undeleted messages that are associated with
    * the claim available to other workers. This operation is useful when a worker is performing a graceful shutdown,
    * fails to process one or more messages, or is taking longer than expected to process messages, and wants to make
    * the remainder of the messages available to other workers.
    *
    * @param claimId Specific claim ID of the message to get.
    */
   @Named("claim:delete")
   @DELETE
   @Path("/{claim_id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean release(@PathParam("claim_id") String claimId);
}
