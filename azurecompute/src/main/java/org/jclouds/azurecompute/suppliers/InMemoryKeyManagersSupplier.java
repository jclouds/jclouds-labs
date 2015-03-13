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
package org.jclouds.azurecompute.suppliers;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.io.ByteSource;
import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.util.Collection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509ExtendedKeyManager;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;

import static com.google.common.base.Throwables.propagate;

class InMemoryKeyManagersSupplier implements Supplier<KeyManager[]> {

   private final Crypto crypto;

   private final String identity;

   public InMemoryKeyManagersSupplier(final Crypto crypto, final String identity) {
      this.crypto = crypto;
      this.identity = identity;
   }

   @Override
   public KeyManager[] get() {
      KeyManager[] keyManagers = null;

      try {
         // split in private key and certs
         final int privateKeyBeginIdx = identity.indexOf("-----BEGIN PRIVATE KEY");
         final int privateKeyEndIdx = identity.indexOf("-----END PRIVATE KEY");
         final String pemPrivateKey = identity.substring(privateKeyBeginIdx, privateKeyEndIdx + 26);

         final StringBuilder pemCerts = new StringBuilder();
         int certsBeginIdx = 0;
         do {
            certsBeginIdx = identity.indexOf("-----BEGIN CERTIFICATE", certsBeginIdx);
            if (certsBeginIdx >= 0) {
               final int certsEndIdx = identity.indexOf("-----END CERTIFICATE", certsBeginIdx) + 26;
               pemCerts.append(identity.substring(certsBeginIdx, certsEndIdx));
               certsBeginIdx = certsEndIdx;
            }
         } while (certsBeginIdx != -1);

         // parse private key
         final KeySpec keySpec = Pems.privateKeySpec(ByteSource.wrap(pemPrivateKey.getBytes(Charsets.UTF_8)));
         final PrivateKey privateKey = crypto.rsaKeyFactory().generatePrivate(keySpec);

         // parse cert(s)
         @SuppressWarnings("unchecked")
         final Collection<Certificate> certs = (Collection<Certificate>) CertificateFactory.getInstance("X.509").
                 generateCertificates(new ByteArrayInputStream(pemCerts.toString().getBytes(Charsets.UTF_8)));

         if (certs.isEmpty()) {
            throw new IllegalStateException("Could not find any valid certificate");
         }

         final X509Certificate certificate = (X509Certificate) certs.iterator().next();

         keyManagers = new KeyManager[]{new InMemoryKeyManager(certificate, privateKey)};
      } catch (Exception e) {
         propagate(e);
      }

      return keyManagers;
   }

   private static class InMemoryKeyManager extends X509ExtendedKeyManager {

      private static final String DEFAULT_ALIAS = "azure";

      private final X509Certificate certificate;

      private final PrivateKey privateKey;

      public InMemoryKeyManager(final X509Certificate certificate, final PrivateKey privateKey) {
         this.certificate = certificate;
         this.privateKey = privateKey;
      }

      @Override
      public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
         return DEFAULT_ALIAS;
      }

      @Override
      public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
         return DEFAULT_ALIAS;
      }

      @Override
      public X509Certificate[] getCertificateChain(final String alias) {
         return new X509Certificate[]{certificate};
      }

      @Override
      public String[] getClientAliases(final String keyType, final Principal[] issuers) {
         return new String[]{DEFAULT_ALIAS};
      }

      @Override
      public PrivateKey getPrivateKey(final String alias) {
         return privateKey;
      }

      @Override
      public String[] getServerAliases(final String keyType, final Principal[] issuers) {
         return new String[]{DEFAULT_ALIAS};
      }
   }
}
