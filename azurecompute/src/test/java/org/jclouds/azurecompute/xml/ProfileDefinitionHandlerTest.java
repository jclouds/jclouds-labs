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
package org.jclouds.azurecompute.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;
import org.jclouds.azurecompute.domain.ProfileDefinition;
import org.jclouds.azurecompute.domain.ProfileDefinition.Status;
import org.jclouds.azurecompute.domain.ProfileDefinitionEndpoint;
import org.jclouds.azurecompute.domain.ProfileDefinitionMonitor;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ProfileDefinitionHandlerTest")
public class ProfileDefinitionHandlerTest extends BaseHandlerTest {

   public void test() {
      final InputStream is = getClass().getResourceAsStream("/profiledefinition.xml");
      final ProfileDefinition result = factory.create(new ProfileDefinitionHandler(
              new ProfileDefinitionMonitorHandler(), new ProfileDefinitionEndpointHandler())).parse(is);
      assertEquals(result, expected());
   }

   public static ProfileDefinition expected() {
      final ArrayList<ProfileDefinitionMonitor> monitors = new ArrayList<ProfileDefinitionMonitor>();
      monitors.add(ProfileDefinitionMonitor.create(
              30, 10, 3, ProfileDefinition.Protocol.HTTP, 80, "GET", "/", 200));

      final ArrayList<ProfileDefinitionEndpoint> endpoints = new ArrayList<ProfileDefinitionEndpoint>();
      endpoints.add(ProfileDefinitionEndpoint.create("jclouds1.cloudapp.net",
              Status.ENABLED,
              ProfileDefinition.HealthStatus.STOPPED,
              ProfileDefinitionEndpoint.Type.CLOUDSERVICE,
              null,
              1,
              null));

      endpoints.add(ProfileDefinitionEndpoint.create("jclouds2.cloudapp.net",
              Status.ENABLED,
              ProfileDefinition.HealthStatus.STOPPED,
              ProfileDefinitionEndpoint.Type.CLOUDSERVICE,
              null,
              1,
              null));

      return ProfileDefinition.create(300,
              Status.ENABLED,
              "1",
              monitors,
              ProfileDefinition.LBMethod.ROUNDROBIN,
              endpoints,
              ProfileDefinition.HealthStatus.INACTIVE
      );
   }
}
