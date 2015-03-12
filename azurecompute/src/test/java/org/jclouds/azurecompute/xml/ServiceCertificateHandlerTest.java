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
import org.jclouds.azurecompute.domain.ServiceCertificate;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ServiceCertificateHandlerTest")
public class ServiceCertificateHandlerTest extends BaseHandlerTest {

   public void test() {
      final InputStream is = getClass().getResourceAsStream("/servicecertificate.xml");
      final ServiceCertificate result = factory.create(new ServiceCertificateHandler()).parse(is);
      assertEquals(result, expected());
   }

   public static ServiceCertificate expected() {
      return ServiceCertificate.create(
              null,
              null,
              null,
              "MIIDyzCCArOgAwIBAgICEAcwDQYJKoZIhvcNAQELBQAwfzELMAkGA1UEBhMCSVQxDjAMBgNVBAgMBUl0YWx5MRAwDgYDVQQHDAdQZXN"
              + "jYXJhMQ8wDQYDVQQKDAZUaXJhc2ExDDAKBgNVBAsMA0lUQzEPMA0GA1UEAwwGVGlyYXNhMR4wHAYJKoZIhvcNAQkBFg9pbmZvQHRp"
              + "cmFzYS5uZXQwHhcNMTUwMzA0MTQ1MzQwWhcNMTYwMzAzMTQ1MzQwWjBVMQswCQYDVQQGEwJJVDEQMA4GA1UECAwHUGVzY2FyYTEPM"
              + "A0GA1UECgwGVGlyYXNhMQswCQYDVQQLDAJBTTEWMBQGA1UEAwwNYW0udGlyYXNhLm5ldDCCASIwDQYJKoZIhvcNAQEBBQADggEPAD"
              + "CCAQoCggEBAMzqTZFbzahmEqp60txg8aUYw4Y7PL44A7rzHVnb5cb01/4VVjNeijmROOL8o5ZEbkNkQly43zjoZcrkw4bpvOz95OP"
              + "8/NH/ZgyYKR42VqcTlxcj/22iq2Ie1XhWsKARmObdnNUcFCsdqXWXBo0bLF+WuUYh4ZoMxFMlP7YYl7WOCCgekE8E9sL02RuLZgq7"
              + "v2M6fsxhT5rEG81jzUlmY5c/jXZKbUIBaltKtzC3DnBpuk9u+S87WseqTeuhUzF6VvgwmiQ+zeHdr5Hjqxrvmq445DPz+2U3PYN1v"
              + "FoB/6QzjtZVakSfOSZ0YAtFhZFHmR11kJTNMfVQ5k5oIQPtHksCAwEAAaN7MHkwCQYDVR0TBAIwADAsBglghkgBhvhCAQ0EHxYdT3"
              + "BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYEFOIYM6WyNdc4odsThFVtOefT/xg1MB8GA1UdIwQYMBaAFDqheOl"
              + "4dpXYelhPC/bM+VdN1AXpMA0GCSqGSIb3DQEBCwUAA4IBAQB33qLYghIYa2j1ycHBpeZVadsb8xb4AnfnAW9g5dYfZP1eIvmKzOxN"
              + "3CjpuCRKNI4vyKHiLbucfFDl5zi9BdYwwdduPbYTgE8F8Ilyit3irSRJFk1wHICX0sBPq5ulz39MPZsP2Fmzbrphr9BrRZOc1RJdH"
              + "nj8C7phrfBneGSfwoY+qH5H6/h5A5rS8oDAraeklR2RJK4ztK+yDvp8orRDJQq5LAALQtWDhdW8Qj7WoIbGUeB77aJLluLOgriJLK"
              + "+kKaGoUuAaKFRJXPyTmtUC17CJUJbapmtDwivILhU/dSdz6+1YXTg0ddNNlug3I6L5VVRnlwJJc/hIna1VjQJO"
      );
   }
}
