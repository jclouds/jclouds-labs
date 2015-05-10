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
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.jdbc.module.TestContextModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.utils.TestUtils.randomByteSource;

public abstract class BaseJdbcStorageStrategyTest {

   private static final String CONTAINER_NAME = "jclouds-test-container";
   private static final String BLOB_NAME = "jclouds-test-blob";

   private final String jpaModuleName;

   private JdbcStorageStrategy storageStrategy;
   private Injector injector;

   protected BaseJdbcStorageStrategyTest(String jpaModuleName) {
      this.jpaModuleName = jpaModuleName;
   }

   @BeforeMethod
   protected void setUp() throws Exception {
      injector = Guice.createInjector(ImmutableSet.<Module> of(new TestContextModule(), new JpaPersistModule(jpaModuleName)));
      storageStrategy = injector.getInstance(JdbcStorageStrategy.class);
   }

   @AfterMethod
   protected void tearDown() {
      injector.getInstance(PersistService.class).stop();
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
      assertThat(storageStrategy.getAllContainerNames()).containsExactly(CONTAINER_NAME + "1", CONTAINER_NAME + "2",
            CONTAINER_NAME + "3");
   }

   @Test
   public void testGetContainerMetadata() {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      StorageMetadata storageMetadata = storageStrategy.getContainerMetadata(CONTAINER_NAME);
      assertThat(storageMetadata.getName()).isEqualTo(CONTAINER_NAME);
      assertThat(storageMetadata.getType()).isEqualTo(StorageType.CONTAINER);
      assertThat(storageMetadata.getCreationDate()).isBefore(new Date());
   }

   @Test
   public void testBlobDoesNotExist() throws IOException {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      assertThat(storageStrategy.blobExists(CONTAINER_NAME, BLOB_NAME)).isFalse();
   }

   @Test
   public void testPutBlob() throws IOException {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      storageStrategy.putBlob(CONTAINER_NAME,
            new BlobBuilderImpl().name(BLOB_NAME).payload(randomByteSource().slice(0, 4 * 1024 * 1024)).build());
      assertThat(storageStrategy.blobExists(CONTAINER_NAME, BLOB_NAME)).isTrue();
   }

   @Test
   public void testGetBlobKeysInContainer() throws IOException {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      storageStrategy.putBlob(CONTAINER_NAME,
            new BlobBuilderImpl().name(BLOB_NAME + "1").payload(randomByteSource().slice(0, 4 * 1024 * 1024)).build());
      storageStrategy.putBlob(CONTAINER_NAME,
            new BlobBuilderImpl().name(BLOB_NAME + "2").payload(randomByteSource().slice(0, 4 * 1024 * 1024)).build());
      storageStrategy.putBlob(CONTAINER_NAME, new BlobBuilderImpl()
            .name(BLOB_NAME + "3")
            .payload(randomByteSource().slice(0, 4 * 1024 * 1024))
            .build());
      assertThat(storageStrategy.getBlobKeysInsideContainer(CONTAINER_NAME))
            .containsExactly(BLOB_NAME + "1", BLOB_NAME + "2", BLOB_NAME + "3");
   }

    @Test
    public void testGetBlob() throws IOException {
        assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
        Blob blob = new BlobBuilderImpl()
                .name(BLOB_NAME)
                .payload(getByteArray('a', 4 * 1024 * 1024))
                .build();
        storageStrategy.putBlob(CONTAINER_NAME, blob);
        InputStream data = storageStrategy.getBlob(CONTAINER_NAME, BLOB_NAME).getPayload().openStream();
        int i;
        while ((i = data.read()) != -1) {
            assertThat((char) i).isEqualTo('a');
        }
    }

   @Test
   public void testRemoveBlob() throws IOException {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      storageStrategy.putBlob(CONTAINER_NAME,
            new BlobBuilderImpl().name(BLOB_NAME).payload(randomByteSource().slice(0, 4 * 1024 * 1024)).build());
      assertThat(storageStrategy.blobExists(CONTAINER_NAME, BLOB_NAME)).isTrue();
      storageStrategy.removeBlob(CONTAINER_NAME, BLOB_NAME);
      assertThat(storageStrategy.blobExists(CONTAINER_NAME, BLOB_NAME)).isFalse();
   }

   @Test
   public void testGetBlobAccess() throws IOException {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      storageStrategy.putBlob(CONTAINER_NAME,
            new BlobBuilderImpl().name(BLOB_NAME).payload(randomByteSource().slice(0, 4 * 1024 * 1024)).build());
      assertThat(storageStrategy.blobExists(CONTAINER_NAME, BLOB_NAME)).isTrue();
      assertThat(storageStrategy.getBlobAccess(CONTAINER_NAME, BLOB_NAME)).isEqualTo(BlobAccess.PRIVATE);
   }

   @Test
   public void testClearContainer() throws IOException {
      assertThat(storageStrategy.createContainerInLocation(CONTAINER_NAME, null, null)).isTrue();
      storageStrategy.putBlob(CONTAINER_NAME,
            new BlobBuilderImpl().name(BLOB_NAME + "1").payload(randomByteSource().slice(0, 4 * 1024 * 1024)).build());
      storageStrategy.putBlob(CONTAINER_NAME,
            new BlobBuilderImpl().name(BLOB_NAME + "2").payload(randomByteSource().slice(0, 4 * 1024 * 1024)).build());
      storageStrategy.putBlob(CONTAINER_NAME,
            new BlobBuilderImpl().name(BLOB_NAME + "3").payload(randomByteSource().slice(0, 4 * 1024 * 1024)).build());
      assertThat(storageStrategy.getBlobKeysInsideContainer(CONTAINER_NAME))
            .containsExactly(BLOB_NAME + "1", BLOB_NAME + "2", BLOB_NAME + "3");
      storageStrategy.clearContainer(CONTAINER_NAME);
      assertThat(storageStrategy.getBlobKeysInsideContainer(CONTAINER_NAME)).isEmpty();
      storageStrategy.deleteContainer(CONTAINER_NAME);
      assertThat(storageStrategy.containerExists(CONTAINER_NAME)).isFalse();
   }

    private byte[] getByteArray(char c, int len) {
        byte[] array = new byte[len];
        Arrays.fill(array, (byte) c);
        return array;
    }

}
