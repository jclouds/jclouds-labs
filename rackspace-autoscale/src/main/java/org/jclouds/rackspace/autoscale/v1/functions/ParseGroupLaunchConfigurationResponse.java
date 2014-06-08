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
package org.jclouds.rackspace.autoscale.v1.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration.LaunchConfigurationType;
import org.jclouds.rackspace.autoscale.v1.domain.LoadBalancer;
import org.jclouds.rackspace.autoscale.v1.domain.Personality;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

/**
 * This parses the group response and decouples domain objects from the json object returned by the service.
 */
public class ParseGroupLaunchConfigurationResponse implements Function<HttpResponse, LaunchConfiguration> {

   private final ParseJson<Map<String, Object>> json;

   @Inject
   ParseGroupLaunchConfigurationResponse(ParseJson<Map<String, Object>> json) {
      this.json = checkNotNull(json, "json");
   }

   /**
    * Parses the launch configuration from the response
    */
   @SuppressWarnings("unchecked")
   public LaunchConfiguration apply(HttpResponse from) {
      // This needs to be refactored when the service is in a more final state and changing less often
      // A lot of the complexity is expected to go away

      Map<String, Object> result = json.apply(from);

      Map<String, Object> launchConfigurationMap = (Map<String, Object>) result.get("launchConfiguration");
      Map<String, Object> args = (Map<String, Object>) launchConfigurationMap.get("args");
      Map<String, Object> server = (Map<String, Object>) args.get("server");      

      ImmutableList.Builder<Personality> personalities = ImmutableList.builder();
      ImmutableList.Builder<String> networks = ImmutableList.builder();
      
      for (Map<String, String> jsonPersonality : (List<Map<String, String>>) server.get("personality")) {
         personalities.add(Personality.builder().path(jsonPersonality.get("path")).contents(jsonPersonality.get("contents")).build());
      }

      for (Map<String, String> jsonNetwork : (List<Map<String, String>>) server.get("networks")) {
         networks.add(jsonNetwork.get("uuid"));
      }

      ImmutableList.Builder<LoadBalancer> loadBalancers = ImmutableList.builder();
      for (Map<String, Double> jsonLoadBalancer : (List<Map<String, Double>>) args.get("loadBalancers")) {
         loadBalancers.add(
               LoadBalancer.builder().id( ((Double)jsonLoadBalancer.get("loadBalancerId")).intValue() ).port( ((Double)jsonLoadBalancer.get("port")).intValue() ).build()
               );
      }

      LaunchConfiguration launchConfiguration = LaunchConfiguration.builder()
            .loadBalancers(loadBalancers.build())
            .serverName((String) server.get("name"))
            .serverImageRef((String) server.get("imageRef"))
            .serverFlavorRef((String) server.get("flavorRef"))
            .serverDiskConfig((String) server.get("OS-DCF:diskConfig"))
            .serverMetadata((Map<String, String>) server.get("metadata"))
            .personalities(personalities.build())
            .networks(networks.build())
            .type(LaunchConfigurationType.getByValue((String) launchConfigurationMap.get("type")).get())
            .build();

      return launchConfiguration;
   }
}
