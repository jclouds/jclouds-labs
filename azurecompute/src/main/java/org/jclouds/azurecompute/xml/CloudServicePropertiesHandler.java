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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.jclouds.azurecompute.domain.CloudServiceProperties;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import javax.inject.Inject;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static org.jclouds.util.SaxUtils.currentOrNull;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/ee460806.aspx" >Response body description</a>
 */
public final class CloudServicePropertiesHandler
      extends ParseSax.HandlerForGeneratedRequestWithResult<CloudServiceProperties> {
   private String name;
   private URI url;
   private String location;
   private String affinityGroup;
   private String label;
   private String description;
   private CloudServiceProperties.Status status;
   private Date created;
   private Date lastModified;
   private Map<String, String> extendedProperties = Maps.newLinkedHashMap();
   private List<Deployment> deploymentList = Lists.newArrayList();

   private boolean inHostedServiceProperties;
   private boolean inDeployment;
   private String propertyName;
   private StringBuilder currentText = new StringBuilder();
   private final DateService dateService;
   private final DeploymentHandler deploymentHandler;

   @Inject CloudServicePropertiesHandler(DateService dateService, DeploymentHandler deploymentHandler) {
      this.dateService = dateService;
      this.deploymentHandler = deploymentHandler;
   }

   @Override public CloudServiceProperties getResult() {
      CloudServiceProperties result = CloudServiceProperties
            .create(name, url, location, affinityGroup, label, description, status, created, //
                  lastModified, ImmutableMap.copyOf(extendedProperties), ImmutableList.copyOf(deploymentList));
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      name = description = location = affinityGroup = label = null;
      status = null;
      created = lastModified = null;
      extendedProperties.clear();
      inHostedServiceProperties = false;
      propertyName = null;
   }

   @Override public void startElement(String ignoredUri, String ignoredLocalName, String qName,
         Attributes ignoredAttributes) {
      if (qName.equals("HostedServiceProperties")) {
         inHostedServiceProperties = true;
      } else if (qName.equals("Deployment")) {
         inDeployment = true;
      }
      if (inDeployment) {
         deploymentHandler.startElement(ignoredUri, ignoredLocalName, qName, ignoredAttributes);
      }
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("HostedServiceProperties")) {
         inHostedServiceProperties = false;
      } else if (inHostedServiceProperties) {
         if (qName.equals("DateCreated")) {
            created = dateService.iso8601SecondsDateParse(currentOrNull(currentText));
         } else if (qName.equals("DateLastModified")) {
            lastModified = dateService.iso8601SecondsDateParse(currentOrNull(currentText));
         } else if (qName.equals("Status")) {
            String statusText = currentOrNull(currentText);
            if (statusText != null) {
               status = status(statusText);
            }
         } else if (qName.equals("Name")) {
            propertyName = currentOrNull(currentText);
         } else if (qName.equals("Value")) {
            extendedProperties.put(propertyName, currentOrNull(currentText));
            propertyName = null;
         } else if (qName.equals("Description")) {
            description = currentOrNull(currentText);
         } else if (qName.equals("Location")) {
            location = currentOrNull(currentText);
         } else if (qName.equals("AffinityGroup")) {
            affinityGroup = currentOrNull(currentText);
         } else if (qName.equals("Label")) {
            label = currentOrNull(currentText);
         }
      } else if (qName.equals("ServiceName")) {
         name = currentOrNull(currentText);
      } else if (qName.equals("Deployment")) {
         deploymentList.add(deploymentHandler.getResult());
         inDeployment = false;
      } else if (inDeployment) {
         deploymentHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("Url")) {
         String link = currentOrNull(currentText);
         if (link != null) {
            url = URI.create(link);
         }
      }
      currentText.setLength(0);
   }

   @Override public void characters(char[] ch, int start, int length) {
      if (inDeployment) {
         deploymentHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

   private static CloudServiceProperties.Status status(String status) {
      try {
         return CloudServiceProperties.Status.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, status));
      } catch (IllegalArgumentException e) {
         return CloudServiceProperties.Status.UNRECOGNIZED;
      }
   }
}
