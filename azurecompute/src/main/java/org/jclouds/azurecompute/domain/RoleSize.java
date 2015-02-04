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

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * A Role Size that is available in a given subscription.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157176" >api</a>
 */
@AutoValue
public abstract class RoleSize {

   public enum Type {
      BASIC_A0 ("Basic_A0"), BASIC_A1 ("Basic_A1"), BASIC_A2 ("Basic_A2"), BASIC_A3 ("Basic_A3"), BASIC_A4 ("Basic_A4"),
      A0 ("A0"), A1 ("A1"), A2 ("A2"), A3 ("A3"), A4 ("A4"), A5 ("A5"), A6 ("A6"), A7 ("A7"), A8 ("A8"), A9 ("A9"),
      STANDARD_A0 ("Standard_A0"), STANDARD_A1 ("Standard_A1"), STANDARD_A2 ("Standard_A2"), STANDARD_A3
              ("Standard_A3"),
      STANDARD_A4 ("Standard_A4"), STANDARD_A5 ("Standard_A5"), STANDARD_A6 ("Standard_A6"),
      STANDARD_A7 ("Standard_A7"),
      STANDARD_A8 ("Standard_A8"), STANDARD_A9 ("Standard_A9"),
      STANDARD_D1 ("Standard_D1"), STANDARD_D2 ("Standard_D2"), STANDARD_D3 ("Standard_D3"), STANDARD_D4
              ("Standard_D4"),
      STANDARD_D11 ("Standard_D11"),
      STANDARD_D12 ("Standard_D12"), STANDARD_D13 ("Standard_D13"),
      STANDARD_D14 ("Standard_D14"),
      STANDARD_G1 ("Standard_G1"), STANDARD_G2 ("Standard_G2"), STANDARD_G3 ("Standard_G3"),
      STANDARD_G4 ("Standard_G4"), STANDARD_G5 ("Standard_G5"),
      EXTRASMALL ("ExtraSmall"), SMALL ("Small"), MEDIUM ("Medium"), LARGE ("Large"), EXTRALARGE ("ExtraLarge"),
      UNRECOGNIZED ("UNRECOGNIZED");

      private String text;

      Type(String text) {
         this.text = text;
      }

      public String getText() {
         return this.text;
      }

      public static Type fromString(String text) {
         if (text != null) {
            for (Type b : Type.values()) {
               if (text.equalsIgnoreCase(b.text)) {
                  return b;
               }
            }
         }
         throw new IllegalArgumentException("No constant with text " + text + " found");
      }
   }

   RoleSize() {} // For AutoValue only!

   /**
    * The name of the role size.
    */
   public abstract Type name();

   /**
    * The description of the role size.
    */
   @Nullable
   public abstract String label();

   /**
    * The number of cores that are available in the role size.
    */
   public abstract Integer cores();

   /**
    * The amount of memory that is available in the role size.
    */
   public abstract Integer memoryInMb();

   /**
    * Indicates whether the role size supports web roles or worker roles.
    */
   public abstract Boolean supportedByWebWorkerRoles();

   /**
    * Indicates whether the role size supports Virtual Machines.
    */
   public abstract Boolean supportedByVirtualMachines();

   /**
    * The maximum number of data disks that can be attached to the role.
    */
   public abstract Integer maxDataDiskCount();

   /**
    * The size of the resource disk for a web role or worker role.
    */
   public abstract Integer webWorkerResourceDiskSizeInMb();

   /**
    * The size of the resource disk for a Virtual Machine.
    */
   public abstract Integer virtualMachineResourceDiskSizeInMb();

   public static RoleSize create(Type name, String label, Integer cores, Integer memoryInMb, Boolean
           supportedByWebWorkerRoles, Boolean supportedByVirtualMachines, Integer maxDataDiskCount, Integer webWorkerResourceDiskSizeInMb, Integer virtualMachineResourceDiskSizeInMb) {
      return new AutoValue_RoleSize(name, label, cores, memoryInMb, supportedByWebWorkerRoles, supportedByVirtualMachines, maxDataDiskCount, webWorkerResourceDiskSizeInMb, virtualMachineResourceDiskSizeInMb);
   }

}
