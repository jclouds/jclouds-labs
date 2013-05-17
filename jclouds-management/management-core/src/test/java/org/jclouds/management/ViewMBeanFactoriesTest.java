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
package org.jclouds.management;

import com.google.common.reflect.TypeToken;
import org.jclouds.apis.Compute;
import org.jclouds.apis.Storage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "ViewManagementFactoriesTest")
public class ViewMBeanFactoriesTest {

   private final ViewMBeanFactory storageFactory = new StorageMBeanFactory();
   private final ViewMBeanFactory computeTestFactory = new ComputeMBeanFactory();

   @Test
   void testAll() {
      Iterable<ViewMBeanFactory> factories = ViewMBeanFactories.all();
      assertTrue(contains(factories, storageFactory));
      assertTrue(contains(factories, computeTestFactory));
   }

   @Test
   void testManagesView() {
      Iterable<ViewMBeanFactory> storageFactories = ViewMBeanFactories.forType(TypeToken.of(Storage.class));
      Iterable<ViewMBeanFactory> otherTestViewfactories = ViewMBeanFactories.forType(TypeToken.of(Compute.class));

      assertTrue(contains(storageFactories, storageFactory));
      assertFalse(contains(storageFactories, computeTestFactory));

      assertFalse(contains(otherTestViewfactories, storageFactory));
      assertTrue(contains(otherTestViewfactories, computeTestFactory));
   }


   private static boolean contains(Iterable<ViewMBeanFactory> factories, ViewMBeanFactory f) {
      for (ViewMBeanFactory factory : factories) {
         if (f.equals(factory)) {
            return true;
         }
      }
      return false;
   }
}
