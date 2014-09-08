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
package org.jclouds.digitalocean.strategy;

import static com.google.common.collect.Iterables.all;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.digitalocean.internal.BaseDigitalOceanLiveTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Live tests for the {@link ListSshKeys} strategy.
 */
@Test(groups = "live", testName = "ListSshKeysLiveTest")
public class ListSshKeysLiveTest extends BaseDigitalOceanLiveTest {

   private ListSshKeys strategy;

   private SshKey rsaKey;

   private SshKey dsaKey;

   @Override
   protected void initialize() {
      super.initialize();
      strategy = new ListSshKeys(api, MoreExecutors.sameThreadExecutor());
   }

   @BeforeClass
   public void setupKeys() throws IOException {
      String rsa = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-rsa.txt"));
      String dsa = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-dsa.txt"));

      rsaKey = api.getKeyPairApi().create("jclouds-test-rsa", rsa);
      dsaKey = api.getKeyPairApi().create("jclouds-test-dsa", dsa);
   }

   @AfterClass(alwaysRun = true)
   public void cleanupKeys() {
      if (rsaKey != null) {
         api.getKeyPairApi().delete(rsaKey.getId());
      }
      if (dsaKey != null) {
         api.getKeyPairApi().delete(dsaKey.getId());
      }
   }

   public void testListWithDetails() {
      List<SshKey> keys = strategy.execute();

      assertTrue(keys.size() >= 2);
      assertTrue(all(keys, new Predicate<SshKey>() {
         @Override
         public boolean apply(SshKey input) {
            return input.getPublicKey() != null;
         }
      }));
   }
}
