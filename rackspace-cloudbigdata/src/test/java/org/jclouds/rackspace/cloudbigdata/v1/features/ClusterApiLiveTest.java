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
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.jclouds.rackspace.cloudbigdata.v1.domain.Cluster;
import org.jclouds.rackspace.cloudbigdata.v1.domain.Cluster.Status;
import org.jclouds.rackspace.cloudbigdata.v1.domain.CreateCluster;
import org.jclouds.rackspace.cloudbigdata.v1.domain.CreateProfile;
import org.jclouds.rackspace.cloudbigdata.v1.domain.Profile;
import org.jclouds.rackspace.cloudbigdata.v1.domain.ProfileSSHKey;
import org.jclouds.rackspace.cloudbigdata.v1.domain.CreateCluster.ClusterType;
import org.jclouds.rackspace.cloudbigdata.v1.internal.BaseCloudBigDataApiLiveTest;
import org.jclouds.rackspace.cloudbigdata.v1.predicates.ClusterPredicates;
import org.jclouds.ssh.SshKeys;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Profile live test
 * 
 * @author Zack Shoylev
 */
@Test(groups = "live", testName = "ProfileApiLiveTest", singleThreaded = true)
public class ClusterApiLiveTest extends BaseCloudBigDataApiLiveTest {   

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      for (String zone : filterZones(api.getConfiguredZones())) {
         ClusterApi clusterApi = api.getClusterApiForZone(zone);

         CreateCluster createCluster = null;
         try {
            // A Profile must exist before a cluster is created.
            
            ProfileApi profileApi = api.getProfileApiForZone(zone);

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
            
            createCluster = CreateCluster.builder()
                  .name("slice")
                  .clusterType(ClusterType.HADOOP_HDP1_3.name())
                  .flavorId("hadoop1-7")
                  .nodeCount(1)
                  .postInitScript(new URI("http://example.com/configure_cluster.sh"))
                  .build();
         } catch (URISyntaxException e1) {
            e1.printStackTrace();
            fail("Unexpected URI exception");
         }

         Cluster cluster = clusterApi.create(createCluster);
         ClusterPredicates.awaitAvailable(clusterApi).apply(cluster);
         cluster = clusterApi.get(cluster.getId()); // update cluster for status
         
         assertNotNull(cluster);
         assertNotNull(cluster.getId());
         assertNotNull(cluster.getCreated());
         assertEquals(cluster.getName(), "slice");
         assertEquals(cluster.getClusterType(), ClusterType.HADOOP_HDP1_3.name());
         assertEquals(cluster.getFlavorId(), "hadoop1-7");
         assertEquals(cluster.getNodeCount(), 1);
         assertEquals(cluster.getPostInitScriptStatus(), "PENDING");
         assertTrue(cluster.getProgress() >= 0.0F);
         assertEquals(cluster.getStatus(), Status.ACTIVE);
      }
   }
      
   @Test
   public void getCluster() {
      for (String zone : filterZones(api.getConfiguredZones())) {
         ClusterApi clusterApi = api.getClusterApiForZone(zone);
         
         Cluster clusterFromList = clusterApi.list().get(0);
         Cluster clusterFromGet = clusterApi.get(clusterFromList.getId());
         assertNotNull(clusterFromGet.getId());
         assertNotNull(clusterFromGet.getName());
         assertEquals(clusterFromGet, clusterFromList);
      }
   }
   
   @Test
   public void resizeCluster() {
      for (String zone : filterZones(api.getConfiguredZones())) {
         ClusterApi clusterApi = api.getClusterApiForZone(zone);
         
         Cluster cluster = clusterApi.list().get(0);
         Cluster clusterResized = clusterApi.resize(cluster.getId(), 2);
         ClusterPredicates.awaitAvailable(clusterApi).apply(cluster);
         cluster = clusterApi.get(cluster.getId()); // update cluster for status
         
         assertEquals(clusterResized.getNodeCount(), 2);
      }
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   public void tearDown() {
      for (String zone : filterZones(api.getConfiguredZones())) {
         ClusterApi clusterApi = api.getClusterApiForZone(zone);
         for (Cluster cluster : clusterApi.list()) {
            ClusterPredicates.awaitAvailable(clusterApi).apply(cluster);
            clusterApi.delete(cluster.getId());
         }
      }
      super.tearDown();
   }
}
