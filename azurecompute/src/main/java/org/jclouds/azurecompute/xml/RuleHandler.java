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

import org.jclouds.azurecompute.domain.Rule;
import org.jclouds.http.functions.ParseSax;

final class RuleHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Rule> {

   private String name;
   private String type;
   private String priority;
   private String action;
   private String sourceAddressPrefix;
   private String sourcePortRange;
   private String destinationAddressPrefix;
   private String destinationPortRange;
   private String protocol;
   private String state;
   private Boolean isDefault;

   private final StringBuilder currentText = new StringBuilder();

   @Override public Rule getResult() {
      Rule result = Rule.create(name, type, priority, action, sourceAddressPrefix, sourcePortRange,
              destinationAddressPrefix, destinationPortRange, protocol, state, isDefault);
      name = type = priority = action = sourceAddressPrefix = sourcePortRange = destinationAddressPrefix =
             destinationPortRange = protocol = state = null; // handler is called in a loop.
             isDefault = false;
      return result;
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("Name")) {
         name = currentOrNull(currentText);
      } else if (qName.equals("Type")) {
         type = currentOrNull(currentText);
      } else if (qName.equals("Priority")) {
         priority = currentOrNull(currentText);
      } else if (qName.equals("Action")) {
         action = currentOrNull(currentText);
      } else if (qName.equals("SourceAddressPrefix")) {
         sourceAddressPrefix = currentOrNull(currentText);
      } else if (qName.equals("SourcePortRange")) {
         sourcePortRange = currentOrNull(currentText);
      } else if (qName.equals("DestinationAddressPrefix")) {
         destinationAddressPrefix = currentOrNull(currentText);
      } else if (qName.equals("DestinationPortRange")) {
         destinationPortRange = currentOrNull(currentText);
      } else if (qName.equals("Protocol")) {
         protocol = currentOrNull(currentText);
      } else if (qName.equals("State")) {
         state = currentOrNull(currentText);
      } else if (qName.equals("IsDefault")) {
         String isDefaultString = currentOrNull(currentText);
         if (isDefaultString != null) {
            isDefault = Boolean.valueOf(isDefaultString);
         }
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
