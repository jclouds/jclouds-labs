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
package org.jclouds.azurecompute.config;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.suppliers.KeyStoreSupplier;
import org.jclouds.azurecompute.suppliers.SSLContextWithKeysSupplier;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;

import com.google.common.base.Supplier;
import com.google.inject.TypeLiteral;

@ConfiguresHttpApi
public class AzureComputeHttpApiModule extends HttpApiModule<AzureComputeApi> {
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Supplier<SSLContext>>() {
      }).to(new TypeLiteral<SSLContextWithKeysSupplier>() {
      });
      bind(new TypeLiteral<Supplier<KeyStore>>() {
      }).to(new TypeLiteral<KeyStoreSupplier>() {
      });
   }
}
