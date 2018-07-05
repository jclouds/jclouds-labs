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
package org.jclouds.aliyun.ecs.compute.features;

import com.google.common.base.Predicate;
import org.jclouds.aliyun.ecs.compute.internal.BaseECSComputeServiceApiLiveTest;
import org.jclouds.aliyun.ecs.domain.internal.Regions;
import org.jclouds.aliyun.ecs.domain.Request;
import org.jclouds.aliyun.ecs.domain.SecurityGroupRequest;
import org.jclouds.aliyun.ecs.domain.Tag;
import org.jclouds.aliyun.ecs.domain.options.CreateSecurityGroupOptions;
import org.jclouds.aliyun.ecs.domain.options.ListTagsOptions;
import org.jclouds.aliyun.ecs.domain.options.TagOptions;
import org.jclouds.aliyun.ecs.features.TagApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

@Test(groups = "live", testName = "TagApiLiveTest")
public class TagApiLiveTest extends BaseECSComputeServiceApiLiveTest {

   public static final String RESOURCE_TYPE = "securitygroup";

   private String securityGroupName = "pre-test-security";
   private String securityGroupId;

   @BeforeClass
   public void setUp() {
      SecurityGroupRequest preRequisite = api.securityGroupApi().create(Regions.EU_CENTRAL_1.getName(),
              CreateSecurityGroupOptions.Builder.securityGroupName(securityGroupName)
      );
      securityGroupId = preRequisite.getSecurityGroupId();
      Request request = api().add(Regions.EU_CENTRAL_1.getName(), securityGroupId, RESOURCE_TYPE,
              TagOptions.Builder.tag(1, "owner"));
      assertNotNull(request.getRequestId());
   }

   @AfterClass
   public void tearDown() {
      api().remove(Regions.EU_CENTRAL_1.getName(), securityGroupId, RESOURCE_TYPE);
      if (securityGroupId != null) {
         api.securityGroupApi().delete(Regions.EU_CENTRAL_1.getName(), securityGroupId);
      }
   }

   public void testList() {
      final AtomicInteger found = new AtomicInteger(0);
      assertFalse(api().list(Regions.EU_CENTRAL_1.getName(), ListTagsOptions.Builder.resourceId(securityGroupId))
              .filter(new Predicate<Tag>() {
                 @Override
                 public boolean apply(Tag input) {
                    found.incrementAndGet();
                    return !isNullOrEmpty(input.tagKey());
                 }
              }).isEmpty(), "All tags must have the 'key' field populated");
      assertTrue(found.get() > 0, "Expected some tags to be returned");
   }

   private TagApi api() {
      return api.tagApi();
   }
}
