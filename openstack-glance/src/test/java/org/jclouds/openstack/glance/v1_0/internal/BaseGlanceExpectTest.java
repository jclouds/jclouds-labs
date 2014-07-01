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
package org.jclouds.openstack.glance.v1_0.internal;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.glance.functions.ZoneToEndpointNegotiateVersion;
import org.jclouds.openstack.glance.v1_0.GlanceApi;
import org.jclouds.openstack.keystone.v2_0.internal.KeystoneFixture;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

/**
 * Base class for writing Glance Expect tests
 */
public abstract class BaseGlanceExpectTest extends BaseRestApiExpectTest<GlanceApi> {

   protected HttpRequest keystoneAuthWithUsernameAndPassword;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKey;
   protected String authToken;
   protected HttpResponse responseWithKeystoneAccess;
   protected HttpRequest versionNegotiationRequest;
   protected HttpResponse versionNegotiationResponse;
   protected HttpResponse unmatchedExtensionsOfGlanceResponse;

   public BaseGlanceExpectTest() {
      provider = "openstack-glance";
      keystoneAuthWithUsernameAndPassword = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPasswordAndTenantName(identity,
            credential);
      keystoneAuthWithAccessKeyAndSecretKey = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKeyAndTenantName(identity,
            credential);

      authToken = KeystoneFixture.INSTANCE.getAuthToken();
      responseWithKeystoneAccess = KeystoneFixture.INSTANCE.responseWithAccess();
      // now, createContext arg will need tenant prefix
      identity = KeystoneFixture.INSTANCE.getTenantName() + ":" + identity;
      // version negotiation
      versionNegotiationRequest = HttpRequest.builder().method("GET")
            .endpoint("https://glance.jclouds.org:9292/")
            .addHeader(ZoneToEndpointNegotiateVersion.VERSION_NEGOTIATION_HEADER, "true").build();
      versionNegotiationResponse = HttpResponse.builder().statusCode(300).message("HTTP/1.1 300 Multiple Choices").payload(
            payloadFromResourceWithContentType("/glanceVersionResponse.json", "application/json")).build();
   }
}
