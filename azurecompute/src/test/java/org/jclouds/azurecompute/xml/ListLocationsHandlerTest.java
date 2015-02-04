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
import java.util.List;

import org.jclouds.azurecompute.domain.Location;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ListLocationsHandlerTest")
public class ListLocationsHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/locations.xml");
      List<Location> result = factory.create(new ListLocationsHandler(new LocationHandler())).parse(is);

      assertEquals(result, expected());
   }

   public static List<Location> expected() {
      List<String> availableServices = ImmutableList.of("Compute", "Storage", "PersistentVMRole");
      return ImmutableList.of( //
            Location.create("West US", "West US", availableServices), //
            Location.create("East US", "East US", availableServices), //
            Location.create("East Asia", "East Asia", availableServices), //
            Location.create("Southeast Asia", "Southeast Asia", availableServices), //
            Location.create("North Europe", "North Europe", availableServices), //
            Location.create("West Europe", "West Europe", availableServices) //
      );
   }

}
