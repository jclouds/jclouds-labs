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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.jclouds.http.options.BaseHttpRequestOptions;

import java.util.Arrays;

public class DeleteKeyPairOptions extends BaseHttpRequestOptions {

   public static final String KEYPAIR_NAMES_PARAM = "KeyPairNames";

   public DeleteKeyPairOptions keyPairNames(String... keyPairNames) {
      String keyPairNamesAsString = Joiner.on(",")
            .join(Iterables.transform(Arrays.asList(keyPairNames), new Function<String, String>() {
               @Override
               public String apply(String s) {
                  return new StringBuilder(s.length() + 1).append('"').append(s).append('"').toString();
               }
            }));
      queryParameters.put(KEYPAIR_NAMES_PARAM, String.format("[%s]", keyPairNamesAsString));
      return this;
   }

   public static final class Builder {

      /**
       * @see {@link DeleteKeyPairOptions#keyPairNames(String...)}
       */
      public static DeleteKeyPairOptions keyPairNames(String... keyPairNames) {
         return new DeleteKeyPairOptions().keyPairNames(keyPairNames);
      }
   }
}
