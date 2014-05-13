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
package org.jclouds.management.internal;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.jclouds.Context;
import org.jclouds.View;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.management.ManagedBean;
import org.jclouds.management.ManagementContext;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public enum BaseManagementContext implements ManagementContext {

   INSTANCE;

   private final Map<String, View> views = Maps.newHashMap();
   private final Map<Key, ManagedBean> mbeans = Maps.newHashMap();

   //The MBeanServer can be bind/unbind (especially inside OSGi) so its not always available.
   //Thus is represented as Optional.
   private Optional<MBeanServer> mBeanServer = Optional.of(ManagementFactory.getPlatformMBeanServer());

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void manage(ManagedBean mBean, String name) {
      if (mBeanServer.isPresent()) {
         ManagementUtils.register(mBeanServer.get(), mBean, mBean.getType(), name);
      }
      mbeans.put(new Key(mBean, name), mBean);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void unmanage(ManagedBean mBean, String name) {
      if (mBeanServer.isPresent()) {
         ManagementUtils.unregister(mBeanServer.get(), mBean.getType(), name);
      }
      mbeans.remove(new Key(mBean, name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void bind(MBeanServer server) {
      this.mBeanServer = Optional.of(server);
      for (Map.Entry<Key, ManagedBean> entry : mbeans.entrySet()) {
         String name = entry.getKey().getName();
         ManagedBean mBean = entry.getValue();
         ManagementUtils.register(server, mBean, mBean.getType(), name);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void unbind(MBeanServer server) {
      for (Map.Entry<Key, ManagedBean> entry : mbeans.entrySet()) {
         String name = entry.getKey().getName();
         ManagedBean mBean = entry.getValue();
         ManagementUtils.unregister(server, mBean.getType(), name);
      }
      this.mBeanServer = Optional.absent();
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public <V extends View> void register(V view) {
      views.put(view.unwrap().getName(), view);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public <V extends View> void unregister(V view) {
      views.remove(view.unwrap().getName());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<? extends Context> listContexts() {
      return Iterables.transform(views.values(), new Function<View, Context>() {
         @Override
         public Context apply(@Nullable View input) {
            return input.unwrap();
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Context getContext(String name) {
      return views.get(name).unwrap();
   }

   private class Key {

      private final String type;
      private final String name;

      public Key(String type, String name) {
         this.type = checkNotNull(type, "type");
         this.name = checkNotNull(name, "name");
      }

      public Key(ManagedBean mbean, String name) {
         this(checkNotNull(mbean, "mbean").getType(), name);
      }

      public String getType() {
         return type;
      }

      public String getName() {
         return name;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;

         Key key = (Key) o;

         if (name != null ? !name.equals(key.name) : key.name != null) return false;
         if (type != null ? !type.equals(key.type) : key.type != null) return false;

         return true;
      }

      @Override
      public int hashCode() {
         int result = type != null ? type.hashCode() : 0;
         result = 31 * result + (name != null ? name.hashCode() : 0);
         return result;
      }
   }
}
