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
package org.jclouds.abiquo.domain.config;

import static com.google.common.collect.Iterables.find;
import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertNotNull;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Live integration tests for the {@link Category} domain class.
 */
@Test(groups = "api", testName = "CategoryLiveApiTest")
public class CategoryLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testCreateAndGet() {
      Category category = Category.builder(env.context.getApiContext()).name(PREFIX + "-test-category").build();
      category.save();

      Category apiCategory = find(env.context.getAdministrationService().listCategories(), new Predicate<Category>() {
         @Override
         public boolean apply(Category input) {
            return input.getName().equals(PREFIX + "-test-category");
         }
      });

      apiCategory.delete();
   }

   @Test(dependsOnMethods = "testCreateAndGet")
   public void testUpdate() {
      Iterable<Category> categories = env.context.getAdministrationService().listCategories();
      assertNotNull(categories);

      Category category = categories.iterator().next();
      String name = category.getName();

      category.setName(PREFIX + "-test-category-updated");
      category.update();

      find(env.context.getAdministrationService().listCategories(), new Predicate<Category>() {
         @Override
         public boolean apply(Category input) {
            return input.getName().equals(PREFIX + "-test-category-updated");
         }
      });

      category.setName(name);
      category.update();
   }
}
