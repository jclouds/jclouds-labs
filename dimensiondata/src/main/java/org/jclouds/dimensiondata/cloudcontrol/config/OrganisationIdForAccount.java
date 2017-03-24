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
package org.jclouds.dimensiondata.cloudcontrol.config;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.domain.Account;
import org.jclouds.dimensiondata.cloudcontrol.features.AccountApi;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class OrganisationIdForAccount implements Supplier<String> {

   private final AccountApi api;

   @Inject
   OrganisationIdForAccount(DimensionDataCloudControlApi api) {
      this.api = api.getAccountApi();
   }

   @Override
   public String get() {
      Account account = checkNotNull(api.getMyAccount(), "account");
      return account.organization().id();
   }
}
