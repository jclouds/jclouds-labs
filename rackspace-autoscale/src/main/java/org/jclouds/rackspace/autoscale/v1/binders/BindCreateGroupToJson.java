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
package org.jclouds.rackspace.autoscale.v1.binders;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rackspace.autoscale.v1.domain.GroupConfiguration;
import org.jclouds.rackspace.autoscale.v1.internal.ParseHelper;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

/**
 * Decouple building the json object from the domain objects structure by using the binder
 */
public class BindCreateGroupToJson implements MapBinder {

   private final BindToJsonPayload jsonBinder;

   @Inject
   private BindCreateGroupToJson(BindToJsonPayload jsonBinder) {
      this.jsonBinder = jsonBinder;
   }

   @Override    
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {

      ImmutableMap<String, Object> launchConfigurationMap = ParseHelper.buildLaunchConfigurationRequestMap(postParams);
      GroupConfiguration groupConfiguration = (GroupConfiguration) postParams.get("groupConfiguration");
      ImmutableList<Map<String, Object>> scalingPoliciesList = ParseHelper.buildScalingPoliciesRequestList(postParams);

      return jsonBinder.bindToRequest(request, ImmutableMap.of(
            "launchConfiguration", launchConfigurationMap, 
            "groupConfiguration", groupConfiguration, 
            "scalingPolicies", scalingPoliciesList));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("Create scaling group is a POST operation");
   }
}
