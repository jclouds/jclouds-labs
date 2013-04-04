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

import com.google.common.base.Function;
import org.jclouds.blobstore.representations.ContentMetadata;
import org.jclouds.javax.annotation.Nullable;

public enum ToContentContentMetadata implements Function<org.jclouds.io.ContentMetadata, ContentMetadata> {

   INSTANCE;

   @Override
   public ContentMetadata apply(@Nullable org.jclouds.io.ContentMetadata input) {
      if (input == null) {
         return null;
      }
      return ContentMetadata.builder().type(input.getContentType()).disposition(input.getContentDisposition())
                            .encoding(input.getContentEncoding()).length(input.getContentLength())
                            .expires(input.getExpires())
                            .build();
   }
}
