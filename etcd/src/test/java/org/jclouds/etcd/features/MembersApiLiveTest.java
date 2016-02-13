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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.etcd.BaseEtcdApiLiveTest;
import org.jclouds.etcd.domain.members.CreateMember;
import org.jclouds.etcd.domain.members.Member;
import org.jclouds.rest.ResourceAlreadyExistsException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "live", testName = "MembersApiLiveTest", singleThreaded = true)
public class MembersApiLiveTest extends BaseEtcdApiLiveTest {

   private String selfID;
   private Member nonSelfMember;
   private Member addedMember;

   @BeforeClass
   protected void init() {
      selfID = api.statisticsApi().self().id();
      assertNotNull(selfID);
   }

   public void testListMembers() {
      List<Member> members = api().list();
      assertNotNull(members);
      assertTrue(members.size() > 0);
      for (Member member : members) {
         if (!member.id().equals(selfID)) {
            this.nonSelfMember = member;
            return;
         }
      }
      throw new RuntimeException("Could not find another member in cluster with different id");
   }

   @Test(dependsOnMethods = "testListMembers")
   public void testDeleteMember() {
      boolean successful = api().delete(nonSelfMember.id());
      assertTrue(successful);
   }

   @Test(dependsOnMethods = "testDeleteMember")
   public void testAddMember() {
      assertNotNull(nonSelfMember);

      addedMember = api().add(CreateMember.create(null, nonSelfMember.peerURLs(), null));
      assertNotNull(addedMember);
      assertTrue(addedMember.peerURLs().containsAll(nonSelfMember.peerURLs()));
   }

   @Test(dependsOnMethods = "testAddMember", expectedExceptions = ResourceAlreadyExistsException.class)
   public void testAddExistingMember() {
      assertNotNull(addedMember);

      Member existingMember = api().add(CreateMember.create(null, addedMember.peerURLs(), addedMember.clientURLs()));
      assertNull(existingMember);
   }

   @Test(dependsOnMethods = "testAddExistingMember", expectedExceptions = IllegalArgumentException.class)
   public void testAddMemberWithMalformedURL() {
      api().add(CreateMember.create(null, ImmutableList.of("htp:/hello/world:11bye"), null));
   }

   @Test(dependsOnMethods = "testAddMemberWithMalformedURL", expectedExceptions = IllegalArgumentException.class)
   public void testAddMemberWithIllegalFormat() {
      api().add(CreateMember.create(null, ImmutableList.of("http://www.google.com"), null));
   }

   @Test(dependsOnMethods = "testAddMemberWithIllegalFormat")
   public void testDeleteMemberNonExistentMember() {
      boolean successful = api().delete(randomString());
      assertFalse(successful);
   }

   private MembersApi api() {
      return api.membersApi();
   }
}
