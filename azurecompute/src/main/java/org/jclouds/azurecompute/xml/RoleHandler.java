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
import java.util.List;

import org.jclouds.azurecompute.domain.DataVirtualHardDisk;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.domain.Role.ConfigurationSet;
import org.jclouds.azurecompute.domain.Role.ResourceExtensionReference;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class RoleHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Role> {

   private String roleName;
   private String roleType;
   private String vmImage;
   private String mediaLocation;
   private List<ConfigurationSet> configurationSets = Lists.newArrayList();
   private List<ResourceExtensionReference> resourceExtensionReferences = Lists.newArrayList();
   private String availabilitySetName;
   private List<DataVirtualHardDisk> dataVirtualHardDisks = Lists.newArrayList();
   private Role.OSVirtualHardDisk osVirtualHardDisk;
   private RoleSize.Type roleSize;
   private Boolean provisionGuestAgent;
   private String defaultWinRmCertificateThumbprint;

   private boolean inConfigurationSets;
   private boolean inOSVirtualHardDisk;
   private boolean inDataVirtualHardDisks;
   private boolean inResourceExtensionReference;

   private final ConfigurationSetHandler configurationSetHandler;
   private final OSVirtualHardDiskHandler osVirtualDiskHandler;
   private final DataVirtualHardDiskHandler dataVirtualHardDiskHandler;
   private final ResourceExtensionReferenceHandler resourceExtensionReferenceHandler;

   @Inject
   RoleHandler(ConfigurationSetHandler configurationSetHandler, OSVirtualHardDiskHandler osVirtualDiskHandler,
                      DataVirtualHardDiskHandler dataVirtualHardDiskHandler, ResourceExtensionReferenceHandler resourceExtensionReferenceHandler) {
      this.configurationSetHandler = configurationSetHandler;
      this.osVirtualDiskHandler = osVirtualDiskHandler;
      this.dataVirtualHardDiskHandler = dataVirtualHardDiskHandler;
      this.resourceExtensionReferenceHandler = resourceExtensionReferenceHandler;
   }

   private StringBuilder currentText = new StringBuilder();

   @Override public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (qName.equals("ConfigurationSets")) {
         inConfigurationSets = true;
      }
      if (inConfigurationSets) {
         configurationSetHandler.startElement(uri, localName, qName, attributes);
      }
      if (qName.equals("OSVirtualHardDisk")) {
         inOSVirtualHardDisk = true;
      }
      if (qName.equals("DataVirtualHardDisks")) {
         inDataVirtualHardDisks = true;
      }
      if (inDataVirtualHardDisks) {
         dataVirtualHardDiskHandler.startElement(uri, localName, qName, attributes);
      }
      if (qName.equals("ResourceExtensionReference")) {
         inResourceExtensionReference = true;
      }
      if (inResourceExtensionReference) {
         resourceExtensionReferenceHandler.startElement(uri, localName, qName, attributes);
      }

   }

   private void resetState() {
      roleName = roleType = vmImage = mediaLocation = availabilitySetName = defaultWinRmCertificateThumbprint = null;
      configurationSets = null;
      osVirtualHardDisk = null;
      configurationSets = Lists.newArrayList();
      resourceExtensionReferences = Lists.newArrayList();
      dataVirtualHardDisks = Lists.newArrayList();
      roleSize = null;
      provisionGuestAgent = null;
   }

   @Override
   public Role getResult() {
      Role result = Role.create(roleName, roleType, vmImage, mediaLocation, configurationSets,
              resourceExtensionReferences, availabilitySetName, dataVirtualHardDisks, osVirtualHardDisk, roleSize,
              provisionGuestAgent, defaultWinRmCertificateThumbprint);
      resetState(); // handler is called in a loop.
      return result;
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("DataVirtualHardDisks")) {
         inDataVirtualHardDisks = false;
      } else if (qName.equals("ConfigurationSet")) {
         inConfigurationSets = false;
         configurationSets.add(configurationSetHandler.getResult());
      } else if (inConfigurationSets) {
         configurationSetHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("DataVirtualHardDisks")) {
         inDataVirtualHardDisks = false;
         dataVirtualHardDisks.add(dataVirtualHardDiskHandler.getResult());
      } else if (inDataVirtualHardDisks) {
         dataVirtualHardDiskHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("RoleName")) {
         roleName = currentOrNull(currentText);
      } else if (qName.equals("VMImage")) {
         vmImage = currentOrNull(currentText);
      } else if (qName.equals("MediaLocation")) {
         mediaLocation = currentOrNull(currentText);
      } else if (qName.equals("AvailabilitySetName")) {
         availabilitySetName = currentOrNull(currentText);
      } else if (qName.equals("DefaultWinRmCertificateThumbprint")) {
         defaultWinRmCertificateThumbprint = currentOrNull(currentText);
      } else if (qName.equals("RoleType")) {
         roleType = currentOrNull(currentText);
      } else if (qName.equals("OSVirtualHardDisk")) {
         inOSVirtualHardDisk = false;
         osVirtualHardDisk = osVirtualDiskHandler.getResult();
      } else if (inOSVirtualHardDisk) {
         osVirtualDiskHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("RoleSize")) {
         roleSize = RoleSize.Type.valueOf(currentOrNull(currentText).toUpperCase());
      } else if (qName.equals("ProvisionGuestAgent")) {
         String provisionGuestAgentString = currentOrNull(currentText);
         if (provisionGuestAgentString != null) {
            provisionGuestAgent = Boolean.valueOf(provisionGuestAgentString);
         }
      } else if (qName.equals("ResourceExtensionReferences")) {
         inResourceExtensionReference = false;
         resourceExtensionReferences.add(resourceExtensionReferenceHandler.getResult());
      } else if (inResourceExtensionReference) {
         resourceExtensionReferenceHandler.endElement(ignoredUri, ignoredName, qName);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      if (inConfigurationSets) {
         configurationSetHandler.characters(ch, start, length);
      } else if (inOSVirtualHardDisk) {
         osVirtualDiskHandler.characters(ch, start, length);
      } else if (inResourceExtensionReference) {
         resourceExtensionReferenceHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
