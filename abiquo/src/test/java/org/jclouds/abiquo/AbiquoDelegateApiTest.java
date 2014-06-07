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
package org.jclouds.abiquo;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.abiquo.features.BaseAbiquoApiTest;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests asynchronous and synchronous API delegates.
 */
@Test(groups = "unit", testName = "AbiquoDelegateApiTest")
public class AbiquoDelegateApiTest extends BaseAbiquoApiTest<AbiquoApi> {

   private AbiquoApi syncApi;

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      syncApi = injector.getInstance(AbiquoApi.class);
   }

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assertNotNull(syncApi.getAdminApi());
      assertNotNull(syncApi.getConfigApi());
      assertNotNull(syncApi.getInfrastructureApi());
      assertNotNull(syncApi.getEnterpriseApi());
      assertNotNull(syncApi.getCloudApi());
      assertNotNull(syncApi.getVirtualMachineTemplateApi());
      assertNotNull(syncApi.getTaskApi());
   }

   @Override
   protected void checkFilters(final HttpRequest request) {

   }
}
