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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.jclouds.profitbricks.rest.binder.firewall.CreateFirewallRuleRequestBinder;
import org.apache.jclouds.profitbricks.rest.binder.firewall.UpdateFirewallRuleRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.FirewallRule;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.functions.ParseRequestStatusURI;
import org.apache.jclouds.profitbricks.rest.functions.RequestStatusURIParser;
import org.apache.jclouds.profitbricks.rest.util.ParseId;
import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.util.Strings2;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

@Path("/datacenters/{dataCenterId}/servers/{serverId}/nics/{nicId}/firewallrules")
@RequestFilters(BasicAuthentication.class)
public interface FirewallApi extends Closeable {
   
   @Named("firewallRule:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<FirewallRule> list(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId, @PathParam("nicId") String nicId);

   @Named("firewallRule:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<FirewallRule> list(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId, @PathParam("nicId") String nicId, DepthOptions options);
   
   @Named("firewallRule:get")
   @GET   
   @Path("/{firewallRuleId}")
   @ResponseParser(FirewallApi.FirewallRuleParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   FirewallRule get(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId, @PathParam("nicId") String nicId, @PathParam("firewallRuleId") String firewallRuleId);

   @Named("firewallRule:get")
   @GET   
   @Path("/{firewallRuleId}")
   @ResponseParser(FirewallApi.FirewallRuleParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   FirewallRule get(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId, @PathParam("nicId") String nicId, @PathParam("firewallRuleId") String firewallRuleId, DepthOptions options);
   
   @Named("firewallRule:create")
   @POST
   @MapBinder(CreateFirewallRuleRequestBinder.class)
   @ResponseParser(FirewallApi.FirewallRuleParser.class)
   FirewallRule create(@PayloadParam("firewallRule") FirewallRule.Request.CreatePayload payload);
   
   @Named("firewallRule:update")
   @PATCH
   @MapBinder(UpdateFirewallRuleRequestBinder.class)
   @ResponseParser(FirewallApi.FirewallRuleParser.class)
   @Produces("application/json")
   FirewallRule update(@PayloadParam("firewallRule") FirewallRule.Request.UpdatePayload payload);
   
   @Named("firewallRule:delete")
   @DELETE
   @Path("/{firewallRuleId}")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   @ResponseParser(ParseRequestStatusURI.class)
   URI delete(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId, @PathParam("nicId") String nicId, @PathParam("firewallRuleId") String firewallRuleId);
   
   static final class FirewallRuleParser extends RequestStatusURIParser<FirewallRule> {
      
      private final ParseId parseService;
            
      @Inject FirewallRuleParser(Json json, ParseId parseId, ParseRequestStatusURI parseRequestStatusURI) {
         super(json, TypeLiteral.get(FirewallRule.class), parseRequestStatusURI);
         this.parseService = parseId;
      }

      @SuppressWarnings("unchecked")
      @Override      
      public <V> V apply(InputStream stream, Type type) throws IOException {
         try {
            return (V) json.fromJson(
               this.parseService.parseId(
                  this.parseService.parseId(
                     this.parseService.parseId(Strings2.toStringAndClose(stream), "datacenters", "dataCenterId"),
                     "servers", "serverId"
                  ),
                  "nics", "nicId"
               )
            , type);
         } finally {
            if (stream != null)
               stream.close();
         }
      }
      
   }
   
}
