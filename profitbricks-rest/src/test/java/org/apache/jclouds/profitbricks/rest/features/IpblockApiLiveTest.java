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
package org.apache.jclouds.profitbricks.rest.features;

import com.google.common.base.Predicate;
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.IpBlock;
import org.apache.jclouds.profitbricks.rest.domain.Location;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "IpblockApiLiveTest")
public class IpblockApiLiveTest extends BaseProfitBricksLiveTest {

   IpBlock testIpBlock;

   private IpBlockApi ipBlockApi() {
      return api.ipBlockApi();
   }

   @BeforeClass
   public void setupTest() {
      testIpBlock = ipBlockApi().create(IpBlock.Request.creatingBuilder()
              .properties(IpBlock.PropertiesRequest.create("jclouds ipBlock", Location.US_LAS.getId(), 1)).build());
      assertIpBlockAvailable(testIpBlock);
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      if (testIpBlock != null) {
         ipBlockApi().delete(testIpBlock.id());
         assertIpBlockRemoved(testIpBlock);

      }
   }

   @Test
   public void testGetNic() {
      IpBlock ipBlock = ipBlockApi().get(testIpBlock.id());

      assertNotNull(ipBlock);
      assertEquals(ipBlock.id(), testIpBlock.id());
   }

   @Test
   public void testList() {
      List<IpBlock> ipBlocks = ipBlockApi().list();

      assertNotNull(ipBlocks);
      assertFalse(ipBlocks.isEmpty());
   }

   private void assertIpBlockAvailable(IpBlock ipblock) {
      assertPredicate(new Predicate<IpBlock>() {
         @Override
         public boolean apply(IpBlock testIpBlock) {
            IpBlock ipBlock = ipBlockApi().get(testIpBlock.id());
            if (ipBlock == null || ipBlock.metadata() == null) {
               return false;
            }

            return ipBlock.metadata().state() == State.AVAILABLE;
         }
      }, ipblock);
   }

   private void assertIpBlockRemoved(IpBlock ipblock) {
      assertPredicate(new Predicate<IpBlock>() {
         @Override
         public boolean apply(IpBlock testIpBlock) {
            return ipBlockApi().get(testIpBlock.id()) == null;
         }
      }, ipblock);
   }

}
