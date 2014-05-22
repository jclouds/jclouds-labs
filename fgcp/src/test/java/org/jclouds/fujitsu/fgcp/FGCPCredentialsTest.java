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
package org.jclouds.fujitsu.fgcp;

import static com.google.common.base.Suppliers.ofInstance;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.jclouds.domain.Credentials;
import org.jclouds.fujitsu.fgcp.suppliers.FGCPCredentialsSupplier;
import org.jclouds.fujitsu.fgcp.suppliers.FGCPCredentialsSupplier.FGCPCredentialsForCredentials;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class FGCPCredentialsTest {

   /**
    * Test loading the credentials by extracting a pk from a PKCS12 keystore.
    */
   public static FGCPCredentials loadFGCPCredentials(File pathToPem) throws IOException, NoSuchAlgorithmException,
         CertificateException, InvalidKeySpecException {
      FGCPCredentialsSupplier loader = new FGCPCredentialsSupplier(ofInstance(new Credentials("foo",
            Files.asCharSource(pathToPem, Charsets.UTF_8).read())),
            new FGCPCredentialsForCredentials());
      return loader.get();
   }

   public void testLoadPKStringFromTestCert() throws IOException, NoSuchAlgorithmException, KeyStoreException,
         CertificateException, UnrecoverableKeyException, InvalidKeySpecException {
      FGCPCredentials creds = loadFGCPCredentials(new File("src/test/resources/certs/jclouds-test-fgcp.pem"));
      assertNotNull(creds);
      assertEquals(creds.identity, "foo");
      assertNotNull(creds.privateKey);
   }
}
