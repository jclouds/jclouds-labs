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

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.BaseEncoding.base64;
import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Deployment.InstanceStatus;
import org.jclouds.azurecompute.domain.Deployment.Slot;
import org.jclouds.azurecompute.domain.Deployment.Status;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.annotations.VisibleForTesting;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460804" >Response body description</a>.
 */
public final class DeploymentHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Deployment> {
   private String name;
   private Slot slot;
   private Status status;
   private String label;
   private String virtualMachineName;
   private String instanceName;
   private InstanceStatus instanceStatus;
   private String instanceStateDetails;
   private String instanceErrorCode;
   private RoleSize instanceSize;
   private String privateIpAddress;
   private String publicIpAddress;

   private int depth;
   private final StringBuilder currentText = new StringBuilder();

   @Override public Deployment getResult() { // Fields don't need to be reset as this isn't used in a loop.
      return Deployment.create(name, slot, status, label, virtualMachineName, instanceName, instanceStatus, //
            instanceStateDetails, instanceErrorCode, instanceSize, privateIpAddress, publicIpAddress);
   }

   @Override public void startElement(String url, String name, String qName, Attributes attributes) {
      depth++;
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("Name") && depth == 2) {
         name = currentOrNull(currentText);
      } else if (qName.equals("DeploymentSlot")) {
         String slotText = currentOrNull(currentText);
         if (slotText != null) {
            slot = parseSlot(slotText);
         }
      } else if (qName.equals("Status")) {
         String statusText = currentOrNull(currentText);
         if (statusText != null) {
            status = parseStatus(statusText);
         }
      } else if (qName.equals("Label")) {
         String labelText = currentOrNull(currentText);
         if (labelText != null) {
            label = new String(base64().decode(labelText), UTF_8);
         }
      } else if (qName.equals("RoleName")) {
         virtualMachineName = currentOrNull(currentText);
      } else if (qName.equals("InstanceName")) {
         instanceName = currentOrNull(currentText);
      } else if (qName.equals("InstanceStatus")) {
         String instanceStatusText = currentOrNull(currentText);
         if (instanceStatusText != null) {
            instanceStatus = parseInstanceStatus(instanceStatusText);
         }
      } else if (qName.equals("InstanceStateDetails")) {
         instanceStateDetails = currentOrNull(currentText);
      } else if (qName.equals("InstanceErrorCode")) {
         instanceErrorCode = currentOrNull(currentText);
      } else if (qName.equals("InstanceSize")) {
         String instanceSizeText = currentOrNull(currentText);
         if (instanceSizeText != null) {
            instanceSize = parseRoleSize(instanceSizeText);
         }
      } else if (qName.equals("IpAddress")) {
         privateIpAddress = currentOrNull(currentText);
      } else if (qName.equals("Vip")) {
         publicIpAddress = currentOrNull(currentText);
      }
      currentText.setLength(0);
      depth--;
   }

   @Override public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   private static Status parseStatus(String status) {
      try {
         return Status.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, status));
      } catch (IllegalArgumentException e) {
         return Status.UNRECOGNIZED;
      }
   }

   private static Slot parseSlot(String slot) {
      try {
         return Slot.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, slot));
      } catch (IllegalArgumentException e) {
         return Slot.UNRECOGNIZED;
      }
   }

   @VisibleForTesting static InstanceStatus parseInstanceStatus(String instanceStatus) {
      try {
         // Azure isn't exactly upper-camel, as some states end in VM, not Vm.
         return InstanceStatus.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, instanceStatus).replace("V_M", "VM"));
      } catch (IllegalArgumentException e) {
         return InstanceStatus.UNRECOGNIZED;
      }
   }

   private static RoleSize parseRoleSize(String roleSize) {
      try {
         return RoleSize.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, roleSize));
      } catch (IllegalArgumentException e) {
         return RoleSize.UNRECOGNIZED;
      }
   }
}
