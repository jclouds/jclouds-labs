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
package org.jclouds.digitalocean.features;

import static org.testng.Assert.assertFalse;

import java.util.List;

import org.jclouds.digitalocean.domain.Size;
import org.jclouds.digitalocean.internal.BaseDigitalOceanLiveTest;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link SizesApi} class.
 */
@Test(groups = "live", testName = "SizeApiLiveTest")
public class SizeApiLiveTest extends BaseDigitalOceanLiveTest {

   private SizesApi sizesApi;

   @Override
   protected void initialize() {
      super.initialize();
      sizesApi = api.getSizesApi();
   }

   public void testListSizes() {
      List<Size> sizes = sizesApi.list();

      assertFalse(sizes.isEmpty(), "Size list should not be empty");
   }
}
