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
package org.jclouds.openstack.swift.v1.internal;

import static com.google.common.base.Preconditions.checkState;

import java.util.Properties;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.BulkDeleteResponse;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

public class BaseSwiftApiLiveTest extends BaseApiLiveTest<SwiftApi> {

   public BaseSwiftApiLiveTest() {
      provider = "openstack-swift";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, KeystoneProperties.CREDENTIAL_TYPE);
      return props;
   }

   protected void deleteAllObjectsInContainer(String regionId, final String containerName) {
      ImmutableList<String> pathsToDelete = api.objectApiInRegionForContainer(regionId, containerName)
            .list(new ListContainerOptions()).transform(new Function<SwiftObject, String>() {

               public String apply(SwiftObject input) {
                  return containerName + "/" + input.name();
               }

            }).toList();
      if (!pathsToDelete.isEmpty()) {
         BulkDeleteResponse response = api.bulkApiInRegion(regionId).bulkDelete(pathsToDelete);
         checkState(response.errors().isEmpty(), "Errors deleting paths %s: %s", pathsToDelete, response);
      }
   }
}
