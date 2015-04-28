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
package org.jclouds.jdbc.util;

import javax.inject.Provider;

import org.jclouds.blobstore.LocalStorageStrategy;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.jdbc.strategy.JdbcStorageStrategy;

import com.google.inject.Inject;

/**
 * Implements the {@link BlobUtils} interfaced and act as a bridge to
 * {@link LocalStorageStrategy} when used inside {@link BlobStore}
 */
public class JdbcBlobUtils implements BlobUtils {

   protected final JdbcStorageStrategy storageStrategy;
   protected final Provider<BlobBuilder> blobBuilders;

   @Inject
   JdbcBlobUtils(LocalStorageStrategy storageStrategy, Provider<BlobBuilder> blobBuilders) {
      this.storageStrategy = (JdbcStorageStrategy) storageStrategy;
      this.blobBuilders = blobBuilders;
   }

   @Override
   public BlobBuilder blobBuilder() {
      return blobBuilders.get();
   }

   @Override public boolean directoryExists(String s, String s1) {
      return false;
   }

   @Override public void createDirectory(String s, String s1) {

   }

   @Override public long countBlobs(String s, ListContainerOptions listContainerOptions) {
      return 0;
   }

   @Override
   public void clearContainer(String container, ListContainerOptions options) {
      storageStrategy.clearContainer(container, options);
   }

   @Override public void deleteDirectory(String s, String s1) {

   }

}
