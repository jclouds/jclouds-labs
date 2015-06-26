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

import com.google.common.collect.ImmutableList;
import com.google.inject.persist.Transactional;
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
import org.jclouds.jdbc.entity.Container;
import org.jclouds.jdbc.predicates.validators.JdbcBlobKeyValidator;
import org.jclouds.jdbc.predicates.validators.JdbcContainerNameValidator;
import org.jclouds.jdbc.service.JdbcService;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * JdbcStorageStrategy implements a blob store that stores objects
 * on a jdbc supported database. Content metadata and user attributes are stored in
 * the database as well.
 */
public class JdbcStorageStrategy implements LocalStorageStrategy {

   @Resource
   private Logger logger = Logger.NULL;

   private final Provider<BlobBuilder> blobBuilders;
   private final JdbcService jdbcService;
   private final JdbcContainerNameValidator jdbcContainerNameValidator;
   private final JdbcBlobKeyValidator jdbcBlobKeyValidator;

   @Inject
   JdbcStorageStrategy(Provider<BlobBuilder> blobBuilders,
         JdbcContainerNameValidator jdbcContainerNameValidator, JdbcBlobKeyValidator jdbcBlobKeyValidator,
         JdbcService jdbcService)
         throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
      this.jdbcService = jdbcService;
      this.blobBuilders = blobBuilders;
      this.jdbcContainerNameValidator = jdbcContainerNameValidator;
      this.jdbcBlobKeyValidator = jdbcBlobKeyValidator;
   }

   @Override
   public boolean containerExists(String container) {
      jdbcContainerNameValidator.validate(container);
      try {
         jdbcService.findContainerByName(container);
      } catch (NoResultException e) {
         return false;
      }
      return true;
   }

   @Override
   public Collection<String> getAllContainerNames() {
      List<Container> containers = jdbcService.findAllContainers();
      ImmutableList.Builder<String> result = ImmutableList.builder();
      for (Container c : containers) {
         result.add(c.getName());
      }
      return result.build();
   }

   @Override
   public boolean createContainerInLocation(String container, Location location,
         CreateContainerOptions createContainerOptions) {
      logger.debug("Creating container %s", container);
      ContainerAccess containerAccess = createContainerOptions == null ? ContainerAccess.PRIVATE
            : (createContainerOptions.isPublicRead() ? ContainerAccess.PUBLIC_READ
            : ContainerAccess.PRIVATE);
      try {
         jdbcContainerNameValidator.validate(container);
         jdbcService.createContainer(container, containerAccess);
      } catch (PersistenceException e) {
         return false;
      } catch (IllegalArgumentException e) {
         return false;
      }
      return true;
   }

   @Override
   public ContainerAccess getContainerAccess(String container) {
      return jdbcService.findContainerByName(container).getContainerAccess();
   }

   @Override
   public void setContainerAccess(String container, ContainerAccess containerAccess) {
      jdbcService.setContainerAccessByName(container, containerAccess);
   }

   @Override
   public void deleteContainer(String container) {
      jdbcContainerNameValidator.validate(container);
      jdbcService.deleteContainerByName(container);
   }

   @Override
   public void clearContainer(String s) {

   }

   @Override
   public void clearContainer(String s, ListContainerOptions listContainerOptions) {

   }

   @Override
   @Transactional
   public StorageMetadata getContainerMetadata(String container) {
      MutableStorageMetadata metadata = new MutableStorageMetadataImpl();
      metadata.setName(container);
      metadata.setType(StorageType.CONTAINER);
      metadata.setLocation(null);
      metadata.setCreationDate(jdbcService.findContainerByName(container).getCreationDate());
      return metadata;
   }

   @Override
   public boolean blobExists(String s, String s1) {
      return false;
   }

   @Override
   public Iterable<String> getBlobKeysInsideContainer(String s) throws IOException {
      return null;
   }

   @Override
   public Blob getBlob(String s, String s1) {
      return null;
   }

   @Override
   public String putBlob(String s, Blob blob) throws IOException {
      return null;
   }

   @Override
   public void removeBlob(String s, String s1) {

   }

   @Override
   public BlobAccess getBlobAccess(String s, String s1) {
      return null;
   }

   @Override
   public void setBlobAccess(String s, String s1, BlobAccess blobAccess) {

   }

   @Override
   public Location getLocation(String name) {
      return null;
   }

   @Override
   public String getSeparator() {
      return "/";
   }
}
