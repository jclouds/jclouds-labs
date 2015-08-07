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
package org.jclouds.etcd.features;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.util.List;

import org.jclouds.etcd.EtcdApi;
import org.jclouds.etcd.EtcdApiMetadata;
import org.jclouds.etcd.domain.members.CreateMember;
import org.jclouds.etcd.domain.members.Member;
import org.jclouds.etcd.internal.BaseEtcdMockTest;
import org.jclouds.rest.ResourceAlreadyExistsException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.etcd.features.MembersApi} class.
 */
@Test(groups = "unit", testName = "MembersApiMockTest")
public class MembersApiMockTest extends BaseEtcdMockTest {

   public void testListMembers() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/members.json")).setResponseCode(200));
      EtcdApi etcdApi = api(server.getUrl("/"));
      MembersApi api = etcdApi.membersApi();
      try {
         List<Member> members = api.list();
         assertNotNull(members);
         assertTrue(members.size() == 2);
         assertSent(server, "GET", "/" + EtcdApiMetadata.API_VERSION + "/members");
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   public void testAddMember() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/members-added.json")).setResponseCode(201));
      EtcdApi etcdApi = api(server.getUrl("/"));
      MembersApi api = etcdApi.membersApi();
      try {
         String peerURL = "http://10.0.0.10:2380";
         String clientURL = "http://10.0.0.10:2381";
         Member member = api.add(CreateMember.create(null, ImmutableList.of(peerURL), ImmutableList.of(clientURL)));
         assertNotNull(member);
         assertTrue(member.peerURLs().contains(peerURL));
         assertTrue(member.clientURLs().contains(clientURL));
         assertSent(server, "POST", "/" + EtcdApiMetadata.API_VERSION + "/members");
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testAddMemberWithMalformedURL() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(
            new MockResponse().setBody(payloadFromResource("/members-add-malformed-url.json")).setResponseCode(400));
      EtcdApi etcdApi = api(server.getUrl("/"));
      MembersApi api = etcdApi.membersApi();
      try {
         String peerURL = "htp:/hello/world:11bye";
         api.add(CreateMember.create(null, ImmutableList.of(peerURL), null));
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testAddMemberWithIllegalFormat() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(
            new MockResponse().setBody(payloadFromResource("/members-add-illegal-format.json")).setResponseCode(400));
      EtcdApi etcdApi = api(server.getUrl("/"));
      MembersApi api = etcdApi.membersApi();
      try {
         String peerURL = "http://www.google.com";
         api.add(CreateMember.create(null, ImmutableList.of(peerURL), null));
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceAlreadyExistsException.class)
   public void testAddExistingMember() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(
            new MockResponse().setBody(payloadFromResource("/members-add-existent.json")).setResponseCode(409));
      EtcdApi etcdApi = api(server.getUrl("/"));
      MembersApi api = etcdApi.membersApi();
      try {
         String peerURL = "http://10.0.0.10:2380";
         Member member = api.add(CreateMember.create(null, ImmutableList.of(peerURL), null));
         assertNull(member);
         assertSent(server, "POST", "/" + EtcdApiMetadata.API_VERSION + "/members");
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   public void testDeleteMember() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody("").setResponseCode(204));
      EtcdApi etcdApi = api(server.getUrl("/"));
      MembersApi api = etcdApi.membersApi();
      try {
         String memberID = "123456789";
         boolean deleted = api.delete(memberID);
         assertTrue(deleted);
         assertSent(server, "DELETE", "/" + EtcdApiMetadata.API_VERSION + "/members/" + memberID);
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   public void testDeleteNonExistentMember() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(
            new MockResponse().setBody(payloadFromResource("/members-delete-nonexistent.json")).setResponseCode(404));
      EtcdApi etcdApi = api(server.getUrl("/"));
      MembersApi api = etcdApi.membersApi();
      try {
         String memberID = "1234567890";
         boolean deleted = api.delete(memberID);
         assertFalse(deleted);
         assertSent(server, "DELETE", "/" + EtcdApiMetadata.API_VERSION + "/members/" + memberID);
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }
}
