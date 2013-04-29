/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.elb.loadbalancer.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.elb.ELBApi;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.domain.regionscoped.LoadBalancerInRegion;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;
import org.jclouds.location.Region;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ELBListLoadBalancersStrategy implements ListLoadBalancersStrategy {

   private final ELBApi api;
   private final Function<LoadBalancerInRegion, LoadBalancerMetadata> converter;
   private final Supplier<Set<String>> regions;

   @Inject
   protected ELBListLoadBalancersStrategy(ELBApi api, Function<LoadBalancerInRegion, LoadBalancerMetadata> converter,
         @Region Supplier<Set<String>> regions) {
      this.api = checkNotNull(api, "api");
      this.regions = checkNotNull(regions, "regions");
      this.converter = checkNotNull(converter, "converter");
   }

   @Override
   public Iterable<LoadBalancerMetadata> listLoadBalancers() {
      return FluentIterable.from(regions.get()).transformAndConcat(
            new Function<String, Iterable<LoadBalancerMetadata>>() {
               public Iterable<LoadBalancerMetadata> apply(final String from) {
                  return api.getLoadBalancerApiForRegion(from).list().concat()
                        .transform(new Function<LoadBalancer, LoadBalancerMetadata>() {
                           @Override
                           public LoadBalancerMetadata apply(LoadBalancer lb) {
                              return converter.apply(new LoadBalancerInRegion(lb, from));
                           }
                        });
               }
            });
   }
}
