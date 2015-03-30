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

import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import org.jclouds.azurecompute.domain.NetworkSecurityGroup;

@Test(groups = "unit", testName = "ListNetworkSecurityGroupsHandlerTest")
public class ListNetworkSecurityGroupsHandlerTest extends BaseHandlerTest {

   public void test() {
      final InputStream is = getClass().getResourceAsStream("/networksecuritygroups.xml");
      final List<NetworkSecurityGroup> result = factory.create(
              new ListNetworkSecurityGroupsHandler(new NetworkSecurityGroupHandler())).parse(is);

      assertEquals(result, expected());
   }

   public static List<NetworkSecurityGroup> expected() {
      return ImmutableList.of(
              NetworkSecurityGroup.create("group1", "sec group 1", "West Europe", null, null),
              NetworkSecurityGroup.create("group2", "sec group 2", "North Europe", null, null));
   }

}
