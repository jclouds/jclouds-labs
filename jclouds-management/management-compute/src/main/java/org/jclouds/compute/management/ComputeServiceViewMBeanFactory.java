/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.compute.management;

import com.google.common.reflect.TypeToken;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.management.ViewMBean;
import org.jclouds.management.ViewMBeanFactory;


public class ComputeServiceViewMBeanFactory implements ViewMBeanFactory<ComputeServiceContext> {

   private static final TypeToken<ComputeServiceContext> TYPE = TypeToken.of(ComputeServiceContext.class);
   /**
    * Creates a {@link org.jclouds.management.ManagedBean} for the Context.
    *
    * @param context
    * @return
    */
   @Override
   public ViewMBean<ComputeServiceContext> create(ComputeServiceContext context) {
      return new ComputeServiceManagement(context);
   }

   /**
    * Returns the {@link com.google.common.reflect.TypeToken} of the {@link org.jclouds.View}.
    *
    * @return
    */
   @Override
   public TypeToken<ComputeServiceContext> getViewType() {
      return  TYPE;
   }
}
