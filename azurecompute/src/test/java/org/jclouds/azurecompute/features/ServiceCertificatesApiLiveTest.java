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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jclouds.azurecompute.domain.ServiceCertificate;
import org.jclouds.azurecompute.domain.ServiceCertificateParams;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import static org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest.LOCATION;
import org.testng.Assert;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ServiceCertificatesApiLivTest", singleThreaded = true)
public class ServiceCertificatesApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String CLOUD_SERVICE = String.format("%s%d-%s",
           System.getProperty("user.name"), RAND, ServiceCertificatesApiLiveTest.class.getSimpleName()).toLowerCase();

   private static final String FORMAT = "pfx";
   private static final String PASSWORD = "password";

   private static final String THUMBPRINT = "8D6ED1395205C57D23E518672903FDAF144EE8AE";
   private static final String THUMBPRINT_ALGO = "sha1";
   private static final String DATA
           = "MIIDyzCCArOgAwIBAgICEAcwDQYJKoZIhvcNAQELBQAwfzELMAkGA1UEBhMCSVQxDjAMBgNVBAgMBUl0YWx5MRAwDgYDVQQ"
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
           + "Tg0ddNNlug3I6L5VVRnlwJJc/hIna1VjQJO";

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      final String requestId
              = api.getCloudServiceApi().createWithLabelInLocation(CLOUD_SERVICE, CLOUD_SERVICE, LOCATION);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);
   }

   @Test
   public void testAdd() {

      final String requestId = api().add(CLOUD_SERVICE,
              ServiceCertificateParams.builder().data(DATA).format(FORMAT).password(PASSWORD).build());

      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);
   }

   @Test(dependsOnMethods = "testAdd")
   public void testList() {
      final List<ServiceCertificate> res = api().list(CLOUD_SERVICE);
      Assert.assertEquals(res.size(), 1);
      Assert.assertEquals(res.get(0).data(), DATA);
      Assert.assertEquals(res.get(0).thumbprintAlgorithm(), THUMBPRINT_ALGO);
      Assert.assertEquals(res.get(0).thumbprint(), THUMBPRINT);
   }

   @Test(dependsOnMethods = "testList")
   public void testGet() {
      Assert.assertNotNull(api().get(CLOUD_SERVICE, THUMBPRINT_ALGO, THUMBPRINT));
   }

   @Test(dependsOnMethods = "testGet")
   public void testDelete() {
      final String requestId = api().delete(CLOUD_SERVICE, THUMBPRINT_ALGO, THUMBPRINT);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);

      Assert.assertNull(api().get(CLOUD_SERVICE, THUMBPRINT_ALGO, THUMBPRINT));
   }

   @Override
   @AfterClass(alwaysRun = true)
   protected void tearDown() {
      final String requestId = api.getCloudServiceApi().delete(CLOUD_SERVICE);
      if (requestId != null) {
         operationSucceeded.apply(requestId);
      }

      super.tearDown();
   }

   private ServiceCertificatesApi api() {
      return api.getServiceCertificatesApi();
   }
}
