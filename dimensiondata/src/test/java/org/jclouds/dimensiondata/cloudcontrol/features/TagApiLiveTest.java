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
package org.jclouds.dimensiondata.cloudcontrol.features;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.jclouds.collect.PagedIterable;
import org.jclouds.dimensiondata.cloudcontrol.domain.TagKey;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

@Test(groups = "live", testName = "TagApiLiveTest", singleThreaded = true)
public class TagApiLiveTest extends BaseDimensionDataCloudControlApiLiveTest {

   private String tagKeyId;
   private String tagKeyName;

   @BeforeClass
   public void setup() {
      super.setup();
      createTagKeyIfNotExist();
   }

   private void createTagKey() {
      tagKeyName = "jcloudsTagKeyName" + System.currentTimeMillis();
      tagKeyId = api().createTagKey(tagKeyName, "jcloudsTagKeyDescription", Boolean.TRUE, Boolean.FALSE);
      assertNotNull(tagKeyId);
      assertTagKeyExistsAndIsValid(tagKeyId, tagKeyName, "jcloudsTagKeyDescription", Boolean.TRUE, Boolean.FALSE);
   }

   @Test
   public void testEditTagKey() {
      tagKeyName = "jcloudsTagKeyName" + System.currentTimeMillis();
      api().editTagKey(tagKeyName, tagKeyId, "newDescription", Boolean.FALSE, Boolean.FALSE);
      assertTagKeyExistsAndIsValid(tagKeyId, tagKeyName, "newDescription", Boolean.FALSE, Boolean.FALSE);
   }

   @Test
   public void testListTagKeys() {
      PagedIterable<TagKey> response = api().listTagKeys();
      // assert that the created tag is present in the list of tag keys.
      assertTrue(FluentIterable.from(response.concat().toList()).anyMatch(new Predicate<TagKey>() {
         @Override
         public boolean apply(TagKey input) {
            return input.id().equals(tagKeyId);
         }
      }));
   }

   private void createTagKeyIfNotExist() {
      if (tagKeyId == null) {
         createTagKey();
      }
   }

   private void assertTagKeyExistsAndIsValid(String tagKeyId, String tagKeyName, String description,
         boolean valueRequired, boolean displayOnReport) {
      TagKey tagKey = api().tagKeyById(tagKeyId);
      assertNotNull(tagKey);
      assertEquals(tagKey.name(), tagKeyName);
      assertEquals(tagKey.description(), description);
      assertEquals(tagKey.valueRequired(), valueRequired);
      assertEquals(tagKey.displayOnReport(), displayOnReport);
   }

   @AfterClass(alwaysRun = true)
   public void cleanup() {
      if (tagKeyId != null && !tagKeyId.isEmpty()) {
         api().deleteTagKey(tagKeyId);
      }
   }

   private TagApi api() {
      return api.getTagApi();
   }
}
