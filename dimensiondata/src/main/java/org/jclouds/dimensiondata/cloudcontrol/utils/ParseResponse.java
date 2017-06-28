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
package org.jclouds.dimensiondata.cloudcontrol.utils;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.TypeLiteral;
import org.jclouds.dimensiondata.cloudcontrol.domain.Property;
import org.jclouds.dimensiondata.cloudcontrol.domain.Response;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import java.io.InputStream;

import static org.jclouds.http.HttpUtils.releasePayload;

public class ParseResponse implements Function<HttpResponse, String> {

   @Resource
   protected Logger logger = Logger.NULL;
   protected final Json json;
   protected final String propertyName;

   protected ParseResponse(Json json, String propertyName) {
      this.json = json;
      this.propertyName = propertyName;
   }

   public String apply(HttpResponse from) {
      try {
         InputStream gson = from.getPayload().openStream();

         final Response response = json.fromJson(gson, TypeLiteral.get(Response.class).getType());
         return tryFindInfoPropertyValue(response);
      } catch (Exception e) {
         StringBuilder message = new StringBuilder();
         message.append("Error parsing input: ");
         message.append(e.getMessage());
         logger.error(e, message.toString());
         throw new HttpResponseException(message.toString() + "\n" + from, null, from, e);
      } finally {
         releasePayload(from);
      }
   }

   String tryFindInfoPropertyValue(Response response) {
      if (!response.info().isEmpty()) {
         Optional<String> optionalPropertyName = FluentIterable.from(response.info())
               .firstMatch(new Predicate<Property>() {
                  @Override
                  public boolean apply(Property input) {
                     return input.name().equals(propertyName);
                  }
               }).transform(new Function<Property, String>() {
                  @Override
                  public String apply(Property input) {
                     return input.value();
                  }
               });
         if (!optionalPropertyName.isPresent()) {
            throw new IllegalStateException("Could not find expected property name: " + propertyName);
         }
         return optionalPropertyName.get();
      }
      return "";
   }
}
