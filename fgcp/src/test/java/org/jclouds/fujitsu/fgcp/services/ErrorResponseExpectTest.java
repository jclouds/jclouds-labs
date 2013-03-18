/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.fujitsu.fgcp.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * @author Dies Koper
 */
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
         assertTrue(e.getMessage().contains("RECONFIG_ING"));
      }
   }

   public void testAuthenticationErrorDueToCertFromDifferentRegion() {
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
         assertTrue(e.getMessage().contains("RESOURCE_NOT_FOUND"));
      }
   }

}
