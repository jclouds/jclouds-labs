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

import java.net.URI;
import javax.inject.Inject;
import org.jclouds.azurecompute.domain.HostedService;
import org.jclouds.azurecompute.domain.HostedService.Builder;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >api</a>
 */
public class HostedServiceHandler extends ParseSax.HandlerForGeneratedRequestWithResult<HostedService> {

   private final HostedServicePropertiesHandler hostedServicePropertiesHandler;

   @Inject protected HostedServiceHandler(HostedServicePropertiesHandler hostedServicePropertiesHandler) {
      this.hostedServicePropertiesHandler = hostedServicePropertiesHandler;
   }

   private StringBuilder currentText = new StringBuilder();
   protected HostedService.Builder<?> builder = builder();

   protected Builder<?> builder() {
      return HostedService.builder();
   }

   private boolean inHostedServiceProperties;

   @Override
   public HostedService getResult() {
      try {
         return builder.build();
      } finally {
         builder = builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "HostedServiceProperties")) {
         inHostedServiceProperties = true;
      }
      if (inHostedServiceProperties) {
         hostedServicePropertiesHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {

      if (equalsOrSuffix(qName, "HostedServiceProperties")) {
         builder.properties(hostedServicePropertiesHandler.getResult());
         inHostedServiceProperties = false;
      } else if (inHostedServiceProperties) {
         hostedServicePropertiesHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "Url")) {
         builder.url(URI.create(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "ServiceName")) {
         builder.name(currentOrNull(currentText));
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inHostedServiceProperties) {
         hostedServicePropertiesHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
