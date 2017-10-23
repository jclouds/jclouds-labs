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
package org.jclouds.jdbc.conversion;

import com.google.common.base.Function;
import com.google.common.hash.HashCode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.jdbc.entity.BlobEntity;
import org.jclouds.jdbc.entity.PayloadEntity;
import org.jclouds.jdbc.service.JdbcService;
import org.jclouds.jdbc.util.JdbcInputStream;

public class BlobEntityToBlob implements Function<BlobEntity, Blob> {

   private final Provider<BlobBuilder> blobBuilders;
   private final JdbcService jdbcService;

   @Inject
   BlobEntityToBlob(Provider<BlobBuilder> blobBuilders, JdbcService jdbcService) {
      this.blobBuilders = blobBuilders;
      this.jdbcService = jdbcService;
   }

   @Override
   public Blob apply(BlobEntity blobEntity) {
      if (blobEntity == null) {
         return null;
      }

      PayloadEntity payload = blobEntity.getPayload();
      BlobBuilder builder = blobBuilders.get()
            .name(blobEntity.getKey())
            .userMetadata(blobEntity.getUserMetadata());

      if (blobEntity.isDirectory()) {
         builder.type(StorageType.FOLDER);
      }
      else {
         builder.payload(new JdbcInputStream(jdbcService, blobEntity.getPayload().getChunks()));
      }

      Blob blob = builder.build();

      blob.getMetadata().setContainer(blobEntity.getContainerEntity().getName());
      blob.getMetadata().setCreationDate(blobEntity.getCreationDate());
      blob.getMetadata().setLastModified(blobEntity.getLastModified());
      blob.getMetadata().setSize(blobEntity.getSize());
      blob.getMetadata().setUserMetadata(blobEntity.getUserMetadata());

      blob.getMetadata().getContentMetadata().setCacheControl(payload.getCacheControl());
      blob.getMetadata().getContentMetadata().setContentType(payload.getContentType());
      blob.getMetadata().getContentMetadata().setContentDisposition(payload.getContentDisposition());
      blob.getMetadata().getContentMetadata().setContentEncoding(payload.getContentEncoding());
      blob.getMetadata().getContentMetadata().setContentLanguage(payload.getContentLanguage());
      blob.getMetadata().getContentMetadata().setContentLength(payload.getContentLength());
      blob.getMetadata().getContentMetadata().setContentMD5(payload.getContentMD5() == null ?
            null :
            HashCode.fromBytes(payload.getContentMD5()));
      blob.getMetadata().setETag(blobEntity.getEtag());
      blob.getMetadata().getContentMetadata().setExpires(payload.getExpires());
      blob.getMetadata().setTier(blobEntity.getTier());
      return blob;
   }

}
