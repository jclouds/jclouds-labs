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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.PersistenceException;

import org.jclouds.blobstore.LocalStorageStrategy;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.io.ContentMetadata;
import org.jclouds.jdbc.conversion.BlobEntityToBlob;
import org.jclouds.jdbc.entity.BlobEntity;
import org.jclouds.jdbc.entity.ContainerEntity;
import org.jclouds.jdbc.predicates.validators.JdbcBlobKeyValidator;
import org.jclouds.jdbc.predicates.validators.JdbcContainerNameValidator;
import org.jclouds.jdbc.service.JdbcService;

import com.google.common.collect.ImmutableList;

/**
 * JdbcStorageStrategy implements a blob store that stores objects
 * on a jdbc supported database. Content metadata and user attributes are stored in
 * the database as well.
 */
public class JdbcStorageStrategy implements LocalStorageStrategy {

   private final Provider<BlobBuilder> blobBuilders;
   private final JdbcService jdbcService;
   private final JdbcContainerNameValidator jdbcContainerNameValidator;
   private final JdbcBlobKeyValidator jdbcBlobKeyValidator;
   private final BlobEntityToBlob blobEntityToBlob;
   private final Location mockLocation;

   @Inject
   JdbcStorageStrategy(Provider<BlobBuilder> blobBuilders,
         JdbcContainerNameValidator jdbcContainerNameValidator, JdbcBlobKeyValidator jdbcBlobKeyValidator,
         JdbcService jdbcService, BlobEntityToBlob blobEntityToBlob)
         throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
      this.jdbcService = jdbcService;
      this.blobBuilders = blobBuilders;
      this.jdbcContainerNameValidator = jdbcContainerNameValidator;
      this.jdbcBlobKeyValidator = jdbcBlobKeyValidator;
      this.blobEntityToBlob = blobEntityToBlob;
      this.mockLocation = new LocationBuilder()
            .id("jdbc")
            .scope(LocationScope.PROVIDER)
            .description("http://localhost/transient")
            .build();
   }

   /**
    * Checks if a container exists
    *
    * @param container the name of the container to check
    * @return true if the container exists, false otherwise
    */
   @Override
   public boolean containerExists(String container) {
      jdbcContainerNameValidator.validate(container);
      return jdbcService.findContainerByName(container) != null;
   }

   /**
    * Lists all the container names
    *
    * @return an iterable with the container names
    */
   @Override
   public Collection<String> getAllContainerNames() {
      List<ContainerEntity> containerEntities = jdbcService.findAllContainers();
      ImmutableList.Builder<String> result = ImmutableList.builder();
      for (ContainerEntity c : containerEntities) {
         result.add(c.getName());
      }
      return result.build();
   }

   /**
    * Creates a container. The location is ignored.
    *
    * @param container the name of the container to create
    * @param createContainerOptions creation options to the container. defaults the ContainerAccess
    *                               to ContainerAccess.PRIVATE
    * @return true if the container was created, false otherwise
    */
   @Override
   public boolean createContainerInLocation(String container, Location location,
         CreateContainerOptions createContainerOptions) {
      jdbcContainerNameValidator.validate(container);

      ContainerAccess containerAccess = createContainerOptions == null ? ContainerAccess.PRIVATE
            : (createContainerOptions.isPublicRead() ? ContainerAccess.PUBLIC_READ
            : ContainerAccess.PRIVATE);
      try {
         jdbcService.createContainer(container, containerAccess);
      } catch (PersistenceException e) {
         return false;
      } catch (IllegalArgumentException e) {
         return false;
      }
      return true;
   }

   /**
    * Gets the container accessibility
    *
    * @param container the name of the container
    * @return a value with the container's accessibility
    */
   @Override
   public ContainerAccess getContainerAccess(String container) {
      return jdbcService.findContainerByName(container).getContainerAccess();
   }

   /**
    * Sets the container accessibility
    *
    * @param container the name of the container
    * @param containerAccess the new container access for the container
    */
   @Override
   public void setContainerAccess(String container, ContainerAccess containerAccess) {
      jdbcService.setContainerAccessByName(container, containerAccess);
   }

   /**
    * Deletes a container and all the blobs in it
    *
    * @param container the name of the container to delete
    */
   @Override
   public void deleteContainer(String container) {
      jdbcContainerNameValidator.validate(container);
      jdbcService.deleteBlobsByContainer(container);
      jdbcService.deleteContainerByName(container);
   }

   /**
    * Deletes all the blobs in a container
    *
    * @param container the name of the container to clear
    */
   @Override
   public void clearContainer(String container) {
      jdbcService.deleteBlobsByContainer(container);
   }

   /**
    * Deletes all the blobs in a container
    *
    * @param container the name of the container to clear
    * @param options options to filter what blobs are cleared
    */
   @Override
   public void clearContainer(String container, ListContainerOptions options) {
      if (options.getDir() != null) {
         jdbcService.deleteBlobsByDirectory(container, options.getDir(), options.isRecursive());
      }
      else {
         clearContainer(container);
      }
   }

   /**
    * Gets a container's metadata
    *
    * @param containerName the name of the container
    * @return the container's metadata
    */
   @Override
   public StorageMetadata getContainerMetadata(String containerName) {
      ContainerEntity containerEntity = jdbcService.findContainerByName(containerName);
      MutableStorageMetadata metadata = null;
      if (containerEntity != null) {
         metadata = new MutableStorageMetadataImpl();
         metadata.setName(containerName);
         metadata.setType(StorageType.CONTAINER);
         metadata.setLocation(mockLocation);
         metadata.setCreationDate(containerEntity.getCreationDate());
      }
      return metadata;
   }

   /**
    * Checks if a blob exists
    *
    * @param container the name of the container containing the blob
    * @param key the blob's key
    * @return true if the blob exists in the container, false otherwise
    */
   @Override
   public boolean blobExists(String container, String key) {
      jdbcContainerNameValidator.validate(container);
      jdbcBlobKeyValidator.validate(key);
      return jdbcService.blobExists(container, key);
   }

   /**
    * Lists all the blob keys in a container
    *
    * @param container the name of the container
    * @return the blob keys inside the container
    */
   @Override
   public Iterable<String> getBlobKeysInsideContainer(String container) throws IOException {
      List<BlobEntity> blobEntities = jdbcService.findBlobsByContainer(container);
      ImmutableList.Builder<String> result = ImmutableList.builder();
      for (BlobEntity blobEntity : blobEntities) {
         result.add(blobEntity.getKey());
      }
      return result.build();
   }

   /**
    * Gets a blob in a container
    *
    * @param container the name of the container containing the blob
    * @param key the key of the blob to get
    * @return the blob in the container or null if the blob does not exist
    */
   @Override
   public Blob getBlob(String container, String key) {
      return blobEntityToBlob.apply(jdbcService.findBlobById(container, key));
   }

   /**
    * Store a blob in a container
    *
    * @param container the name of the container
    * @param blob the blob to store
    * @return the blob's etag
    */
   @Override
   public String putBlob(String container, Blob blob) throws IOException {
      String key = blob.getMetadata().getName();
      jdbcContainerNameValidator.validate(container);
      jdbcBlobKeyValidator.validate(key);
      return jdbcService.createOrModifyBlob(container, blob).getEtag();
   }

   /**
    * Removes a blob from a container
    *
    * @param container the name of the container containing the blob
    * @param key the blob's key
    */
   @Override
   public void removeBlob(String container, String key) {
      jdbcService.deleteBlob(container, key);
   }

   /**
    * Gets the blob accessibility
    *
    * @param container the name of the container containing the blob
    * @param key the blob's key
    * @return a value with the container's accessibility
    */
   @Override
   public BlobAccess getBlobAccess(String container, String key) {
      return jdbcService.findBlobById(container, key).getBlobAccess();
   }

   /**
    * Sets the blob accessibility
    *
    * @param container the name of the container containing the blob
    * @param key the blob's key
    * @param blobAccess the new blob access for the blob
    */
   @Override
   public void setBlobAccess(String container, String key, BlobAccess blobAccess) {
      jdbcService.setBlobAccessById(container, key, blobAccess);
   }

   /**
    * Gets the container location
    *
    * @return always return null
    */
   @Override
   public Location getLocation(String container) {
      return mockLocation;
   }

   /**
    * Gets the separator used for directory naming
    *
    * @return a string containing "/"
    */
   @Override
   public String getSeparator() {
      return "/";
   }

   /**
    * Count the blobs in a container
    *
    * @param container the name of the container
    * @param options options to filter what blobs are counted
    * @return the number of blobs in the container
    */
   public long countBlobs(String container, ListContainerOptions options) {
      return options.getDir() == null ? jdbcService.findBlobsByContainer(container).size()
             : jdbcService.findBlobsByDirectory(container, options.getDir(), options.isRecursive()).size();
   }

   /**
    * Checks if a directory exists
    *
    * @param container the name of the container
    * @param directory the name of the directory
    * @return true if the directory exists, false otherwise
    */
   public boolean directoryExists(String container, String directory) {
      Blob blob = getBlob(container, directory);
      return blob != null && "application/directory".equals(
            blob.getMetadata().getContentMetadata().getContentType());
   }

   /**
    * Store a blob in a directory
    *
    * @param container the name of the container
    * @param blob the blob to store
    * @return the blob's etag
    */
   private String putDirectoryBlob(final String container, final Blob blob) {
      String blobKey = blob.getMetadata().getName();
      ContentMetadata metadata = blob.getMetadata().getContentMetadata();
      Long contentLength = metadata.getContentLength();
      if (contentLength != null && contentLength != 0) {
         throw new IllegalArgumentException(
               "Directory blob cannot have content: " + blobKey);
      }
      return jdbcService.createDirectoryBlob(container, blob).getEtag();
   }

   /**
    * Creates a new directory
    *
    * @param container the name of the container
    * @param directory the name of the directory to create
    */
   public void createDirectory(String container, String directory) {
      Blob blob = blobBuilders.get().name(directory).payload("").build();
      blob.getMetadata().getContentMetadata().setContentType("application/directory");
      putDirectoryBlob(container, blob);
   }

   /**
    * Delete an existing directory
    *
    * @param container the name of the container
    * @param directory the name of the directory to delete
    */
   public void deleteDirectory(String container, String directory) {
      BlobEntity entity = jdbcService.findBlobById(container, directory);
      if (entity != null && entity.isDirectory()) {
         jdbcService.deleteBlob(container, directory);
      }
   }

}
