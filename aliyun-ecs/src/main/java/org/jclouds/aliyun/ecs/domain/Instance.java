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
package org.jclouds.aliyun.ecs.domain;

import com.google.auto.value.AutoValue;
import com.google.common.base.CaseFormat;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@AutoValue
public abstract class Instance {

   public enum InternetChargeType {
      PAY_BY_TRAFFIC("PayByTraffic"),
      DEFAULT("");

      private final String internetChargeType;

      InternetChargeType(String internetChargeType) {
         this.internetChargeType = internetChargeType;
      }

      public static InternetChargeType fromValue(String value) {
         return Enums.getIfPresent(InternetChargeType.class, CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, value)).or(InternetChargeType.DEFAULT);
      }

      public String internetChargeType() {
         return internetChargeType;
      }

      @Override
      public String toString() {
         return internetChargeType();
      }
   }


   public enum Status {
      STARTING, RUNNING, STOPPING, STOPPED;

      public static Status fromValue(String value) {
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         checkArgument(status.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(Status.values()),
                 value);
         return status.get();
      }
   }

   Instance() {}

   @SerializedNames(
         { "InnerIpAddress", "ImageId", "InstanceTypeFamily", "VlanId", "NetworkInterfaces", "InstanceId", "EipAddress",
               "InternetMaxBandwidthIn", "ZoneId", "InternetChargeType", "SpotStrategy", "StoppedMode", "SerialNumber",
               "IoOptimized", "Memory", "Cpu", "VpcAttributes", "InternetMaxBandwidthOut", "DeviceAvailable",
               "SecurityGroupIds", "SaleCycle", "SpotPriceLimit", "AutoReleaseTime", "StartTime", "InstanceName",
               "Description", "ResourceGroupId", "OSType", "OSName", "InstanceNetworkType", "PublicIpAddress",
               "HostName", "InstanceType", "CreationTime", "Status", "Tags", "ClusterId", "Recyclable", "RegionId",
               "GPUSpec", "DedicatedHostAttribute", "OperationLocks", "InstanceChargeType", "GPUAmount",
               "ExpiredTime" })
   public static Instance create(Map<String, List<String>> innerIpAddress, String imageId, String instanceTypeFamily,
                                 String vlanId, Map<String, List<NetworkInterface>> networkInterfaces, String id, EipAddress eipAddress,
                                 Integer internetMaxBandwidthIn, String zoneId, InternetChargeType internetChargeType, String spotStrategy,
                                 String stoppedMode, String serialNumber, Boolean ioOptimized, Integer memory, Integer cpu,
                                 VpcAttributes vpcAttributes, Integer internetMaxBandwidthOut, Boolean deviceAvailable,
                                 Map<String, List<String>> securityGroupIds, String saleCycle, Double spotPriceLimit, String autoReleaseTime,
                                 Date startTime, String name, String description, String resourceGroupId, String osType, String osName,
                                 String instanceNetworkType, Map<String, List<String>> publicIpAddress, String hostname, String instanceType,
                                 Date creationTime, Status status, Map<String, List<Tag>> tags, String clusterId, Boolean recyclable,
                                 String regionId, String gpuSpec, DedicatedHostAttribute dedicatedHostAttribute,
                                 Map<String, List<String>> operationLocks, String instanceChargeType, Integer gpuAmount, Date expiredTime) {
      return builder().innerIpAddress(innerIpAddress).imageId(imageId).instanceTypeFamily(instanceTypeFamily).vlanId(vlanId)
              .networkInterfaces(networkInterfaces).id(id).eipAddress(eipAddress).internetMaxBandwidthIn(internetMaxBandwidthIn)
              .zoneId(zoneId).internetChargeType(internetChargeType).spotStrategy(spotStrategy).stoppedMode(stoppedMode).serialNumber(serialNumber)
              .ioOptimized(ioOptimized).memory(memory).cpu(cpu).vpcAttributes(vpcAttributes).internetMaxBandwidthOut(internetMaxBandwidthOut).deviceAvailable(deviceAvailable)
              .securityGroupIds(securityGroupIds).saleCycle(saleCycle).spotPriceLimit(spotPriceLimit).autoReleaseTime(autoReleaseTime).startTime(startTime).name(name)
              .description(description).resourceGroupId(resourceGroupId).osType(osType).osName(osName).instanceNetworkType(instanceNetworkType).publicIpAddress(publicIpAddress)
              .hostname(hostname).instanceType(instanceType).creationTime(creationTime).status(status).tags(tags).clusterId(clusterId).recyclable(recyclable).regionId(regionId)
              .gpuSpec(gpuSpec).dedicatedHostAttribute(dedicatedHostAttribute).operationLocks(operationLocks).instanceChargeType(instanceChargeType).gpuAmount(gpuAmount)
              .expiredTime(expiredTime).build();
   }

   public abstract Map<String, List<String>> innerIpAddress();

   public abstract String imageId();

   public abstract String instanceTypeFamily();

   public abstract String vlanId();

   public abstract Map<String, List<NetworkInterface>> networkInterfaces();

   public abstract String id();

   public abstract EipAddress eipAddress();

   public abstract Integer internetMaxBandwidthIn();

   public abstract String zoneId();

   public abstract InternetChargeType internetChargeType();

   public abstract String spotStrategy();

   public abstract String stoppedMode();

   public abstract String serialNumber();

   public abstract Boolean ioOptimized();

   public abstract Integer memory();

   public abstract Integer cpu();

   public abstract VpcAttributes vpcAttributes();

   public abstract Integer internetMaxBandwidthOut();

   public abstract Boolean deviceAvailable();

   public abstract Map<String, List<String>> securityGroupIds();

   public abstract String saleCycle();

   public abstract Double spotPriceLimit();

   public abstract String autoReleaseTime();

   public abstract Date startTime();

   public abstract String name();

   public abstract String description();

   public abstract String resourceGroupId();

   public abstract String osType();

   public abstract String osName();

   public abstract String instanceNetworkType();

   public abstract Map<String, List<String>> publicIpAddress();

   public abstract String hostname();

   public abstract String instanceType();

   public abstract Date creationTime();

   public abstract Status status();

   @Nullable
   public abstract Map<String, List<Tag>> tags();

   public abstract String clusterId();

   public abstract Boolean recyclable();

   public abstract String regionId();

   public abstract String gpuSpec();

   public abstract DedicatedHostAttribute dedicatedHostAttribute();

   public abstract Map<String, List<String>> operationLocks();

   public abstract String instanceChargeType();

   public abstract Integer gpuAmount();

   public abstract Date expiredTime();

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_Instance.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder innerIpAddress(Map<String, List<String>> innerIpAddress);

      public abstract Builder imageId(String imageId);

      public abstract Builder instanceTypeFamily(String instanceTypeFamily);

      public abstract Builder vlanId(String vlanId);

      public abstract Builder networkInterfaces(Map<String, List<NetworkInterface>> networkInterfaces);

      public abstract Builder id(String id);

      public abstract Builder eipAddress(EipAddress eipAddress);

      public abstract Builder internetMaxBandwidthIn(Integer internetMaxBandwidthIn);

      public abstract Builder zoneId(String zoneId);

      public abstract Builder internetChargeType(InternetChargeType internetChargeType);

      public abstract Builder spotStrategy(String spotStrategy);

      public abstract Builder stoppedMode(String stoppedMode);

      public abstract Builder serialNumber(String serialNumber);

      public abstract Builder ioOptimized(Boolean ioOptimized);

      public abstract Builder memory(Integer memory);

      public abstract Builder cpu(Integer cpu);

      public abstract Builder vpcAttributes(VpcAttributes vpcAttributes);

      public abstract Builder internetMaxBandwidthOut(Integer internetMaxBandwidthOut);

      public abstract Builder deviceAvailable(Boolean deviceAvailable);

      public abstract Builder securityGroupIds(Map<String, List<String>> securityGroupIds);

      public abstract Builder saleCycle(String saleCycle);

      public abstract Builder spotPriceLimit(Double spotPriceLimit);

      public abstract Builder autoReleaseTime(String autoReleaseTime);

      public abstract Builder startTime(Date startTime);

      public abstract Builder name(String name);

      public abstract Builder description(String description);

      public abstract Builder resourceGroupId(String resourceGroupId);

      public abstract Builder osType(String osType);

      public abstract Builder osName(String osName);

      public abstract Builder instanceNetworkType(String instanceNetworkType);

      public abstract Builder publicIpAddress(Map<String, List<String>> publicIpAddress);

      public abstract Builder hostname(String hostname);

      public abstract Builder instanceType(String instanceType);

      public abstract Builder creationTime(Date creationTime);

      public abstract Builder status(Status status);

      public abstract Builder tags(Map<String, List<Tag>> tags);

      public abstract Builder clusterId(String clusterId);

      public abstract Builder recyclable(Boolean recyclable);

      public abstract Builder regionId(String regionId);

      public abstract Builder gpuSpec(String gpuSpec);

      public abstract Builder dedicatedHostAttribute(DedicatedHostAttribute dedicatedHostAttribute);

      public abstract Builder operationLocks(Map<String, List<String>> operationLocks);

      public abstract Builder instanceChargeType(String InstanceChargeType);

      public abstract Builder gpuAmount(Integer gpuAmount);

      public abstract Builder expiredTime(Date expiredTime);

      abstract Instance autoBuild();

      abstract Map<String, List<String>> innerIpAddress();

      abstract Map<String, List<NetworkInterface>> networkInterfaces();

      abstract Map<String, List<String>> securityGroupIds();

      abstract Map<String, List<String>> publicIpAddress();

      abstract Map<String, List<String>> operationLocks();

      abstract Map<String, List<Tag>> tags();

      public Instance build() {
         innerIpAddress(innerIpAddress() == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(innerIpAddress()));
         securityGroupIds(securityGroupIds() == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(securityGroupIds()));
         networkInterfaces(networkInterfaces() == null ? ImmutableMap.<String, List<NetworkInterface>>of() : ImmutableMap.copyOf(networkInterfaces()));
         publicIpAddress(publicIpAddress() == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(publicIpAddress()));
         operationLocks(operationLocks() == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(operationLocks()));
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : ImmutableMap.<String, List<Tag>>of());
         return autoBuild();
      }
   }

}
