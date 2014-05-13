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
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy.ScalingPolicyTargetType;
import org.jclouds.rackspace.autoscale.v1.domain.CreateScalingPolicy.ScalingPolicyType;
import org.jclouds.rackspace.autoscale.v1.domain.ScalingPolicy;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.math.DoubleMath;
import com.google.inject.Inject;

/**
 * This parses the scaling policy response and decouples domain objects from the json object returned by the service.
 * @author Zack Shoylev
 */
public class ParseScalingPolicyResponse implements Function<HttpResponse, ScalingPolicy> {

   private final ParseJson<Map<String, Object>> json;

   @Inject
   ParseScalingPolicyResponse(ParseJson<Map<String, Object>> json) {
      this.json = checkNotNull(json, "json");
   }

   /**
    * Parse a single scaling policy response
    */
   @SuppressWarnings("unchecked")
   public ScalingPolicy apply(HttpResponse from) {
      // This needs to be refactored when the service is in a more final state and changing less often
      // A lot of the complexity is expected to go away

      Map<String, Object> singleMap = json.apply(from);
      Map<String, Object> scalingPolicyMap = (Map<String, Object>) singleMap.get("policy");

      ScalingPolicyTargetType targetType = null;
      for (String key : scalingPolicyMap.keySet()) {
         if (ScalingPolicyTargetType.getByValue(key).isPresent()) {
            targetType = ScalingPolicyTargetType.getByValue(key).get();
            break;
         }  
      }

      ImmutableList.Builder<Link> links = ImmutableList.builder();
      for (Map<String, String> linkMap : (List<Map<String, String>>) scalingPolicyMap.get("links")) {
         Link link = Link.builder().href(URI.create(linkMap.get("href"))).relation(Relation.fromValue(linkMap.get("rel"))).build();
         links.add(link);
      }

      Double d = (Double)scalingPolicyMap.get(targetType.toString()); // GSON only knows double now
      ScalingPolicy scalingPolicyResponse =
            new ScalingPolicy(
                  (String)scalingPolicyMap.get("name"),
                  ScalingPolicyType.getByValue((String)scalingPolicyMap.get("type")).get(),
                  ((Double)scalingPolicyMap.get("cooldown")).intValue(),
                  DoubleMath.isMathematicalInteger(d) ? Integer.toString(d.intValue()) : Double.toString(d),
                        targetType,
                        (Map<String, String>) scalingPolicyMap.get("args"),
                        ImmutableList.copyOf(links.build()),
                        (String) scalingPolicyMap.get("id")
                  );

      return scalingPolicyResponse;
   }
}
