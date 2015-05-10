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
package org.jclouds.jdbc.integration;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.jclouds.blobstore.integration.internal.BaseContainerIntegrationTest;
import org.testng.annotations.Test;

@Test(groups = { "integration" }, singleThreaded = true,  testName = "blobstore.EclipselinkH2ContainerIntegrationTest")
public class EclipselinkH2ContainerIntegrationTest extends BaseContainerIntegrationTest {
   public EclipselinkH2ContainerIntegrationTest() {
      provider = "jdbc";
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(this.getLoggingModule(), new JpaPersistModule("jclouds-test-h2"));
   }
}
