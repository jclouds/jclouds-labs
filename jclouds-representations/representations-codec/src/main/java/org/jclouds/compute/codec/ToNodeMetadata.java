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

package org.jclouds.compute.codec;

import com.google.common.base.Function;
import org.jclouds.compute.representations.NodeMetadata;
import org.jclouds.javax.annotation.Nullable;

public enum ToNodeMetadata implements Function<org.jclouds.compute.domain.NodeMetadata, NodeMetadata> {

   INSTANCE;

   @Override
   public NodeMetadata apply(@Nullable org.jclouds.compute.domain.NodeMetadata input) {
      if (input == null) {
         return null;
      }
      return NodeMetadata.builder().id(input.getId()).name(input.getName()).status(input.getStatus().name())
                           .hostname(input.getHostname())
                           .loginPort(input.getLoginPort()).group(input.getGroup())
                           .tags(input.getTags()).metadata(input.getUserMetadata())
                           .locationId(input.getLocation() != null ? input.getLocation().getId() : null)
                           .imageId(input.getImageId())
                           .defaultCredentials(ToLoginCredentials.INSTANCE.apply(input.getCredentials())).build();
   }
}
