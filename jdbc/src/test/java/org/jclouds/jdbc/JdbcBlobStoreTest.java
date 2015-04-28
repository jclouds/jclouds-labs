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
package org.jclouds.jdbc;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "JdbcBlobStoreTest", singleThreaded = true)
public class JdbcBlobStoreTest {

   private static final String CONTAINER_NAME = "test-container";
   private static final Set<Module> MODULES = ImmutableSet.<Module> of(new JpaPersistModule("jclouds-test"));

   private static final String PROVIDER = "jdbc";

   private BlobStoreContext context = null;
   private BlobStore blobStore = null;

   @BeforeMethod
   protected void setUp() throws Exception {
      context = ContextBuilder.newBuilder(PROVIDER)
            .modules(MODULES)
            .build(BlobStoreContext.class);
      blobStore = context.getBlobStore();
   }

   @AfterMethod
   protected void tearDown() throws IOException {
      context.close();
   }

   @Test
   public void testCreateContainerInLocation() {
      assertThat(blobStore.createContainerInLocation(null, CONTAINER_NAME)).isTrue();
      assertThat(blobStore.containerExists(CONTAINER_NAME)).isTrue();
   }

   @Test
   public void testDeleteContainer() {
      assertThat(blobStore.createContainerInLocation(null, CONTAINER_NAME)).isTrue();
      assertThat(blobStore.containerExists(CONTAINER_NAME)).isTrue();
      blobStore.deleteContainer(CONTAINER_NAME);
      assertThat(blobStore.containerExists(CONTAINER_NAME)).isFalse();
   }

}
