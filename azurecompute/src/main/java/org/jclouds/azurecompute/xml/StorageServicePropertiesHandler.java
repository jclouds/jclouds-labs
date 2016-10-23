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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.BaseEncoding.base64;
import static org.jclouds.util.SaxUtils.currentOrNull;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.azurecompute.domain.StorageService.AccountType;
import org.jclouds.azurecompute.domain.StorageService.RegionStatus;
import org.jclouds.azurecompute.domain.StorageService.Status;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;

import org.xml.sax.Attributes;

public class StorageServicePropertiesHandler
        extends ParseSax.HandlerForGeneratedRequestWithResult<StorageService.StorageServiceProperties> {

   private String description;

   private String affinityGroup;

   private String location;

   private String label;

   private StorageService.Status status;

   private final List<URL> endpoints = Lists.newArrayList();

   private boolean inEndpoints;

   private String geoPrimaryRegion;

   private StorageService.RegionStatus statusOfPrimary;

   private Date lastGeoFailoverTime;

   private String geoSecondaryRegion;

   private StorageService.RegionStatus statusOfSecondary;

   private Date creationTime;

   private final List<String> customDomains = Lists.newArrayList();

   private final List<URL> secondaryEndpoints = Lists.newArrayList();

   private boolean inSecondaryEndpoints;

   private AccountType accountType;

   private final DateService dateService;

   private final StringBuilder currentText = new StringBuilder();

   public StorageServicePropertiesHandler(final DateService dateService) {
      this.dateService = dateService;
   }

   @Override
   public StorageService.StorageServiceProperties getResult() {
      final StorageService.StorageServiceProperties result = StorageService.StorageServiceProperties.create(
              description, affinityGroup, location, label, status, endpoints,
              geoPrimaryRegion, statusOfPrimary, lastGeoFailoverTime, geoSecondaryRegion, statusOfSecondary,
              creationTime, customDomains, secondaryEndpoints, accountType);

      description = affinityGroup = location = label = null;
      status = null;
      endpoints.clear();
      geoPrimaryRegion = null;
      statusOfPrimary = null;
      lastGeoFailoverTime = null;
      geoSecondaryRegion = null;
      statusOfSecondary = null;
      creationTime = null;
      customDomains.clear();
      secondaryEndpoints.clear();
      accountType = null;

      return result;
   }

   @Override
   public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {
      if ("Endpoints".equals(qName)) {
         inEndpoints = true;
      } else if ("SecondaryEndpoints".equals(qName)) {
         inSecondaryEndpoints = true;
      }
   }

   @Override
   public void endElement(final String ignoredUri, final String ignoredName, final String qName) {
      if ("Description".equals(qName)) {
         description = currentOrNull(currentText);
      } else if ("AffinityGroup".equals(qName)) {
         affinityGroup = currentOrNull(currentText);
      } else if ("Location".equals(qName)) {
         location = currentOrNull(currentText);
      } else if ("Label".equals(qName)) {
         label = new String(base64().decode(currentOrNull(currentText)), UTF_8);
      } else if ("Status".equals(qName)) {
         status = Status.fromString(currentOrNull(currentText));
      } else if ("Endpoints".equals(qName)) {
         inEndpoints = false;
      } else if ("SecondaryEndpoints".equals(qName)) {
         inSecondaryEndpoints = false;
      } else if ("Endpoint".equals(qName)) {
         final String urlText = currentOrNull(currentText);
         try {
            if (inEndpoints) {
               endpoints.add(new URL(urlText));
            } else if (inSecondaryEndpoints) {
               secondaryEndpoints.add(new URL(urlText));
            }
         } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
         }
      } else if ("GeoPrimaryRegion".equals(qName)) {
         geoPrimaryRegion = currentOrNull(currentText);
      } else if ("StatusOfPrimary".equals(qName)) {
         statusOfPrimary = RegionStatus.fromString(currentOrNull(currentText));
      } else if ("LastGeoFailoverTime".equals(qName)) {
         lastGeoFailoverTime = dateService.iso8601SecondsDateParse(currentOrNull(currentText));
      } else if ("GeoSecondaryRegion".equals(qName)) {
         geoSecondaryRegion = currentOrNull(currentText);
      } else if ("StatusOfSecondary".equals(qName)) {
         final String text = currentOrNull(currentText);
         if (text != null) {
            statusOfSecondary = RegionStatus.fromString(text);
         }
      } else if ("CreationTime".equals(qName)) {
         creationTime = dateService.iso8601SecondsDateParse(currentOrNull(currentText));
      } else if ("Name".equals(qName)) {
         final String text = currentOrNull(currentText);
         if (text != null) {
            customDomains.add(text);
         }
      } else if ("AccountType".equals(qName)) {
         accountType = AccountType.fromString(currentOrNull(currentText));
      }

      currentText.setLength(0);
   }

   @Override
   public void characters(final char[] ch, final int start, final int length) {
      currentText.append(ch, start, length);
   }
}
