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

import org.jclouds.azurecompute.domain.Deployment.InstanceEndpoint;
import org.jclouds.azurecompute.domain.Deployment.InstanceStatus;
import org.jclouds.azurecompute.domain.Deployment.PowerState;
import org.jclouds.azurecompute.domain.Deployment.RoleInstance;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.Lists;

public class RoleInstanceHandler extends ParseSax.HandlerForGeneratedRequestWithResult<RoleInstance> {

   private String roleName;

   private String instanceName;

   private InstanceStatus instanceStatus;

   private PowerState powerState;

   private int instanceUpgradeDomain;

   private int instanceFaultDomain;

   private RoleSize.Type instanceSize;

   private String ipAddress;

   private String hostname;

   private boolean inInstanceEndpoints;

   private final InstanceEndpointHandler instanceEndpointHandler = new InstanceEndpointHandler();

   private List<InstanceEndpoint> instanceEndpoints = Lists.newArrayList();

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (qName.equals("InstanceEndpoints")) {
         inInstanceEndpoints = true;
      }
      if (inInstanceEndpoints) {
         instanceEndpointHandler.startElement(uri, localName, qName, attributes);
      }
   }

   private void resetState() {
      roleName = instanceName = ipAddress = hostname = null;
      instanceStatus = null;
      powerState = null;
      instanceUpgradeDomain = instanceFaultDomain = -1;
      instanceSize = null;
      instanceEndpoints = Lists.newArrayList();
   }

   private StringBuilder currentText = new StringBuilder();

   @Override
   public RoleInstance getResult() {
      RoleInstance result = RoleInstance.create(roleName, instanceName, instanceStatus, powerState,
              instanceUpgradeDomain, instanceFaultDomain, instanceSize, ipAddress, hostname, instanceEndpoints);
      resetState(); // handler is called in a loop.
      return result;
   }

   @Override
   public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("InstanceEndpoints")) {
         inInstanceEndpoints = false;
      } else if (qName.equals("InstanceEndpoint")) {
         instanceEndpoints.add(instanceEndpointHandler.getResult());
      } else if (inInstanceEndpoints) {
         instanceEndpointHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("RoleName")) {
         roleName = currentOrNull(currentText);
      } else if (qName.equals("InstanceName")) {
         instanceName = currentOrNull(currentText);
      } else if (qName.equals("InstanceStatus")) {
         instanceStatus = InstanceStatus.fromString(currentOrNull(currentText));
      } else if (qName.equals("PowerState")) {
         powerState = PowerState.fromString(currentOrNull(currentText));
      } else if (qName.equals("InstanceUpgradeDomain")) {
         String upgradeDomain = currentOrNull(currentText);
         if (upgradeDomain != null) {
            instanceUpgradeDomain = Integer.parseInt(upgradeDomain);
         }
      } else if (qName.equals("InstanceFaultDomain")) {
         String faultDomain = currentOrNull(currentText);
         if (faultDomain != null) {
            instanceFaultDomain = Integer.parseInt(faultDomain);
         }
      } else if (qName.equals("InstanceSize")) {
         String size = currentOrNull(currentText);
         if (size != null) {
            instanceSize = RoleSize.Type.fromString(size);
         }
      } else if (qName.equals("IpAddress")) {
         ipAddress = currentOrNull(currentText);
      } else if (qName.equals("HostName")) {
         hostname = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inInstanceEndpoints) {
         instanceEndpointHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
