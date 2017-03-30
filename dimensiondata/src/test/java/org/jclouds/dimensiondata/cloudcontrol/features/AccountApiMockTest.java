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
package org.jclouds.dimensiondata.cloudcontrol.features;

import org.jclouds.dimensiondata.cloudcontrol.domain.Account;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlMockTest;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Test(groups = "unit", testName = "AccountApiMockTest", singleThreaded = true)
public class AccountApiMockTest extends BaseDimensionDataCloudControlMockTest {

   @Test
   public void testGetAccount() throws Exception {
      server.enqueue(jsonResponse("/account.json"));
      Account account = api.getAccountApi().getMyAccount();
      assertNotNull(account);
      assertSent(HttpMethod.GET, "/" + VERSION + "/user/myUser");
   }

   @Test
   public void testGetAccount_404() throws Exception {
      server.enqueue(response404());
      Account account = api.getAccountApi().getMyAccount();
      assertNull(account);
      assertSent(HttpMethod.GET, "/" + VERSION + "/user/myUser");
   }

}
