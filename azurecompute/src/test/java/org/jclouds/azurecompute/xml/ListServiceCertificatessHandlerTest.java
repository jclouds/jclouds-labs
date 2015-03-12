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
import java.util.List;

import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import java.net.URI;
import org.jclouds.azurecompute.domain.ServiceCertificate;

@Test(groups = "unit", testName = "ListServiceCertificatessHandlerTest")
public class ListServiceCertificatessHandlerTest extends BaseHandlerTest {

   public void test() {
      final InputStream is = getClass().getResourceAsStream("/servicecertificates.xml");
      final ListServiceCertificatesHandler handler
              = new ListServiceCertificatesHandler(new ServiceCertificateHandler());
      final List<ServiceCertificate> result = factory.create(handler).parse(is);

      assertEquals(result, expected());
   }

   public static List<ServiceCertificate> expected() {
      return ImmutableList.of(
              ServiceCertificate.create(
                      URI.create("https://management.core.windows.net/d6769fbe-4649-453f-8435-c07f0cc0709d/services/"
                              + "hostedservices/prova/certificates/sha1-8D6ED1395205C57D23E518672903FDAF144EE8AE"),
                      "8D6ED1395205C57D23E518672903FDAF144EE8AE",
                      "sha1",
                      "MIIDyzCCArOgAwIBAgICEAcwDQYJKoZIhvcNAQELBQAwfzELMAkGA1UEBhMCSVQxDjAMBgNVBAgMBUl0YWx5MRAwDgYDVQQ"
                      + "HDAdQZXNjYXJhMQ8wDQYDVQQKDAZUaXJhc2ExDDAKBgNVBAsMA0lUQzEPMA0GA1UEAwwGVGlyYXNhMR4wHAYJ"
                      + "KoZIhvcNAQkBFg9pbmZvQHRpcmFzYS5uZXQwHhcNMTUwMzA0MTQ1MzQwWhcNMTYwMzAzMTQ1MzQwWjBVMQswC"
                      + "QYDVQQGEwJJVDEQMA4GA1UECAwHUGVzY2FyYTEPMA0GA1UECgwGVGlyYXNhMQswCQYDVQQLDAJBTTEWMBQGA1"
                      + "UEAwwNYW0udGlyYXNhLm5ldDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMzqTZFbzahmEqp60tx"
                      + "g8aUYw4Y7PL44A7rzHVnb5cb01/4VVjNeijmROOL8o5ZEbkNkQly43zjoZcrkw4bpvOz95OP8/NH/ZgyYKR42"
                      + "VqcTlxcj/22iq2Ie1XhWsKARmObdnNUcFCsdqXWXBo0bLF+WuUYh4ZoMxFMlP7YYl7WOCCgekE8E9sL02RuLZ"
                      + "gq7v2M6fsxhT5rEG81jzUlmY5c/jXZKbUIBaltKtzC3DnBpuk9u+S87WseqTeuhUzF6VvgwmiQ+zeHdr5Hjqx"
                      + "rvmq445DPz+2U3PYN1vFoB/6QzjtZVakSfOSZ0YAtFhZFHmR11kJTNMfVQ5k5oIQPtHksCAwEAAaN7MHkwCQY"
                      + "DVR0TBAIwADAsBglghkgBhvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE"
                      + "FOIYM6WyNdc4odsThFVtOefT/xg1MB8GA1UdIwQYMBaAFDqheOl4dpXYelhPC/bM+VdN1AXpMA0GCSqGSIb3D"
                      + "QEBCwUAA4IBAQB33qLYghIYa2j1ycHBpeZVadsb8xb4AnfnAW9g5dYfZP1eIvmKzOxN3CjpuCRKNI4vyKHiLb"
                      + "ucfFDl5zi9BdYwwdduPbYTgE8F8Ilyit3irSRJFk1wHICX0sBPq5ulz39MPZsP2Fmzbrphr9BrRZOc1RJdHnj"
                      + "8C7phrfBneGSfwoY+qH5H6/h5A5rS8oDAraeklR2RJK4ztK+yDvp8orRDJQq5LAALQtWDhdW8Qj7WoIbGUeB7"
                      + "7aJLluLOgriJLK+kKaGoUuAaKFRJXPyTmtUC17CJUJbapmtDwivILhU/dSdz6+1YXTg0ddNNlug3I6L5VVRnl"
                      + "wJJc/hIna1VjQJO"
              ),
              ServiceCertificate.create(
                      URI.create("https://management.core.windows.net/d6769fbe-4649-453f-8435-c07f0cc0709d/services/"
                              + "hostedservices/prova/certificates/sha1-CCA59C5AA24866BB292F01B81E6A77FAF8FCDC73"),
                      "CCA59C5AA24866BB292F01B81E6A77FAF8FCDC73",
                      "sha1",
                      "MIIDzTCCArWgAwIBAgICEAYwDQYJKoZIhvcNAQELBQAwfzELMAkGA1UEBhMCSVQxDjAMBgNVBAgMBUl0YWx5MRAwDgYDVQ"
                      + "QHDAdQZXNjYXJhMQ8wDQYDVQQKDAZUaXJhc2ExDDAKBgNVBAsMA0lUQzEPMA0GA1UEAwwGVGlyYXNhMR4wHAY"
                      + "JKoZIhvcNAQkBFg9pbmZvQHRpcmFzYS5uZXQwHhcNMTUwMzA0MTQ1MTI2WhcNMTYwMzAzMTQ1MTI2WjBXMQsw"
                      + "CQYDVQQGEwJJVDEQMA4GA1UECAwHUGVzY2FyYTEPMA0GA1UECgwGVGlyYXNhMQwwCgYDVQQLDANJZE0xFzAVB"
                      + "gNVBAMMDmlkbS50aXJhc2EubmV0MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1G1ejg5eHZzhVc"
                      + "a8OOD6pq0fj5VDEYIxG7CGDl4I8N3NE+svIGTYumdKUn1+FFyfgxsPCh7zfoSq0hnelRpBI3w9wfic1856/wN"
                      + "z4ZsLQgcrm6wwwVXEfWGKYF1r8pTBzAYqNzQFqypSL9kU/YJfeY8XR3eJ3vQersAiKUQVQqk1H10R2aURqlCF"
                      + "s1xc/ta9INNS+SLgWEBmQNnpwHfb7IsIYmPfqvbZsfAJ9KDqIdA5mjPz1elHNLLMi4phGPpbAH7AszZbrRaFt"
                      + "bI0o5nAL6tS37f3iEV1L7cWo/am6MGg0PF4T9GRdL8D0gl9BDskMUHD+n8cJOEO2sQVJBKszwIDAQABo3sweT"
                      + "AJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4"
                      + "EFgQU4GbbmOK+5WNBil8edbwKYHIhCJMwHwYDVR0jBBgwFoAUOqF46Xh2ldh6WE8L9sz5V03UBekwDQYJKoZI"
                      + "hvcNAQELBQADggEBAMqr+GoBBf7UPo4dBoQef4OhrNGCcYz6E0B8WID7ZG6KLv6TyTi5iEna/bbc60HisVqUo"
                      + "FIMyMa+bHiLNwObNQ/+edklNyPe48OHYX2421x3H//M/n6SpRxP5i1NSlqtcw6WnXzaTHUwV8v+5ctG1QAKrJ"
                      + "+nwwDRWzALxPJvw4TDeggRIkzAqIySrN+nRkVNliGlnZEI8NjJdelS/83E02LAxj3sPJp1yS5lWia88eNg6UX"
                      + "Y2vQf9CwrXjOz0aOvVOwHJxXBxS0tqv+bg0D5B640WdcZOhgzDxte6DDkiSU+P7nZUW1Bwtk0WD9GKN2+YPg/"
                      + "ElLLB5nSEGZSVN6Xfn8="
              ));
   }
}
