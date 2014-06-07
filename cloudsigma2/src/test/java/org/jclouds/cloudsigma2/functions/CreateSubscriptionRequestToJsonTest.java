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
package org.jclouds.cloudsigma2.functions;

import com.google.gson.JsonObject;
import com.google.inject.Guice;
import org.jclouds.cloudsigma2.domain.CreateSubscriptionRequest;
import org.jclouds.cloudsigma2.domain.SubscriptionResource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class CreateSubscriptionRequestToJsonTest {

   private static final CreateSubscriptionRequestToJson CREATE_SUBSCRIPTION_REQUEST_TO_JSON = Guice
         .createInjector()
         .getInstance(CreateSubscriptionRequestToJson.class);

   private CreateSubscriptionRequest input;
   private JsonObject expected;

   @BeforeMethod
   public void setUp() throws Exception {
      input = new CreateSubscriptionRequest.Builder()
            .amount("30000")
            .period("1 month")
            .resource(SubscriptionResource.DSSD)
            .build();

      expected = new JsonObject();
      expected.addProperty("amount", "30000");
      expected.addProperty("period", "1 month");
      expected.addProperty("resource", "dssd");
   }

   public void test() {
      Assert.assertEquals(CREATE_SUBSCRIPTION_REQUEST_TO_JSON.apply(input), expected);
   }
}
