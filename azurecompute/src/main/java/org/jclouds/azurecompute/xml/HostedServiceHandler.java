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

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.azurecompute.domain.HostedService;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >Response body description</a>
 */
public final class HostedServiceHandler extends ParseSax.HandlerForGeneratedRequestWithResult<HostedService> {
   private String name;
   private String location;
   private String affinityGroup;
   private String label;
   private String description;
   private HostedService.Status status;
   private Date created;
   private Date lastModified;
   private Map<String, String> extendedProperties = Maps.newLinkedHashMap();

   private boolean inHostedServiceProperties;
   private String propertyName;
   private StringBuilder currentText = new StringBuilder();
   private final DateService dateService;

   @Inject HostedServiceHandler(DateService dateService) {
      this.dateService = dateService;
   }

   @Override public HostedService getResult() {
      HostedService result = HostedService.create(name, location, affinityGroup, label, description, status, created, //
            lastModified, ImmutableMap.copyOf(extendedProperties));
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

   @Override public void startElement(String ignoredUri, String ignoredLocalName, String qName, Attributes ignoredAttributes) {
      if (qName.equals("HostedServiceProperties")) {
         inHostedServiceProperties = true;
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
            label = new String(base64().decode(currentOrNull(currentText)), UTF_8);
         }
      } else if (qName.equals("ServiceName")) {
         name = currentOrNull(currentText);
      }
      currentText = new StringBuilder();
   }

   @Override public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   private static HostedService.Status status(String status) {
      try {
         return HostedService.Status.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, status));
      } catch (IllegalArgumentException e) {
         return HostedService.Status.UNRECOGNIZED;
      }
   }
}
