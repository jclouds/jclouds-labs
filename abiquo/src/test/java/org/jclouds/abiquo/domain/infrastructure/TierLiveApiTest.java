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
package org.jclouds.abiquo.domain.infrastructure;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Live integration tests for the {@link StorageDevice} domain class.
 */
@Test(groups = "api", testName = "TierLiveApiTest")
public class TierLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testUpdate() {
      Tier tier = getLast(env.datacenter.listTiers());

      String previousName = tier.getName();
      tier.setName("Updated tier");
      tier.update();

      // Verify the tier has been updated
      find(env.datacenter.listTiers(), name("Updated tier"));

      // Restore the original name
      tier.setName(previousName);
      tier.update();
   }

   public void testListTiers() {
      Iterable<Tier> tiers = env.datacenter.listTiers();
      assertEquals(size(tiers), 4);

      tiers = filter(env.datacenter.listTiers(), name("FAIL"));
      assertEquals(size(tiers), 0);
   }

   private static Predicate<Tier> name(final String name) {
      return new Predicate<Tier>() {
         @Override
         public boolean apply(Tier input) {
            return input.getName().equals(name);
         }
      };
   }
}
