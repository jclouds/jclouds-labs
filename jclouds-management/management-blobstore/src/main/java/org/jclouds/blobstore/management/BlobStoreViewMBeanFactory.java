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
package org.jclouds.blobstore.management;

import com.google.common.reflect.TypeToken;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.management.ViewMBean;
import org.jclouds.management.ViewMBeanFactory;

public class BlobStoreViewMBeanFactory implements ViewMBeanFactory<BlobStoreContext> {

   private static final TypeToken<BlobStoreContext> TYPE = TypeToken.of(BlobStoreContext.class);

   /**
    * Creates a {@link org.jclouds.management.ManagedBean} for the Context.
    *
    * @param view
    * @return
    */
   @Override
   public ViewMBean<BlobStoreContext> create(BlobStoreContext view) {
      return new BlobStoreManagement(view);
   }

   /**
    * Returns the {@link com.google.common.reflect.TypeToken} of the {@link org.jclouds.View}.
    *
    * @return
    */
   @Override
   public TypeToken<BlobStoreContext> getViewType() {
      return TYPE;
   }
}
