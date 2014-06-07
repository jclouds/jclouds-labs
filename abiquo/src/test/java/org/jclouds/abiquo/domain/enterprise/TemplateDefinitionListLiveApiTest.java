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
package org.jclouds.abiquo.domain.enterprise;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Live integration tests for the {@link TemplateDefinitionList} domain class.
 */
@Test(groups = "api", testName = "TemplateDefinitionListLiveApiTest")
public class TemplateDefinitionListLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private TemplateDefinitionList list;

   public void testUpdate() {
      list.setName(list.getName() + "Updated");
      list.update();

      Iterable<TemplateDefinitionList> lists = filter(env.enterprise.listTemplateDefinitionLists(),
            new Predicate<TemplateDefinitionList>() {
               @Override
               public boolean apply(TemplateDefinitionList input) {
                  return input.getName().equals("myListUpdated");
               }
            });

      assertEquals(size(lists), 1);
   }

   public void testListStates() {
      Iterable<TemplateState> states = list.listStatus(env.datacenter);
      assertNotNull(states);
   }

   @BeforeClass
   public void setup() {
      list = TemplateDefinitionList.builder(env.context.getApiContext(), env.enterprise).name("myList")
            .url("http://virtualapp-repository.com/vapp1.ovf").build();

      list.save();

      assertNotNull(list.getId());
   }

   @AfterClass
   public void tearDown() {
      Integer idTemplateList = list.getId();
      list.delete();
      assertNull(env.enterprise.getTemplateDefinitionList(idTemplateList));
   }
}
