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

import org.jclouds.azurecompute.domain.Role.ConfigurationSet;
import org.jclouds.azurecompute.domain.Role.ConfigurationSet.InputEndpoint;
import org.jclouds.azurecompute.domain.Role.ConfigurationSet.PublicIP;
import org.jclouds.azurecompute.domain.Role.ConfigurationSet.SubnetName;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ConfigurationSetHandler extends ParseSax.HandlerForGeneratedRequestWithResult<ConfigurationSet> {
   private String configurationSetType;
   private List<InputEndpoint> inputEndpoint = Lists.newArrayList();
   private List<SubnetName> subnetNames = Lists.newArrayList();
   private String staticVirtualNetworkIPAddress;
   private List<PublicIP> publicIPs = Lists.newArrayList();
   private String networkSecurityGroup;

   private boolean inInputEndpoint;
   private boolean inSubnetNames;

   private final InputEndpointHandler inputEndpointHandler;
   private final SubnetNameHandler subnetNameHandler;
   private final StringBuilder currentText = new StringBuilder();

   @Inject ConfigurationSetHandler(InputEndpointHandler inputEndpointHandler, SubnetNameHandler subnetNameHandler) {
      this.inputEndpointHandler = inputEndpointHandler;
      this.subnetNameHandler = subnetNameHandler;
   }

   @Override
   public ConfigurationSet getResult() {
      ConfigurationSet result = ConfigurationSet.create(configurationSetType, inputEndpoint, subnetNames,
              staticVirtualNetworkIPAddress, publicIPs, networkSecurityGroup);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      configurationSetType = staticVirtualNetworkIPAddress = networkSecurityGroup = null;
      inputEndpoint = Lists.newArrayList();
      subnetNames = Lists.newArrayList();
      publicIPs = Lists.newArrayList();
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (qName.equals("InputEndpoint")) {
         inInputEndpoint = true;
      }
      if (inInputEndpoint) {
         inputEndpointHandler.startElement(uri, localName, qName, attributes);
      }
      if (qName.equals("SubnetNames")) {
         inSubnetNames = true;
      }
      if (inSubnetNames) {
         subnetNameHandler.startElement(uri, localName, qName, attributes);
      }
   }

   @Override
   public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("InputEndpoint")) {
         inInputEndpoint = false;
         inputEndpoint.add(inputEndpointHandler.getResult());
      } else if (qName.equals("SubnetNames")) {
         inSubnetNames = false;
         subnetNames.add(subnetNameHandler.getResult());
      } else if (inInputEndpoint) {
         inputEndpointHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (inSubnetNames) {
         subnetNameHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("ConfigurationSetType")) {
         configurationSetType = currentOrNull(currentText);
      } else if (qName.equals("NetworkSecurityGroup")) {
         networkSecurityGroup = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inInputEndpoint) {
         inputEndpointHandler.characters(ch, start, length);
      } else if (inSubnetNames) {
         subnetNameHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
