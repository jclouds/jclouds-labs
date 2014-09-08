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
package org.jclouds.digitalocean.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;

import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.digitalocean.ssh.DSAKeys;
import org.jclouds.ssh.SshKeys;

import com.google.common.base.Predicate;

/**
 * Predicate to compare SSH keys by fingerprint.
 */
public class SameFingerprint implements Predicate<SshKey> {

   public final String fingerprint;

   public SameFingerprint(PublicKey key) {
      this.fingerprint = computeFingerprint(checkNotNull(key, "key cannot be null"));
   }

   @Override
   public boolean apply(SshKey key) {
      checkNotNull(key, "key cannot be null");
      checkNotNull(key.getPublicKey(), "public key cannot be null");
      return fingerprint.equals(computeFingerprint(key.getPublicKey()));
   }

   public static String computeFingerprint(PublicKey key) {
      if (key instanceof RSAPublicKey) {
         RSAPublicKey rsaKey = (RSAPublicKey) key;
         return SshKeys.fingerprint(rsaKey.getPublicExponent(), rsaKey.getModulus());
      } else if (key instanceof DSAPublicKey) {
         DSAPublicKey dsaKey = (DSAPublicKey) key;
         return DSAKeys.fingerprint(dsaKey.getParams().getP(), dsaKey.getParams().getQ(), dsaKey.getParams().getG(),
               dsaKey.getY());
      } else {
         throw new IllegalArgumentException("Only RSA and DSA keys are supported");
      }
   }
}
