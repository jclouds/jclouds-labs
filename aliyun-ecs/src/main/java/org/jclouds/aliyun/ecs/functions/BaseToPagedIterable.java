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
package org.jclouds.aliyun.ecs.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.domain.options.ListImagesOptions;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.Arg0ToPagedIterable;

/**
 * Base class to implement the functions that build the
 * <code>PagedIterable</code>. Subclasses just need to override the
 * {@link #fetchPageUsingOptions(ListImagesOptions, Optional)} to invoke the right API
 * method with the given options parameter to get the next page.
 */
public abstract class BaseToPagedIterable<T, O extends ListImagesOptions> extends
        Arg0ToPagedIterable<T, BaseToPagedIterable<T, O>> {
   private final Function<Integer, O> pageNumberToOptions;
   protected final ECSComputeServiceApi api;

   protected BaseToPagedIterable(ECSComputeServiceApi api, Function<Integer, O> pageNumberToOptions) {
      this.api = api;
      this.pageNumberToOptions = pageNumberToOptions;
   }

   protected abstract IterableWithMarker<T> fetchPageUsingOptions(O options, Optional<Object> arg0);

   @Override
   protected Function<Object, IterableWithMarker<T>> markerToNextForArg0(final Optional<Object> arg0) {
      return new Function<Object, IterableWithMarker<T>>() {
         @Override
         public IterableWithMarker<T> apply(Object input) {
            O nextOptions = pageNumberToOptions.apply(Integer.class.cast(input));
            return fetchPageUsingOptions(nextOptions, arg0);
         }
      };
   }

}
