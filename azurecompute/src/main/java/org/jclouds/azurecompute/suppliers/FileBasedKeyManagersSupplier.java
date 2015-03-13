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

import com.google.common.base.Supplier;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import org.jclouds.util.Closeables2;

import static com.google.common.base.Throwables.propagate;

class FileBasedKeyManagersSupplier implements Supplier<KeyManager[]> {

   private final File pkcs12File;

   private final char[] credential;

   public FileBasedKeyManagersSupplier(final File pkcs12File, final char[] credential) {
      this.pkcs12File = pkcs12File;
      this.credential = credential;
   }

   @Override
   public KeyManager[] get() {
      KeyManager[] keyManagers = null;

      FileInputStream stream = null;
      try {
         stream = new FileInputStream(pkcs12File);

         final KeyStore keyStore = KeyStore.getInstance("PKCS12");
         keyStore.load(stream, credential);

         final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
         keyManagerFactory.init(keyStore, credential);

         keyManagers = keyManagerFactory.getKeyManagers();
      } catch (Exception e) {
         propagate(e);
      } finally {
         Closeables2.closeQuietly(stream);
      }

      return keyManagers;
   }

}
