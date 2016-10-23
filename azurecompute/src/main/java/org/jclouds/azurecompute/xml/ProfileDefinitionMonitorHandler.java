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

import org.jclouds.azurecompute.domain.ProfileDefinition;
import org.jclouds.azurecompute.domain.ProfileDefinitionMonitor;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >Response body description</a>
 */
public final class ProfileDefinitionMonitorHandler
        extends ParseSax.HandlerForGeneratedRequestWithResult<ProfileDefinitionMonitor> {

   private Integer intervall;

   private Integer timeout;

   private Integer toleration;

   private ProfileDefinition.Protocol protocol;

   private Integer port;

   private String verb;

   private String path;

   private Integer expected;

   private final StringBuilder currentText = new StringBuilder();

   @Override
   public ProfileDefinitionMonitor getResult() {
      final ProfileDefinitionMonitor result = ProfileDefinitionMonitor.create(
              intervall, timeout, toleration, protocol, port, verb, path, expected);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      intervall = timeout = toleration = port = expected = null;
      protocol = null;
      verb = path = null;
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
      if (qName.equals("IntervalInSeconds")) {
         final String value = currentText.toString().trim();
         intervall = value.isEmpty()
                 ? ProfileDefinitionMonitor.DEFAULT_INTERVAL
                 : Integer.parseInt(value);
      } else if (qName.equals("TimeoutInSeconds")) {
         final String value = currentText.toString().trim();
         timeout = value.isEmpty()
                 ? ProfileDefinitionMonitor.DEFAULT_TIMEOUT
                 : Integer.parseInt(value);
      } else if (qName.equals("ToleratedNumberOfFailures")) {
         final String value = currentText.toString().trim();
         toleration = value.isEmpty()
                 ? ProfileDefinitionMonitor.DEFAULT_TOLERAION
                 : Integer.parseInt(value);
      } else if (qName.equals("Protocol")) {
         protocol = ProfileDefinition.Protocol.fromString(currentOrNull(currentText));
      } else if (qName.equals("Port")) {
         port = Integer.parseInt(currentOrNull(currentText));
      } else if (qName.equals("Verb")) {
         final String value = currentText.toString().trim();
         verb = value.isEmpty()
                 ? ProfileDefinitionMonitor.DEFAULT_VERB
                 : value;
      } else if (qName.equals("RelativePath")) {
         path = currentOrNull(currentText);
      } else if (qName.equals("ExpectedStatusCode")) {
         final String value = currentText.toString().trim();
         expected = value.isEmpty()
                 ? ProfileDefinitionMonitor.DEFAULT_EXPECTED
                 : Integer.parseInt(value);
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(final char[] ch, final int start, final int length) {
      currentText.append(ch, start, length);
   }
}
