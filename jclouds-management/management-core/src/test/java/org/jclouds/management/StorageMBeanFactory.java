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

import com.google.common.reflect.TypeToken;
import org.jclouds.apis.Storage;

public class StorageMBeanFactory implements ViewMBeanFactory<Storage> {

   /**
    * Creates a {@link org.jclouds.management.ManagedBean} for the Context.
    *
    * @param view
    * @return
    */
   @Override
   public ViewMBean<Storage> create(Storage view) {
      return new StorageManagement();
   }

   /**
    * Returns the {@link com.google.common.reflect.TypeToken} of the {@link org.jclouds.View}.
    *
    * @return
    */
   @Override
   public TypeToken getViewType() {
      return TypeToken.of(Storage.class);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      return true;
   }
}
