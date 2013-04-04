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
import org.jclouds.View;

/**
 * A factory for {@ViewManagement}.
 * @param <V>
 */
public interface ViewMBeanFactory<V extends View> {

   /**
    * Creates a {@link ManagedBean} for the Context.
    * @param view
    * @return
    */
   ViewMBean<V> create(V view);

   /**
    * Returns the {@link TypeToken} of the {@link View}.
    * @return
    */
   TypeToken<V> getViewType();
}
