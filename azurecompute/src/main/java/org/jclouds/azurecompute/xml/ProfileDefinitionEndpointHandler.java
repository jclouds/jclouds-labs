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

import org.jclouds.azurecompute.domain.ProfileDefinition;
import org.jclouds.azurecompute.domain.ProfileDefinitionEndpoint;
import org.jclouds.azurecompute.domain.ProfileDefinition.HealthStatus;
import org.jclouds.azurecompute.domain.ProfileDefinition.Status;

import org.jclouds.http.functions.ParseSax;

import org.xml.sax.Attributes;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >Response body description</a>
 */
public final class ProfileDefinitionEndpointHandler
        extends ParseSax.HandlerForGeneratedRequestWithResult<ProfileDefinitionEndpoint> {

   private String domain;

   private Status status;

   private HealthStatus healthStatus;

   private ProfileDefinitionEndpoint.Type type;

   private String location;

   private Integer weight;

   private Integer min;

   private final StringBuilder currentText = new StringBuilder();

   @Override
   public ProfileDefinitionEndpoint getResult() {
      final ProfileDefinitionEndpoint result = ProfileDefinitionEndpoint.create(
              domain, status, healthStatus, type, location, weight, min);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      domain = location = null;
      min = null;
      status = null;
      type = null;
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
      if (qName.equals("DomainName")) {
         domain = currentOrNull(currentText);
      } else if (qName.equals("Status")) {
         final String value = currentText.toString().trim();
         status = value.isEmpty()
                 ? null
                 : Status.fromString(value);
      } else if (qName.equals("MonitorStatus")) {
         final String value = currentText.toString().trim();
         healthStatus = value.isEmpty()
                 ? null
                 : ProfileDefinition.HealthStatus.fromString(value);
      } else if (qName.equals("Type")) {
         final String value = currentText.toString().trim();
         type = value.isEmpty()
                 ? ProfileDefinitionEndpoint.Type.CLOUDSERVICE
                 : ProfileDefinitionEndpoint.Type.fromString(value);
      } else if (qName.equals("Location")) {
         location = currentOrNull(currentText);
      } else if (qName.equals("Weight")) {
         final String value = currentText.toString().trim();
         weight = value.isEmpty() ? 1 : Integer.parseInt(value);
      } else if (qName.equals("MinChildEndpoints")) {
         final String value = currentText.toString().trim();
         min = value.isEmpty() ? 1 : Integer.parseInt(value);
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      currentText.append(ch, start, length);
   }
}
