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

import org.jclouds.azurecompute.domain.NetworkConfiguration.AddressSpace;
import org.jclouds.azurecompute.domain.NetworkConfiguration.Subnet;
import org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkSite;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.Lists;

public class VirtualNetworkSiteHandler extends ParseSax.HandlerForGeneratedRequestWithResult<VirtualNetworkSite> {

   private String id;
   private String name;
   private String location;
   private AddressSpace addressSpace;
   private List<Subnet> subnets = Lists.newArrayList();

   private boolean inSubnet;
   private boolean inAddressSpace;
   private final SubnetHandler subnetHandler = new SubnetHandler();
   private final AddressSpaceHandler addressSpaceHandler = new AddressSpaceHandler();

   private StringBuilder currentText = new StringBuilder();

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (qName.equalsIgnoreCase("VirtualNetworkSite")){
         name = attributes.getValue("name");
         location = attributes.getValue("Location");
      }

      if (qName.equals("AddressSpace")) {
         inAddressSpace = true;
      } else if (qName.equals("Subnet")) {
         inSubnet = true;
      }
      if (inAddressSpace) {
         addressSpaceHandler.startElement(uri, name, qName, attributes);
      }
      if (inSubnet) {
         subnetHandler.startElement(uri, name, qName, attributes);
      }
   }

   private void resetState() {
      id = name = location = null;
      subnets = Lists.newArrayList();
      addressSpace = null;
   }

   @Override
   public VirtualNetworkSite getResult() {
      VirtualNetworkSite result = VirtualNetworkSite.create(id, name, location, addressSpace, subnets);
      resetState(); // handler is called in a loop.
      return result;
   }

   @Override
   public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("AddressSpace")) {
         inAddressSpace = false;
         addressSpace = addressSpaceHandler.getResult();
      } else if (inAddressSpace) {
         addressSpaceHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("Subnet")) {
         inSubnet = false;
         subnets.add(subnetHandler.getResult());
      } else if (inSubnet) {
         subnetHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("Id")) {
         id = currentOrNull(currentText);
      } else if (qName.equals("Name")) {
         name = currentOrNull(currentText);
      } else if (qName.equals("Location")) {
         location = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      if (inAddressSpace) {
         addressSpaceHandler.characters(ch, start, length);
      } else if (inSubnet) {
         subnetHandler.characters(ch, start, length);
      } else if (!inAddressSpace && !inSubnet) {
         currentText.append(ch, start, length);
      }
   }

}
