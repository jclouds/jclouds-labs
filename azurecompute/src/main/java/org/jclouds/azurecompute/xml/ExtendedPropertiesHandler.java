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

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.helpers.DefaultHandler;

public class ExtendedPropertiesHandler extends DefaultHandler {

   private final List<String> extendedProperties = Lists.newArrayList();

   private final StringBuilder currentText = new StringBuilder();

   public Map<String, String> getResult() {
      final Map<String, String> result = new HashMap<String, String>();
      for (int i = 0; i < extendedProperties.size(); i += 2) {
         result.put(extendedProperties.get(i), extendedProperties.get(i + 1));
      }
      extendedProperties.clear();
      return result;
   }

   @Override
   public void endElement(final String uri, final String localName, final String qName) {
      if ("Name".equals(qName) || "Value".equals(qName)) {
         extendedProperties.add(currentOrNull(currentText));
      }

      currentText.setLength(0);
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      currentText.append(ch, start, length);
   }
}
