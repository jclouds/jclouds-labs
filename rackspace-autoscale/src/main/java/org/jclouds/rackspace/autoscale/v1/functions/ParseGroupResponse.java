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

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.rackspace.autoscale.v1.domain.Group;
import org.jclouds.rackspace.autoscale.v1.domain.GroupConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration;
import org.jclouds.rackspace.autoscale.v1.domain.LaunchConfiguration.LaunchConfigurationType;
import org.jclouds.rackspace.autoscale.v1.domain.LoadBalancer;
import org.jclouds.rackspace.autoscale.v1.domain.Personality;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy.ScalingPolicyTargetType;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy.ScalingPolicyType;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicyResponse;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.math.DoubleMath;
import com.google.inject.Inject;

/**
 * This parses the group response and decouples domain objects from the json object returned by the service.
 * @author Zack Shoylev
 */
public class ParseGroupResponse implements Function<HttpResponse, Group> {

   private final ParseJson<Map<String, Object>> json;

   @Inject
   ParseGroupResponse(ParseJson<Map<String, Object>> json) {
      this.json = checkNotNull(json, "json");
   }

   /**
    * Parses the Group from the response
    */
   @SuppressWarnings("unchecked")
   public Group apply(HttpResponse from) {
      // This needs to be refactored when the service is in a more final state and changing less often
      // A lot of the complexity is expected to go away

      Map<String, Object> result = json.apply(from);

      Map<String, Object> group = (Map<String, Object>) result.get("group");
      Map<String, Object> groupConfigurationMap = (Map<String, Object>) group.get("groupConfiguration");
      Map<String, Object> launchConfigurationMap = (Map<String, Object>) group.get("launchConfiguration");
      ImmutableList.Builder<ScalingPolicyResponse> scalingPoliciesList = ImmutableList.builder();
      Map<String, Object> args = (Map<String, Object>) launchConfigurationMap.get("args");
      Map<String, Object> server = (Map<String, Object>) args.get("server");      

      ImmutableList.Builder<Personality> personalities = ImmutableList.builder();
      ImmutableList.Builder<String> networks = ImmutableList.builder();
      for(Map<String,String> jsonPersonality : (List<Map<String,String>>) server.get("personality")) {
         personalities.add(Personality.builder().path(jsonPersonality.get("path")).contents(jsonPersonality.get("contents")).build());
      }

      for(Map<String,String> jsonNetwork : (List<Map<String,String>>) server.get("networks")) {
         networks.add(jsonNetwork.get("uuid"));
      }

      ImmutableList.Builder<LoadBalancer> loadBalancers = ImmutableList.builder();
      for(Map<String,Double> jsonLoadBalancer : (List<Map<String,Double>>) args.get("loadBalancers")) {
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

      GroupConfiguration groupConfiguration = GroupConfiguration.builder()
            .cooldown(((Double) groupConfigurationMap.get("cooldown")).intValue())
            .minEntities(((Double) groupConfigurationMap.get("minEntities")).intValue())
            .maxEntities(((Double) groupConfigurationMap.get("maxEntities")).intValue())
            .name((String) groupConfigurationMap.get("name"))
            .metadata((Map<String, String>) groupConfigurationMap.get("metadata"))
            .build();

      for(Map<String, Object> scalingPolicyMap : (List<Map<String, Object>>) group.get("scalingPolicies")) {
         ScalingPolicyTargetType targetType = null;
         for(String key : scalingPolicyMap.keySet()) {
            if(ScalingPolicyTargetType.getByValue(key).isPresent()) {
               targetType = ScalingPolicyTargetType.getByValue(key).get();
               break;
            }  
         }

         ImmutableList.Builder<Link> links = ImmutableList.builder();
         for(Map<String, String> linkMap : (List<Map<String, String>>) scalingPolicyMap.get("links")) {
            Link link = Link.builder().href(URI.create(linkMap.get("href"))).relation(Relation.fromValue(linkMap.get("rel"))).build();
            links.add(link);
         }

         Double d = (Double)scalingPolicyMap.get(targetType.toString()); // GSON only knows double now

         ScalingPolicyResponse scalingPolicyResponse = 
               new ScalingPolicyResponse(
                     (String)scalingPolicyMap.get("name"),
                     ScalingPolicyType.getByValue((String)scalingPolicyMap.get("type")).get(),
                     ((Double)scalingPolicyMap.get("cooldown")).intValue(),
                     DoubleMath.isMathematicalInteger(d) ? Integer.toString(d.intValue()) : Double.toString(d), 
                           targetType,
                           ImmutableList.copyOf(links.build()),
                           (String) scalingPolicyMap.get("id")
                     );
         scalingPoliciesList.add(scalingPolicyResponse);
      }

      ImmutableList.Builder<Link> links = ImmutableList.builder();
      for(Map<String, String> linkMap : (List<Map<String, String>>) group.get("links")) {
         Link link = Link.builder().href(URI.create(linkMap.get("href"))).relation(Relation.fromValue(linkMap.get("rel"))).build();
         links.add(link);
      }

      String groupId = (String) group.get("id");
      return Group.builder()
            .id(groupId)
            .scalingPolicy(scalingPoliciesList.build())
            .groupConfiguration(groupConfiguration)
            .launchConfiguration(launchConfiguration)
            .links(links.build())
            .build();
   }
}
