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
import com.google.common.collect.ImmutableMap;

import org.jclouds.azurecompute.domain.Profile;
import org.jclouds.azurecompute.domain.ProfileDefinition;

@Test(groups = "unit", testName = "ListProfilesHandlerTest")
public class ListProfilesHandlerTest extends BaseHandlerTest {

   public void test() {
      final InputStream is = getClass().getResourceAsStream("/listprofiles.xml");
      final ListProfilesHandler handler = new ListProfilesHandler(new ProfileHandler());
      final List<Profile> result = factory.create(handler).parse(is);
      assertEquals(result, expected());
   }

   public static List<Profile> expected() {
      return ImmutableList.of(
              Profile.create("jclouds.trafficmanager.net",
                      "jclouds",
                      ProfileDefinition.Status.ENABLED,
                      "1",
                      ImmutableMap.<String, ProfileDefinition.Status>builder().put("1", ProfileDefinition.Status.ENABLED).build()
              ),
              Profile.create("jclouds2.trafficmanager.net",
                      "jclouds2",
                      ProfileDefinition.Status.DISABLED,
                      null,
                      ImmutableMap.<String, ProfileDefinition.Status>builder().build()
              ));
   }
}
