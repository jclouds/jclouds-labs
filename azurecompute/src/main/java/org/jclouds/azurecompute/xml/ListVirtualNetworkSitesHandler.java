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

import java.util.List;

import org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkSite;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

public class ListVirtualNetworkSitesHandler extends ParseSax.HandlerForGeneratedRequestWithResult<List<VirtualNetworkSite>> {

   private boolean inVirtualNetworkSite;
   private final VirtualNetworkSiteHandler virtualNetworkSiteHandler;
   private final ImmutableList.Builder<VirtualNetworkSite> virtualNetworkSites = ImmutableList.builder();

   @Inject ListVirtualNetworkSitesHandler(VirtualNetworkSiteHandler virtualNetworkSiteHandler) {
      this.virtualNetworkSiteHandler = virtualNetworkSiteHandler;
   }

   @Override public List<VirtualNetworkSite> getResult() {
      return virtualNetworkSites.build();
   }

   @Override public void startElement(String url, String name, String qName, Attributes attributes) {
      if (qName.equals("VirtualNetworkSite")) {
         inVirtualNetworkSite = true;
      }
      if (inVirtualNetworkSite) {
         virtualNetworkSiteHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override public void endElement(String uri, String name, String qName) {
      if (qName.equals("VirtualNetworkSite")) {
         inVirtualNetworkSite = false;
         virtualNetworkSites.add(virtualNetworkSiteHandler.getResult());
      } else if (inVirtualNetworkSite) {
         virtualNetworkSiteHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inVirtualNetworkSite) {
         virtualNetworkSiteHandler.characters(ch, start, length);
      }
   }
}
