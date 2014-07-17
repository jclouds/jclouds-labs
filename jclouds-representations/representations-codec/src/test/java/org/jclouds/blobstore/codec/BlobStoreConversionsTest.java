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
package org.jclouds.blobstore.codec;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteSource;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.representations.Blob;
import org.jclouds.blobstore.representations.StorageMetadata;
import org.testng.annotations.Test;

import java.util.Set;

import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Test
public class BlobStoreConversionsTest {

   private BlobStore getBlobStore() {
      BlobStoreContext context = ContextBuilder.newBuilder("transient").name("test-transient").credentials("user", "pass").build(BlobStoreContext.class);
      return context.getBlobStore();
   }

   @Test
   void testToStorageMetadata() {
      assertNull(ToStorageMetadata.INSTANCE.apply(null));
      BlobStore blobStore = getBlobStore();
      blobStore.createContainerInLocation(null, "test");
      blobStore.createDirectory("test", "one");
      Set<StorageMetadata> storageMetadataSet = ImmutableSet.<StorageMetadata>builder()
                                                            .addAll(transform(blobStore.list(), ToStorageMetadata.INSTANCE))
                                                            .build();
      assertFalse(storageMetadataSet.isEmpty());
      StorageMetadata representation = storageMetadataSet.iterator().next();
      assertEquals("test", representation.getName());
   }

   @Test
   void testToBlob() {
      assertNull(ToBlob.INSTANCE.apply(null));
      BlobStore blobStore = getBlobStore();
      blobStore.createContainerInLocation(null, "container");
      blobStore.createDirectory("container", "one");

      blobStore.putBlob("container", blobStore.blobBuilder("myblob").payload(ByteSource.wrap("testcontent".getBytes())).build());
      Blob representation = ToBlob.INSTANCE.apply(blobStore.getBlob("container", "myblob"));
      assertNotNull(representation);
      assertNotNull(representation.getBlobMetadata());
   }
}
