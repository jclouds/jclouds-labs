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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

import com.google.common.base.Supplier;
import java.io.File;
import java.security.SecureRandom;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.jclouds.crypto.Crypto;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.SSLModule.TrustAllCerts;
import org.jclouds.location.Provider;
import org.jclouds.rest.AuthorizationException;

/**
 * This supplier handles two different types of authentication: PKCS12 and PEM.
 * <br/>
 * Out of the {@link Credentials} instance:
 * <ol>
 * <li><tt>PKCS12</tt>: where {@link Credentials#identity} is keystore path and {@link Credentials#credential} is
 * keystore password</li>
 * <li><tt>PEM</tt>: where {@link Credentials#identity} is PEM-encoded certificate content and
 * {@link Credentials#credential} is PEM-encoded private key</li>
 * </ol>
 */
@Singleton
public class DelegatingSSLContextSupplier implements Supplier<SSLContext> {

   private final Crypto crypto;

   private final TrustManager[] trustManager;

   private final Supplier<Credentials> creds;

   @Inject
   DelegatingSSLContextSupplier(
           Crypto crypto, @Provider Supplier<Credentials> creds, HttpUtils utils, TrustAllCerts trustAllCerts) {

      this.crypto = crypto;
      this.trustManager = utils.trustAllCerts() ? new TrustManager[]{trustAllCerts} : null;
      this.creds = creds;
   }

   @Override
   public SSLContext get() {
      final Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");
      final String identity = checkNotNull(currentCreds.identity, "credential supplier returned null identity");
      final String credential = checkNotNull(currentCreds.credential, "credential supplier returned null credential");

      final File pkcs12File = new File(identity);

      final KeyManager[] keyManagers = pkcs12File.isFile()
              ? // identity is path to PKCS12 file, credential holds keystore password
              new FileBasedKeyManagersSupplier(pkcs12File, credential.toCharArray()).get()
              : // identity is PEM-encoded certificate content, credentials PEM-encoded private key
              new InMemoryKeyManagersSupplier(crypto, identity).get();

      if (keyManagers == null) {
         throw new AuthorizationException("Could not setup any viable authentication method");
      }

      try {
         final SSLContext sslContext = SSLContext.getInstance("TLS");
         sslContext.init(keyManagers, trustManager, new SecureRandom());
         return sslContext;
      } catch (Exception e) {
         throw propagate(e);
      }
   }
}
