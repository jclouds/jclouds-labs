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
package org.jclouds.azurecompute.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.http.functions.ParseSax;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/dn469422.aspx" >api</a>
 */
final class RoleSizeHandler extends ParseSax.HandlerForGeneratedRequestWithResult<RoleSize> {
   private RoleSize.Type name;
   private String label;
   private Integer cores;
   private Integer memoryInMb;
   private Boolean supportedByWebWorkerRoles;
   private Boolean supportedByVirtualMachines;
   private Integer maxDataDiskCount;
   private Integer webWorkerResourceDiskSizeInMb;
   private Integer virtualMachineResourceDiskSizeInMb;

   private final StringBuilder currentText = new StringBuilder();

   @Override
   public RoleSize getResult() {
      return RoleSize.create(name, label, cores, memoryInMb, supportedByWebWorkerRoles,
              supportedByVirtualMachines, maxDataDiskCount, webWorkerResourceDiskSizeInMb, virtualMachineResourceDiskSizeInMb);
   }

   @Override
   public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("Name")) {
         String type = currentOrNull(currentText);
         if (type != null) {
            name = RoleSize.Type.valueOf(currentOrNull(currentText).toUpperCase());
         }
      } else if (qName.equals("Label")) {
         label = currentOrNull(currentText);

      } else if (qName.equals("Cores")) {
         String coresString = currentOrNull(currentText);
         if (coresString != null) {
            cores = Integer.parseInt(coresString);
         }
      } else if (qName.equals("MemoryInMb")) {
         String memoryInMbString = currentOrNull(currentText);
         if (memoryInMbString != null) {
            memoryInMb = Integer.parseInt(memoryInMbString);
         }
      } else if (qName.equals("SupportedByWebWorkerRoles")) {
         String supportedByWebWorkerRolesString = currentOrNull(currentText);
         if (supportedByWebWorkerRolesString != null) {
            supportedByWebWorkerRoles = Boolean.valueOf(supportedByWebWorkerRolesString);
         }
      } else if (qName.equals("SupportedByVirtualMachines")) {
         String supportedByVirtualMachinesString = currentOrNull(currentText);
         if (supportedByVirtualMachinesString != null) {
            supportedByVirtualMachines = Boolean.valueOf(supportedByVirtualMachinesString);
         }
      } else if (qName.equals("MaxDataDiskCount")) {
         String maxDataDiskCountString = currentOrNull(currentText);
         if (maxDataDiskCountString != null) {
            maxDataDiskCount = Integer.parseInt(maxDataDiskCountString);
         }
      } else if (qName.equals("WebWorkerResourceDiskSizeInMb")) {
         String webWorkerResourceDiskSizeInMbString = currentOrNull(currentText);
         if (webWorkerResourceDiskSizeInMbString != null) {
            webWorkerResourceDiskSizeInMb = Integer.parseInt(webWorkerResourceDiskSizeInMbString);
         }
      } else if (qName.equals("VirtualMachineResourceDiskSizeInMb")) {
         String virtualMachineResourceDiskSizeInMbString = currentOrNull(currentText);
         if (virtualMachineResourceDiskSizeInMbString != null) {
            virtualMachineResourceDiskSizeInMb = Integer.parseInt(virtualMachineResourceDiskSizeInMbString);
         }
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
