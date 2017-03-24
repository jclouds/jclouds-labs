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
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlApiLiveTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

@Test(groups = "live", testName = "AccountApiLiveTest")
public class AccountApiLiveTest extends BaseDimensionDataCloudControlApiLiveTest {
   @Test
   public void testGetAccount() {
      Account account = api().getMyAccount();
      assertNotNull(account);
      assertNotNull(account.userName());
      assertNotNull(account.fullName());
      assertNotNull(account.firstName());
      assertNotNull(account.lastName());
      assertNotNull(account.emailAddress());
      assertNotNull(account.organization());
      assertNotNull(account.organization().id());
      assertNotNull(account.organization().name());
      assertNotNull(account.organization().homeGeoName());
      assertNotNull(account.organization().homeGeoApiHost());
      assertNotNull(account.organization().homeGeoId());
      assertNotNull(account.state());
   }

   private AccountApi api() {
      return api.getAccountApi();
   }
}
