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

import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import org.jclouds.azurecompute.domain.ReservedIPAddress;
import org.jclouds.azurecompute.domain.ReservedIPAddress.State;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn722412.aspx" >Response body description</a>
 */
public final class ReservedIPAddressHandler extends ParseSax.HandlerForGeneratedRequestWithResult<ReservedIPAddress> {

   private String name;

   private String address;

   private String id;

   private String label;

   private State state;

   private Boolean inUse;

   private String serviceName;

   private String deploymentName;

   private String location;

   private final StringBuilder currentText = new StringBuilder();

   @Override
   public ReservedIPAddress getResult() {
      final ReservedIPAddress result = ReservedIPAddress.create(
              name, address, id, label, state, inUse, serviceName, deploymentName, location);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      name = address = id = label = serviceName = deploymentName = location = null;
      state = null;
      inUse = null;
   }

   @Override
   public void startElement(
           final String ignoredUri,
           final String ignoredLocalName,
           final String qName,
           final Attributes ignoredAttributes) {
   }

   @Override
   public void endElement(final String ignoredUri, final String ignoredName, final String qName) {
      if (qName.equals("Name")) {
         name = currentOrNull(currentText);
      } else if (qName.equals("Address")) {
         address = currentOrNull(currentText);
      } else if (qName.equals("Id")) {
         id = currentOrNull(currentText);
      } else if (qName.equals("Label")) {
         label = currentOrNull(currentText);
      } else if (qName.equals("State")) {
         state = State.fromString(currentOrNull(currentText));
      } else if (qName.equals("InUse")) {
         final String use = currentOrNull(currentText);
         inUse = use == null ? null : Boolean.valueOf(use);
      } else if (qName.equals("ServiceName")) {
         serviceName = currentOrNull(currentText);
      } else if (qName.equals("DeploymentName")) {
         deploymentName = currentOrNull(currentText);
      } else if (qName.equals("Location")) {
         location = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      currentText.append(ch, start, length);
   }
}
