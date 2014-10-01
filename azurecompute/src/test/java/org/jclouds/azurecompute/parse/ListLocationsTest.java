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
package org.jclouds.azurecompute.parse;

import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import java.util.List;
import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.xml.ListLocationsHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "LocationsTest")
public class ListLocationsTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/locations.xml");

      List<Location> expected = expected();

      ListLocationsHandler handler = injector.getInstance(ListLocationsHandler.class);
      List<Location> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public static List<Location> expected() {
      List<String> availableServices = ImmutableList.of("Compute", "Storage", "PersistentVMRole");
      return ImmutableList.<Location>builder()
                         .add(Location.builder()
                                      .name("West US")
                                      .displayName("West US")
                                      .availableServices(availableServices)
                                      .build())
                         .add(Location.builder()
                                      .name("East US")
                                      .displayName("East US")
                                      .availableServices(availableServices)
                                      .build())
                         .add(Location.builder()
                                      .name("East Asia")
                                      .displayName("East Asia")
                                      .availableServices(availableServices)
                                      .build())
                         .add(Location.builder()
                                      .name("Southeast Asia")
                                      .displayName("Southeast Asia")
                                      .availableServices(availableServices)
                                      .build())
                         .add(Location.builder()
                                      .name("North Europe")
                                      .displayName("North Europe")
                                      .availableServices(availableServices)
                                      .build())
                         .add(Location.builder()
                                      .name("West Europe")
                                      .displayName("West Europe")
                                      .availableServices(availableServices)
                                      .build()).build();
   }

}
