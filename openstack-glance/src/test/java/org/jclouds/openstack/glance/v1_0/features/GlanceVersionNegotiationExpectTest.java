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
package org.jclouds.openstack.glance.v1_0.features;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.glance.v1_0.GlanceApi;
import org.jclouds.openstack.glance.v1_0.internal.BaseGlanceExpectTest;
import org.jclouds.openstack.glance.v1_0.parse.ParseImagesTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "GlanceVersionNegotiationExpectTest")
public class GlanceVersionNegotiationExpectTest extends BaseGlanceExpectTest {

    /*
     * Test that if Glance returns a URL for a version with a different scheme
     * than we used for the base endpoint we use the scheme associated w/ the
     * base endpoint.
     *
     * This is useful for when Glance is behind a proxy.
     */
   public void testSchemeMismatch() throws Exception {
      // The referenced resource contains http endpoints for versions instead of the https endpoint returned by Keystone
      versionNegotiationResponse = HttpResponse.builder().statusCode(300).message("HTTP/1.1 300 Multiple Choices").payload(
            payloadFromResourceWithContentType("/glanceVersionResponseSchemeMismatch.json", "application/json")).build();

      HttpRequest list = HttpRequest.builder().method("GET")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();


      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/images.json")).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, versionNegotiationRequest, versionNegotiationResponse,
            list, listResponse);

      assertEquals(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").list().concat().toString(),
            new ParseImagesTest().expected().toString());
   }

    /*
     * Test that Glance version negotiation fails with an exception if there is
     * no endpoint returned for the requested version.
     */
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testNonExistentVersion() throws Exception {
      // The referenced resource only an endpoint for v999.999 of the GlanceApi
      HttpResponse localVersionNegotiationResponse = HttpResponse.builder().statusCode(300).message("HTTP/1.1 300 Multiple Choices").payload(
            payloadFromResourceWithContentType("/glanceVersionResponseVersionUnavailable.json", "application/json")).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, versionNegotiationRequest, localVersionNegotiationResponse);

      apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").list();
   }

    /*
     * Test that Glance version negotiation happens with the base endpoint if
     * the Keystone catalog returns an already versioned endpoint for Glance
     */
   public void testKeystoneReturnsVersionedEndpoint() throws Exception {
      // This sets the keystone response to return a Glance endpoint w/ version already present
      HttpResponse localResponseWithKeystoneAccess = HttpResponse.builder().statusCode(200).message("HTTP/1.1 200").payload(
            payloadFromResourceWithContentType("/keystoneAuthResponseVersionedGlanceEndpoint.json", "application/json")).build();

      HttpRequest list = HttpRequest.builder().method("GET")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();


      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/images.json")).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            localResponseWithKeystoneAccess, versionNegotiationRequest, versionNegotiationResponse,
            list, listResponse);

      assertEquals(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").list().concat().toString(),
            new ParseImagesTest().expected().toString());
   }
}
