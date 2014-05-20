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
package org.jclouds.openstack.swift.v1.blobstore.functions;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.domain.SwiftObject.Builder;

import com.google.common.base.Function;
import com.google.common.io.ByteSource;

public class ToSwiftObject implements Function<StorageMetadata, SwiftObject> {

   @Override
   public SwiftObject apply(StorageMetadata in) {
      if (!(in instanceof BlobMetadata)) {
         return null;
      }
      BlobMetadata from = BlobMetadata.class.cast(in);
      Builder to = SwiftObject.builder();
      to.name(from.getName());
      to.etag(from.getETag());
      to.lastModified(from.getLastModified());
      long bytes = from.getContentMetadata().getContentLength();
      String contentType = from.getContentMetadata().getContentType();
      to.payload(payload(bytes, contentType));
      to.metadata(from.getUserMetadata());
      return to.build();
   }

   private static Payload payload(long bytes, String contentType) {
      Payload payload = Payloads.newByteSourcePayload(ByteSource.empty());
      payload.getContentMetadata().setContentLength(bytes);
      payload.getContentMetadata().setContentType(contentType);
      return payload;
   }
}
