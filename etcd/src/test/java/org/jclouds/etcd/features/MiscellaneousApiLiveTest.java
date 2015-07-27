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
import static org.testng.Assert.assertTrue;
import org.jclouds.etcd.BaseEtcdApiLiveTest;
import org.jclouds.etcd.domain.miscellaneous.Version;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "MiscellaneousApiLiveTest")
public class MiscellaneousApiLiveTest extends BaseEtcdApiLiveTest {

   private final String versionRegex = "^\\d+\\.\\d+\\.\\d+$";

   @Test
   public void testGetVersion() {
      Version version = api().version();
      assertNotNull(version);
      assertTrue(version.etcdServer().matches(versionRegex));
      assertTrue(version.etcdCluster().matches(versionRegex));
   }

   @Test
   public void testGetHealth() {
      boolean health = api().health();
      assertTrue(health);
   }

   @Test
   public void testGetMetrics() {
      String metrics = api().metrics();
      assertNotNull(metrics);
   }

   private MiscellaneousApi api() {
      return api.miscellaneousApi();
   }
}
