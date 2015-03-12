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

import org.jclouds.azurecompute.domain.AffinityGroup;
import org.jclouds.azurecompute.domain.AffinityGroup.ComputeCapabilities;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ListAffinityGroupsHandlerTest")
public class ListAffinityGroupsHandlerTest extends BaseHandlerTest {

   public void test() {
      final InputStream input = getClass().getResourceAsStream("/affinityGroups.xml");
      final List<AffinityGroup> result = factory.create(new ListAffinityGroupsHandler(
              new AffinityGroupHandler(new ComputeCapabilitiesHandler()))).parse(input);

      assertEquals(result, expected());
   }

   public static List<AffinityGroup> expected() {
      return ImmutableList.of(
              AffinityGroup.create(
                      "Test1",
                      "Test1",
                      "Test 1 description",
                      "West Europe",
                      ImmutableList.of(
                              AffinityGroup.Capability.PersistentVMRole,
                              AffinityGroup.Capability.HighMemory
                      ),
                      new SimpleDateFormatDateService().iso8601DateOrSecondsDateParse("2015-03-09T15:15:29Z"),
                      ComputeCapabilities.create(
                              ImmutableList.of(
                                      RoleSize.Type.A10,
                                      RoleSize.Type.A11
                              ),
                              ImmutableList.of(
                                      RoleSize.Type.A10,
                                      RoleSize.Type.A11)
                      )
              ),
              AffinityGroup.create(
                      "Test2",
                      "Test2",
                      null,
                      "Southeast Asia",
                      ImmutableList.of(
                              AffinityGroup.Capability.PersistentVMRole,
                              AffinityGroup.Capability.HighMemory
                      ),
                      new SimpleDateFormatDateService().iso8601DateOrSecondsDateParse("2015-03-09T15:16:10Z"),
                      ComputeCapabilities.create(
                              ImmutableList.of(
                                      RoleSize.Type.EXTRALARGE,
                                      RoleSize.Type.EXTRASMALL
                              ),
                              ImmutableList.of(
                                      RoleSize.Type.EXTRALARGE,
                                      RoleSize.Type.EXTRASMALL
                              )
                      )
              )
      );
   }
}
