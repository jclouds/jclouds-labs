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

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import java.io.Closeable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.functions.ParseRequestStatusURI;
import org.apache.jclouds.profitbricks.rest.functions.RequestStatusURIParser;
import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

@Path("/datacenters")
@RequestFilters(BasicAuthentication.class)
public interface DataCenterApi extends Closeable {

   @Named("datacenter:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<DataCenter> list();
   
   @Named("datacenter:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<DataCenter> list(DepthOptions options);
   
   /**
    * @param id Data Center identifier
    * @return Returns information about an existing virtual data center's state and configuration or <code>null</code>
    * if it doesn't exist.
    */
   @Named("datacenter:get")
   @GET   
   @Path("/{id}")
   @ResponseParser(DataCenterParser.class)
   @Fallback(NullOnNotFoundOr404.class)
   DataCenter getDataCenter(@PathParam("id") String id);

   @Named("datacenter:get")
   @GET   
   @Path("/{id}")
   @ResponseParser(DataCenterParser.class)
   @Fallback(NullOnNotFoundOr404.class)
   DataCenter getDataCenter(@PathParam("id") String id, DepthOptions options);
   
   @Named("datacenter:create")
   @POST
   @ResponseParser(DataCenterParser.class)
   @Produces("application/json")
   @MapBinder(DataCenterCreateMapBinder.class)
   DataCenter create(
      @PayloadParam("name") String name, 
      @PayloadParam("description") String description,
      @PayloadParam("location") String location
   );

   @Named("datacenter:update")
   @PATCH
   @Path("/{id}")
   @ResponseParser(DataCenterParser.class)
   @Produces("application/json")
   @MapBinder(BindToJsonPayload.class)
   DataCenter update(
      @PathParam("id") String id, 
      @PayloadParam("name") String name
   );

   @Named("datacenter:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   @ResponseParser(ParseRequestStatusURI.class)
   URI delete(@PathParam("id") String id);
   
   static final class DataCenterParser extends RequestStatusURIParser<DataCenter> {
      @Inject DataCenterParser(Json json, ParseRequestStatusURI parseRequestStatusURI) {
         super(json, TypeLiteral.get(DataCenter.class), parseRequestStatusURI);
      }
   }
   
   static final class DataCenterCreateMapBinder implements org.jclouds.rest.MapBinder {

      protected final Json jsonBinder;

      @Inject
      public DataCenterCreateMapBinder(Json jsonBinder) {
         this.jsonBinder = checkNotNull(jsonBinder, "jsonBinder");
      }

      @Override
      public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
         return bindToRequest(request, (Object) postParams);
      }

      @Override
      public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("properties", payload);
         
         String json = jsonBinder.toJson(checkNotNull(params, "payload"));
         request.setPayload(json);
         request.getPayload().getContentMetadata().setContentType("application/json");
         return request;
      }
      
   }
   
}
