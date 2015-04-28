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
package org.jclouds.jdbc.strategy;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.jdbc.module.TestContextModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "JdbcStorageStrategyTest", singleThreaded = true)
public class JdbcStorageStrategyTest {

   private static final String CONTAINER_NAME = "jclouds-test";
   private JdbcStorageStrategy storageStrategy;

   @BeforeMethod
   protected void setUp() throws Exception {
      storageStrategy = Guice.createInjector(ImmutableSet.<Module> of(new TestContextModule(), new JpaPersistModule("jclouds-test"))).getInstance(JdbcStorageStrategy.class);
   }

   @Test
   public void testCreateContainerInLocation() {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      assertThat(storageStrategy.containerExists(CONTAINER_NAME)).isTrue();
   }

   @Test
   public void testCreateDuplicateContainerInLocation() {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isFalse();
   }

   @Test
   public void testDefaultContainerAccess() {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      assertThat(storageStrategy.getContainerAccess(CONTAINER_NAME)).isEqualTo(ContainerAccess.PRIVATE);
   }

   @Test
   public void testOverridedPublicContainerAccess() {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, new CreateContainerOptions().publicRead())).isTrue();
      assertThat(storageStrategy.getContainerAccess(CONTAINER_NAME)).isEqualTo(ContainerAccess.PUBLIC_READ);
   }

   @Test
   public void testOverridedPrivateContainerAccess() {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, CreateContainerOptions.NONE)).isTrue();
      assertThat(storageStrategy.getContainerAccess(CONTAINER_NAME)).isEqualTo(ContainerAccess.PRIVATE);
   }

   @Test
   public void testDeleteContainer() {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      assertThat(storageStrategy.containerExists(CONTAINER_NAME)).isTrue();
      storageStrategy.deleteContainer(CONTAINER_NAME);
      assertThat(storageStrategy.containerExists(CONTAINER_NAME)).isFalse();
   }

   @Test
   public void testGetAllContainerNames() {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME + "1", null, null)).isTrue();
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME + "2", null, null)).isTrue();
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME + "3", null, null)).isTrue();
      assertThat(storageStrategy.getAllContainerNames()).containsExactly(CONTAINER_NAME + "1", CONTAINER_NAME + "2", CONTAINER_NAME + "3");
   }

   @Test
   public void testGetContainerMetadata() {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      StorageMetadata storageMetadata = storageStrategy.getContainerMetadata(CONTAINER_NAME);
      assertThat(storageMetadata.getName()).isEqualTo(CONTAINER_NAME);
      assertThat(storageMetadata.getType()).isEqualTo(StorageType.CONTAINER);
      assertThat(storageMetadata.getCreationDate()).isBefore(new Date());
   }

}
