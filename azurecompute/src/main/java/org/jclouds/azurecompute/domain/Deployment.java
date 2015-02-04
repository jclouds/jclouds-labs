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
package org.jclouds.azurecompute.domain;

import static com.google.common.collect.ImmutableList.copyOf;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Deployment {

   public enum Slot {
      PRODUCTION, STAGING,
      UNRECOGNIZED;
   }

   public enum Status {
      RUNNING, SUSPENDED, RUNNING_TRANSITIONING, SUSPENDED_TRANSITIONING, STARTING, SUSPENDING, DEPLOYING, DELETING,
      UNRECOGNIZED;
   }

   public enum InstanceStatus {
      CREATING_VM, STARTING_VM, CREATING_ROLE, STARTING_ROLE, READY_ROLE, BUSY_ROLE, STOPPING_ROLE, STOPPING_VM,
      DELETING_VM, STOPPED_VM, RESTARTING_ROLE, CYCLING_ROLE, FAILED_STARTING_ROLE, FAILED_STARTING_VM, UNRESPONSIVE_ROLE,
      STOPPED_DEALLOCATED, PREPARING,
      /** Unknown to Azure. */
      UNKNOWN,
      /** Not parsable into one of the above. */
      UNRECOGNIZED,
   }

   @AutoValue
   public abstract static class VirtualIP {

      public abstract String address();

      public abstract Boolean isDnsProgrammed();

      public abstract String name();

      VirtualIP() { // For AutoValue only!
      }

      public static VirtualIP create(String address, Boolean isDnsProgrammed, String name) {
         return new AutoValue_Deployment_VirtualIP(address, isDnsProgrammed, name);
      }
   }

   @AutoValue
   public abstract static class InstanceEndpoint {

      public abstract String name();

      public abstract String vip();

      public abstract int publicPort();

      public abstract int localPort();

      public abstract String protocol();

      InstanceEndpoint() { // For AutoValue only!
      }

      public static InstanceEndpoint create(String name, String vip, int publicPort, int localPort, String protocol) {
         return new AutoValue_Deployment_InstanceEndpoint(name, vip, publicPort, localPort, protocol);
      }
   }

   @AutoValue
   public abstract static class RoleInstance {

      public abstract String roleName();

      public abstract String instanceName();

      public abstract InstanceStatus instanceStatus();

      public abstract int instanceUpgradeDomain();

      public abstract int instanceFaultDomain();

      public abstract RoleSize.Type instanceSize();

      public abstract String ipAddress();

      @Nullable public abstract String hostname();

      @Nullable public abstract List<InstanceEndpoint> instanceEndpoints();

      RoleInstance() { // For AutoValue only!
      }

      public static RoleInstance create(String roleName, String instanceName, InstanceStatus instanceStatus, int instanceUpgradeDomain,
                                        int instanceFaultDomain, RoleSize.Type instanceSize, String ipAddress, String hostname, List<InstanceEndpoint> instanceEndpoints) {
         return new AutoValue_Deployment_RoleInstance(roleName, instanceName, instanceStatus, instanceUpgradeDomain,
                 instanceFaultDomain, instanceSize, ipAddress, hostname, copyOf(instanceEndpoints));
      }
   }

   Deployment() {} // For AutoValue only!


   /** The user-supplied name for this deployment. */
   public abstract String name();

   /** The environment to which the cloud service is deployed. */
   public abstract Slot slot();

   public abstract Status status();

   /**
    * The user-supplied name of the deployment returned as a base-64 encoded
    * string. This name can be used identify the deployment for your tracking
    * purposes.
    */
   public abstract String label();

   /**
    * The instance state is returned as an English human-readable string that,
    * when present, provides a snapshot of the state of the virtual machine at
    * the time the operation was called.
    *
    * For example, when the instance is first being initialized a
    * "Preparing Windows for first use." could be returned.
    */
   @Nullable public abstract String instanceStateDetails();

   /**
    * Error code of the latest role or VM start
    *
    * For VMRoles the error codes are:
    *
    * WaitTimeout - The virtual machine did not communicate back to Azure
    * infrastructure within 25 minutes. Typically this indicates that the
    * virtual machine did not start or that the guest agent is not installed.
    *
    * VhdTooLarge - The VHD image selected was too large for the virtual
    * machine hosting the role.
    *
    * AzureInternalError – An internal error has occurred that has caused to
    * virtual machine to fail to start. Contact support for additional
    * assistance.
    *
    * For web and worker roles this field returns an error code that can be provided to Windows Azure support to assist
    * in resolution of errors. Typically this field will be empty.
    */
   @Nullable public abstract String instanceErrorCode();

   public abstract List<VirtualIP> virtualIPs();

   public abstract List<RoleInstance> roleInstanceList();

   @Nullable public abstract List<Role> roles();

   @Nullable public abstract String virtualNetworkName();

   public static Deployment create(String name, Slot slot, Status status, String label, String instanceStateDetails, String instanceErrorCode,
                                   List<VirtualIP> virtualIPs, List<RoleInstance> roleInstanceList, List<Role> roles, String virtualNetworkName) {
      return new AutoValue_Deployment(name, slot, status, label, instanceStateDetails,
              instanceErrorCode, copyOf(virtualIPs), copyOf(roleInstanceList), copyOf(roles), virtualNetworkName);
   }
}
