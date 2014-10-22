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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

public final class Deployment {

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

   /** The user-supplied name for this deployment. */
   public String name() {
      return name;
   }

   /** The environment to which the cloud service is deployed. */
   public Slot slot() {
      return slot;
   }

   public Status status() {
      return status;
   }

   /**
    * The user-supplied name of the deployment returned as a base-64 encoded
    * string. This name can be used identify the deployment for your tracking
    * purposes.
    */
   public String label() {
      return label;
   }

   /** Specifies the name for the virtual machine. The name must be unique within Windows Azure. */
   public String virtualMachineName() {
      return virtualMachineName;
   }

   /** The name of the specific role instance (if any). */
   @Nullable public String instanceName() {
      return instanceName;
   }

   /** The current status of this instance. */
   public InstanceStatus instanceStatus() {
      return instanceStatus;
   }

   /**
    * The instance state is returned as an English human-readable string that,
    * when present, provides a snapshot of the state of the virtual machine at
    * the time the operation was called.
    *
    * For example, when the instance is first being initialized a
    * "Preparing Windows for first use." could be returned.
    */
   @Nullable public String instanceStateDetails() {
      return instanceStateDetails;
   }

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
   @Nullable public String instanceErrorCode() {
      return instanceErrorCode;
   }

   public RoleSize instanceSize() {
      return instanceSize;
   }

   public String privateIpAddress() {
      return privateIpAddress;
   }

   public String publicIpAddress() {
      return publicIpAddress;
   }

   public static Deployment create(String name, Slot slot, Status status, String label, String virtualMachineName,
         String instanceName, InstanceStatus instanceStatus, String instanceStateDetails, String instanceErrorCode,
         RoleSize instanceSize, String privateIpAddress, String publicIpAddress) {
      return new Deployment(name, slot, status, label, virtualMachineName, instanceName, instanceStatus, instanceStateDetails,
            instanceErrorCode, instanceSize, privateIpAddress, publicIpAddress);
   }

   // TODO: Remove from here down with @AutoValue.
   private Deployment(String name, Slot slot, Status status, String label, String virtualMachineName, String instanceName,
            InstanceStatus instanceStatus, String instanceStateDetails, String instanceErrorCode, RoleSize instanceSize,
            String privateIpAddress, String publicIpAddress) {
      this.name = checkNotNull(name, "name");
      this.slot = checkNotNull(slot, "slot");
      this.status = checkNotNull(status, "status");
      this.label = checkNotNull(label, "label");
      this.virtualMachineName = checkNotNull(virtualMachineName, "virtualMachineName");
      this.instanceName = instanceName;
      this.instanceStatus = checkNotNull(instanceStatus, "instanceStatus");
      this.instanceStateDetails = instanceStateDetails;
      this.instanceErrorCode = instanceErrorCode;
      this.instanceSize = checkNotNull(instanceSize, "instanceSize");
      this.privateIpAddress = checkNotNull(privateIpAddress, "privateIpAddress");
      this.publicIpAddress = checkNotNull(publicIpAddress, "publicIpAddress");
   }

   private final String name;
   private final Slot slot;
   private final Status status;
   private final String label;
   private final String virtualMachineName;
   private final String instanceName;
   private final InstanceStatus instanceStatus;
   private final String instanceStateDetails;
   private final String instanceErrorCode;
   private final RoleSize instanceSize;
   private final String privateIpAddress;
   private final String publicIpAddress;

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Deployment) {
         Deployment that = Deployment.class.cast(object);
         return equal(name, that.name)
               && equal(slot, that.slot)
               && equal(label, that.label)
               && equal(virtualMachineName, that.virtualMachineName)
               && equal(instanceName, that.instanceName)
               && equal(instanceStatus, that.instanceStatus)
               && equal(instanceStateDetails, that.instanceStateDetails)
               && equal(instanceErrorCode, that.instanceErrorCode)
               && equal(instanceSize, that.instanceSize)
               && equal(privateIpAddress, that.privateIpAddress)
               && equal(publicIpAddress, that.publicIpAddress);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, slot, label, virtualMachineName, instanceName, instanceStatus, instanceStateDetails,
            instanceErrorCode, instanceSize, privateIpAddress, publicIpAddress);
   }

   @Override
   public String toString() {
      return toStringHelper(this)
            .add("name", name)
            .add("slot", slot)
            .add("label", label)
            .add("virtualMachineName", virtualMachineName)
            .add("instanceName", instanceName)
            .add("instanceStatus", instanceStatus)
            .add("instanceStateDetails", instanceStateDetails)
            .add("instanceErrorCode", instanceErrorCode)
            .add("instanceSize", instanceSize)
            .add("privateIpAddress", privateIpAddress)
            .add("publicIpAddress", publicIpAddress).toString();
   }
}
