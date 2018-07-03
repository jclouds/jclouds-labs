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
package org.jclouds.aliyun.ecs.compute.internal;

import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.domain.internal.Regions;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.compute.config.ComputeServiceProperties;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

public class BaseECSComputeServiceApiLiveTest extends BaseApiLiveTest<ECSComputeServiceApi> {

   protected static final String TEST_REGION = System.getProperty("test.alibaba-ecs.region", Regions.EU_CENTRAL_1.getName());
   protected static final String TEST_ZONE = System.getProperty("test.alibaba-ecs.zone", TEST_REGION + "a");

   protected Predicate<String> instanceRunningPredicate;
   protected Predicate<String> instanceSuspendedPredicate;

   public BaseECSComputeServiceApiLiveTest() {
      provider = "alibaba-ecs";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(ComputeServiceProperties.POLL_INITIAL_PERIOD, 1000);
      props.put(ComputeServiceProperties.POLL_MAX_PERIOD, 10000);
      props.put(ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE, TimeUnit.MINUTES.toMillis(45));
      return props;
   }

   @Override
   protected ECSComputeServiceApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();

      instanceRunningPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(TIMEOUT_NODE_RUNNING)));
      instanceSuspendedPredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }, Names.named(TIMEOUT_NODE_SUSPENDED)));

      return injector.getInstance(ECSComputeServiceApi.class);
   }

}
