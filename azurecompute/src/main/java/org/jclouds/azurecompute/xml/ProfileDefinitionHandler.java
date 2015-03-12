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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import javax.inject.Inject;
import org.jclouds.azurecompute.domain.ProfileDefinition;
import org.jclouds.azurecompute.domain.ProfileDefinitionEndpoint;
import org.jclouds.azurecompute.domain.ProfileDefinitionMonitor;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >Response body description</a>
 */
public final class ProfileDefinitionHandler extends ParseSax.HandlerForGeneratedRequestWithResult<ProfileDefinition> {

   private Integer ttl;

   private ProfileDefinition.Status status;

   private String version;

   private Builder<ProfileDefinitionMonitor> monitors = ImmutableList.<ProfileDefinitionMonitor>builder();

   private ProfileDefinition.LBMethod lb;

   private Builder<ProfileDefinitionEndpoint> endpoints = ImmutableList.<ProfileDefinitionEndpoint>builder();

   private ProfileDefinition.HealthStatus healthStatus;

   private final StringBuilder currentText = new StringBuilder();

   private final ProfileDefinitionMonitorHandler monitorHandler;

   private final ProfileDefinitionEndpointHandler endpointHandler;

   private boolean inMonitor = false;
   private boolean inEndpoint = false;

   @Inject
   public ProfileDefinitionHandler(
           ProfileDefinitionMonitorHandler monitorHandler, ProfileDefinitionEndpointHandler endpointHandler) {
      this.monitorHandler = monitorHandler;
      this.endpointHandler = endpointHandler;
   }

   @Override
   public ProfileDefinition getResult() {
      final ProfileDefinition result = ProfileDefinition.create(
              ttl, status, version, monitors.build(), lb, endpoints.build(), healthStatus);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      ttl = null;
      status = null;
      version = null;
      monitors = ImmutableList.<ProfileDefinitionMonitor>builder();
      lb = null;
      endpoints = ImmutableList.<ProfileDefinitionEndpoint>builder();
      inMonitor = false;
      inEndpoint = false;
   }

   @Override
   public void startElement(
           final String url,
           final String name,
           final String qName,
           final Attributes attributes) {

      if (!inEndpoint && qName.equals("Monitor")) {
         inMonitor = true;
      }

      if (!inMonitor && qName.equals("Endpoint")) {
         inEndpoint = true;
      }

      if (inMonitor) {
         monitorHandler.startElement(url, name, qName, attributes);
      } else if (inEndpoint) {
         endpointHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(final String uri, final String name, final String qName) {
      if (!inEndpoint && qName.equals("Monitor")) {
         inMonitor = false;
         monitors.add(monitorHandler.getResult());
      } else if (!inMonitor && qName.equals("Endpoint")) {
         inEndpoint = false;
         endpoints.add(endpointHandler.getResult());
      } else if (inMonitor) {
         monitorHandler.endElement(uri, name, qName);
      } else if (inEndpoint) {
         endpointHandler.endElement(uri, name, qName);
      } else {
         if (qName.equals("TimeToLiveInSeconds")) {
            ttl = Integer.parseInt(currentOrNull(currentText));
         } else if (qName.equals("Status")) {
            status = ProfileDefinition.Status.fromString(currentOrNull(currentText));
         } else if (qName.equals("Version")) {
            version = currentOrNull(currentText);
         } else if (qName.equals("LoadBalancingMethod")) {
            lb = ProfileDefinition.LBMethod.fromString(currentOrNull(currentText));
         } else if (qName.equals("MonitorStatus")) {
            final String value = currentText.toString().trim();
            healthStatus = value.isEmpty()
                    ? null
                    : ProfileDefinition.HealthStatus.fromString(value);
         }
         currentText.setLength(0);
      }
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      if (inMonitor) {
         monitorHandler.characters(ch, start, length);
      } else if (inEndpoint) {
         endpointHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
