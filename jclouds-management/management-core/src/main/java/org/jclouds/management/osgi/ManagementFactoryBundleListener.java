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
package org.jclouds.management.osgi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jclouds.management.ViewMBeanFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import static org.jclouds.osgi.Bundles.instantiateAvailableClasses;
import static org.jclouds.osgi.Bundles.stringsForResourceInBundle;
import static org.osgi.framework.BundleEvent.STARTED;
import static org.osgi.framework.BundleEvent.STOPPING;
import static org.osgi.framework.BundleEvent.STOPPED;

/**
 * A {@link org.osgi.framework.BundleListener} that listens for {@link org.osgi.framework.BundleEvent} and searches for
 * {@link org.jclouds.providers.ProviderMetadata} and {@link org.jclouds.apis.ApiMetadata} in newly installed Bundles.
 * This is used as a workaround for OSGi environments where the ServiceLoader cannot cross bundle boundaries.
 */
public class ManagementFactoryBundleListener implements BundleListener {

   private final Multimap<Long, ViewMBeanFactory> managedViewFactoryMap = ArrayListMultimap.create();

   /**
    * Starts the listener. Checks the bundles that are already active and registers {@link org.jclouds.providers.ProviderMetadata} and
    * {@link org.jclouds.apis.ApiMetadata} found. Registers the itself as a {@link org.osgi.framework.BundleListener}.
    *
    * @param bundleContext
    */
   public synchronized void start(BundleContext bundleContext) {
      bundleContext.addBundleListener(this);
      for (Bundle bundle : bundleContext.getBundles()) {
         if (bundle.getState() == Bundle.ACTIVE) {
            addBundle(bundle);
         }
      }
      bundleContext.addBundleListener(this);
   }

   /**
    * Stops the listener. Removes itself from the {@link org.osgi.framework.BundleListener}s. Clears metadata maps and listeners lists.
    *
    * @param bundleContext
    */
   public void stop(BundleContext bundleContext) {
      bundleContext.removeBundleListener(this);
      managedViewFactoryMap.clear();
   }

   @Override
   public synchronized void bundleChanged(BundleEvent event) {
      switch (event.getType()) {
      case STARTED:
         addBundle(event.getBundle());
         break;
      case STOPPING:
      case STOPPED:
         removeBundle(event.getBundle());
         break;
      }
   }

   /**
    * Searches for {@link org.jclouds.providers.ProviderMetadata} and {@link org.jclouds.apis.ApiMetadata} inside the {@link org.osgi.framework.Bundle}. If metadata are found
    * they are registered in the {@link org.jclouds.osgi.ProviderRegistry} and {@link org.jclouds.osgi.ApiRegistry}. Also the {@link org.jclouds.osgi.ProviderListener} and
    * {@link org.jclouds.osgi.ApiListener} are notified.
    *
    * @param bundle
    */
   private synchronized void addBundle(Bundle bundle) {
      for (ViewMBeanFactory viewMBeanFactory : listManagedViewFactories(bundle)) {
         if (viewMBeanFactory != null) {
            ViewManagementFactoryRegistry.registerFactory(viewMBeanFactory);
            managedViewFactoryMap.put(bundle.getBundleId(), viewMBeanFactory);
         }
      }
   }

   /**
    * Searches for {@link org.jclouds.providers.ProviderMetadata} and {@link org.jclouds.apis.ApiMetadata} registered under the {@link org.osgi.framework.Bundle} id. If metadata
    * are found they are removed the {@link org.jclouds.osgi.ProviderRegistry} and {@link org.jclouds.osgi.ApiRegistry}. Also the {@link org.jclouds.osgi.ProviderListener}
    * and {@link org.jclouds.osgi.ApiListener} are notified.
    *
    * @param bundle
    */
   private synchronized void removeBundle(Bundle bundle) {
      for (ViewMBeanFactory viewMBeanFactory : managedViewFactoryMap.removeAll(bundle.getBundleId())) {
            ViewManagementFactoryRegistry.registerFactory(viewMBeanFactory);
      }

   }

   /**
    * Creates an instance of {@link org.jclouds.management.ViewMBeanFactory} from the {@link org.osgi.framework.Bundle}.
    *
    * @param bundle
    * @return
    */
   public Iterable<ViewMBeanFactory> listManagedViewFactories(Bundle bundle) {
      Iterable<String> classNames = stringsForResourceInBundle("/META-INF/services/" + ViewMBeanFactory.class.getName(), bundle);
      return instantiateAvailableClasses(bundle, classNames, ViewMBeanFactory.class);
   }
}
