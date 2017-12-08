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

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.jclouds.dimensiondata.cloudcontrol.domain.Tag;
import org.jclouds.dimensiondata.cloudcontrol.domain.TagInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.TagKey;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseAccountAwareCloudControlMockTest;
import org.jclouds.http.Uris;
import org.testng.annotations.Test;

import java.util.Collections;

import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.POST;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

@Test(groups = "unit", testName = "TagApiMockTest", singleThreaded = true)
public class TagApiMockTest extends BaseAccountAwareCloudControlMockTest {

   @Test
   public void testCreateTagKey() throws Exception {
      server.enqueue(jsonResponse("/createTagKeyResponse.json"));
      final String tagKeyId = api.getTagApi()
            .createTagKey("myTagKey", "myTagKeyDescription", Boolean.TRUE, Boolean.FALSE);
      assertEquals("c452ceac-8627-423f-a8d2-5bb4a03c01d3", tagKeyId);
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/createTagKey");
      assertBodyContains(recordedRequest,
            "{\"name\":\"myTagKey\",\"description\":\"myTagKeyDescription\",\"valueRequired\":true,\"displayOnReport\":false}");
   }

   @Test
   public void testEditTagKey() throws Exception {
      server.enqueue(response200());
      api.getTagApi().editTagKey("myTagKey", "myTagKeyId", "myTagKeyDescription", Boolean.TRUE, Boolean.FALSE);
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/editTagKey");
      assertBodyContains(recordedRequest,
            "{\"name\":\"myTagKey\",\"id\":\"myTagKeyId\",\"description\":\"myTagKeyDescription\",\"valueRequired\":true,\"displayOnReport\":false}");
   }

   @Test
   public void testRemoveTags() throws Exception {
      server.enqueue(response200());
      api.getTagApi().removeTags("b8201405-bf9c-4896-b9cb-97fce95553a1", "SERVER",
            Collections.singletonList("f357d63e-5a00-44ab-8c2f-ccd2923f5849"));
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/removeTags");
      assertBodyContains(recordedRequest,
            "{\"assetId\":\"b8201405-bf9c-4896-b9cb-97fce95553a1\",\"assetType\":\"SERVER\",\"tagKeyId\":[\"f357d63e-5a00-44ab-8c2f-ccd2923f5849\"]}");
   }

   @Test
   public void testApplyTags() throws Exception {
      server.enqueue(response200());
      TagInfo tagInfo = TagInfo.builder().tagKeyId("f357d63e-5a00-44ab-8c2f-ccd2923f5849").value("jcloudsValue")
            .build();
      api.getTagApi().applyTags("b8201405-bf9c-4896-b9cb-97fce95553a1", "SERVER", Collections.singletonList(tagInfo));
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/applyTags");
      assertBodyContains(recordedRequest,
            "{\"assetId\":\"b8201405-bf9c-4896-b9cb-97fce95553a1\",\"assetType\":\"SERVER\",\"tagById\":[{\"tagKeyId\":\"f357d63e-5a00-44ab-8c2f-ccd2923f5849\",\"value\":\"jcloudsValue\"}]}");
   }

   @Test
   public void testListTags() throws Exception {
      server.enqueue(jsonResponse("/tags.json"));
      ImmutableList<Tag> tags = api.getTagApi().listTags().concat().toList();
      assertNotNull(tags);
      assertEquals(4, tags.size());

      assertSent(GET, expectedListTagsUriBuilder().toString());
   }

   @Test
   public void testListTagsWithPagination() throws Exception {
      server.enqueue(jsonResponse("/tags-page1.json"));
      server.enqueue(jsonResponse("/tags-page2.json"));
      ImmutableList<Tag> tags = api.getTagApi().listTags().concat().toList();
      consumeIterableAndAssertAdditionalPagesRequested(tags, 8, 0);

      assertSent(GET, expectedListTagsUriBuilder().toString());
      assertSent(GET, addPageNumberToUriBuilder(expectedListTagsUriBuilder(), 2).toString());
   }

   @Test
   public void testListTags_404() throws Exception {
      server.enqueue(response404());
      final ImmutableList<Tag> emptyList = api.getTagApi().listTags().concat().toList();
      assertSent(GET, expectedListTagsUriBuilder().toString());
      assertTrue(emptyList.isEmpty());
   }

   @Test
   public void testListTagKeys() throws Exception {
      server.enqueue(jsonResponse("/tagkeys.json"));
      ImmutableList<TagKey> tagKeys = api.getTagApi().listTagKeys().concat().toList();
      assertNotNull(tagKeys);
      assertEquals(9, tagKeys.size());
      assertSent(GET, expectedListTagKeysUriBuilder().toString());
   }

   @Test
   public void testListTagKeysWithPagination() throws Exception {
      server.enqueue(jsonResponse("/tagkeys-page1.json"));
      server.enqueue(jsonResponse("/tagkeys-page2.json"));
      ImmutableList<TagKey> tagKeys = api.getTagApi().listTagKeys().concat().toList();
      consumeIterableAndAssertAdditionalPagesRequested(tagKeys, 18, 0);

      assertSent(GET, expectedListTagKeysUriBuilder().toString());
      assertSent(GET, addPageNumberToUriBuilder(expectedListTagKeysUriBuilder(), 2).toString());
   }

   private Uris.UriBuilder expectedListTagKeysUriBuilder() {
      Uris.UriBuilder uriBuilder = Uris
            .uriBuilder("/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/tagKey");
      return uriBuilder;
   }

   private Uris.UriBuilder expectedListTagsUriBuilder() {
      Uris.UriBuilder uriBuilder = Uris
            .uriBuilder("/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/tag");
      return uriBuilder;
   }

   @Test
   public void testListTagKeys_404() throws Exception {
      server.enqueue(response404());
      ImmutableList<TagKey> tagKeys = api.getTagApi().listTagKeys().concat().toList();
      assertNotNull(tagKeys);
      assertTrue(tagKeys.isEmpty());

      assertSent(GET, "/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/tagKey");
   }

   @Test
   public void testDeleteTagKey() throws Exception {
      server.enqueue(response200());
      api.getTagApi().deleteTagKey("tagKeyId");
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/deleteTagKey");
      assertBodyContains(recordedRequest, "{\"id\":\"tagKeyId\"}");
   }

   @Test
   public void testDeleteTagKey_404() throws Exception {
      server.enqueue(response404());
      api.getTagApi().deleteTagKey("tagKeyId");
      assertSent(POST, "/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/deleteTagKey");
   }

   @Test
   public void testTagKeyById() throws Exception {
      server.enqueue(jsonResponse("/tagkey.json"));
      TagKey tagKey = api.getTagApi().tagKeyById("tagKeyId");
      assertSent(GET, "/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/tagKey/tagKeyId");
      assertNotNull(tagKey);
   }

   @Test
   public void testTagKeyById_404() throws Exception {
      server.enqueue(response404());
      final TagKey tagKey = api.getTagApi().tagKeyById("tagKeyId");
      assertSent(GET, "/caas/" + VERSION + "/6ac1e746-b1ea-4da5-a24e-caf1a978789d/tag/tagKey/tagKeyId");
      assertNull(tagKey);
   }
}

