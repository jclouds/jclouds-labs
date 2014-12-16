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

import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkConfiguration;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.inject.Inject;

public class NetworkConfigurationHandler extends ParseSax.HandlerForGeneratedRequestWithResult<NetworkConfiguration> {

   private VirtualNetworkConfiguration virtualNetworkConfiguration;

   private boolean inVirtualNetworkConfiguration;
   private final VirtualNetworkConfigurationHandler virtualNetworkConfigurationHandler;

   @Inject
   NetworkConfigurationHandler(VirtualNetworkConfigurationHandler virtualNetworkConfigurationHandler) {
      this.virtualNetworkConfigurationHandler = virtualNetworkConfigurationHandler;
   }

   private final StringBuilder currentText = new StringBuilder();

   @Override public void startElement(String url, String name, String qName, Attributes attributes) {
      if (qName.equals("VirtualNetworkConfiguration")) {
         inVirtualNetworkConfiguration = true;
      }
      if (inVirtualNetworkConfiguration) {
         virtualNetworkConfigurationHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public NetworkConfiguration getResult() {
      return NetworkConfiguration.create(virtualNetworkConfiguration);
   }

   @Override
   public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("VirtualNetworkConfiguration")) {
         virtualNetworkConfiguration = virtualNetworkConfigurationHandler.getResult();
      } else if (inVirtualNetworkConfiguration) {
         virtualNetworkConfigurationHandler.endElement(ignoredUri, ignoredName, qName);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      if (inVirtualNetworkConfiguration) {
         virtualNetworkConfigurationHandler.characters(ch, start, length);
      } else
         currentText.append(ch, start, length);
      }
}
