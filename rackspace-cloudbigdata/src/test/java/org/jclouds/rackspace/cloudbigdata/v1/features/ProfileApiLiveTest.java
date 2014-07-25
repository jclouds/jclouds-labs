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
package org.jclouds.rackspace.cloudbigdata.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.UUID;

import org.jclouds.rackspace.cloudbigdata.v1.domain.CreateProfile;
import org.jclouds.rackspace.cloudbigdata.v1.domain.Profile;
import org.jclouds.rackspace.cloudbigdata.v1.domain.ProfileSSHKey;
import org.jclouds.rackspace.cloudbigdata.v1.internal.BaseCloudBigDataApiLiveTest;
import org.jclouds.ssh.SshKeys;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Profile live test
 */
@Test(groups = "live", testName = "ProfileApiLiveTest", singleThreaded = true)
public class ProfileApiLiveTest extends BaseCloudBigDataApiLiveTest {

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      for (String region : filterRegions(api.getConfiguredRegions())) {
         ProfileApi profileApi = api.getProfileApi(region);

         CreateProfile createProfile = CreateProfile.builder()
               .username("john.doe")
               .password("1Aa+" + UUID.randomUUID().toString())
               .sshKeys(ImmutableList.of(ProfileSSHKey.builder().name("t@test")
               .publicKey(SshKeys.generate().get("public"))
               .build()))
               .credentialsUsername("jdoe")
               .credentialsApiKey(UUID.randomUUID().toString())
               .build();

         Profile profile = profileApi.create(createProfile);

         assertNotNull(profile);
         assertEquals(profile.getUsername(), "john.doe");
         assertEquals(profile.getSSHKeys().get(0).getName(), "t@test");
         assertEquals(profile.getCredentialsUsername(), "jdoe");
         assertNull(profile.getCredentialsApiKey());
      }
   }

   @Test
   public void updateProfile() {
      for (String region : filterRegions(api.getConfiguredRegions())) {
         ProfileApi profileApi = api.getProfileApi(region);

         CreateProfile createProfile = CreateProfile.builder()
               .username("john.doe2")
               .password("1Aa+" + UUID.randomUUID().toString())
               .sshKeys(ImmutableList.of(ProfileSSHKey.builder().name("t@test")
               .publicKey(SshKeys.generate().get("public"))
               .build()))
               .credentialsUsername("jdoe")
               .credentialsApiKey(UUID.randomUUID().toString())
               .build();

         Profile profile = profileApi.create(createProfile);

         assertNotNull(profile);
         assertEquals(profile.getUsername(), "john.doe2");
         assertEquals(profile.getSSHKeys().get(0).getName(), "t@test");
         assertEquals(profile.getCredentialsUsername(), "jdoe");
      }
   }

      @Test
      public void getProfile() {
         for (String region : filterRegions(api.getConfiguredRegions())) {
            ProfileApi profileApi = api.getProfileApi(region);

            Profile profile = profileApi.get();

            assertNotNull(profile);
            assertEquals(profile.getSSHKeys().get(0).getName(), "t@test");
            assertEquals(profile.getCredentialsUsername(), "jdoe");
         }
   }
}
