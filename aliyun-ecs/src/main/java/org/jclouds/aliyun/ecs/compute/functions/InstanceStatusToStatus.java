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
package org.jclouds.aliyun.ecs.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import org.jclouds.aliyun.ecs.domain.Instance;
import org.jclouds.compute.domain.NodeMetadata;

import javax.inject.Singleton;

/**
 * Transforms an {@link Instance.Status} to the jclouds portable model.
 */
@Singleton
public class InstanceStatusToStatus implements Function<Instance.Status, NodeMetadata.Status> {

   private static final Function<Instance.Status, NodeMetadata.Status> toPortableStatus = Functions.forMap(
         ImmutableMap.<Instance.Status, NodeMetadata.Status>builder()
                 .put(Instance.Status.STARTING, NodeMetadata.Status.PENDING)
                 .put(Instance.Status.STOPPING, NodeMetadata.Status.PENDING)
                 .put(Instance.Status.STOPPED, NodeMetadata.Status.SUSPENDED)
                 .put(Instance.Status.RUNNING, NodeMetadata.Status.RUNNING).build(), NodeMetadata.Status.UNRECOGNIZED);

   @Override
   public NodeMetadata.Status apply(Instance.Status input) {
      return toPortableStatus.apply(input);
   }
}
