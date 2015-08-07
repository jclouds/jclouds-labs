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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.etcd.BaseEtcdApiLiveTest;
import org.jclouds.etcd.EtcdApi;
import org.jclouds.etcd.domain.members.Member;
import org.jclouds.etcd.domain.statistics.Self;
import org.testng.annotations.Test;

import com.google.inject.Module;

@Test(groups = "live", testName = "StatisticsApiLiveTest")
public class StatisticsApiLiveTest extends BaseEtcdApiLiveTest {

   private Self self;

   @Test
   public void testGetSelf() {
      self = api().self();
      assertNotNull(self);
   }

   @Test(dependsOnMethods = "testGetSelf")
   public void testGetLeader() {

      /*
       * It's possible the default end-point is not the cluster leader. If true
       * we will iterate through all members to find the leader and execute the
       * 'leader' endpoint against its client URL.
       */
      if (self.state().equals("StateLeader")) {
         assertNotNull(api().leader());
      } else {
         for (Member possibleLeader : api.membersApi().list()) {
            if (possibleLeader.id().equals(self.leaderInfo().leader())) {
               Properties properties = new Properties();
               properties.setProperty(Constants.PROPERTY_ENDPOINT, possibleLeader.clientURLs().get(0));
               Iterable<Module> modules = setupModules();
               EtcdApi etcdApi = super.create(properties, modules);
               assertNotNull(etcdApi.statisticsApi().leader());
               return;
            }
         }
         fail("Could not find a leader within cluster");
      }
   }

   @Test
   public void testGetStore() {
      assertNotNull(api().store());
   }

   private StatisticsApi api() {
      return api.statisticsApi();
   }
}
