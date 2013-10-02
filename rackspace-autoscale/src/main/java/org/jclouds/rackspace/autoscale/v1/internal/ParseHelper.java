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
package org.jclouds.rackspace.autoscale.v1.internal;

import java.util.List;
import java.util.Map;

import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

/**
 * @author Zack Shoylev
 * 
 * Helper methods for parsing autoscale JSON
 */
public class ParseHelper {
   public static ImmutableMap<String, Object> buildLaunchConfigurationRequestMap(Map<String, Object> postParams) {
      LaunchConfiguration launchConfigurationRequest = (LaunchConfiguration) postParams.get("launchConfiguration");

      ImmutableMap.Builder<String, Object> launchConfigurationMapBuilder = ImmutableMap.builder();
      ImmutableMap.Builder<String, Object> argsBuilder = ImmutableMap.builder();
      ImmutableMap.Builder<String, Object> serverBuilder = ImmutableMap.builder();
      ImmutableList.Builder<Map<String, String>> networksBuilder = ImmutableList.builder();

      for (String networkId : launchConfigurationRequest.getNetworks()) {
         Map<String, String> network = Maps.newHashMap();
         network.put("uuid", networkId);
         networksBuilder.add(network);
      }

      serverBuilder.put("name", launchConfigurationRequest.getServerName());
      serverBuilder.put("imageRef", launchConfigurationRequest.getServerImageRef());
      serverBuilder.put("flavorRef", launchConfigurationRequest.getServerFlavorRef());
      serverBuilder.put("OS-DCF:diskConfig", launchConfigurationRequest.getServerDiskConfig());
      serverBuilder.put("metadata", launchConfigurationRequest.getServerMetadata());
      serverBuilder.put("personality", launchConfigurationRequest.getPersonalities());
      serverBuilder.put("networks", networksBuilder.build());

      argsBuilder.put("loadBalancers", launchConfigurationRequest.getLoadBalancers());
      argsBuilder.put("server", serverBuilder.build());

      launchConfigurationMapBuilder.put("type", launchConfigurationRequest.getType().toString());
      launchConfigurationMapBuilder.put("args", argsBuilder.build());

      return launchConfigurationMapBuilder.build();
   }

   @SuppressWarnings("unchecked")
   public static ImmutableList<Map<String, Object>> buildScalingPoliciesRequestList(Map<String, Object> postParams) {
      List<ScalingPolicy> scalingPoliciesRequest = (List<ScalingPolicy>) postParams.get("scalingPolicies");
      ImmutableList.Builder<Map<String, Object>> scalingPoliciesListBuilder = ImmutableList.builder();

      for (ScalingPolicy scalingPolicy : scalingPoliciesRequest) {
         scalingPoliciesListBuilder.add(buildScalingPolicyMap(scalingPolicy));
      }
      return scalingPoliciesListBuilder.build();
   }
   
   public static ImmutableMap<String, Object> buildScalingPolicyMap(ScalingPolicy scalingPolicy) {
      ImmutableMap.Builder<String, Object> scalingPolicyMapBuilder = ImmutableMap.builder();
      scalingPolicyMapBuilder.put("cooldown", scalingPolicy.getCooldown());
      scalingPolicyMapBuilder.put("type", scalingPolicy.getType().toString());
      scalingPolicyMapBuilder.put("name", scalingPolicy.getName());
      // A couple of different scaling policies are supported, such as percent or number based, or targeting specific numbers of instances
      String targetString = scalingPolicy.getTarget();
      Integer targetInt = Ints.tryParse(targetString);
      // Notes:
      // 1. Refactor when autoscale is complete and service is released to dry this code.
      // 2. Refactor to use simpler code for the number parsing. Polymorphism or a facade might work.
      // 3. Potentially remove or rework the enum code.
      Float targetFloat;
      if (targetInt != null) {
         scalingPolicyMapBuilder.put(scalingPolicy.getTargetType().toString(), targetInt);
      } else if ((targetFloat = Floats.tryParse(targetString)) != null) {
         scalingPolicyMapBuilder.put(scalingPolicy.getTargetType().toString(), targetFloat);
      } else {
         scalingPolicyMapBuilder.put(scalingPolicy.getTargetType().toString(), targetString);
      }

      return scalingPolicyMapBuilder.build();
   }
}
