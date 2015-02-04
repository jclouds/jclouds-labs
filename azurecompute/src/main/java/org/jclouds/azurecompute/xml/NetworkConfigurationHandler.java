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

import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.Lists;

public class NetworkConfigurationHandler extends ParseSax.HandlerForGeneratedRequestWithResult<NetworkConfiguration> {

   private String dns;
   private List<NetworkConfiguration.VirtualNetworkSite> virtualNetworkSites = Lists.newArrayList();

   private boolean inVirtualNetworkSites;
   private final VirtualNetworkSiteHandler virtualNetworkSiteHandler = new VirtualNetworkSiteHandler();

   private final StringBuilder currentText = new StringBuilder();

   @Override public void startElement(String url, String name, String qName, Attributes attributes) {
      if (qName.equals("VirtualNetworkSite")) {
         inVirtualNetworkSites = true;
      }
   }

   @Override
   public NetworkConfiguration getResult() {
      return NetworkConfiguration.create(dns, virtualNetworkSites);
   }

   @Override
   public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("Dns")) {
         dns = currentOrNull(currentText);
      } else if (qName.equals("VirtualNetworkSites")) {
         inVirtualNetworkSites = false;
      } else if (qName.equals("VirtualNetworkSite")) {
         virtualNetworkSites.add(virtualNetworkSiteHandler.getResult());
      } else if (inVirtualNetworkSites) {
         virtualNetworkSiteHandler.endElement(ignoredUri, ignoredName, qName);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      if (inVirtualNetworkSites) {
         virtualNetworkSiteHandler.characters(ch, start, length);
      } else
         currentText.append(ch, start, length);
      }
}
