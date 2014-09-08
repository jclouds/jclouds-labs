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

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.digitalocean.ssh.DSAKeys;
import org.jclouds.ssh.SshKeys;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link SameFingerprint} class.
 */
@Test(groups = "unit", testName = "SameFingerprintTest")
public class SameFingerprintTest {

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "key cannot be null")
   public void testPublicKeyCannotBeNull() {
      new SameFingerprint(null);
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "public key cannot be null")
   public void testPublicKeyInSshKeyCannotBeNull() throws IOException, InvalidKeySpecException,
   NoSuchAlgorithmException {
      String rsa = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-rsa.txt"));
      PublicKey key = KeyFactory.getInstance("RSA").generatePublic(SshKeys.publicKeySpecFromOpenSSH(rsa));

      SameFingerprint predicate = new SameFingerprint(key);
      predicate.apply(new SshKey(0, "foo", null));
   }

   @Test
   public void testSameFingerPrintRSA() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
      String rsa = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-rsa.txt"));
      PublicKey key = KeyFactory.getInstance("RSA").generatePublic(SshKeys.publicKeySpecFromOpenSSH(rsa));

      SameFingerprint predicate = new SameFingerprint(key);
      assertTrue(predicate.apply(new SshKey(0, "foo", key)));
   }

   @Test
   public void testSameFingerPrintDSA() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
      String dsa = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-dsa.txt"));
      PublicKey key = KeyFactory.getInstance("DSA").generatePublic(DSAKeys.publicKeySpecFromOpenSSH(dsa));

      SameFingerprint predicate = new SameFingerprint(key);
      assertTrue(predicate.apply(new SshKey(0, "foo", key)));
   }

}
