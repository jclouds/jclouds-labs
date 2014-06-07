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
package org.jclouds.digitalocean.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static com.google.inject.Scopes.SINGLETON;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.digitalocean.ssh.DSAKeys;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.ssh.SshKeys;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Custom parser bindings.
 */
public class DigitalOceanParserModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class).in(SINGLETON);
   }

   @Singleton
   public static class SshPublicKeyAdapter extends TypeAdapter<PublicKey> {

      @Override
      public void write(JsonWriter out, PublicKey value) throws IOException {
         if (value instanceof RSAPublicKey) {
            out.value(SshKeys.encodeAsOpenSSH((RSAPublicKey) value));
         } else if (value instanceof DSAPublicKey) {
            out.value(DSAKeys.encodeAsOpenSSH((DSAPublicKey) value));
         } else {
            throw new IllegalArgumentException("Only RSA and DSA keys are supported");
         }
      }

      @Override
      public PublicKey read(JsonReader in) throws IOException {
         String input = in.nextString().trim();
         Iterable<String> parts = Splitter.on(' ').split(input);
         checkArgument(size(parts) >= 2, "bad format, should be: [ssh-rsa|ssh-dss] AAAAB3...");
         String type = get(parts, 0);

         try {
            if ("ssh-rsa".equals(type)) {
               RSAPublicKeySpec spec = SshKeys.publicKeySpecFromOpenSSH(input);
               return KeyFactory.getInstance("RSA").generatePublic(spec);
            } else if ("ssh-dss".equals(type)) {
               DSAPublicKeySpec spec = DSAKeys.publicKeySpecFromOpenSSH(input);
               return KeyFactory.getInstance("DSA").generatePublic(spec);
            } else {
               throw new IllegalArgumentException("bad format, should be: [ssh-rsa|ssh-dss] AAAAB3...");
            }
         } catch (InvalidKeySpecException ex) {
            throw propagate(ex);
         } catch (NoSuchAlgorithmException ex) {
            throw propagate(ex);
         }
      }
   }

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings(SshPublicKeyAdapter sshPublicKeyAdapter) {
      return ImmutableMap.<Type, Object> of(PublicKey.class, sshPublicKeyAdapter);
   }

}
