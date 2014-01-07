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
package org.jclouds.rackspace.autoscale.v1.features;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.autoscale.v1.binders.BindWebhookToJson;
import org.jclouds.rackspace.autoscale.v1.binders.BindWebhookUpdateToJson;
import org.jclouds.rackspace.autoscale.v1.binders.BindWebhooksToJson;
import org.jclouds.rackspace.autoscale.v1.domain.CreateWebhook;
import org.jclouds.rackspace.autoscale.v1.domain.Webhook;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.collect.FluentIterable;

/**
 * The API for controlling autoscale webhooks.
 * @author Zack Shoylev
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface WebhookApi extends Closeable {
   /**
    * Create a webhook.
    * @param name The webhook name. Required.
    * @param metadata A map of associated metadata. Use String keys. Required. 
    * @return WebhookResponse The webhook created by this call.
    * @see CreateWebhook
    * @see Webhook
    * @see Group
    * @see CreateScalingPolicy
    */
   @Named("Webhook:create")
   @POST
   @Path("/webhooks")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @MapBinder(BindWebhookToJson.class)
   @SelectJson("webhooks")
   FluentIterable<Webhook> create(@PayloadParam("name") String name, @PayloadParam("metadata") Map<String, Object> metadata);

   /**
    * Create webhooks.
    * @param webhooks A list of webhooks.
    * @return WebhookResponse The webhook created by this call.
    * @see CreateWebhook
    * @see Webhook
    * @see Group
    * @see CreateScalingPolicy
    */
   @Named("Webhook:create")
   @POST
   @Path("/webhooks")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @MapBinder(BindWebhooksToJson.class)
   @SelectJson("webhooks")
   FluentIterable<Webhook> create(@PayloadParam("webhooks") List<CreateWebhook> webhooks);
   
   /**
    * List webhooks.
    * @return A list of webhooks
    * @see CreateWebhook
    * @see Webhook
    * @see Group
    * @see CreateScalingPolicy
    */
   @Named("Webhook:list")
   @GET
   @Path("/webhooks")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @SelectJson("webhooks")
   FluentIterable<Webhook> list();

   /**
    * Get a webhook.
    * @param String id The id of the webhook.
    * @return The webhook
    * @see CreateWebhook
    * @see Webhook
    * @see Group
    * @see CreateScalingPolicy
    */
   @Named("Webhook:get")
   @GET
   @Path("/webhooks/{webhookId}")   
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("webhook")
   Webhook get(@PathParam("webhookId") String id);

   /**
    * Update a webhook.
    * @param id The webhook id
    * @param name The webhook name
    * @param metadata A map of associated metadata. Use String keys.
    * @return true when successful.
    * @see CreateWebhook
    * @see Webhook
    * @see Group
    * @see CreateScalingPolicy
    */
   @Named("Webhook:update")
   @PUT
   @Path("/webhooks/{webhookId}")   
   @Fallback(FalseOnNotFoundOr404.class)
   @MapBinder(BindWebhookUpdateToJson.class)
   boolean update(@PathParam("webhookId") String id, @PayloadParam("name") String name, @PayloadParam("metadata") Map<String, Object> metadata);

   /**
    * Delete a webhook.
    * @param String id The id of the webhook.
    * @return true if successful.
    * @see CreateWebhook
    * @see Webhook
    * @see Group
    * @see CreateScalingPolicy
    */
   @Named("Webhook:delete")
   @DELETE
   @Path("/webhooks/{webhookId}")   
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("webhookId") String id);
}
