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

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;

/**
 * In most cases, you won’t need to specify a Reserved Private IP address (RPIP) for your virtual machine. VMs in a
 * virtual network will automatically receive a private IP address from a range that you specify. But in certain cases,
 * specifying a reserved private IP address for a particular VM makes sense. For example, a VM may provide DNS services,
 * or act as a domain controller, or any othr critical role that relies on known IP address assignment. Also, if you
 * have a VM that you plan to stop and deprovision at some point, but want retain the RPIP for the VM when you provision
 * it again. An RPIP stays with the VM even through a stop/deprovision state. You can specify an RPIP by using
 * PowerShell at the time you create the VM, or you can update an existing VM.
 *
 * If you have both VMs and PaaS instances in your virtual network, you may want to separate the VMs that have static
 * DIPs from your PaaS instances by creating a separate subnet for the VMs and deploying them to that subnet. It’s not
 * only helpful for you to be able to see your static VMs in a separate subnet and know immediately which have an RPIP,
 * but for this release, it also prevents a new PaaS instance from acquiring the RPIP from a VM that is in the process
 * of being stopped/deprovisioned (not just restarted). This is a current limitation in this release for mixed VM/PaaS
 * subnets and RPIPs. This issue doesn’t happen if you deploy only VMs to the subnet, even if some of the VMs don’t have
 * an RPIP. If you’ve already deployed your VMs, you can easily move them to a new subnet to avoid this potential issue.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/dn630228.aspx" >Set a Reserved Private IP Address for a VM</a>
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn722412.aspx" >Get Reserved IP Address</a>
 */
@AutoValue
public abstract class ReservedIPAddress {

   public static enum State {

      CREATED,
      CREATING,
      UPDATING,
      DELETING,
      UNAVAILABLE,
      UNRECOGNIZED;

      public static State fromString(final String text) {
         if (text != null) {
            for (State state : State.values()) {
               if (text.equalsIgnoreCase(state.name())) {
                  return state;
               }
            }
         }
         return UNRECOGNIZED;
      }
   }

   ReservedIPAddress() {
   } // For AutoValue only!

   /**
    * Specifies the name that is assigned to the reserved IP address.
    *
    * @return Reserver IP address name..
    */
   public abstract String name();

   /**
    * Specifies the IPv4 address that is reserved.
    *
    * @return IPv4 address.
    */
   public abstract String address();

   /**
    * Specifies the unique identifier that was generated for the reserved IP address.
    *
    * @return unique identifier.
    */
   public abstract String id();

   /**
    * Specifies a label that can be used to identify the reserved IP address. The label can be up to 100 characters long
    * and can be used for your tracking purposes.
    *
    * @return label.
    */
   @Nullable
   public abstract String label();

   /**
    * Specifies the state of the reserved IP address.
    *
    * @return state.
    */
   public abstract State state();

   /**
    * Indicates whether the reserved IP address is being used.
    *
    * @return <tt>true</tt> if in use; <tt>false</tt> otherwise.
    */
   @Nullable
   public abstract Boolean inUse();

   /**
    * Specifies the name of the cloud service that is associated with the reserved IP address.
    *
    * @return associated cloud service name.
    */
   @Nullable
   public abstract String serviceName();

   /**
    * Specifies the name of the deployment that is associated with the reserved IP address.
    *
    * @return associated deployment name.
    */
   @Nullable
   public abstract String deploymentName();

   /**
    * Specifies the location of the reserved IP address. This is the same location as the associated cloud service.
    *
    * @return location.
    */
   public abstract String location();

   public static ReservedIPAddress create(
           final String name,
           final String address,
           final String id,
           final String label,
           final State state,
           final Boolean inUse,
           final String serviceName,
           final String deploymentName,
           final String location
   ) {

      return new AutoValue_ReservedIPAddress(
              name, address, id, label, state, inUse, serviceName, deploymentName, location);
   }
}
