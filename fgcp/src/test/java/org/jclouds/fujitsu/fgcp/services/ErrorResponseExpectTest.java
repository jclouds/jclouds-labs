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
package org.jclouds.fujitsu.fgcp.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ErrorResponseExpectTest", singleThreaded = true)
public class ErrorResponseExpectTest extends BaseFGCPRestApiExpectTest {

   public void testIllegalStateDueToSystemReconfiguration() {
      HttpRequest request = buildGETWithQuery("Action=CreateVServer" + "&vserverName=vm1" + "&vserverType=economy"
            + "&diskImageId=IMG_A1B2C3_1234567890ABCD" + "&networkId=ABCDEFGH-A123B456CE-N-DMZ"
            + "&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse.builder().statusCode(500)
            .payload(payloadFromResource("/IllegalState_RECONFIG_ING-response.xml")).build();
      VirtualSystemApi api = requestSendsResponse(request, response).getVirtualSystemApi();

      try {
         api.createServer("vm1", "economy", "IMG_A1B2C3_1234567890ABCD", "ABCDEFGH-A123B456CE-N-DMZ");
         fail("should have thrown an exception");
      } catch (IllegalStateException e) {
         assertTrue(e.getMessage().contains("RECONFIG_ING"), e.getMessage());
      }
   }

   public void testAuthorizationErrorDueToCertFromDifferentRegion() {
      HttpRequest request = buildGETWithQuery("Action=CreateVServer" + "&vserverName=vm1" + "&vserverType=economy"
            + "&diskImageId=IMG_A1B2C3_1234567890ABCD" + "&networkId=ABCDEFGH-A123B456CE-N-DMZ"
            + "&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse.builder().statusCode(500)
            .payload(payloadFromResource("/Auth_UserNotInSelectData-response.xml")).build();
      VirtualSystemApi api = requestSendsResponse(request, response).getVirtualSystemApi();

      try {
         api.createServer("vm1", "economy", "IMG_A1B2C3_1234567890ABCD", "ABCDEFGH-A123B456CE-N-DMZ");
         fail("should have thrown an exception");
      } catch (AuthorizationException e) {
         assertEquals(e.getMessage(), "ERROR: User not found in selectData. searchKey:userid");
      }
   }

   public void testIllegalStateDueToStartingRunningServer() {
      HttpRequest request = buildGETWithQuery("Action=StartVServer" + "&vserverId=ABCDEFGH-A123B456CE-S-0004"
            + "&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse.builder().statusCode(500)
            .payload(payloadFromResource("/IllegalState_RUNNING-response.xml")).build();
      VirtualServerApi api = requestSendsResponse(request, response).getVirtualServerApi();

      try {
         api.start("ABCDEFGH-A123B456CE-S-0004");
         fail("should have thrown an exception");
      } catch (IllegalStateException e) {
         assertTrue(e.getMessage().contains("RUNNING"), e.getMessage());
      }
   }

   public void testIllegalStateDueToStoppingStoppedServer() {
      HttpRequest request = buildGETWithQuery("Action=StopVServer" + "&vserverId=ABCDEFGH-A123B456CE-S-0004"
            + "&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse.builder().statusCode(500)
            .payload(payloadFromResource("/IllegalState_ALREADY_STOPPED-response.xml")).build();
      VirtualServerApi api = requestSendsResponse(request, response).getVirtualServerApi();

      try {
         api.stop("ABCDEFGH-A123B456CE-S-0004");
         fail("should have thrown an exception");
      } catch (IllegalStateException e) {
         assertTrue(e.getMessage().contains("ALREADY_STOPPED"), e.getMessage());
      }
   }

   public void testErrorDueToWrongResourceId() {
      HttpRequest request = buildGETWithQuery("Action=CreateVServer" + "&vserverName=vm1" + "&vserverType=economy"
            + "&diskImageId=IMG_DOES_NOT_EXIST" + "&networkId=ABCDEFGH-A123B456CE-N-DMZ"
            + "&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse.builder().statusCode(500)
            .payload(payloadFromResource("/RESOURCE_NOT_FOUND-response.xml")).build();
      VirtualSystemApi api = requestSendsResponse(request, response).getVirtualSystemApi();

      try {
         api.createServer("vm1", "economy", "IMG_DOES_NOT_EXIST", "ABCDEFGH-A123B456CE-N-DMZ");
         fail("should have thrown an exception");
      } catch (ResourceNotFoundException e) {
         assertTrue(e.getMessage().contains("RESOURCE_NOT_FOUND"), e.getMessage());
      }
   }

}
