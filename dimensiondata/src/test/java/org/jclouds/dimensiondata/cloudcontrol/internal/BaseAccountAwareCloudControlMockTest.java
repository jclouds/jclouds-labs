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
package org.jclouds.dimensiondata.cloudcontrol.internal;

import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.testng.annotations.BeforeMethod;

import javax.ws.rs.HttpMethod;

/**
 * Base class for Dimension Data Cloud Control mock tests for API calls that include account information.
 * <p>
 * The first time an account-aware method is called in a testcase the 'myUser' endpoint should be called to look up
 * the account for the user. This class checks that this call happened before the first call to other endpoints.
 */
public class BaseAccountAwareCloudControlMockTest extends BaseDimensionDataCloudControlMockTest {

   private boolean accountRetrieved;

   @Override
   protected void applyAdditionalServerConfig() {
      server.enqueue(jsonResponse("/account.json"));
   }

   @BeforeMethod
   public void reset() {
      accountRetrieved = false;
   }

   @Override
   protected RecordedRequest assertSent(String method, String path) throws InterruptedException {
      if (!accountRetrieved) {
         super.assertSent(HttpMethod.GET, "/caas/2.4/user/myUser");
         accountRetrieved = true;
      }
      return super.assertSent(method, path);
   }
}
