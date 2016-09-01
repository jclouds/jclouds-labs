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

import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.PublicIp;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "PublicIpApiLiveTest")
public class PublicIpApiLiveTest extends BaseOneAndOneLiveTest {

   private PublicIp currentPublicIp;
   private List<PublicIp> publicIps;

   private PublicIpApi publicIpApi() {
      return api.publicIpApi();
   }

   @BeforeClass
   public void setupTest() {
      currentPublicIp = publicIpApi().create(PublicIp.CreatePublicIp.create("jcloudsdns.com", null, Types.IPType.IPV4));
   }

   @Test
   public void testList() {
      publicIps = publicIpApi().list();

      Assert.assertTrue(publicIps.size() > 0);
   }

   @Test
   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "jclouds", null);
      List<PublicIp> resultWithQuery = publicIpApi().list(options);

      Assert.assertTrue(resultWithQuery.size() > 0);
   }

   @Test
   public void testGet() {
      PublicIp result = publicIpApi().get(currentPublicIp.id());

      assertEquals(result.id(), currentPublicIp.id());
   }

   @Test
   public void testUpdate() throws InterruptedException {
      String updatedName = "updatejcloudsdns.com";

      PublicIp updateResult = publicIpApi().update(currentPublicIp.id(), PublicIp.UpdatePublicIp.create(updatedName));
      assertEquals(updateResult.reverseDns(), updatedName);
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      if (currentPublicIp != null) {
         publicIpApi().delete(currentPublicIp.id());
      }
   }

}
