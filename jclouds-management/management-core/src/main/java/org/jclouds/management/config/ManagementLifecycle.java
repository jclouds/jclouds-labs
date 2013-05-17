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
package org.jclouds.management.config;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.jclouds.View;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.lifecycle.Closer;
import org.jclouds.management.ManagementContext;
import org.jclouds.management.ViewMBean;
import org.jclouds.management.ViewMBeanFactories;
import org.jclouds.management.ViewMBeanFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * A {@link com.google.inject.Module} for managing the lifecycle of {@link org.jclouds.management.ViewMBean} beans.
 * The goal of this module is to create {@link org.jclouds.management.ViewMBean} beans that correspond to each {@link View} created/destroyed
 * and register/un-register them to the {@link ManagementContext}.
 */
public class ManagementLifecycle extends AbstractModule {

   private final ManagementContext managementContext;

   public ManagementLifecycle(ManagementContext context) {
      this.managementContext = context;
   }

   @Override
   protected void configure() {
      bindListener(subClassOf(View.class), new TypeListener() {
         @Override
         public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
            typeEncounter.register(new InjectionListener<I>() {
               @Override
               public void afterInjection(Object object) {
                  final View view = (View) object;
                  final Iterable<ViewMBean> viewMamanagementBeans  = viewManagementOf(view);
                  final Closer closer = view.unwrap().utils().injector().getInstance(Closer.class);
                  //We get the name from the view and not from the view management object to avoid proxy issues.
                  final String name = view.unwrap().getName();
                  managementContext.register(view);

                  //Manage the created management view objects to the context.
                  for (ViewMBean viewMBean : viewMamanagementBeans) {
                     managementContext.manage(viewMBean, name);
                  }

                  //Add the the management view objects to the Closer, so that they are unregistered on close.
                  closer.addToClose(new Closeable() {
                     @Override
                     public void close() throws IOException {
                        for (ViewMBean viewMBean : viewMamanagementBeans) {
                           managementContext.unmanage(viewMBean, name);
                        }
                        managementContext.unregister(view);
                     }
                  });
               }
            });
         }
      });
   }

   /**
    * Returns an {@link Iterable} of {@link org.jclouds.management.ViewMBean} for the specified {@link View}.
    * @param view
    * @return
    */
   private static Iterable<ViewMBean> viewManagementOf(final View view) {
      TypeToken type = TypeToken.of(view.getClass());
      return Iterables.transform(ViewMBeanFactories.forType(type), new Function<ViewMBeanFactory, ViewMBean>() {
         @Override
         public ViewMBean apply(@Nullable ViewMBeanFactory factory) {
            return factory.create(view);
         }
      });
   }

   /**
    * Creates a {@link TypeLiteral} {@link Matcher} for matching subclasses.
    * This is for use in bindListener.
    * @param clazz
    * @return
    */
   private static Matcher<TypeLiteral> subClassOf(final Class<?> clazz) {
      return new AbstractMatcher<TypeLiteral>() {
         public boolean matches (TypeLiteral typeLiteral){
            return Matchers.subclassesOf(clazz).matches(typeLiteral.getRawType());
         }
      };
   }
}
