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
package org.apache.jclouds.profitbricks.rest.features;

import java.io.Closeable;
import java.net.URI;
import java.util.List;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.jclouds.profitbricks.rest.domain.IpBlock;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.functions.ParseRequestStatusURI;
import org.apache.jclouds.profitbricks.rest.functions.RequestStatusURIParser;
import org.apache.jclouds.profitbricks.rest.util.ParseId;
import org.jclouds.Fallbacks;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

@Path("/ipblocks")
@RequestFilters(BasicAuthentication.class)
public interface IpBlockApi extends Closeable {

   @Named("IpBlock:list")
   @GET
   @SelectJson("items")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<IpBlock> list();

   @Named("IpBlock:list")
   @GET
   @SelectJson("items")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<IpBlock> list(DepthOptions options);

   @Named("IpBlock:get")
   @GET
   @Path("/{ipblockId}")
   @ResponseParser(IpBlockApi.IpBlockParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   IpBlock get(@PathParam("ipblockId") String ipblockId);

   @Named("IpBlock:create")
   @POST
   @Produces("application/json")
   @ResponseParser(IpBlockApi.IpBlockParser.class)
   IpBlock create(@BinderParam(BindToJsonPayload.class) IpBlock.Request.CreatePayload payload);

   @Named("IpBlock:delete")
   @DELETE
   @Path("/{ipblockId}")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   @ResponseParser(ParseRequestStatusURI.class)
   URI delete(@PathParam("ipblockId") String ipblockId);

   static final class IpBlockParser extends RequestStatusURIParser<IpBlock> {

      final ParseId parseService;

      @Inject
      IpBlockParser(Json json, ParseId parseId, ParseRequestStatusURI parseRequestStatusURI) {
         super(json, TypeLiteral.get(IpBlock.class), parseRequestStatusURI);
         this.parseService = parseId;
      }
   }
}
