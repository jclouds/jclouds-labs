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

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import org.jclouds.openstack.heat.v1.domain.Stack;
import org.jclouds.openstack.heat.v1.domain.StackResource;
import org.jclouds.openstack.heat.v1.domain.StackResourceStatus;
import org.jclouds.openstack.heat.v1.domain.StackStatus;
import org.jclouds.openstack.heat.v1.internal.BaseHeatApiLiveTest;
import org.jclouds.openstack.heat.v1.options.CreateStack;
import org.jclouds.openstack.heat.v1.options.UpdateStack;
import org.jclouds.util.Strings2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.util.Predicates2.retry;

/**
 * Tests parsing and Guice wiring of StackApi
 */
@Test(groups = "live", testName = "StackApiLiveTest")
public class StackApiLiveTest extends BaseHeatApiLiveTest {

   public static final String TEMPLATE_URL = "http://10.5.5.121/Installs/cPaaS/YAML/simple_stack.yaml";
   protected String stackName = System.getProperty("user.name").replace('.', '-').toLowerCase();

   public void testList() {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);

         List<Stack> stacks = stackApi.list();

         assertThat(stacks).isNotEmpty();

         int oldStackSize = stacks.size();
         CreateStack createStack = CreateStack.builder().name(getName()).templateUrl(TEMPLATE_URL).build();
         Stack stack = stackApi.create(createStack);
         assertThat(stack).isNotNull();
         stacks = stackApi.list();
         assertThat(stacks.size()).isEqualTo(oldStackSize + 1);
      }
   }


   public void testGetStackWitnNameAndID() {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);

         List<Stack> stacks = stackApi.list();
         assertThat(stacks).isNotNull();

         Stack stack = stackApi.get(stacks.get(0).getName(), stacks.get(0).getId());
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isEqualTo(stacks.get(0).getId());
      }
   }

   public void testGetStack() {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);

         List<Stack> stacks = stackApi.list();
         assertThat(stacks).isNotNull();

         Stack stack = stackApi.get(stacks.get(0).getId());
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isEqualTo(stacks.get(0).getId());
      }
   }

   public void testCreateWithTempletUrl() {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);
         CreateStack createStack = CreateStack.builder().name(getName()).templateUrl(TEMPLATE_URL).build();
         Stack stack = stackApi.create(createStack);
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isNotEmpty();

      }
   }

   public void testDeleteStack() {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);
         String stackName = getName();
         CreateStack createStack = CreateStack.builder().name(stackName).templateUrl(TEMPLATE_URL).build();
         Stack stack = stackApi.create(createStack);
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isNotEmpty();
         assertThat(stackApi.delete(stackName, stack.getId())).isTrue();
         Stack stackAfterDelete = stackApi.get(stackName, stack.getId());
         assertThat(stackAfterDelete).isNotNull();
      }
   }

   public void testCreateWithDisableRollback() {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);
         CreateStack createStack = CreateStack.builder().name(getName()).templateUrl(TEMPLATE_URL).disableRollback(false).build();
         Stack stack = stackApi.create(createStack);
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isNotEmpty();
         assertThat(stack.isDisableRollback()).isFalse();

      }
   }

   public void testCreateWithTemplate() throws ParseException {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);
         JSONParser parser = new JSONParser();
         Object obj = parser.parse(stringFromResource("/simple_stack.json"));

         JSONObject jsonObject = (JSONObject) obj;

         CreateStack createStack = CreateStack.builder().name(getName()).template(String.valueOf(jsonObject.get("template"))).build();
         Stack stack = stackApi.create(createStack);
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isNotEmpty();
      }
   }

   public void testUpdateStack() throws ParseException {
      for (String region : api.getConfiguredRegions()) {
         final StackApi stackApi = api.getStackApi(region);
         JSONParser parser = new JSONParser();
         Object obj = parser.parse(stringFromResource("/simple_stack.json"));

         JSONObject jsonObject = (JSONObject) obj;

         String stackName = getName();
         CreateStack createStack = CreateStack.builder().name(stackName).template(String.valueOf(jsonObject.get("template"))).build();
         Stack stack = stackApi.create(createStack);
         assertThat(stack).isNotNull();
         String stackId = stack.getId();
         assertThat(stackId).isNotEmpty();

         boolean success = retry(new Predicate<String>() {
            public boolean apply(String stackId) {
               return stackApi.get(stackId).getStatus() == StackStatus.CREATE_COMPLETE;
            }

         }, 60, 1, SECONDS).apply(stackId);

         if (!success) {
            Assert.fail("Stack didn't get to status CREATE_COMPLETE in 20m.");
         }

         UpdateStack updateOptions = UpdateStack.builder().templateUrl(TEMPLATE_URL).build();
         assertThat(stackApi.update(stackName, stack.getId(), updateOptions)).isTrue();
         Stack stackAfterUpdate = stackApi.get(stackName, stack.getId());
         assertThat(stackAfterUpdate.getStatus()).isEqualTo(StackStatus.UPDATE_IN_PROGRESS);

      }
   }

   public void testCreateWithParameters() throws ParseException {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);
         Map<String, Object> parameters = new HashMap<String, Object>();
         parameters.put("key_name", "myKey");

         JSONParser parser = new JSONParser();
         Object obj = parser.parse(stringFromResource("/stack_with_parameters.json"));

         JSONObject jsonObject = (JSONObject) obj;

         CreateStack createStack = CreateStack.builder().name(getName()).template(String.valueOf(jsonObject.get("template")))
               .parameters(parameters).build();
         Stack stack = stackApi.create(createStack);
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isNotEmpty();

         Stack stackFromList = stackApi.get(stack.getId());
         assertThat(stackFromList).isNotNull();
         assertThat(stackFromList.getId()).isEqualTo(stack.getId());
         for (String parmName : parameters.keySet()) {
            assertThat(stackFromList.getParameters().containsKey(parmName)).isTrue();
         }

      }
   }

   public void testCreateWithFilesAndEnvironment() throws IOException, ParseException {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);
         JSONParser parser = new JSONParser();
         Object obj = parser.parse(stringFromResource("/stack_with_environment_and_files.json"));
         JSONObject jsonObject = (JSONObject) obj;

         Object objFilesJsone = parser.parse(stringFromResource("/files_for_atck_template.json"));
         JSONObject filesAsJason = (JSONObject) objFilesJsone;
         Map<String, String> files = new HashMap<String, String>();
         files.put("VolumeB.template.yaml", String.valueOf(filesAsJason.get("VolumeB.template.yaml")));

         CreateStack createStack = CreateStack.builder().name(getName())
               .template(String.valueOf(jsonObject.get("template")))
               .environment(String.valueOf(jsonObject.get("environment")))
               .files(files).build();

         Stack stack = stackApi.create(createStack);
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isNotEmpty();
      }
   }

   public void testListAndGetStackResources() throws ParseException {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);
         JSONParser parser = new JSONParser();
         Object obj = parser.parse(stringFromResource("/stack_with_parameters.json"));

         JSONObject jsonObject = (JSONObject) obj;
         Map<String, Object> parameters = new HashMap<String, Object>();
         parameters.put("key_name", "myKey");

         String stackName = getName();
         CreateStack createStack = CreateStack.builder().name(stackName).template(String.valueOf(jsonObject.get("template"))).parameters(parameters).build();
         Stack stack = stackApi.create(createStack);
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isNotEmpty();
         List<StackResource> resources = stackApi.listStackResources(stackName, stack.getId());
         assertThat(resources).isNotNull();
         assertThat(resources).isNotEmpty();
         for (StackResource stackResource : resources) {
            assertThat(stackResource).isNotNull();
            String stackResourceName = stackResource.getName();
            assertThat(stackResourceName).isNotNull();
            assertThat(stackResourceName).isNotEmpty();
            assertThat(stackResource.getStatus()).isNotEqualTo(StackResourceStatus.UNRECOGNIZED);
            StackResource resourceFromGet = stackApi.getStackResource(stackName, stack.getId(), stackResourceName);
            assertThat(resourceFromGet).isNotNull();
            assertThat(resourceFromGet.getName()).isEqualTo(stackResourceName);
         }
      }
   }

   public void testGetStackResourceMetadata() throws ParseException {
      for (String region : api.getConfiguredRegions()) {
         StackApi stackApi = api.getStackApi(region);
         JSONParser parser = new JSONParser();
         Object obj = parser.parse(stringFromResource("/simple_stack.json"));

         JSONObject jsonObject = (JSONObject) obj;

         String stackName = getName();
         CreateStack createStack = CreateStack.builder().name(stackName).template(String.valueOf(jsonObject.get("template"))).build();
         Stack stack = stackApi.create(createStack);
         assertThat(stack).isNotNull();
         assertThat(stack.getId()).isNotEmpty();
         List<StackResource> resources = stackApi.listStackResources(stackName, stack.getId());
         assertThat(resources).isNotNull();
         assertThat(resources).isNotEmpty();
         for (StackResource stackResource : resources) {
            assertThat(stackResource).isNotNull();
            String stackResourceName = stackResource.getName();
            assertThat(stackResourceName).isNotNull();
            assertThat(stackResourceName).isNotEmpty();
            assertThat(stackResource.getStatus()).isNotEqualTo(StackResourceStatus.UNRECOGNIZED);
            Map metadata = stackApi.getStackResourceMetadata(stackName, stack.getId(), stackResourceName);
            assertThat(metadata).isNotNull();
            assertThat(metadata).isNotEmpty();
         }
      }
   }

 @AfterClass
 public void cleanup(){

    for (String region : api.getConfiguredRegions()) {
       StackApi stackApi = api.getStackApi(region);
       List<Stack> stacks = stackApi.list();
       for (Stack stack : stacks){
          if (stack.getName().startsWith(stackName)){
             stackApi.delete(stack.getName(), stack.getId());
          }
       }
    }

 }

   private String getName() {
      return stackName + "_" + System.currentTimeMillis();
   }

   private String stringFromResource(String resourceName) {
      try {
         return Strings2.toStringAndClose(getClass().getResourceAsStream(resourceName));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

}
