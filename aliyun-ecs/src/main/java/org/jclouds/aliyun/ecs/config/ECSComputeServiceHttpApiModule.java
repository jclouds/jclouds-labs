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
package org.jclouds.aliyun.ecs.config;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.handlers.ECSComputeServiceErrorHandler;
import org.jclouds.aliyun.ecs.handlers.ECSErrorRetryHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;

import javax.inject.Singleton;
import java.util.Set;

@ConfiguresHttpApi
public class ECSComputeServiceHttpApiModule extends HttpApiModule<ECSComputeServiceApi> {

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ECSComputeServiceErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ECSComputeServiceErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ECSComputeServiceErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(ECSErrorRetryHandler.class);
   }

   /**
    * It combines the error codes explicitly described as retryable from ECS and VPC
    * https://error-center.alibabacloud.com/status/product/Ecs?spm=a2c63.p38356.a3.5.2a9859c1Fzi5nr
    * https://error-center.alibabacloud.com/status/product/Vpc?spm=a2c63.p38356.a3.1.1442dd2f4qFMSW
    */
   @Provides
   @ClientError
   @Singleton
   protected final Set<String> provideRetryableCodes() {
      return ImmutableSet.of("InstanceNotReady", "IncorrectInstanceStatus.Initializing", "DependencyViolation", "IncorrectVpcStatus", "IncorrectStatus");
   }

}
