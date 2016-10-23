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

import org.jclouds.azurecompute.domain.Location;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.jclouds.azurecompute.domain.ComputeCapabilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >api</a>
 */
final class LocationHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Location> {

   private String name;

   private String displayName;

   private final List<String> availableServices = Lists.newArrayList();

   private ComputeCapabilities computeCapabilities;

   private boolean inComputeCapabilities = false;

   private final ComputeCapabilitiesHandler computeCapabilitiesHandler;

   private final StringBuilder currentText = new StringBuilder();

   @Inject
   LocationHandler(final ComputeCapabilitiesHandler computeCapabilitiesHandler) {
      this.computeCapabilitiesHandler = computeCapabilitiesHandler;
   }

   @Override
   public Location getResult() {
      Location result = Location.create(
              name, displayName, ImmutableList.copyOf(availableServices), computeCapabilities);

      // handler is called in a loop.
      name = displayName = null;
      availableServices.clear();
      computeCapabilities = null;

      return result;
   }

   @Override
   public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
           throws SAXException {

      if ("ComputeCapabilities".equals(qName)) {
         inComputeCapabilities = true;
      } else if (inComputeCapabilities) {
         computeCapabilitiesHandler.startElement(uri, localName, qName, attributes);
      }
   }

   @Override
   public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("Name")) {
         name = currentOrNull(currentText);
      } else if (qName.equals("DisplayName")) {
         displayName = currentOrNull(currentText);
      } else if (qName.equals("AvailableService")) {
         availableServices.add(currentOrNull(currentText));
      } else if ("ComputeCapabilities".equals(qName)) {
         inComputeCapabilities = false;
         computeCapabilities = computeCapabilitiesHandler.getResult();
      } else if (inComputeCapabilities) {
         computeCapabilitiesHandler.endElement(ignoredUri, ignoredName, qName);
      }

      currentText.setLength(0);
   }

   @Override
   public void characters(final char[] ch, final int start, final int length) {
      if (inComputeCapabilities) {
         computeCapabilitiesHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
