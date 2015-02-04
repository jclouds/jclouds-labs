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

import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.base.Throwables;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460787.aspx" >api</a>
 */
final class StorageServiceHandler extends ParseSax.HandlerForGeneratedRequestWithResult<StorageService> {
   private URL url;
   private String serviceName;
   private StorageService.StorageServiceProperties storageServiceProperties;

   private boolean inStorageServiceProperties;
   private final StorageServicePropertiesHandler storageServicePropertiesHandler = new StorageServicePropertiesHandler();
   private final StringBuilder currentText = new StringBuilder();

   @Override
   public StorageService getResult() {
      StorageService result = StorageService.create(url, serviceName, storageServiceProperties);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      serviceName = null;
      url = null;
      storageServiceProperties = null;
   }

   @Override public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (qName.equals("StorageServiceProperties")) {
         inStorageServiceProperties = true;
      }
   }

   @Override
   public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("StorageServiceProperties")) {
         storageServiceProperties = storageServicePropertiesHandler.getResult();
         inStorageServiceProperties = false;
      } else if (inStorageServiceProperties) {
         storageServicePropertiesHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("Url")) {
         String urlText = currentOrNull(currentText);
         if (urlText != null) {
            try {
               url = new URL(urlText);
            } catch (MalformedURLException e) {
               throw Throwables.propagate(e);
            }
         }
      } else if (qName.equals("ServiceName")) {
         serviceName = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      if (inStorageServiceProperties) {
         storageServicePropertiesHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
