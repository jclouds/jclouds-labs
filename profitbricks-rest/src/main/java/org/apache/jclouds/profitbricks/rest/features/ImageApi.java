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
import java.util.List;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.jclouds.profitbricks.rest.domain.Image;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.functions.ParseRequestStatusURI;
import org.apache.jclouds.profitbricks.rest.functions.RequestStatusURIParser;
import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

@Path("/images")
@RequestFilters(BasicAuthentication.class)
public interface ImageApi extends Closeable {
   

   @Named("image:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Image> getList();

   @Named("image:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Image> getList(DepthOptions options);
   
   @Named("image:get")
   @GET   
   @Path("/{imageId}")
   @ResponseParser(ImageApi.ImageParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Image getImage(@PathParam("imageId") String imageId);

   @Named("image:get")
   @GET   
   @Path("/{imageId}")
   @ResponseParser(ImageApi.ImageParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Image getImage(@PathParam("imageId") String imageId, DepthOptions options);
   
   static final class ImageParser extends RequestStatusURIParser<Image> {
      @Inject ImageParser(Json json, ParseRequestStatusURI parseRequestStatusURI) {
         super(json, TypeLiteral.get(Image.class), parseRequestStatusURI);
      }
   }
   
}
