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
import static com.google.common.base.Throwables.propagate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.jclouds.fujitsu.fgcp.FGCPCredentials;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.SSLModule.TrustAllCerts;

import com.google.common.base.Supplier;

/**
 * Takes PK and chain from credentials to build a SSLContext for HTTPS with
 * client authentication.
 */
@Singleton
public class SSLContextWithKeysSupplier implements Supplier<SSLContext> {
   private final Supplier<FGCPCredentials> creds;
   private final TrustManager[] trustManager;

   @Inject
   SSLContextWithKeysSupplier(Supplier<FGCPCredentials> creds, HttpUtils utils,
         TrustAllCerts trustAllCerts) {
      this.creds = creds;
      this.trustManager = utils.trustAllCerts() ? new TrustManager[] { trustAllCerts } : null;
   }

   @Override
   public SSLContext get() {
      FGCPCredentials currentCreds = checkNotNull(creds.get(), "fgcpcredential supplier returned null");
      PrivateKey privateKey = checkNotNull(currentCreds.privateKey, "fgcpcredential's privateKey is null");
      Collection<X509Certificate> certs = checkNotNull(currentCreds.certificates, "fgcpcredential's certificates returned null");

      try {
         KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
         KeyStore keyStore = KeyStore.getInstance("pkcs12");
         keyStore.load(null, null);
         keyStore.setKeyEntry("dummy alias", privateKey, null, (Certificate[]) certs.toArray(new Certificate[0]));
         kmf.init(keyStore, null);

         SSLContext sc = SSLContext.getInstance("TLS");
         sc.init(kmf.getKeyManagers(), trustManager, new SecureRandom());
         return sc;
      } catch (GeneralSecurityException e) {
         throw propagate(e);
      } catch (IOException e) {
         throw propagate(e);
      }
   }
}
