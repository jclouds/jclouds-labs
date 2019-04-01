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
package org.jclouds.dimensiondata.cloudcontrol;

import com.google.common.base.Predicate;
import com.google.inject.Provides;
import org.jclouds.dimensiondata.cloudcontrol.features.AccountApi;
import org.jclouds.dimensiondata.cloudcontrol.features.CustomerImageApi;
import org.jclouds.dimensiondata.cloudcontrol.features.InfrastructureApi;
import org.jclouds.dimensiondata.cloudcontrol.features.NetworkApi;
import org.jclouds.dimensiondata.cloudcontrol.features.ServerApi;
import org.jclouds.dimensiondata.cloudcontrol.features.ServerImageApi;
import org.jclouds.dimensiondata.cloudcontrol.features.TagApi;
import org.jclouds.rest.annotations.Delegate;

import javax.inject.Named;
import java.io.Closeable;

import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.CUSTOMER_IMAGE_DELETED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.NETWORK_DOMAIN_DELETED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.NETWORK_DOMAIN_NORMAL_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.SERVER_DELETED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.SERVER_NORMAL_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.SERVER_STARTED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.SERVER_STOPPED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.VLAN_DELETED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.VLAN_NORMAL_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.VM_TOOLS_RUNNING_PREDICATE;

public interface DimensionDataCloudControlApi extends Closeable {

   @Delegate
   AccountApi getAccountApi();

   @Delegate
   InfrastructureApi getInfrastructureApi();

   @Delegate
   ServerImageApi getServerImageApi();

   @Delegate
   NetworkApi getNetworkApi();

   @Delegate
   ServerApi getServerApi();

   @Delegate
   TagApi getTagApi();

   @Delegate
   CustomerImageApi getCustomerImageApi();

   @Provides
   @Named(VLAN_DELETED_PREDICATE)
   Predicate<String> vlanDeletedPredicate();

   @Provides
   @Named(NETWORK_DOMAIN_DELETED_PREDICATE)
   Predicate<String> networkDomainDeletedPredicate();

   @Provides
   @Named(NETWORK_DOMAIN_NORMAL_PREDICATE)
   Predicate<String> networkDomainNormalPredicate();

   @Provides
   @Named(VLAN_NORMAL_PREDICATE)
   Predicate<String> vlanNormalPredicate();

   @Provides
   @Named(SERVER_STOPPED_PREDICATE)
   Predicate<String> serverStoppedPredicate();

   @Provides
   @Named(SERVER_DELETED_PREDICATE)
   Predicate<String> serverDeletedPredicate();

   @Provides
   @Named(SERVER_STARTED_PREDICATE)
   Predicate<String> serverStartedPredicate();

   @Provides
   @Named(SERVER_NORMAL_PREDICATE)
   Predicate<String> serverNormalPredicate();

   @Provides
   @Named(VM_TOOLS_RUNNING_PREDICATE)
   Predicate<String> vmToolsRunningPredicate();

   @Provides
   @Named(CUSTOMER_IMAGE_DELETED_PREDICATE)
   Predicate<String> customerImageDeletedPredicate();

}
