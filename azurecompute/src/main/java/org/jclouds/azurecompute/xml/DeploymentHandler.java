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

import java.util.List;

import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Deployment.InstanceStatus;
import org.jclouds.azurecompute.domain.Deployment.Slot;
import org.jclouds.azurecompute.domain.Deployment.Status;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460804" >Response body description</a>.
 */
public final class DeploymentHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Deployment> {
   private String name;
   private Slot slot;
   private Status status;
   private String label;
   private String instanceStateDetails;
   private String virtualNetworkName;
   private List<Deployment.VirtualIP> virtualIPs = Lists.newArrayList();
   private List<Deployment.RoleInstance> roleInstanceList = Lists.newArrayList();
   private List<Role> roleList = Lists.newArrayList();
   private String instanceErrorCode;

   private boolean inRoleInstanceList;
   private boolean inRoleList;
   private boolean inListVirtualIPs;
   private final VirtualIPHandler virtualIPHandler;
   private final RoleInstanceHandler roleInstanceHandler;
   private final RoleHandler roleHandler;
   private final StringBuilder currentText = new StringBuilder();

   @Inject DeploymentHandler(VirtualIPHandler virtualIPHandler, RoleInstanceHandler roleInstanceHandler, RoleHandler roleHandler) {
      this.virtualIPHandler = virtualIPHandler;
      this.roleInstanceHandler = roleInstanceHandler;
      this.roleHandler = roleHandler;
   }

   @Override public Deployment getResult() { // Fields don't need to be reset as this isn't used in a loop.
      return Deployment.create(name, slot, status, label, //
            instanceStateDetails, instanceErrorCode, virtualIPs, roleInstanceList, roleList, virtualNetworkName);
   }

   @Override public void startElement(String url, String name, String qName, Attributes attributes) {
      if (qName.equals("VirtualIPs")) {
         inListVirtualIPs = true;
      } else if (qName.equals("RoleInstanceList")) {
         inRoleInstanceList = true;
      } else if (qName.equals("RoleList")) {
         inRoleList = true;
      }
      if (inRoleInstanceList) {
         roleInstanceHandler.startElement(url, name, qName, attributes);
      }
      if (inRoleList) {
         roleHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("RoleInstanceList")) {
         inRoleInstanceList = false;
      } else if (qName.equals("RoleInstance")) {
         roleInstanceList.add(roleInstanceHandler.getResult());
      } else if (inRoleInstanceList) {
         roleInstanceHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("RoleList")) {
         inRoleList = false;
      } else if (qName.equals("Role")) {
         roleList.add(roleHandler.getResult());
      } else if (inRoleList) {
         roleHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("VirtualIPs")) {
         inListVirtualIPs = false;
         virtualIPs.add(virtualIPHandler.getResult());
      } else if (inListVirtualIPs) {
         virtualIPHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("Name") && name == null) {
         name = currentOrNull(currentText);
      } else if (qName.equals("DeploymentSlot")) {
         String slotText = currentOrNull(currentText);
         if (slotText != null) {
            slot = parseSlot(slotText);
         }
      } else if (qName.equals("Status")) {
         String statusText = currentOrNull(currentText);
         if (status == null && statusText != null) {
            status = parseStatus(statusText);
         }
      } else if (qName.equals("Label")) {
         String labelText = currentOrNull(currentText);
         if (labelText != null) {
            label = new String(base64().decode(labelText), UTF_8);
         }
      } else if (qName.equals("InstanceStateDetails")) {
         instanceStateDetails = currentOrNull(currentText);
      } else if (qName.equals("InstanceErrorCode")) {
         instanceErrorCode = currentOrNull(currentText);
      } else if (qName.equals("VirtualNetworkName")) {
         virtualNetworkName = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      if (inListVirtualIPs) {
         virtualIPHandler.characters(ch, start, length);
      } else if (inRoleInstanceList) {
         roleInstanceHandler.characters(ch, start, length);
      } else if (inRoleList) {
         roleHandler.characters(ch, start, length);
      } else if (!inListVirtualIPs && !inRoleInstanceList && !inRoleList) {
         currentText.append(ch, start, length);
      }
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

   private static RoleSize.Type parseRoleSize(String roleSize) {
      try {
         return RoleSize.Type.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, roleSize));
      } catch (IllegalArgumentException e) {
         return RoleSize.Type.UNRECOGNIZED;
      }
   }
}
