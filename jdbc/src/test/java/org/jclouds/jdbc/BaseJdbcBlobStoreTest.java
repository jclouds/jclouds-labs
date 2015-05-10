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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;
import com.google.inject.Module;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.PhantomPayload;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.util.Closeables2;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.google.common.io.BaseEncoding.base16;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.utils.TestUtils.randomByteSource;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.FileAssert.fail;

public abstract class BaseJdbcBlobStoreTest {

   private static final String CONTAINER_NAME = "test-container";
   private static final String BLOB_NAME = "jclouds-test-blob";
   private static final String PROVIDER = "jdbc";

   private final String jpaModuleName;

   private BlobStoreContext context = null;
   private BlobStore blobStore = null;

   protected BaseJdbcBlobStoreTest(String jpaModuleName) {
      this.jpaModuleName = jpaModuleName;
   }

   @BeforeMethod
   protected void setUp() throws Exception {
      context = ContextBuilder.newBuilder(PROVIDER)
            .modules(ImmutableSet.<Module> of(new JpaPersistModule(jpaModuleName)))
            .build(BlobStoreContext.class);
      blobStore = context.getBlobStore();
   }

   @AfterMethod
   protected void tearDown() throws IOException {
      context.utils().injector().getInstance(PersistService.class).stop();
      context.close();
   }

   @Test
   public void testCreateContainerDoesNotExist() {
      assertThat(blobStore.containerExists(CONTAINER_NAME)).isFalse();
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

   @Test
   public void testPutBlob() {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      assertThat(blobStore.blobExists(CONTAINER_NAME, BLOB_NAME)).isFalse();
      createBlobInContainer(CONTAINER_NAME, BLOB_NAME);
      assertThat(blobStore.blobExists(CONTAINER_NAME, BLOB_NAME)).isTrue();
   }

   @Test
   public void testListRoot() throws IOException {
      PageSet<? extends StorageMetadata> containersRetrieved = blobStore.list();
      assertTrue(containersRetrieved.isEmpty(), "List operation returns a not empty set of container");

      String[] containerNames = {"34343", "aaaa", "bbbbb"};
      Set<String> containersCreated = Sets.newHashSet();
      for (String containerName : containerNames) {
         blobStore.createContainerInLocation(null, containerName);
         containersCreated.add(containerName);
      }

      containersRetrieved = blobStore.list();
      assertEquals(containersCreated.size(), containersRetrieved.size(), "Different numbers of container");

      for (StorageMetadata data : containersRetrieved) {
         String containerName = data.getName();
         if (!containersCreated.remove(containerName)) {
            fail("Container list contains unexpected value [" + containerName + "]");
         }
      }
      assertTrue(containersCreated.isEmpty(), "List operation doesn't return all values.");

      for (String containerName : containerNames) {
         blobStore.deleteContainer(containerName);
      }
      containersRetrieved = blobStore.list();
      assertTrue(containersRetrieved.isEmpty(), "List operation returns a not empty set of container");
   }

   @Test
   public void testListNoOptionSingleContainer() throws IOException {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      checkForContainerContent(CONTAINER_NAME, null);

      List<String> blobsExpected = createBlobsInContainer(CONTAINER_NAME, "a", 5);
      checkForContainerContent(CONTAINER_NAME, blobsExpected);
   }

   @Test
   public void testListRootNonRecursive() throws IOException {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      checkForContainerContent(CONTAINER_NAME, null);
      List<String> result = createBlobsInContainer(CONTAINER_NAME, "a", 4);
      ListContainerOptions options = ListContainerOptions.Builder
            .withDetails()
            .inDirectory("");
      PageSet<? extends StorageMetadata> res = blobStore.list(CONTAINER_NAME, options);
      checkForContainerContent(CONTAINER_NAME, "", result);
   }

   @Test(expectedExceptions = ContainerNotFoundException.class)
   public void testListNotExistingContainer() {
      blobStore.list(CONTAINER_NAME);
   }

   @Test
   public void testListNoOptionDoubleContainer() throws IOException {
      final String CONTAINER_NAME2 = "container2";

      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      checkForContainerContent(CONTAINER_NAME, null);

      blobStore.createContainerInLocation(null, CONTAINER_NAME2);
      checkForContainerContent(CONTAINER_NAME2, null);

      List<String> blobNamesCreatedInContainer1 = createBlobsInContainer(CONTAINER_NAME, "a", 5);

      blobStore.createContainerInLocation(null, CONTAINER_NAME2);
      List<String> blobNamesCreatedInContainer2 = createBlobsInContainer(CONTAINER_NAME2, "b", 5);

      checkForContainerContent(CONTAINER_NAME, blobNamesCreatedInContainer1);
      checkForContainerContent(CONTAINER_NAME2, blobNamesCreatedInContainer2);
   }

   @Test
   public void testListSubdirectory() throws IOException {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      checkForContainerContent(CONTAINER_NAME, null);

      createBlobInContainer(CONTAINER_NAME, "bbb/ccc/ddd/1234.jpg");
      createBlobInContainer(CONTAINER_NAME, "4rrr.jpg");

      List<String> blobsExpected = Lists.newArrayList();
      blobsExpected.add(createBlobInContainer(CONTAINER_NAME, "rrr/sss/788.jpg"));
      blobsExpected.add(createBlobInContainer(CONTAINER_NAME, "rrr/wet.kpg"));

      checkForContainerContent(CONTAINER_NAME, "rrr", blobsExpected);
      checkForContainerContent(CONTAINER_NAME, "rrr/", blobsExpected);
   }

   @Test
   public void testClearContainerNotExistingContainer() {
      blobStore.clearContainer(CONTAINER_NAME);
   }

   @Test
   public void testClearContainer_NoOptions() throws IOException {
      final String CONTAINER_NAME2 = "containerToClear";

      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      blobStore.createContainerInLocation(null, CONTAINER_NAME2);

      List<String> blobNamesCreatedInContainer1 = createBlobsInContainer(CONTAINER_NAME, "a", 5);
      List<String> blobNamesCreatedInContainer2 = createBlobsInContainer(CONTAINER_NAME2, "b", 5);

      checkForContainerContent(CONTAINER_NAME, blobNamesCreatedInContainer1);
      checkForContainerContent(CONTAINER_NAME2, blobNamesCreatedInContainer2);

      blobStore.clearContainer(CONTAINER_NAME);
      checkForContainerContent(CONTAINER_NAME, null);
      checkForContainerContent(CONTAINER_NAME2, blobNamesCreatedInContainer2);

      blobStore.clearContainer(CONTAINER_NAME2);
      checkForContainerContent(CONTAINER_NAME, null);
      checkForContainerContent(CONTAINER_NAME2, null);
   }

   @Test
   public void testCountBlobsNotExistingContainer() {
      blobStore.countBlobs(PROVIDER);
   }

   @Test
   public void testCountBlobsNoOptionsEmptyContainer() {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      blobStore.countBlobs(PROVIDER);
   }

   @Test
   public void testCountBlobsNoOptions() {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      blobStore.countBlobs(PROVIDER);
   }

   @Test
   public void testRemoveBlobSimpleBlobKey() {
      final String BLOB_KEY = createRandomBlobKey(null, ".txt");

      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      boolean result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
      assertFalse(result, "Blob exists");

      createBlobInContainer(CONTAINER_NAME, BLOB_KEY);
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
      assertTrue(result, "Blob exists");

      blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY);
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
      assertFalse(result, "Blob still exists");
   }

   @Test
   public void testRemoveBlobTwoSimpleBlobKeys() {
      final String BLOB_KEY1 = createRandomBlobKey();
      final String BLOB_KEY2 = createRandomBlobKey();

      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      boolean result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
      assertFalse(result, "Blob1 exists");
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
      assertFalse(result, "Blob2 exists");

      createBlobInContainer(CONTAINER_NAME, BLOB_KEY1);
      createBlobInContainer(CONTAINER_NAME, BLOB_KEY2);

      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
      assertTrue(result, "Blob " + BLOB_KEY1 + " doesn't exist");
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
      assertTrue(result, "Blob " + BLOB_KEY2 + " doesn't exist");

      blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY1);
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
      assertFalse(result, "Blob1 still exists");
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
      assertTrue(result, "Blob2 doesn't exist");

      blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY2);
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
      assertFalse(result, "Blob2 still exists");
   }

   @Test
   public void testRemoveBlobComplexBlobKey() throws IOException {
      final String BLOB_KEY = createRandomBlobKey("aa/bb/cc/dd/", null);

      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      boolean result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
      assertFalse(result, "Blob exists");

      createBlobInContainer(CONTAINER_NAME, BLOB_KEY);
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
      assertTrue(result, "Blob doesn't exist");

      blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY);
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
      assertFalse(result, "Blob still exists");
   }

   @Test
   public void testRemoveBlobTwoComplexBlobKeys() throws IOException {
      final String BLOB_KEY1 = createRandomBlobKey("aa/bb/cc/dd/", null);
      final String BLOB_KEY2 = createRandomBlobKey("aa/bb/ee/ff/", null);

      blobStore.createContainerInLocation(null, CONTAINER_NAME);

      boolean result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
      assertFalse(result, "Blob1 exists");
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
      assertFalse(result, "Blob2 exists");

      createBlobInContainer(CONTAINER_NAME, BLOB_KEY1);
      createBlobInContainer(CONTAINER_NAME, BLOB_KEY2);

      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
      assertTrue(result, "Blob " + BLOB_KEY1 + " doesn't exist");
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
      assertTrue(result, "Blob " + BLOB_KEY2 + " doesn't exist");

      blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY1);
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
      assertFalse(result, "Blob still exists");

      blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY2);
      result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
      assertFalse(result, "Blob still exists");
   }

   @Test
   public void testCreateContainersInLocation() {
      final String CONTAINER_NAME2 = "funambol-test-2";

      boolean result = blobStore.containerExists(CONTAINER_NAME);
      assertFalse(result, "Container exists");
      result = blobStore.createContainerInLocation(null, CONTAINER_NAME);
      assertTrue(result, "Container not created");
      result = blobStore.containerExists(CONTAINER_NAME);
      assertTrue(result, "Container doesn't exist");

      result = blobStore.containerExists(CONTAINER_NAME2);
      assertFalse(result, "Container exists");
      result = blobStore.createContainerInLocation(null, CONTAINER_NAME2);
      assertTrue(result, "Container not created");
      result = blobStore.containerExists(CONTAINER_NAME2);
      assertTrue(result, "Container doesn't exist");
   }

   @Test
   public void testPutDirectoryBlobs() {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);

      String parentKey = createRandomBlobKey("a/b/c/directory-", "/");
      String childKey = createRandomBlobKey(parentKey + "directory-", "/");

      blobStore.putBlob(CONTAINER_NAME, createDirBlob(parentKey));
      assertTrue(blobStore.blobExists(CONTAINER_NAME, parentKey));

      blobStore.putBlob(CONTAINER_NAME, createDirBlob(childKey));
      assertTrue(blobStore.blobExists(CONTAINER_NAME, childKey));

      blobStore.removeBlob(CONTAINER_NAME, parentKey);
      assertFalse(blobStore.blobExists(CONTAINER_NAME, parentKey));
      assertTrue(blobStore.blobExists(CONTAINER_NAME, childKey));
   }

   @Test
   public void testGetDirectoryBlob() throws IOException {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);

      String blobKey = createRandomBlobKey("a/b/c/directory-", "/");
      blobStore.putBlob(CONTAINER_NAME, createDirBlob(blobKey));

      assertTrue(blobStore.blobExists(CONTAINER_NAME, blobKey));

      Blob blob = blobStore.getBlob(CONTAINER_NAME, blobKey);
      assertEquals(blob.getMetadata().getName(), blobKey, "Created blob name is different");

      assertTrue(!blobStore.blobExists(CONTAINER_NAME,
            blobKey.substring(0, blobKey.length() - 1)));
   }

   @Test
   public void testPutBlobSimpleName() {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      putBlobAndCheckIt(createRandomBlobKey("putBlob-", ".jpg"));
      putBlobAndCheckIt(createRandomBlobKey("putBlob-", ".jpg"));
   }

   @Test
   public void testListDirectoryBlobs() {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      checkForContainerContent(CONTAINER_NAME, null);

      List<String> dirs = ImmutableList.of(createRandomBlobKey("directory-", "/"));
      for (String d : dirs) {
         blobStore.putBlob(CONTAINER_NAME, createDirBlob(d));
         assertTrue(blobStore.blobExists(CONTAINER_NAME, d));
      }
      checkForContainerContent(CONTAINER_NAME, dirs);
   }

   @Test
   public void testListDirectoryBlobsS3FS() {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      checkForContainerContent(CONTAINER_NAME, null);

      String d = createRandomBlobKey("directory-", "");
      blobStore.putBlob(CONTAINER_NAME, createDirBlob(d + "/"));
      assertTrue(blobStore.blobExists(CONTAINER_NAME, d + "/"));

      ListContainerOptions options = ListContainerOptions.Builder
            .withDetails()
            .inDirectory("");
      PageSet<? extends StorageMetadata> res = blobStore.list(CONTAINER_NAME, options);
      assertTrue(res.size() == 1);
      assertEquals(res.iterator().next().getName(), d);
   }

   @Test
   public void testPutBlobComplexName1() {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      putBlobAndCheckIt(createRandomBlobKey("picture/putBlob-", ".jpg"));
      putBlobAndCheckIt(createRandomBlobKey("video/putBlob-", ".jpg"));
      putBlobAndCheckIt(createRandomBlobKey("putBlob-", ".jpg"));
      putBlobAndCheckIt(createRandomBlobKey("video/putBlob-", ".jpg"));
   }

   @Test
   public void testPutBlobComplexName2() {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      putBlobAndCheckIt(createRandomBlobKey("aa/bb/cc/dd/ee/putBlob-", ".jpg"));
      putBlobAndCheckIt(createRandomBlobKey("aa/bb/cc/dd/ee/putBlob-", ".jpg"));
      putBlobAndCheckIt(createRandomBlobKey("putBlob-", ".jpg"));
   }

   @Test
   public void testBlobExists() throws IOException {
      String blobKey = createRandomBlobKey();

      try {
         blobStore.blobExists(CONTAINER_NAME, blobKey);
         fail();
      } catch (ContainerNotFoundException cnfe) {
         // expected
      }

      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      boolean result = blobStore.blobExists(CONTAINER_NAME, blobKey);
      assertFalse(result, "Blob exists");

      createBlobInContainer(CONTAINER_NAME, blobKey);
      result = blobStore.blobExists(CONTAINER_NAME, blobKey);
      assertTrue(result, "Blob doesn't exist");

      blobKey = createRandomBlobKey("ss/asdas/", "");
      result = blobStore.blobExists(CONTAINER_NAME, blobKey);
      assertFalse(result, "Blob exists");
      createBlobInContainer(CONTAINER_NAME, blobKey);
      result = blobStore.blobExists(CONTAINER_NAME, blobKey);
      assertTrue(result, "Blob doesn't exist");
   }

   @Test(expectedExceptions = ContainerNotFoundException.class)
   public void testGetBlob_NotExistingContainer() {
      blobStore.getBlob(CONTAINER_NAME, createRandomBlobKey(), null);
   }

   @Test
   public void testGetBlob() {
      String blobKey = createRandomBlobKey();

      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      Blob resultBlob = blobStore.getBlob(CONTAINER_NAME, blobKey, null);
      assertNull(resultBlob, "Blob exists");

      createBlobInContainer(CONTAINER_NAME, blobKey);
      resultBlob = blobStore.getBlob(CONTAINER_NAME, blobKey, null);
      assertNotNull(resultBlob, "Blob exists");
   }

   @Test
   public void testBlobMetadataWithDefaultMetadata() throws IOException {
      String BLOB_KEY = createRandomBlobKey(null, null);
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      Blob blob = blobStore.blobBuilder(BLOB_KEY)
            .payload(randomByteSource().slice(0, 1024))
            .build();
      blobStore.putBlob(CONTAINER_NAME, blob);

      BlobMetadata metadata = blobStore.blobMetadata(CONTAINER_NAME, BLOB_KEY);
      assertNotNull(metadata, "Metadata null");

      assertEquals(metadata.getName(), BLOB_KEY, "Wrong blob name");
      assertEquals(metadata.getType(), StorageType.BLOB, "Wrong blob type");
      assertEquals(metadata.getContentMetadata().getContentType(), "application/unknown", "Wrong blob content-type");
      assertEquals(base16().lowerCase().encode(metadata.getContentMetadata().getContentMD5()), metadata.getETag(),
            "Wrong blob MD5");
      assertEquals(metadata.getLocation(), null, "Wrong blob location");
      assertEquals(metadata.getProviderId(), null, "Wrong blob provider id");
      assertEquals(metadata.getUri(), null, "Wrong blob URI");
      assertNotNull(metadata.getUserMetadata(), "No blob UserMetadata");
      assertEquals(metadata.getUserMetadata().size(), 0, "Wrong blob UserMetadata");
   }

   @Test
   public void testDeleteContainerNotExistingContainer() {
      blobStore.deleteContainer(CONTAINER_NAME);
   }

   @Test
   public void testDeleteContainerEmptyContanier() {
      boolean result;
      blobStore.createContainerInLocation(null, CONTAINER_NAME);

      result = blobStore.containerExists(CONTAINER_NAME);
      assertTrue(result, "Container doesn't exists");

      blobStore.deleteContainer(CONTAINER_NAME);
      result = blobStore.containerExists(CONTAINER_NAME);
      assertFalse(result, "Container still exists");
   }

   @Test
   public void testDeleteContainers() {
      boolean result;
      String CONTAINER_NAME2 = "container-to-delete";
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      blobStore.createContainerInLocation(null, CONTAINER_NAME2);

      result = blobStore.containerExists(CONTAINER_NAME);
      assertTrue(result, "Container [" + CONTAINER_NAME + "] doesn't exists");
      result = blobStore.containerExists(CONTAINER_NAME2);
      assertTrue(result, "Container [" + CONTAINER_NAME2 + "] doesn't exists");

      createBlobsInContainer(CONTAINER_NAME, "a", 5);
      createBlobsInContainer(CONTAINER_NAME, "b", 5);

      // delete first container
      blobStore.deleteContainer(CONTAINER_NAME);
      result = blobStore.containerExists(CONTAINER_NAME);
      assertFalse(result, "Container [" + CONTAINER_NAME + "] still exists");
      result = blobStore.containerExists(CONTAINER_NAME2);
      assertTrue(result, "Container [" + CONTAINER_NAME2 + "] still exists");
      // delete second container
      blobStore.deleteContainer(CONTAINER_NAME2);
      result = blobStore.containerExists(CONTAINER_NAME2);
      assertFalse(result, "Container [" + CONTAINER_NAME2 + "] still exists");
   }

   @Test
   public void testInvalidContainerName() {
      String containerName = "file/system";
      try {
         blobStore.createContainerInLocation(null, containerName);
         fail("Wrong container name not recognized");
      } catch (IllegalArgumentException e) {
      }
      try {
         blobStore.containerExists(containerName);
         fail("Wrong container name not recognized");
      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void testRanges() throws IOException {
      blobStore.createContainerInLocation(null, CONTAINER_NAME);
      String input = "abcdefgh";
      Payload payload;
      Blob blob = blobStore.blobBuilder("test").payload(new StringPayload(input)).build();
      blobStore.putBlob(CONTAINER_NAME, blob);

      GetOptions getOptionsRangeStartAt = new GetOptions();
      getOptionsRangeStartAt.startAt(1);
      Blob blobRangeStartAt = blobStore.getBlob(CONTAINER_NAME, blob.getMetadata().getName(), getOptionsRangeStartAt);
      payload = blobRangeStartAt.getPayload();
      try {
         assertEquals(input.substring(1), Strings2.toStringAndClose(payload.openStream()));
      } finally {
         Closeables2.closeQuietly(payload);
      }

      GetOptions getOptionsRangeTail = new GetOptions();
      getOptionsRangeTail.tail(3);
      Blob blobRangeTail = blobStore.getBlob(CONTAINER_NAME, blob.getMetadata().getName(), getOptionsRangeTail);
      payload = blobRangeTail.getPayload();
      try {
         assertEquals(input.substring(5), Strings2.toStringAndClose(payload.openStream()));
      } finally {
         Closeables2.closeQuietly(payload);
      }

      GetOptions getOptionsFragment = new GetOptions();
      getOptionsFragment.range(4, 6);
      Blob blobFragment = blobStore.getBlob(CONTAINER_NAME, blob.getMetadata().getName(), getOptionsFragment);
      payload = blobFragment.getPayload();
      try {
         assertEquals(input.substring(4, 7), Strings2.toStringAndClose(payload.openStream()));
      } finally {
         Closeables2.closeQuietly(payload);
      }
   }

   @Test
   public void testBlobRequestSigner() throws Exception {
      String containerName = "container";
      String blobName = "blob";
      URI endPoint = new URI("http", "localhost",
            String.format("/transient/%s/%s", containerName, blobName),
                /*fragment=*/ null);
      BlobRequestSigner signer = context.getSigner();
      HttpRequest request;
      HttpRequest expected;

      request = signer.signGetBlob(containerName, blobName);
      expected = HttpRequest.builder()
            .method("GET")
            .endpoint(endPoint)
            .headers(request.getHeaders())
            .build();
      assertEquals(expected, request);

      request = signer.signRemoveBlob(containerName, blobName);
      expected = HttpRequest.builder()
            .method("DELETE")
            .endpoint(endPoint)
            .headers(request.getHeaders())
            .build();
      assertEquals(expected, request);

      Blob blob = blobStore.blobBuilder(blobName).forSigning().build();
      request = signer.signPutBlob(containerName, blob);
      expected = HttpRequest.builder()
            .method("PUT")
            .endpoint(endPoint)
            .headers(request.getHeaders())
            .payload(new PhantomPayload())
            .build();
      assertEquals(expected, request);
   }

   private List<String> createBlobsInContainer(String containerName, String prefix, int numberOfFiles) {
      List<String> blobNames = Lists.newArrayList();
      for (int i = 0; i < numberOfFiles; i++) {
         String name = prefix + Integer.toString(i);
         blobNames.add(name);
         createBlobInContainer(containerName, name);
      }
      return blobNames;
   }

   private void checkForContainerContent(final String containerName, List<String> expectedBlobKeys) {
      checkForContainerContent(containerName, null, expectedBlobKeys);
   }

   private void checkForContainerContent(final String containerName, String inDirectory, List<String> expectedBlobKeys) {
      ListContainerOptions options = ListContainerOptions.Builder.recursive();
      if (null != inDirectory && !"".equals(inDirectory))
         options.inDirectory(inDirectory);

      PageSet<? extends StorageMetadata> blobsRetrieved = blobStore.list(containerName, options);
      for (Iterator<? extends StorageMetadata> it = blobsRetrieved.iterator(); it.hasNext();) {
         if (it.next().getType() != StorageType.BLOB) {
            it.remove();
         }
      }

      if (null == expectedBlobKeys || 0 == expectedBlobKeys.size()) {
         assertTrue(blobsRetrieved.isEmpty(), "Wrong blob number retrieved in the container [" + containerName + "]");
         return;
      }

      Set<String> expectedBlobKeysCopy = Sets.newHashSet();
      for (String value : expectedBlobKeys) {
         expectedBlobKeysCopy.add(value);
      }
      assertEquals(blobsRetrieved.size(), expectedBlobKeysCopy.size(),
            "Wrong blob number retrieved in the container [" + containerName + "]");
      for (StorageMetadata data : blobsRetrieved) {
         String blobName = data.getName();
         if (!expectedBlobKeysCopy.remove(blobName)) {
            fail("List for container [" + containerName + "] contains unexpected value [" + blobName + "]");
         }
      }
      assertTrue(expectedBlobKeysCopy.isEmpty(), "List operation for container [" + containerName
            + "] doesn't return all values.");
   }

   private static String createRandomBlobKey(String prefix, String extension) {
      String okPrefix = (null != prefix && !"".equals(prefix)) ? prefix : "testkey-";
      String okExtension = (null != extension && !"".equals(extension)) ? extension : ".jpg";
      return okPrefix + UUID.randomUUID().toString() + okExtension;
   }

   public static String createRandomBlobKey() {
      return createRandomBlobKey("", "");
   }

   private String createBlobInContainer(String containerName, String blobName) {
      blobStore.putBlob(containerName,
            blobStore.blobBuilder(blobName).payload(randomByteSource().slice(0, 1024)).build());
      return blobName;
   }

   private String createBlobInContainer(String containerName) {
      String blobName = createRandomBlobKey();
      blobStore.putBlob(containerName, blobStore.blobBuilder(blobName)
            .payload(randomByteSource().slice(0, 1024))
            .build());
      return blobName;
   }

   private Blob createDirBlob(String keyName) {
      return blobStore.blobBuilder(keyName)
            .payload(ByteSource.empty())
            .build();
   }

   private void putBlobAndCheckIt(String blobKey) {
      createBlobInContainer(CONTAINER_NAME, blobKey);
      blobStore.getBlob(CONTAINER_NAME, blobKey);
      assertTrue(blobStore.blobExists(CONTAINER_NAME, blobKey));
   }

}
