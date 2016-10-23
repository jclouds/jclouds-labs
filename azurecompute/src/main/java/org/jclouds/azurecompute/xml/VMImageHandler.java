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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.jclouds.azurecompute.domain.DataVirtualHardDisk;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.domain.VMImage;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.net.URI;
import java.util.Date;
import java.util.List;

import static org.jclouds.util.SaxUtils.currentOrNull;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn499770.aspx" >api</a>
 */
final class VMImageHandler extends ParseSax.HandlerForGeneratedRequestWithResult<VMImage> {
   private String name;
   private String label;
   private String category;
   private String description;
   private VMImage.OSDiskConfiguration osDiskConfiguration;
   private String serviceName;
   private String deploymentName;
   private String roleName;
   private String location;
   private String affinityGroup;
   private Date createdTime;
   private Date modifiedTime;
   private String language;
   private String imageFamily;
   private RoleSize.Type recommendedVMSize;
   private Boolean isPremium;
   private String eula;
   private URI iconUri;
   private URI smallIconUri;
   private URI privacyUri;
   private Date publishedDate;

   private final StringBuilder currentText = new StringBuilder();

   private final DataVirtualHardDiskHandler dataVirtualHardDiskHandler;
   private final OSConfigHandler osConfigHandler;
   private List<DataVirtualHardDisk> dataDiskConfigurations = Lists.newArrayList();

   private boolean inOSConfig;
   private boolean inDataConfig;
   private final DateService dateService = new SimpleDateFormatDateService();

   @Inject VMImageHandler(DataVirtualHardDiskHandler dataVirtualHardDiskHandler, OSConfigHandler osConfigHandler) {
      this.dataVirtualHardDiskHandler = dataVirtualHardDiskHandler;
      this.osConfigHandler = osConfigHandler;
   }

   @Override
   public VMImage getResult() {
      VMImage result = VMImage
            .create(name, label, category, description, osDiskConfiguration, dataDiskConfigurations,
                  serviceName, deploymentName, roleName, location, affinityGroup, createdTime, modifiedTime, language,
                  imageFamily, recommendedVMSize, isPremium, eula, iconUri, smallIconUri, privacyUri, publishedDate);
      resetState(); // handler is called in a loop.
      return result;
   }

   @Override
   public void startElement(String ignoredUri, String ignoredLocalName, String qName,
         Attributes ignoredAttributes) throws SAXException {

      if (qName.equals("OSDiskConfiguration")) {
         inOSConfig = true;
      }
      if (inOSConfig) {
         osConfigHandler.endElement(ignoredUri, ignoredLocalName, qName);
      }
      if (qName.equals("DataDiskConfiguration")) {
         inDataConfig = true;
      }
      if (inDataConfig) {
         dataVirtualHardDiskHandler.startElement(ignoredUri, ignoredLocalName, qName, ignoredAttributes);
      }
   }

   private void resetState() {
      name = affinityGroup = label = description = category = null;
      serviceName = deploymentName = roleName = location = affinityGroup = null;
      createdTime = modifiedTime = null;
      language = imageFamily = eula = null;
      recommendedVMSize = null;
      isPremium = Boolean.FALSE;
      iconUri = smallIconUri = privacyUri = null;
      publishedDate = null;
      osDiskConfiguration = null;
      dataDiskConfigurations = Lists.newArrayList();
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("Name") && !inDataConfig && !inOSConfig) {
         name = currentOrNull(currentText);
      } else if (qName.equals("Label")) {
         label = currentOrNull(currentText);
      } else if (qName.equals("Category")) {
         category = currentOrNull(currentText);
      } else if (qName.equals("Description")) {
         description = currentOrNull(currentText);
      } else if (qName.equals("OSDiskConfiguration")) {
         osDiskConfiguration = osConfigHandler.getResult();
         inOSConfig = false;
      } else if (inOSConfig) {
         osConfigHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("DataDiskConfiguration") && !inOSConfig) {
         dataDiskConfigurations.add(dataVirtualHardDiskHandler.getResult());
         inDataConfig = false;
      } else if (inDataConfig) {
         dataVirtualHardDiskHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("ServiceName")) {
         serviceName = currentOrNull(currentText);
      } else if (qName.equals("DeploymentName")) {
         deploymentName = currentOrNull(currentText);
      } else if (qName.equals("RoleName")) {
         roleName = currentOrNull(currentText);
      } else if (qName.equals("Location")) {
         location = currentOrNull(currentText);
      } else if (qName.equals("AffinityGroup")) {
         affinityGroup = currentOrNull(currentText);
      } else if (qName.equals("CreatedTime")) {
         createdTime = dateService.iso8601DateOrSecondsDateParse(currentOrNull(currentText));
      } else if (qName.equals("ModifiedTime")) {
         modifiedTime = dateService.iso8601DateOrSecondsDateParse(currentOrNull(currentText));
      } else if (qName.equals("Language")) {
         language = currentOrNull(currentText);
      } else if (qName.equals("ImageFamily")) {
         imageFamily = currentOrNull(currentText);
      } else if (qName.equals("Label")) {
         label = currentOrNull(currentText);
      } else if (qName.equals("RecommendedVMSize")) {
         String vmSizeText = currentOrNull(currentText);
         if (vmSizeText != null) {
            recommendedVMSize = parseRoleSize(vmSizeText);
         }
      } else if (qName.equals("IconUri")) {
         String uri = currentOrNull(currentText);
         if (uri != null) {
            iconUri = URI.create(uri);
         }
      } else if (qName.equals("SmallIconUri")) {
         String uri = currentOrNull(currentText);
         if (uri != null) {
            smallIconUri = URI.create(uri);
         }
      } else if (qName.equals("PrivacyUri")) {
         String uri = currentOrNull(currentText);
         if (uri != null) {
            privacyUri = URI.create(uri);
         }
      } else if (qName.equals("IsPremium")) {
         String isPremiumText = currentOrNull(currentText);
         if (isPremiumText != null) {
            isPremium = Boolean.parseBoolean(isPremiumText);
         }
      } else if (qName.equals("Eula")) {
         eula = currentOrNull(currentText);
      } else if (qName.equals("PublishedDate")) {
         publishedDate = dateService.iso8601SecondsDateParse(currentOrNull(currentText));
      }
      currentText.setLength(0);
   }

   @Override public void characters(char[] ch, int start, int length) {
      if (inDataConfig) {
         dataVirtualHardDiskHandler.characters(ch, start, length);
      } else if (inOSConfig) {
         osConfigHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

   private static RoleSize.Type parseRoleSize(String roleSize) {
      try {
         return RoleSize.Type.valueOf(roleSize.toUpperCase().replace(" ", ""));
      } catch (IllegalArgumentException e) {
         return RoleSize.Type.UNRECOGNIZED;
      }
   }
}
