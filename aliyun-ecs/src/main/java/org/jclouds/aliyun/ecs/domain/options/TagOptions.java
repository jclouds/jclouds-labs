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

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.jclouds.http.options.BaseHttpRequestOptions;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class TagOptions extends BaseHttpRequestOptions {

   private static final List<String> FORBIDDEN_PREFIX = ImmutableList.of("aliyun", "acs", "http://", "https://");
   private static final String TAG_KEY_TEMPLATE = "Tag.%d.Key";
   private static final String TAG_VALUE_TEMPLATE = "Tag.%d.Value";

   /**
    * Tag keys can be up to 64 characters in length.
    * Cannot begin with aliyun, acs:, http://, or https://. Cannot be a null string.
    *
    * Tag values can be up to 128 characters in length.
    * Cannot begin with aliyun, http://, or https://. Can be a null string.
    */
   public TagOptions tag(int pos, final String key, final String value) {
      validateInput(key, 64);
      validateInput(value, 128);
      queryParameters.put(String.format(TAG_KEY_TEMPLATE, pos), key);
      queryParameters.put(String.format(TAG_VALUE_TEMPLATE, pos), value);
      return this;
   }

   public TagOptions tag(int pos, String key) {
      validateInput(key, 64);
      queryParameters.put(String.format(TAG_KEY_TEMPLATE, pos), key);
      queryParameters.put(String.format(TAG_VALUE_TEMPLATE, pos), "");
      return this;
   }

   public TagOptions keys(Set<String> keys) {
      checkState(keys.size() <= 5, "keys must be <= 5");
      int i = 1;
      for (String key : keys) {
         tag(i, key);
         i++;
      }
      return this;
   }

   public TagOptions tags(Map<String, String> tags) {
      checkState(tags.size() <= 5, "tags size must be <= 5");
      int i = 1;
      for (Map.Entry<String, String> entry : tags.entrySet()) {
         tag(i, entry.getKey(), entry.getValue());
         i++;
      }
      return this;
   }

   public static class Builder {

      public static TagOptions tag(int pos, String key, String value) {
         return new TagOptions().tag(pos, key, value);
      }

      public static TagOptions tag(int pos, String key) {
         return new TagOptions().tag(pos, key);
      }

      public static TagOptions keys(Set<String> keys) {
         return new TagOptions().keys(keys);
      }

      public static TagOptions tags(Map<String, String> tags) {
         return new TagOptions().tags(tags);
      }
   }

   private void validateInput(final String input, int maxLength) {
      checkNotNull(input);
      checkState(input.length() <= maxLength, String.format("input must be <= %d chars", maxLength));
      checkState(!Iterables.any(FORBIDDEN_PREFIX, new Predicate<String>() {
         @Override
         public boolean apply(String forbiddenPrefix) {
            return input.startsWith(forbiddenPrefix);
         }
      }), input + " cannot starts with any of " + Iterables.toString(FORBIDDEN_PREFIX));
   }

}
