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
package org.apache.jclouds.oneandone.rest.features;

import com.squareup.okhttp.mockwebserver.MockResponse;
import org.apache.jclouds.oneandone.rest.domain.SshKey;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(groups = "unit", testName = "SshKeyApiMockTest", singleThreaded = true)
public class SshKeyApiMockTest extends BaseOneAndOneApiMockTest {

   private SshKeyApi sshKeyApi() {
      return api.sshKeyApi();
   }

   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sshkeys/list.json"))
      );

      List<SshKey> sshKeys = sshKeyApi().list();

      assertNotNull(sshKeys);
      assertEquals(sshKeys.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ssh_keys");
   }

   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<SshKey> sshKeys = sshKeyApi().list();

      assertEquals(sshKeys.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ssh_keys");
   }

   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sshkeys/list.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<SshKey> sshKeys = sshKeyApi().list(options);

      assertNotNull(sshKeys);
      assertEquals(sshKeys.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ssh_keys?q=New");
   }

   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<SshKey> sshKeys = sshKeyApi().list(options);

      assertEquals(sshKeys.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ssh_keys?q=New");
   }

   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sshkeys/get.json"))
      );
      SshKey result = sshKeyApi().get("sshKeyId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ssh_keys/sshKeyId");
   }

   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      SshKey result = sshKeyApi().get("sshKeyId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ssh_keys/sshKeyId");
   }

   public void testCreate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sshkeys/get.json"))
      );

      SshKey response = sshKeyApi().create(SshKey.CreateSshKey.create("name", "desc", null));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/ssh_keys", "{\"name\":\"name\",\"description\":\"desc\"}");
   }

   public void testUpdate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sshkeys/get.json"))
      );
      SshKey response = sshKeyApi().update("sshKeyId", SshKey.UpdateSshKey.create("name", "desc"));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/ssh_keys/sshKeyId", "{\"name\":\"name\",\"description\":\"desc\"}");
   }

   public void testDelete() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sshkeys/get.json"))
      );
      SshKey response = sshKeyApi().delete("sshKeyId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/ssh_keys/sshKeyId");
   }

   public void testDelete404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      SshKey response = sshKeyApi().delete("sshKeyId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/ssh_keys/sshKeyId");
   }
}
