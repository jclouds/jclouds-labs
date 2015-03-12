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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.BaseEncoding.base64;
import static org.jclouds.util.SaxUtils.currentOrNull;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.azurecompute.domain.AffinityGroup;
import org.jclouds.azurecompute.domain.AffinityGroup.Capability;
import org.jclouds.azurecompute.domain.AffinityGroup.ComputeCapabilities;
import org.jclouds.date.internal.SimpleDateFormatDateService;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157191">api</a>
 */
public final class AffinityGroupHandler extends ParseSax.HandlerForGeneratedRequestWithResult<AffinityGroup> {

   private final StringBuilder currentText = new StringBuilder();

   private final ComputeCapabilitiesHandler computeCapabilitiesHandler;

   private String name;

   private String label;

   private String description;

   private String location;

   private final List<Capability> capabilities = Lists.newArrayList();

   private Date createdTime;

   private ComputeCapabilities computeCapabilities;

   private boolean inComputeCapabilities = false;

   @Inject
   AffinityGroupHandler(ComputeCapabilitiesHandler computeCapabilitiesHandler) {
      this.computeCapabilitiesHandler = computeCapabilitiesHandler;
   }

   @Override
   public AffinityGroup getResult() {
      final AffinityGroup result = AffinityGroup.create(
              name, label, description, location, capabilities, createdTime, computeCapabilities);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      name = null;
      label = null;
      description = null;
      location = null;
      capabilities.clear();
      createdTime = null;
      computeCapabilities = null;
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
   public void endElement(final String ignoredUri, final String ignoredName, final String qName) {
      if ("Name".equals(qName)) {
         name = currentOrNull(currentText);
      } else if ("Label".equals(qName)) {
         label = new String(base64().decode(currentOrNull(currentText)), UTF_8);
      } else if ("Description".equals(qName)) {
         description = currentOrNull(currentText);
      } else if ("Location".equals(qName)) {
         location = currentOrNull(currentText);
      } else if ("Capability".equals(qName)) {
         final String capabilityText = currentOrNull(currentText);
         if (capabilityText != null) {
            capabilities.add(Capability.valueOf(capabilityText));
         }
      } else if ("CreatedTime".equals(qName)) {
         final String createdTimeText = currentOrNull(currentText);
         if (createdTimeText != null) {
            createdTime = new SimpleDateFormatDateService().iso8601DateOrSecondsDateParse(createdTimeText);
         }
      } else if ("ComputeCapabilities".equals(qName)) {
         inComputeCapabilities = false;
         computeCapabilities = computeCapabilitiesHandler.getResult();
      } else if (inComputeCapabilities) {
         computeCapabilitiesHandler.endElement(ignoredUri, ignoredName, qName);
      }

      currentText.setLength(0);
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      if (inComputeCapabilities) {
         computeCapabilitiesHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
