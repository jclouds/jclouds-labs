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
package org.jclouds.aliyun.ecs.domain.options;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.jclouds.aliyun.ecs.functions.PutStringInDoubleQuotes;
import org.jclouds.http.options.BaseHttpRequestOptions;

import java.util.Arrays;

public class ListInstancesOptions extends BaseHttpRequestOptions {
   public static final String VPC_ID_PARAM = "VpcId";
   public static final String VSWITCH_ID_PARAM = "VSwitchId";
   public static final String ZONE_ID_PARAM = "ZoneId";
   public static final String INSTANCE_IDS_PARAM = "InstanceIds";
   public static final String INSTANCE_TYPE_PARAM = "InstanceType";
   public static final String INSTANCE_TYPE_FAMILY_PARAM = "InstanceTypeFamily";
   public static final String INSTANCE_NETWORK_TYPE_PARAM = "InstanceNetworkType";
   public static final String PRIVATE_IP_ADDRESSES_PARAM = "PrivateIpAddresses";
   public static final String INNER_IP_ADDRESSES_PARAM = "InnerIpAddresses";
   public static final String PUBLIC_IP_ADDRESSES_PARAM = "PublicIpAddresses";
   public static final String SECURITY_GROUP_ID_PARAM = "SecurityGroupId";
   public static final String INSTANCE_CHARGE_TYPE_PARAM = "InstanceChargeType";
   public static final String SPOT_STRATEGY_PARAM = "SpotStrategy";
   public static final String INTERNET_CHARGE_TYPE_PARAM = "InternetChargeType";
   public static final String INSTANCE_NAME_PARAM = "InstanceName";
   public static final String IMAGE_ID_PARAM = "ImageId";
   public static final String DEPLOYMENT_SET_ID_PARAM = "DeploymentSetId";
   public static final String STATUS_PARAM = "Status";
   public static final String IO_OPTIMIZED_PARAM = "IoOptimized";

   public ListInstancesOptions vpcId(String vpcId) {
      queryParameters.put(VPC_ID_PARAM, vpcId);
      return this;
   }

   public ListInstancesOptions vSwitchId(String vSwitchId) {
      queryParameters.put(VSWITCH_ID_PARAM, vSwitchId);
      return this;
   }

   public ListInstancesOptions zoneId(String zoneId) {
      queryParameters.put(ZONE_ID_PARAM, zoneId);
      return this;
   }

   public ListInstancesOptions instanceIds(String... instanceIds) {
      String instanceIdsAsString = Joiner.on(",")
              .join(Iterables.transform(Arrays.asList(instanceIds), new PutStringInDoubleQuotes()));
      queryParameters.put(INSTANCE_IDS_PARAM, String.format("[%s]", instanceIdsAsString));
      return this;
   }

   public ListInstancesOptions instanceType(String instanceType) {
      queryParameters.put(INSTANCE_TYPE_PARAM, instanceType);
      return this;
   }

   public ListInstancesOptions instanceTypeFamily(String instanceTypeFamily) {
      queryParameters.put(INSTANCE_TYPE_FAMILY_PARAM, instanceTypeFamily);
      return this;
   }

   public ListInstancesOptions instanceNetworkType(String instanceNetworkType) {
      queryParameters.put(INSTANCE_NETWORK_TYPE_PARAM, instanceNetworkType);
      return this;
   }

   public ListInstancesOptions privateIpAddresses(String... privateIpAddresses) {
      String instanceIdsAsString = Joiner.on(",")
              .join(Iterables.transform(Arrays.asList(privateIpAddresses), new PutStringInDoubleQuotes()));
      queryParameters.put(PRIVATE_IP_ADDRESSES_PARAM, String.format("[%s]", instanceIdsAsString));
      return this;
   }

   public ListInstancesOptions innerIpAddresses(String... innerIpAddresses) {
      String instanceIdsAsString = Joiner.on(",")
              .join(Iterables.transform(Arrays.asList(innerIpAddresses), new PutStringInDoubleQuotes()));
      queryParameters.put(INNER_IP_ADDRESSES_PARAM, String.format("[%s]", instanceIdsAsString));
      return this;
   }

   public ListInstancesOptions publicIpAddresses(String... publicIpAddresses) {
      String instanceIdsAsString = Joiner.on(",")
              .join(Iterables.transform(Arrays.asList(publicIpAddresses), new PutStringInDoubleQuotes()));
      queryParameters.put(PUBLIC_IP_ADDRESSES_PARAM, String.format("[%s]", instanceIdsAsString));
      return this;
   }

   public ListInstancesOptions securityGroupId(String securityGroupId) {
      queryParameters.put(SECURITY_GROUP_ID_PARAM, securityGroupId);
      return this;
   }

   public ListInstancesOptions instanceChargeType(String instanceChargeType) {
      queryParameters.put(INSTANCE_CHARGE_TYPE_PARAM, instanceChargeType);
      return this;
   }

   public ListInstancesOptions spotStrategy(String spotStrategy) {
      queryParameters.put(SPOT_STRATEGY_PARAM, spotStrategy);
      return this;
   }

   public ListInstancesOptions internetChargeType(String internetChargeType) {
      queryParameters.put(INTERNET_CHARGE_TYPE_PARAM, internetChargeType);
      return this;
   }

   public ListInstancesOptions instanceName(String instanceName) {
      queryParameters.put(INSTANCE_NAME_PARAM, instanceName);
      return this;
   }

   public ListInstancesOptions imageId(String imageId) {
      queryParameters.put(IMAGE_ID_PARAM, imageId);
      return this;
   }

   public ListInstancesOptions deploymentSetId(String deploymentSetId) {
      queryParameters.put(DEPLOYMENT_SET_ID_PARAM, deploymentSetId);
      return this;
   }

   public ListInstancesOptions status(String status) {
      queryParameters.put(STATUS_PARAM, status);
      return this;
   }

   public ListInstancesOptions ioOptimized(String ioOptimized) {
      queryParameters.put(IO_OPTIMIZED_PARAM, ioOptimized);
      return this;
   }

   public ListInstancesOptions paginationOptions(final PaginationOptions paginationOptions) {
      this.queryParameters.putAll(paginationOptions.buildQueryParameters());
      return this;
   }

   public ListInstancesOptions tagOptions(final TagOptions tagOptions) {
      this.queryParameters.putAll(tagOptions.buildQueryParameters());
      return this;
   }

   public static final class Builder {

      public static ListInstancesOptions vpcId(String vpcId) {
         return new ListInstancesOptions().vpcId(vpcId);
      }

      public static ListInstancesOptions vSwitchId(String vSwitchId) {
         return new ListInstancesOptions().vSwitchId(vSwitchId);
      }

      public static ListInstancesOptions zoneId(String zoneId) {
         return new ListInstancesOptions().zoneId(zoneId);
      }

      public static ListInstancesOptions instanceIds(String... instanceIds) {
         return new ListInstancesOptions().instanceIds(instanceIds);
      }

      public static ListInstancesOptions instanceType(String instanceType) {
         return new ListInstancesOptions().instanceType(instanceType);
      }

      public static ListInstancesOptions instanceTypeFamily(String instanceTypeFamily) {
         return new ListInstancesOptions().instanceTypeFamily(instanceTypeFamily);
      }

      public static ListInstancesOptions instanceNetworkType(String instanceNetworkType) {
         return new ListInstancesOptions().instanceNetworkType(instanceNetworkType);
      }

      public static ListInstancesOptions privateIpAddresses(String... privateIpAddresses) {
         return new ListInstancesOptions().privateIpAddresses(privateIpAddresses);
      }

      public static ListInstancesOptions innerIpAddresses(String... innerIpAddresses) {
         return new ListInstancesOptions().innerIpAddresses(innerIpAddresses);
      }

      public static ListInstancesOptions publicIpAddresses(String... publicIpAddresses) {
         return new ListInstancesOptions().publicIpAddresses(publicIpAddresses);
      }

      public static ListInstancesOptions securityGroupId(String securityGroupId) {
         return new ListInstancesOptions().securityGroupId(securityGroupId);
      }

      public static ListInstancesOptions instanceChargeType(String instanceChargeType) {
         return new ListInstancesOptions().instanceChargeType(instanceChargeType);
      }

      public static ListInstancesOptions instanceName(String instanceName) {
         return new ListInstancesOptions().instanceName(instanceName);
      }

      public static ListInstancesOptions imageId(String imageId) {
         return new ListInstancesOptions().imageId(imageId);
      }

      public static ListInstancesOptions deploymentSetId(String deploymentSetId) {
         return new ListInstancesOptions().deploymentSetId(deploymentSetId);
      }

      public static ListInstancesOptions status(String status) {
         return new ListInstancesOptions().status(status);
      }

      public static ListInstancesOptions ioOptimized(String ioOptimized) {
         return new ListInstancesOptions().ioOptimized(ioOptimized);
      }

      public static ListInstancesOptions paginationOptions(PaginationOptions paginationOptions) {
         return new ListInstancesOptions().paginationOptions(paginationOptions);
      }

      public static ListInstancesOptions tagOptions(TagOptions tagOptions) {
         return new ListInstancesOptions().tagOptions(tagOptions);
      }
   }

}
