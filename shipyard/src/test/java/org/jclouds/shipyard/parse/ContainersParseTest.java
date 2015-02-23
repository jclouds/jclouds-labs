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
package org.jclouds.shipyard.parse;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.shipyard.domain.containers.ContainerImageInfo;
import org.jclouds.shipyard.domain.containers.ContainerInfo;
import org.jclouds.shipyard.domain.engines.EngineSettingsInfo;
import org.jclouds.shipyard.domain.images.ImagePortsInfo;
import org.jclouds.shipyard.internal.BaseShipyardParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Test(groups = "unit")
public class ContainersParseTest extends BaseShipyardParseTest<List<ContainerInfo>> {

   @Override
   public String resource() {
      return "/containers.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public List<ContainerInfo> expected() {
      
      return ImmutableList.of(
              ContainerInfo.create("e2f6784b75ed8768e83b7ec46ca8ef784941f6ce4c53231023804277965da1d2",
                    "/atlassian-stash",
                    ContainerImageInfo.create("nkatsaros/atlassian-stash:3.5", 
                             ImmutableMap.<String, String>of("STASH_HOME", "/var/atlassian/stash", "STASH_VERSION", "3.5.0"), 
                             ImmutableList.<String>of("/docker-entrypoint.sh"), 
                             "e2f6784b75ed", 
                             ImmutableList.<ImagePortsInfo>of(ImagePortsInfo.create("tcp", "0.0.0.0", 8089, 8080)), 
                             ImmutableList.<String>of("/var/atlassian/stash"), 
                             ImmutableMap.<String, String>of(), 
                             true, 
                             "bridge"), 
                    EngineSettingsInfo.create("sdrelnx150", 
                          "http://sdrelnx150:2375", 
                          8, 
                          8096, 
                          ImmutableList.<String>of("sdrelnx150")), 
                    "stopped", 
                    ImmutableList.<ImagePortsInfo>of(ImagePortsInfo.create("tcp", "0.0.0.0", 8089, 8080)))
      );
   }
}
