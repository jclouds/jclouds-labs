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
package org.jclouds.aliyun.ecs.compute.features;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.aliyun.ecs.compute.internal.BaseECSComputeServiceApiLiveTest;
import org.jclouds.aliyun.ecs.domain.KeyPair;
import org.jclouds.aliyun.ecs.domain.KeyPairRequest;
import org.jclouds.aliyun.ecs.features.SshKeyPairApi;
import org.jclouds.compute.ComputeTestUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

@Test(groups = "live", testName = "SecurityGroupApiLiveTest")
public class SshKeyPairApiLiveTest extends BaseECSComputeServiceApiLiveTest {

   private String keyPairName = "jclouds-test";

   @BeforeClass
   public void setUp() {
      KeyPairRequest request = api().create(TEST_REGION, keyPairName);
      assertNotNull(request.getRequestId());
   }

   @AfterClass
   public void tearDown() {
      if (keyPairName != null) {
         api().delete(TEST_REGION, keyPairName);
      }
   }

   public void testImport() {
      String importedKeyPairName = keyPairName  + new Random().nextInt(1024);
      KeyPair imported = api().importKeyPair(
              TEST_REGION,
              ComputeTestUtils.setupKeyPair().get("public"), //SshKeys.generate().get("public"),
              importedKeyPairName);
      assertEquals(imported.name(), importedKeyPairName);
      assertNotNull(imported.privateKeyBody());
      assertNotNull(imported.keyPairFingerPrint());
   }

   public void testList() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().list(TEST_REGION).concat(), new Predicate<KeyPair>() {
         @Override
         public boolean apply(KeyPair input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.name());
         }
      }), "All key pairs must have the 'name' field populated");
      assertTrue(found.get() > 0, "Expected some key pair to be returned");
   }

   private SshKeyPairApi api() {
      return api.sshKeyPairApi();
   }
}
