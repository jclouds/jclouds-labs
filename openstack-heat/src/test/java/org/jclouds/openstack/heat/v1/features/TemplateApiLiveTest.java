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
package org.jclouds.openstack.heat.v1.features;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.jclouds.openstack.heat.v1.domain.Stack;
import org.jclouds.openstack.heat.v1.domain.Template;
import org.jclouds.openstack.heat.v1.internal.BaseHeatApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests parsing and Guice wiring of StackApi
 */
@Test(groups = "live", testName = "StackApiLiveTest")
public class TemplateApiLiveTest extends BaseHeatApiLiveTest {
   public void testGetTemplate() {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);
         TemplateApi templateApi = api.getTemplateApi(region);

         List<Stack> stacks = stackApi.list();
         assertThat(stacks).isNotNull();

         Stack stack = stackApi.get(stacks.get(0).getId());
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isEqualTo(stacks.get(0).getId());

         Template template = templateApi.get(stack.getName(), stack.getId());
         assertThat(template).isNotNull();
      }
   }

   public void testValidateTemplate() {
      for (String region : api.getConfiguredRegions()) {
         TemplateApi templateApi = api.getTemplateApi(region);

         Template template = templateApi.validate(
               "https://raw.githubusercontent.com/openstack/heat-templates/master/contrib/rackspace/RackspaceAutoScale.yaml");
         assertThat(template).isNotNull();
      }
   }
}
