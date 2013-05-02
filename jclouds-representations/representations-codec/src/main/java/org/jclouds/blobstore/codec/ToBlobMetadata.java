/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.blobstore.codec;

import com.google.common.base.Function;
import org.jclouds.blobstore.representations.BlobMetadata;
import org.jclouds.javax.annotation.Nullable;

public enum ToBlobMetadata implements Function<org.jclouds.blobstore.domain.BlobMetadata, BlobMetadata> {

   INSTANCE;

   @Override
   public BlobMetadata apply(@Nullable org.jclouds.blobstore.domain.BlobMetadata input) {
      if (input == null) {
         return null;
      }
      return BlobMetadata.builder()
                         .publicUri(input.getPublicUri()).type(input.getType().name()).providerId(input.getProviderId())
                         .name(input.getName()).uri(input.getUri()).userMetadata(input.getUserMetadata())
                         .eTag(input.getETag()).creationDate(input.getCreationDate()).lastModifiedDate(input.getLastModified())
                         .content(ToContentContentMetadata.INSTANCE.apply(input.getContentMetadata()))
                         .build();
   }
}
