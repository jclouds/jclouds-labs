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
package org.jclouds.azurecompute.features;

import static org.testng.Assert.assertTrue;

import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.azurecompute.domain.ServiceCertificateParams;
import org.jclouds.azurecompute.xml.ListServiceCertificatessHandlerTest;
import org.jclouds.azurecompute.xml.ServiceCertificateHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test(groups = "unit", testName = "ServiceCertificatesApiMockTest")
public class ServiceCertificatesApiMockTest extends BaseAzureComputeApiMockTest {

   public void listWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/servicecertificates.xml"));

      try {
         final ServiceCertificatesApi api = api(server.getUrl("/")).getServiceCertificatesApi();
         assertEquals(api.list("myservice"), ListServiceCertificatessHandlerTest.expected());
         assertSent(server, "GET", "/services/hostedservices/myservice/certificates");
      } finally {
         server.shutdown();
      }
   }

   public void listWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final ServiceCertificatesApi api = api(server.getUrl("/")).getServiceCertificatesApi();
         assertTrue(api.list("myservice").isEmpty());
         assertSent(server, "GET", "/services/hostedservices/myservice/certificates");
      } finally {
         server.shutdown();
      }
   }

   public void getWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/servicecertificate.xml"));

      try {
         final ServiceCertificatesApi api = api(server.getUrl("/")).getServiceCertificatesApi();
         assertEquals(api.get("myservice", "sha1", "8D6ED1395205C57D23E518672903FDAF144EE8AE"),
                 ServiceCertificateHandlerTest.expected());
         assertSent(server, "GET",
                 "/services/hostedservices/myservice/certificates/sha1-8D6ED1395205C57D23E518672903FDAF144EE8AE");
      } finally {
         server.shutdown();
      }
   }

   public void getWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final ServiceCertificatesApi api = api(server.getUrl("/")).getServiceCertificatesApi();
         assertNull(api.get("myservice", "sha1", "8D6ED1395205C57D23E518672903FDAF144EE8AE"));
         assertSent(server, "GET",
                 "/services/hostedservices/myservice/certificates/sha1-8D6ED1395205C57D23E518672903FDAF144EE8AE");
      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final ServiceCertificatesApi api = api(server.getUrl("/")).getServiceCertificatesApi();
         assertEquals(api.delete("myservice", "sha1", "8D6ED1395205C57D23E518672903FDAF144EE8AE"), "request-1");
         assertSent(server, "DELETE",
                 "/services/hostedservices/myservice/certificates/sha1-8D6ED1395205C57D23E518672903FDAF144EE8AE");

      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final ServiceCertificatesApi api = api(server.getUrl("/")).getServiceCertificatesApi();
         assertNull(api.delete("myservice", "sha1", "8D6ED1395205C57D23E518672903FDAF144EE8AE"));
         assertSent(server, "DELETE",
                 "/services/hostedservices/myservice/certificates/sha1-8D6ED1395205C57D23E518672903FDAF144EE8AE");
      } finally {
         server.shutdown();
      }
   }

   public void add() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final ServiceCertificatesApi api = api(server.getUrl("/")).getServiceCertificatesApi();

         final ServiceCertificateParams params = ServiceCertificateParams.builder().
                 data("MIIDyzCCArOgAwIBAgICEAcwDQYJKoZIhvcNAQELBQAwfzELMAkGA1UEBhMCSVQxDjAMBgNVBAgMBUl0YWx5MRAwDgYDVQQ"
                         + "HDAdQZXNjYXJhMQ8wDQYDVQQKDAZUaXJhc2ExDDAKBgNVBAsMA0lUQzEPMA0GA1UEAwwGVGlyYXNhMR4wHAYJKoZIh"
                         + "vcNAQkBFg9pbmZvQHRpcmFzYS5uZXQwHhcNMTUwMzA0MTQ1MzQwWhcNMTYwMzAzMTQ1MzQwWjBVMQswCQYDVQQGEwJ"
                         + "JVDEQMA4GA1UECAwHUGVzY2FyYTEPMA0GA1UECgwGVGlyYXNhMQswCQYDVQQLDAJBTTEWMBQGA1UEAwwNYW0udGlyY"
                         + "XNhLm5ldDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMzqTZFbzahmEqp60txg8aUYw4Y7PL44A7rzHVn"
                         + "b5cb01/4VVjNeijmROOL8o5ZEbkNkQly43zjoZcrkw4bpvOz95OP8/NH/ZgyYKR42VqcTlxcj/22iq2Ie1XhWsKARm"
                         + "ObdnNUcFCsdqXWXBo0bLF+WuUYh4ZoMxFMlP7YYl7WOCCgekE8E9sL02RuLZgq7v2M6fsxhT5rEG81jzUlmY5c/jXZ"
                         + "KbUIBaltKtzC3DnBpuk9u+S87WseqTeuhUzF6VvgwmiQ+zeHdr5Hjqxrvmq445DPz+2U3PYN1vFoB/6QzjtZVakSfO"
                         + "SZ0YAtFhZFHmR11kJTNMfVQ5k5oIQPtHksCAwEAAaN7MHkwCQYDVR0TBAIwADAsBglghkgBhvhCAQ0EHxYdT3BlblN"
                         + "TTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYEFOIYM6WyNdc4odsThFVtOefT/xg1MB8GA1UdIwQYMBaAF"
                         + "DqheOl4dpXYelhPC/bM+VdN1AXpMA0GCSqGSIb3DQEBCwUAA4IBAQB33qLYghIYa2j1ycHBpeZVadsb8xb4AnfnAW9"
                         + "g5dYfZP1eIvmKzOxN3CjpuCRKNI4vyKHiLbucfFDl5zi9BdYwwdduPbYTgE8F8Ilyit3irSRJFk1wHICX0sBPq5ulz"
                         + "39MPZsP2Fmzbrphr9BrRZOc1RJdHnj8C7phrfBneGSfwoY+qH5H6/h5A5rS8oDAraeklR2RJK4ztK+yDvp8orRDJQq"
                         + "5LAALQtWDhdW8Qj7WoIbGUeB77aJLluLOgriJLK+kKaGoUuAaKFRJXPyTmtUC17CJUJbapmtDwivILhU/dSdz6+1YX"
                         + "Tg0ddNNlug3I6L5VVRnlwJJc/hIna1VjQJO").
                 format("pfx").
                 password("password").build();

         assertEquals(api.add("myservice", params), "request-1");
         assertSent(server, "POST", "/services/hostedservices/myservice/certificates", "/servicecertificateparams.xml");
      } finally {
         server.shutdown();
      }
   }
}
