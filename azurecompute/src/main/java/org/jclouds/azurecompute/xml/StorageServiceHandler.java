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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.jclouds.date.DateService;

public class StorageServiceHandler extends ParseSax.HandlerForGeneratedRequestWithResult<StorageService> {

   private URL url;

   private String serviceName;

   private StorageService.StorageServiceProperties storageServiceProperties;

   private boolean inStorageServiceProperties;

   private final StorageServicePropertiesHandler storageServicePropertiesHandler;

   private final Map<String, String> extendedProperties = Maps.newHashMap();

   private boolean inExtendedProperties;

   private final ExtendedPropertiesHandler extendedPropertiesHandler = new ExtendedPropertiesHandler();

   private String capability;

   private final StringBuilder currentText = new StringBuilder();

   @Inject
   StorageServiceHandler(final DateService dateService) {
      this.storageServicePropertiesHandler = new StorageServicePropertiesHandler(dateService);
   }

   @Override
   public StorageService getResult() {
      final StorageService result = StorageService.create(
              url, serviceName, storageServiceProperties, extendedProperties, capability);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      serviceName = null;
      url = null;
      storageServiceProperties = null;
      extendedProperties.clear();
      capability = null;
   }

   @Override
   public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {
      if ("StorageServiceProperties".equals(qName)) {
         inStorageServiceProperties = true;
      } else if (inStorageServiceProperties) {
         storageServicePropertiesHandler.startElement(uri, localName, qName, attributes);
      } else if ("ExtendedProperties".equals(qName)) {
         inExtendedProperties = true;
      }
   }

   @Override
   public void endElement(final String ignoredUri, final String ignoredName, final String qName) {
      if ("StorageServiceProperties".equals(qName)) {
         inStorageServiceProperties = false;
         storageServiceProperties = storageServicePropertiesHandler.getResult();
      } else if (inStorageServiceProperties) {
         storageServicePropertiesHandler.endElement(ignoredUri, ignoredName, qName);
      } else if ("ExtendedProperties".equals(qName)) {
         inExtendedProperties = false;
         extendedProperties.putAll(extendedPropertiesHandler.getResult());
      } else if (inExtendedProperties) {
         extendedPropertiesHandler.endElement(ignoredUri, ignoredName, qName);
      } else if ("ServiceName".equals(qName)) {
         serviceName = currentOrNull(currentText);
      } else if ("Url".equals(qName)) {
         final String urlText = currentOrNull(currentText);
         if (urlText != null) {
            try {
               url = new URL(urlText);
            } catch (MalformedURLException e) {
               throw Throwables.propagate(e);
            }
         }
      } else if ("Capability".equals(qName)) {
         capability = currentOrNull(currentText);
      }

      currentText.setLength(0);
   }

   @Override
   public void characters(final char[] ch, final int start, final int length) {
      if (inStorageServiceProperties) {
         storageServicePropertiesHandler.characters(ch, start, length);
      } else if (inExtendedProperties) {
         extendedPropertiesHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
