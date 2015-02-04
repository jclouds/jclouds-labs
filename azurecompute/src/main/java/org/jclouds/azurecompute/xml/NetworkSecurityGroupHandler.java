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
import java.util.List;

import org.jclouds.azurecompute.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.domain.Rule;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.Lists;

public class NetworkSecurityGroupHandler extends ParseSax.HandlerForGeneratedRequestWithResult<NetworkSecurityGroup> {
   private String name;
   private String label;
   private String location;
   private List<Rule> rules = Lists.newArrayList();

   private boolean inRule;
   private final RuleHandler ruleHandler = new RuleHandler();

   private final StringBuilder currentText = new StringBuilder();

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (qName.equals("Rule")) {
         inRule = true;
      }
   }

   @Override public NetworkSecurityGroup getResult() {
      NetworkSecurityGroup result = NetworkSecurityGroup.create(name, label, location, rules);
      name = label = location = null; // handler is called in a loop.
      rules = Lists.newArrayList();
      return result;
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("Rule")) {
         inRule = false;
         rules.add(ruleHandler.getResult());
      } else if (inRule) {
         ruleHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("Name")) {
         name = currentOrNull(currentText);
      } else if (qName.equals("Label")) {
         label = currentOrNull(currentText);
      } else if (qName.equals("Location")) {
         location = currentOrNull(currentText);
   }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      if (inRule) {
         ruleHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
