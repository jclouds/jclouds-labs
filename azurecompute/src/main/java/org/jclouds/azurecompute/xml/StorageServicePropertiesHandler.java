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

import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.http.functions.ParseSax;

public class StorageServicePropertiesHandler extends ParseSax.HandlerForGeneratedRequestWithResult<StorageService.StorageServiceProperties> {
   private String description;
   private String status;
   private String location;
   private String accountType;

   private final StringBuilder currentText = new StringBuilder();

   @Override public StorageService.StorageServiceProperties getResult() {
      StorageService.StorageServiceProperties result = StorageService.StorageServiceProperties.create(description, status, location, accountType);
      description = status = location = accountType = null; // handler could be called in a loop.
      return result;
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("Description")) {
         description = currentOrNull(currentText);
      } else if (qName.equals("Status")) {
         status = currentOrNull(currentText);
      } else if (qName.equals("Location")) {
         location = currentOrNull(currentText);
      } else if (qName.equals("AccountType")) {
         accountType = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
