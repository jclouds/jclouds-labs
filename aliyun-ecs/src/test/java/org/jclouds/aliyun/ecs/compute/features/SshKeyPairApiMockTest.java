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

import com.google.common.collect.ImmutableMap;
import org.jclouds.aliyun.ecs.compute.internal.BaseECSComputeServiceApiMockTest;
import org.jclouds.aliyun.ecs.domain.KeyPair;
import org.jclouds.aliyun.ecs.domain.KeyPairRequest;
import org.jclouds.aliyun.ecs.domain.Request;
import org.jclouds.aliyun.ecs.domain.options.ListKeyPairsOptions;
import org.jclouds.aliyun.ecs.domain.options.PaginationOptions;
import org.jclouds.collect.IterableWithMarker;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "SshKeyPairApiMockTest", singleThreaded = true)
public class SshKeyPairApiMockTest extends BaseECSComputeServiceApiMockTest {

   public void testCreateSshKey() throws InterruptedException {
      server.enqueue(jsonResponse("/keypair-create-res.json"));
      KeyPairRequest keyPairRequest = api.sshKeyPairApi().create(TEST_REGION, "jclouds");
      assertEquals(keyPairRequest, objectFromResource("/keypair-create-res.json", KeyPairRequest.class));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "CreateKeyPair", ImmutableMap.of("RegionId", TEST_REGION));
   }

   public void testDeleteSshKey() throws InterruptedException {
      server.enqueue(jsonResponse("/keypair-delete-res.json"));
      Request delete = api.sshKeyPairApi().delete(TEST_REGION);
      assertEquals(delete, objectFromResource("/keypair-delete-res.json", Request.class));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "DeleteKeyPairs", ImmutableMap.of("RegionId", TEST_REGION));
   }

   public void testImportSshKey() throws InterruptedException {
      server.enqueue(jsonResponse("/keypair-import-res.json"));
      KeyPair keyPair = api.sshKeyPairApi().importKeyPair(
              TEST_REGION,
                            "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCdgcoNzH4hCc0j3b4MuG503L/J54uyFvwCAOu8vSsYuLpJ4AEyEOv+T0SfdF605fK6GYXA16Rxk3lrPt7mfKGNtXR0Ripbv7Zc6PvCRorwgj/cjh/45miozjrkXAiHD1GFZycfbi4YsoWAqZj7W4mwtctmhrYM0FPdya2XoRpVy89N+A5Xo4Xtd6EZn6JGEKQM5+kF2aL3ggy0od/DqjuEVYwZoyTe1RgUTXZSU/Woh7WMhsRHbqd3eYz4s6ac8n8IJPGKtUaQeqUtH7OK6NRYXVypUrkqNlwdNYZAwrjXg/x5T3D+bo11LENASRt9OJ2OkmRSTqRxBeDkhnVauWK/",
              "jclouds"
      );
      assertEquals(keyPair, objectFromResource("/keypair-import-res.json", KeyPair.class));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "ImportKeyPair", ImmutableMap.of("RegionId", TEST_REGION));
   }

   public void testListImages() throws InterruptedException {
      server.enqueue(jsonResponse("/keypairs-first.json"));
      server.enqueue(jsonResponse("/keypairs-last.json"));

      Iterable<KeyPair> keypairs = api.sshKeyPairApi().list(TEST_REGION).concat();
      assertEquals(size(keypairs), 12);
      assertEquals(server.getRequestCount(), 2);
      assertSent(server, "GET", "DescribeKeyPairs");
      assertSent(server, "GET", "DescribeKeyPairs", 2);
   }

   public void testListKeyPairsReturns404() {
      server.enqueue(response404());
      Iterable<KeyPair> keypairs = api.sshKeyPairApi().list(TEST_REGION).concat();
      assertTrue(isEmpty(keypairs));
      assertEquals(server.getRequestCount(), 1);
   }

   public void testListKeyPairsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/keypairs-first.json"));

      IterableWithMarker<KeyPair> keypairs = api.sshKeyPairApi().list(TEST_REGION, ListKeyPairsOptions.Builder
              .paginationOptions(PaginationOptions.Builder.pageNumber(1)));

      assertEquals(size(keypairs), 10);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "DescribeKeyPairs", 1);
   }

   public void testListKeyPairsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      IterableWithMarker<KeyPair> keypairs = api.sshKeyPairApi().list(TEST_REGION, ListKeyPairsOptions.Builder
              .paginationOptions(PaginationOptions.Builder.pageNumber(2)));

      assertTrue(isEmpty(keypairs));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeKeyPairs", 2);
   }

}
