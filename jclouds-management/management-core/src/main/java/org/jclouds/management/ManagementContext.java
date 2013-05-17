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

import org.jclouds.Context;
import org.jclouds.View;

import javax.management.MBeanServer;

/**
 * The management context, keeps track of the {@link ManagedBean} objects that have been created.
 * It is responsible for exporting beans to the {@link javax.management.MBeanServer}, whenever it becomes available.
 * It also keeps track of {@link View}s created, so that they can be accessed via JMX.
 */
public interface ManagementContext {

   /**
    * Register a {@link ManagedBean} to the MBeanServer.
    * @param mBean   The ManagedBean to add to the context.
    * @param name    The name under which the bean will be exposed.
    */
   void manage(ManagedBean mBean, String name);

   /**
    * Un-registers a {@link ManagedBean} to the MBeanServer.
    * @param mBean   The ManagedBean to remove from the context.
    * @param name    The name under which the bean was exposed.
    */
   void unmanage(ManagedBean mBean, String name);


   /**
    * Bind an {@link javax.management.MBeanServer} to the context.
    * This is mostly useful for dynamic environments where an {@link javax.management.MBeanServer} may come and go.
    * The context should re-register the {@link ManagedBean} objects that have been added to the context.
    * @param mBeanServer
    */
   void bind(MBeanServer mBeanServer);

   /**
    * Unbind an {@link javax.management.MBeanServer} to the context.
    * This is mostly useful for dynamic environments where an {@link javax.management.MBeanServer} may come and go.
    * The context should unregister the {@link ManagedBean} objects that have been added to the context.
    * @param mBeanServer
    */
   void unbind(MBeanServer mBeanServer);

   /**
    * Register {@link org.jclouds.View}.
    * @param view
    * @param <V>
    */
   <V extends View> void register(V view);

   /**
    * Un-register {@link View}.
    * @param view
    * @param <V>
    */
   <V extends View> void unregister(V view);

   /**
    * List all registered {@link Context} objects.
    * @return
    */
   Iterable<? extends Context> listContexts();


   /**
    * Returns {@link Context} by name.
    * @param name
    * @param <C>
    */
   <C extends Context> C getContext(String name);

}
