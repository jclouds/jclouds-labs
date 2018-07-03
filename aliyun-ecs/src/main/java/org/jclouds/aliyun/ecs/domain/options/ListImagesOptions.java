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
package org.jclouds.aliyun.ecs.domain.options;

import com.google.common.base.Joiner;
import org.jclouds.http.options.BaseHttpRequestOptions;

import java.util.Arrays;

public class ListImagesOptions extends BaseHttpRequestOptions {
   public static final String IMAGE_ID_PARAM = "ImageId";
   public static final String STATUS_PARAM = "Status";
   public static final String SNAPSHOT_ID_PARAM = "SnapshotId";
   public static final String IMAGE_NAME_PARAM = "ImageName";
   public static final String IMAGE_OWNER_ALIAS_PARAM = "ImageOwnerAlias";
   public static final String USAGE_PARAM = "Usage";

   public ListImagesOptions imageIds(String... instanceIds) {
      queryParameters.put(IMAGE_ID_PARAM, Joiner.on(",").join(Arrays.asList(instanceIds)));
      return this;
   }

   public ListImagesOptions status(String status) {
      queryParameters.put(STATUS_PARAM, status);
      return this;
   }

   public ListImagesOptions snapshotId(String snapshotId) {
      queryParameters.put(SNAPSHOT_ID_PARAM, snapshotId);
      return this;
   }

   public ListImagesOptions imageName(String imageName) {
      queryParameters.put(IMAGE_NAME_PARAM, imageName);
      return this;
   }

   public ListImagesOptions imageOwnerAlias(String imageOwnerAlias) {
      queryParameters.put(IMAGE_OWNER_ALIAS_PARAM, imageOwnerAlias);
      return this;
   }

   public ListImagesOptions usage(String usage) {
      queryParameters.put(USAGE_PARAM, usage);
      return this;
   }

   public ListImagesOptions paginationOptions(final PaginationOptions paginationOptions) {
      this.queryParameters.putAll(paginationOptions.buildQueryParameters());
      return this;
   }

   public static final class Builder {

      /**
       * @see {@link ListImagesOptions#imageIds(String...)}
       */
      public static ListImagesOptions imageIds(String... imageIds) {
         return new ListImagesOptions().imageIds(imageIds);
      }

      /**
       * @see {@link ListImagesOptions#status(String)}
       */
      public static ListImagesOptions status(String status) {
         return new ListImagesOptions().status(status);
      }

      /**
       * @see {@link ListImagesOptions#snapshotId(String)}
       */
      public static ListImagesOptions snapshotId(String snapshotId) {
         return new ListImagesOptions().snapshotId(snapshotId);
      }

      /**
       * @see {@link ListImagesOptions#imageName(String)}
       */
      public static ListImagesOptions imageName(String imageName) {
         return new ListImagesOptions().imageName(imageName);
      }

      /**
       * @see {@link ListImagesOptions#imageOwnerAlias(String)}
       */
      public static ListImagesOptions imageOwnerAlias(String imageOwnerAlias) {
         return new ListImagesOptions().imageOwnerAlias(imageOwnerAlias);
      }

      /**
       * @see {@link ListImagesOptions#usage(String)}
       */
      public static ListImagesOptions usage(String usage) {
         return new ListImagesOptions().usage(usage);
      }

      /**
       * @see ListImagesOptions#paginationOptions(PaginationOptions)
       */
      public static ListImagesOptions paginationOptions(PaginationOptions paginationOptions) {
         return new ListImagesOptions().paginationOptions(paginationOptions);
      }
   }
}
