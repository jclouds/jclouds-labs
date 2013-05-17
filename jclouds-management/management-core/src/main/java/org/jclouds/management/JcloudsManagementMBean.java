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


import org.jclouds.representations.ApiMetadata;
import org.jclouds.representations.Context;
import org.jclouds.representations.ProviderMetadata;

import java.io.IOException;


public interface JcloudsManagementMBean {

   /**
    * Lists all available {@link org.jclouds.representations.ApiMetadata}.
    * @return
    */
   Iterable<ApiMetadata> getApis();

   /**
    * Find {@link org.jclouds.representations.ApiMetadata} by id.
    * @return
    */
   ApiMetadata findApiById(String id);


   /**
    * Lists all available {@link org.jclouds.representations.ProviderMetadata}
    * @return
    */
   Iterable<ProviderMetadata> getProviders();


   /**
    * Find {@link org.jclouds.representations.ProviderMetadata} by id.
    * @return
    */
   ProviderMetadata findProviderById(String id);

   /**
    * Lists all {@link org.jclouds.representations.Context} objects.
    * @return
    */
   Iterable<Context> getContexts();

   /**
    * Creates a {@link org.jclouds.representations.Context}.
    * @param id
    * @param name
    * @param identity
    * @param credential
    * @param endpoint
    * @param overrides     The override properties as a list of new line separated key value pairs. Key/Values are separated by the equals sign.
    * @return
    */
   Context createContext(String id, String name, String identity, String credential, String endpoint, String overrides) throws IOException;
}
