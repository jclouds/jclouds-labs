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

import java.util.List;
import java.util.Map;
import org.jclouds.http.HttpRequest;
import org.jclouds.rackspace.autoscale.v1.domain.GroupConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 * Decouple building the json object from the domain objects structure by using the binder
 * @author Zack Shoylev
 */
public class BindCreateGroupToJson implements MapBinder {

   private final BindToJsonPayload jsonBinder;

   @Inject
   private BindCreateGroupToJson(BindToJsonPayload jsonBinder) {
      this.jsonBinder = jsonBinder;
   }

   @SuppressWarnings("unchecked")
   @Override    
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      List<ScalingPolicy> scalingPoliciesRequest = (List<ScalingPolicy>) postParams.get("scalingPolicies");
      LaunchConfiguration launchConfigurationRequest = (LaunchConfiguration) postParams.get("launchConfiguration");

      GroupConfiguration groupConfiguration = (GroupConfiguration) postParams.get("groupConfiguration");

      Map<String, Object> launchConfigurationMap = Maps.newHashMap();
      List<Map<String, Object>> scalingPoliciesList = Lists.newArrayList();
      Map<String, Object> args = Maps.newHashMap();
      launchConfigurationMap.put("args", args);
      launchConfigurationMap.put("type", launchConfigurationRequest.getType().toString());

      args.put("loadBalancers", launchConfigurationRequest.getLoadBalancers());
      Map<String, Object> server = Maps.newHashMap();
      args.put("server", server);
      server.put("name", launchConfigurationRequest.getServerName());
      server.put("imageRef", launchConfigurationRequest.getServerImageRef());
      server.put("flavorRef", launchConfigurationRequest.getServerFlavorRef());
      server.put("OS-DCF:diskConfig", launchConfigurationRequest.getServerDiskConfig());
      server.put("metadata", launchConfigurationRequest.getServerMetadata());
      List<Map<String, String>> networks = Lists.newArrayList();
      server.put("networks", networks);
      for(String networkId : launchConfigurationRequest.getNetworks()) {
         Map<String, String> network = Maps.newHashMap();
         network.put("uuid", networkId);
         networks.add(network);
      }
      server.put("personality", launchConfigurationRequest.getPersonalities());

      for(ScalingPolicy scalingPolicy : scalingPoliciesRequest) {
         Map<String,Object> scalingPolicyMap = Maps.newHashMap();
         scalingPoliciesList.add(scalingPolicyMap);
         scalingPolicyMap.put("cooldown", scalingPolicy.getCooldown());
         scalingPolicyMap.put("type", scalingPolicy.getType().toString());
         scalingPolicyMap.put("name", scalingPolicy.getName());
         // A couple of different scaling policies are supported, such as percent or number based, or targeting specific numbers of instances
         scalingPolicyMap.put(scalingPolicy.getTargetType().toString(), scalingPolicy.getTarget());
      }

      return jsonBinder.bindToRequest(request, ImmutableMap.of(
            "launchConfiguration", launchConfigurationMap, 
            "groupConfiguration", groupConfiguration, 
            "scalingPolicies", scalingPoliciesList));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("CreateInstance is a POST operation");
   }
}
