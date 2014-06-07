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
package org.jclouds.fujitsu.fgcp.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.inject.Inject;

import org.jclouds.crypto.Pems;
import org.jclouds.domain.Credentials;
import org.jclouds.fujitsu.fgcp.FGCPCredentials;
import org.jclouds.io.Payloads;
import org.jclouds.location.Provider;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteSource;
import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Loads {@link FGCPCredentials} from a pem file containing certificate and
 * private key.
 */
public class FGCPCredentialsSupplier implements Supplier<FGCPCredentials> {
   private final Supplier<Credentials> creds;
   private final LoadingCache<Credentials, FGCPCredentials> keyCache;

   @Inject
   public FGCPCredentialsSupplier(@Provider Supplier<Credentials> creds, FGCPCredentialsForCredentials loader) {
      this.creds = creds;
      // throw out the private key related to old credentials
      this.keyCache = CacheBuilder.newBuilder().maximumSize(2).build(checkNotNull(loader, "loader"));
   }

   /**
    * It is relatively expensive to extract a certificate with private key from
    * a PEM. Cache the relationship between current credentials so that they are
    * only recalculated once.
    */
   @VisibleForTesting
   public static class FGCPCredentialsForCredentials extends CacheLoader<Credentials, FGCPCredentials> {
      @Override
      public FGCPCredentials load(Credentials in) {
         try {
            String identity = in.identity;
            String pem = in.credential;

            // extract private key
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(Pems.privateKeySpec(ByteSource.wrap(pem.getBytes(Charsets.UTF_8))));

            // extract certificate(s)
            Collection<X509Certificate> certs = x509Certificates(pem);

            return new FGCPCredentials.Builder().identity(identity).credential(pem)
                  .privateKey(privateKey).certificates(certs).build();
         } catch (IOException e) {
            throw Throwables.propagate(e);
         } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("security exception parsing pem. " + e.getMessage(), e);
         } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("not a valid pem: cannot parse pk. " + e.getMessage(), e);
         }
      }

      /**
       * Returns the collection of X509Certificates that are pem encoded in the
       * input string.<br>
       * Other elements except for pem encoded certificates, e.g. private keys
       * and metadata such as bag attributes are ignored in the pem are ignored.
       *
       * @param pem
       *           encoded certificate(s)
       * @return collection of certificates
       * @throws IOException
       *            in an I/O error occurs
       * @throws CertificateException
       *            on parsing errors
       */
      @SuppressWarnings("unchecked")
      public static Collection<X509Certificate> x509Certificates(String pem) throws IOException, CertificateException {
         // in : pem with Bagattributes metadata and pk
         // out: pem with only certs
         LineProcessor<String> callback = new LineProcessor<String>() {
            static final String CERTIFICATE_X509_END_MARKER = "-----END CERTIFICATE-----";
            StringBuilder result = new StringBuilder();
            boolean insideCert = false;

            public boolean processLine(String line) {
               if (line.startsWith(Pems.CERTIFICATE_X509_MARKER)) {
                  insideCert = true;
               }
               if (insideCert) {
                  result.append(line).append('\n');
               }
               if (line.startsWith(CERTIFICATE_X509_END_MARKER)) {
                  insideCert = false;
               }
               return true; // keep going
            }

            public String getResult() {
               return result.toString();
            }
         };
         String filteredPem = CharStreams.readLines(CharStreams.newReaderSupplier(pem), callback);
         CertificateFactory fact = CertificateFactory.getInstance("X.509");
         return (Collection<X509Certificate>) fact.generateCertificates(new ByteArrayInputStream(filteredPem
               .getBytes(Charsets.UTF_8)));
      }
   }

   @Override
   public FGCPCredentials get() {
      try {
         return keyCache.getUnchecked(checkNotNull(creds.get(), "credential supplier returned null"));
      } catch (UncheckedExecutionException e) {
         throw Throwables.propagate(e.getCause());
      }
   }

}
