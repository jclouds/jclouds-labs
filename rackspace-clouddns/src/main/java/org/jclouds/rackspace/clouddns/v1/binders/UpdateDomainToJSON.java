/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rackspace.clouddns.v1.binders;

import static java.lang.String.format;

import java.util.List;
import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rackspace.clouddns.v1.domain.UpdateDomain;
import org.jclouds.rest.MapBinder;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * @author Everett Toews
 */
public class UpdateDomainToJSON implements MapBinder {
   private static final String template = "{%s}";

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      UpdateDomain updateDomain = UpdateDomain.class.cast(postParams.get("updateDomain"));

      return (R) request.toBuilder().payload(toJSON(updateDomain)).build();
   }

   private String toJSON(UpdateDomain updateDomain) {
      List<String> json = Lists.newArrayList();

      if (updateDomain.getTTL().isPresent()) {
         json.add("\"ttl\":" + updateDomain.getTTL().get());
      }
      
      if (updateDomain.getEmail().isPresent()) {
         json.add("\"emailAddress\":\"" + updateDomain.getEmail().get() + "\"");
      }
      
      if (updateDomain.getComment().isPresent()) {
         json.add("\"comment\":\"" + updateDomain.getComment().get() + "\"");
      }
      
      String contentsAsOneString = Joiner.on(",").join(json);

      return format(template, contentsAsOneString);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("use map form");
   }
}
