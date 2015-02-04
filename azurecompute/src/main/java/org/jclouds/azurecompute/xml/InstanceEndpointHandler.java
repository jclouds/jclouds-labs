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

import org.jclouds.azurecompute.domain.Deployment.InstanceEndpoint;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

public class InstanceEndpointHandler extends ParseSax.HandlerForGeneratedRequestWithResult<InstanceEndpoint> {
   private String name;
   private String vip;
   private Integer publicPort;
   private Integer localPort;
   private String protocol;

   private StringBuilder currentText = new StringBuilder();

   @Override public void startElement(String uri, String localName, String qName, Attributes attributes) {
   }

   @Override public InstanceEndpoint getResult() {
      InstanceEndpoint result = InstanceEndpoint.create(name, vip, publicPort, localPort, protocol);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      name = vip = protocol = null;
      publicPort = localPort = null;
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("Name")) {
         name = currentOrNull(currentText);
      } else if (qName.equals("Vip")) {
         vip = currentOrNull(currentText);
      } else if (qName.equals("PublicPort")) {
         String publicPortText = currentOrNull(currentText);
         if (publicPortText != null){
            publicPort = Integer.parseInt(publicPortText);
         }
      } else if (qName.equals("LocalPort")) {
         String localPortText = currentOrNull(currentText);
         if (localPortText != null){
            localPort = Integer.parseInt(localPortText);
         }
      } else if (qName.equals("Protocol")) {
         protocol = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
