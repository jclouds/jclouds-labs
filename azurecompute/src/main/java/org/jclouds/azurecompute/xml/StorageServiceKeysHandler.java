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

import com.google.common.base.Throwables;
import java.net.MalformedURLException;
import java.net.URL;
import org.jclouds.azurecompute.domain.StorageServiceKeys;
import org.jclouds.http.functions.ParseSax;

public class StorageServiceKeysHandler extends ParseSax.HandlerForGeneratedRequestWithResult<StorageServiceKeys> {

   private URL url;

   private String primary;

   private String secondary;

   private final StringBuilder currentText = new StringBuilder();

   @Override
   public StorageServiceKeys getResult() {
      final StorageServiceKeys result = StorageServiceKeys.create(url, primary, secondary);
      url = null;
      primary = null;
      secondary = null;
      return result;
   }

   @Override
   public void endElement(final String ignoredUri, final String ignoredName, final String qName) {
      if ("Url".equals(qName)) {
         final String urlText = currentOrNull(currentText);
         if (urlText != null) {
            try {
               url = new URL(urlText);
            } catch (MalformedURLException e) {
               throw Throwables.propagate(e);
            }
         }
      } else if ("Primary".equals(qName)) {
         primary = currentOrNull(currentText);
      } else if ("Secondary".equals(qName)) {
         secondary = currentOrNull(currentText);
      }
      
      currentText.setLength(0);
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      currentText.append(ch, start, length);
   }
}
