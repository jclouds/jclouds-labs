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
package org.jclouds.azurecompute;

import java.io.Closeable;

import javax.ws.rs.PathParam;

import org.jclouds.azurecompute.features.CloudServiceApi;
import org.jclouds.azurecompute.features.DeploymentApi;
import org.jclouds.azurecompute.features.DiskApi;
import org.jclouds.azurecompute.features.LocationApi;
import org.jclouds.azurecompute.features.NetworkSecurityGroupApi;
import org.jclouds.azurecompute.features.OSImageApi;
import org.jclouds.azurecompute.features.OperationApi;
import org.jclouds.azurecompute.features.StorageAccountApi;
import org.jclouds.azurecompute.features.SubscriptionApi;
import org.jclouds.azurecompute.features.VirtualMachineApi;
import org.jclouds.azurecompute.features.VirtualNetworkApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * The Windows Azure Service Management API is a REST API for managing your services and
 * deployments.
 * <p/>
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460799" >doc</a>
 */
public interface AzureComputeApi extends Closeable {
   /**
    * The Service Management API includes operations for listing the available data center locations
    * for a cloud service in your subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/gg441299">docs</a>
    */
   @Delegate
   LocationApi getLocationApi();

   /**
    * The Service Management API includes operations for managing the cloud services beneath your
    * subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">docs</a>
    */
   @Delegate
   CloudServiceApi getCloudServiceApi();

   /**
    * The Service Management API includes operations for managing the virtual machines in your
    * subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">docs</a>
    */
   @Delegate
   DeploymentApi getDeploymentApiForService(@PathParam("serviceName") String serviceName);

   /**
    * The Service Management API includes operations for managing the virtual machines in your
    * subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">docs</a>
    */
   // TODO: revisit once we have multi-level @Delegate working
   @Delegate
   VirtualMachineApi getVirtualMachineApiForDeploymentInService(@PathParam("deploymentName") String deploymentName,
         @PathParam("serviceName") String serviceName);

   /**
    * The Service Management API includes operations for managing the OS images in your
    * subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157175">docs</a>
    */
   @Delegate
   OSImageApi getOSImageApi();

   /**
    * The Service Management API includes operations for Tracking Asynchronous Service Management
    * Requests.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/ee460791">docs</a>
    */
   @Delegate
   OperationApi getOperationApi();

   /**
    * The Service Management API includes operations for managing Disks in your subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157188">docs</a>
    */
   @Delegate
   DiskApi getDiskApi();

   /**
    * The Service Management API includes operations for retrieving information about a subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/azure/gg715315.aspx">docs</a>
    */
   @Delegate
   SubscriptionApi getSubscriptionApi();

   /**
    * The Service Management API includes operations for managing the virtual networks in your subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157182.aspx">docs</a>
    */
   @Delegate
   VirtualNetworkApi getVirtualNetworkApi();

   /**
    * The Service Management API includes operations for managing the storage accounts in your subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/ee460790.aspx">docs</a>
    */
   @Delegate
   StorageAccountApi getStorageAccountApi();

   /**
    * The Service Management API includes operations for managing the Network Security Groups in your
    * subscription.
    *
    */
   @Delegate
   NetworkSecurityGroupApi getNetworkSecurityGroupApi();
}
