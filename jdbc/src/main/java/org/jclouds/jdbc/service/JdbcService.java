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
package org.jclouds.jdbc.service;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.jdbc.conversion.BlobToBlobEntity;
import org.jclouds.jdbc.entity.BlobEntity;
import org.jclouds.jdbc.entity.BlobEntityPK;
import org.jclouds.jdbc.entity.ChunkEntity;
import org.jclouds.jdbc.entity.ContainerEntity;
import org.jclouds.jdbc.entity.PayloadEntity;
import org.jclouds.jdbc.reference.JdbcConstants;
import org.jclouds.jdbc.repository.BlobRepository;
import org.jclouds.jdbc.repository.ChunkRepository;
import org.jclouds.jdbc.repository.ContainerRepository;
import org.jclouds.util.Closeables2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.common.io.BaseEncoding.base16;

@Singleton
public class JdbcService {

   private static final String DIRECTORY_MD5 = Hashing.md5().hashBytes(new byte[0]).toString();

   private final ContainerRepository containerRepository;
   private final BlobRepository blobRepository;
   private final ChunkRepository chunkRepository;
   private final BlobToBlobEntity blobToBlobEntity;

   @Inject
   JdbcService(ContainerRepository containerRepository, BlobRepository blobRepository, ChunkRepository chunkRepository,
         BlobToBlobEntity blobToBlobEntity) {
      this.containerRepository = containerRepository;
      this.blobRepository = blobRepository;
      this.chunkRepository = chunkRepository;
      this.blobToBlobEntity = blobToBlobEntity;
   }

   @Transactional
   public void createContainer(String containerName, ContainerAccess access) {
      containerRepository.create(ContainerEntity.builder().name(containerName).containerAccess(access).build());
   }

   @Transactional
   public void createContainer(String containerName) {
      createContainer(containerName, null);
   }

   @Transactional
   public List<ContainerEntity> findAllContainers() {
      return containerRepository.findAllContainers();
   }

   @Transactional
   public ContainerEntity findContainerByName(String containerName) {
      return containerRepository.findContainerByName(containerName);
   }

   @Transactional
   public void deleteContainerByName(String containerName) {
      containerRepository.deleteContainerByName(containerName);
   }

   @Transactional
   public void setContainerAccessByName(String containerName, ContainerAccess access) {
      ContainerEntity containerEntity = containerRepository.findContainerByName(containerName);
      containerEntity.setContainerAccess(access);
      containerRepository.save(containerEntity);
   }

   @Transactional
   public boolean blobExists(String containerName, String key) {
      return findBlobById(containerName, key) != null;
   }

   @Transactional(rollbackOn = IOException.class)
   public BlobEntity createOrModifyBlob(String containerName, Blob blob, BlobAccess blobAccess) throws IOException {
      List<Long> chunks;
      HashingInputStream his = new HashingInputStream(Hashing.md5(), blob.getPayload().openStream());
      try {
         chunks = storeData(his);
      } finally {
         Closeables2.closeQuietly(his);
      }
      HashCode actualHashCode = his.hash();
      HashCode expectedHashCode = blob.getPayload().getContentMetadata().getContentMD5AsHashCode();
      if (expectedHashCode != null && !actualHashCode.equals(expectedHashCode)) {
         throw new IOException("MD5 hash code mismatch, actual: " + actualHashCode +
               " expected: " + expectedHashCode);
      }

      String key = blob.getMetadata().getName();
      Date creationDate = null;
      BlobEntity oldBlobEntity = findBlobById(containerName, key);
      if (oldBlobEntity != null) {
         creationDate = oldBlobEntity.getCreationDate();
      }
      BlobEntity blobEntity = blobToBlobEntity.apply(blob);
      blobEntity.getPayload().setChunks(chunks);
      blobEntity.setContainerEntity(containerRepository.findContainerByName(containerName));
      blobEntity.setKey(key);
      blobEntity.setBlobAccess(blobAccess);
      blobEntity.setCreationDate(creationDate);
      blobEntity.setLastModified(new Date());
      blobEntity.setEtag(base16().lowerCase().encode(actualHashCode.asBytes()));
      blobEntity.getPayload().setContentMD5(actualHashCode.asBytes());

      BlobEntity result = blobRepository.save(blobEntity);
      return result;
   }

   @Transactional
   public BlobEntity createDirectoryBlob(String containerName, Blob blob, BlobAccess blobAccess) {
      BlobEntity blobEntity = BlobEntity.builder(null, null)
            .userMetadata(blob.getMetadata().getUserMetadata())
            .directory(true)
            .payload(PayloadEntity.builder().contentType("application/directory").build())
            .build();
      blobEntity.setContainerEntity(containerRepository.findContainerByName(containerName));
      blobEntity.setKey(blob.getMetadata().getName());
      blobEntity.setBlobAccess(blobAccess);
      blobEntity.setEtag(DIRECTORY_MD5);
      return blobRepository.save(blobEntity);
   }

   @Transactional
   public BlobEntity createDirectoryBlob(String containerName, Blob blob) {
      return createDirectoryBlob(containerName, blob, null);
   }

   @Transactional(rollbackOn = IOException.class)
   public BlobEntity createOrModifyBlob(String containerName, Blob blob) throws IOException {
      return createOrModifyBlob(containerName, blob, null);
   }

   @Transactional
   public BlobEntity findBlobById(String containerName, String key) {
      ContainerEntity containerEntity = containerRepository.findContainerByName(containerName);
      return containerEntity == null ? null : blobRepository.find(new BlobEntityPK(containerEntity.getId(), key));
   }

   @Transactional
   public ChunkEntity findChunkById(Long id) {
      return chunkRepository.find(id);
   }

   @Transactional
   public List<BlobEntity> findBlobsByContainer(String containerName) {
      return blobRepository.findBlobsByContainer(containerRepository.findContainerByName(containerName));
   }

   @Transactional
   public List<BlobEntity> findBlobsByDirectory(String containerName, String directoryName, boolean recursive) {
      ImmutableList.Builder<BlobEntity> result = ImmutableList.builder();
      List<BlobEntity> blobEntities = blobRepository
            .findBlobsByDirectory(containerRepository.findContainerByName(containerName), directoryName);
      result.addAll(blobEntities);
      if (recursive) {
         for (BlobEntity blobEntity : blobEntities) {
            if (blobEntity.isDirectory()) {
               result.addAll(findBlobsByDirectory(containerName, blobEntity.getKey(), true));
            }
         }
      }
      return result.build();
   }

   @Transactional
   public void deleteBlobsByContainer(String containerName) {
      List<BlobEntity> blobs = findBlobsByContainer(containerName);
      for (BlobEntity blob : blobs) {
         deleteBlob(containerName, blob.getKey());
      }
   }

   @Transactional
   public void deleteBlobsByDirectory(String containerName, String directoryName, boolean recursive) {
      List<BlobEntity> blobs = findBlobsByDirectory(containerName, directoryName, false);
      for (BlobEntity blob : blobs) {
         if (!blob.isDirectory()) {
            deleteBlob(containerName, blob.getKey());
         }
         else {
            if (recursive) {
               deleteBlobsByDirectory(containerName, blob.getKey(), true);
               deleteBlob(containerName, blob.getKey());
            }
         }
      }
   }

   @Transactional
   public void deleteBlob(String containerName, String key) {
      BlobEntity blobEntity = findBlobById(containerName, key);
      if (blobEntity != null) {
         deleteChunks(blobEntity.getPayload().getChunks());
         blobRepository.delete(blobEntity);
      }
   }

   @Transactional
   public void setBlobAccessById(String containerName, String key, BlobAccess access) {
      BlobEntity blobEntity = findBlobById(containerName, key);
      blobEntity.setBlobAccess(access);
      blobRepository.save(blobEntity);
   }

   @Transactional
   private void deleteChunks(List<Long> chunkIds) {
      for (Long chunkId : chunkIds) {
         chunkRepository.delete(chunkRepository.find(chunkId));
      }
   }

   @Transactional(rollbackOn = IOException.class)
   private List<Long> storeData(InputStream data) throws IOException {
      ImmutableList.Builder<Long> chunks = ImmutableList.builder();
      while (true) {
         byte[] buffer = new byte[JdbcConstants.DEFAULT_CHUNK_SIZE];
         int bytes = ByteStreams.read(data, buffer, 0, JdbcConstants.DEFAULT_CHUNK_SIZE);
         if (bytes == 0) {
            break;
         } else if (bytes != buffer.length) {
            buffer = Arrays.copyOf(buffer, bytes);
         }
         chunks.add(chunkRepository.create(new ChunkEntity(buffer, bytes)).getId());
      }
      return chunks.build();
   }
}
