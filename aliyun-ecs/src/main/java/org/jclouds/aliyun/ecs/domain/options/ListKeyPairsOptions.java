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

import org.jclouds.http.options.BaseHttpRequestOptions;

public class ListKeyPairsOptions extends BaseHttpRequestOptions {
   public static final String KEY_PAIR_FINGERPRINT_PARAM = "KeyPairFingerPrint";
   public static final String KEY_PAIR_NAME_PARAM = "KeyPairName";

   public ListKeyPairsOptions keyPairFingerPrint(String keyPairFingerPrint) {
      queryParameters.put(KEY_PAIR_FINGERPRINT_PARAM, keyPairFingerPrint);
      return this;
   }


   public ListKeyPairsOptions keyPairName(String keyPairName) {
      queryParameters.put(KEY_PAIR_NAME_PARAM, keyPairName);
      return this;
   }

   public ListKeyPairsOptions paginationOptions(final PaginationOptions paginationOptions) {
      this.queryParameters.putAll(paginationOptions.buildQueryParameters());
      return this;
   }

   public static final class Builder {

      /**
       * @see {@link ListKeyPairsOptions#keyPairFingerPrint(String)}
       */
      public static ListKeyPairsOptions keyPairFingerPrint(String keyPairFingerPrint) {
         return new ListKeyPairsOptions().keyPairFingerPrint(keyPairFingerPrint);
      }

      /**
       * @see {@link ListKeyPairsOptions#keyPairName(String)}
       */
      public static ListKeyPairsOptions keyPairName(String keyPairName) {
         return new ListKeyPairsOptions().keyPairName(keyPairName);
      }

      /**
       * @see ListKeyPairsOptions#paginationOptions(PaginationOptions)
       */
      public static ListKeyPairsOptions paginationOptions(PaginationOptions paginationOptions) {
         return new ListKeyPairsOptions().paginationOptions(paginationOptions);
      }
   }
}
