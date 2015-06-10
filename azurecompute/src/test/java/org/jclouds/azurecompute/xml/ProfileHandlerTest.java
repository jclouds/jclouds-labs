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

import org.jclouds.azurecompute.domain.Profile;
import org.jclouds.azurecompute.domain.ProfileDefinition.Status;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

@Test(groups = "unit", testName = "ProfileHandlerTest")
public class ProfileHandlerTest extends BaseHandlerTest {

   public void test() {
      final InputStream is = getClass().getResourceAsStream("/profile.xml");
      final Profile result = factory.create(new ProfileHandler()).parse(is);
      assertEquals(result, expected());
   }

   public static Profile expected() {
      return Profile.create("jclouds.trafficmanager.net",
              "jclouds",
              Status.ENABLED,
              "1",
              ImmutableMap.<String, Status>builder().put("1", Status.ENABLED).build()
      );
   }
}
