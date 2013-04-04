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

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import org.jclouds.management.osgi.ViewManagementFactoryRegistry;

import java.util.ServiceLoader;

import static com.google.common.collect.Iterables.filter;

public class ViewMBeanFactories {

   /**
    * Returns the {@link ViewMBeanFactory} located on the classpath via {@link java.util.ServiceLoader}.
    * @return all available factories loaded from classpath via  {@link java.util.ServiceLoader}
    */
   public static Iterable<ViewMBeanFactory> fromServiceLoader() {
      return ServiceLoader.load(ViewMBeanFactory.class);
   }

   /**
    * Returns the {@link ViewMBeanFactory} found via {@link org.jclouds.management.osgi.ViewManagementFactoryRegistry} and  {@link java.util.ServiceLoader}.
    * @return all available factories.
    */
   public static Iterable<ViewMBeanFactory> all() {
      return ImmutableSet.<ViewMBeanFactory>builder()
              .addAll(fromServiceLoader())
              .addAll(ViewManagementFactoryRegistry.fromRegistry()).build();
   }

   public static Iterable<ViewMBeanFactory> forType(TypeToken viewableAs) {
      return filter(all(), ViewMBeanFactoryPredicates.forType(viewableAs));
   }

}
