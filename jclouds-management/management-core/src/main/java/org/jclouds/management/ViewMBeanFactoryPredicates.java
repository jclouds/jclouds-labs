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

package org.jclouds.management;

import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;
import org.jclouds.View;

import static com.google.common.base.Preconditions.checkNotNull;

public class ViewMBeanFactoryPredicates {

   public static <V extends View> Predicate<ViewMBeanFactory<V>> forType(final TypeToken<V> type) {
      checkNotNull(type, "type");
      return new ViewMBeanFactoryForType<V>(type);
   }

   private static class ViewMBeanFactoryForType<V extends View> implements Predicate<ViewMBeanFactory<V>> {

      private final TypeToken<V> type;

      private ViewMBeanFactoryForType(TypeToken<V> type) {
         this.type = type;
      }

      @Override
      public boolean apply(ViewMBeanFactory factory) {
         return factory.getViewType().isAssignableFrom(type);
      }

      @Override
      public String toString() {
         return "forType(" + type + ")";
      }
   }
}
