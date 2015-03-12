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

import com.google.common.collect.ImmutableMap;
import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import org.jclouds.azurecompute.domain.Profile;
import org.jclouds.azurecompute.domain.ProfileDefinition;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758251.aspx" >Response body description</a>
 */
public final class ProfileHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Profile> {

   private String domain;

   private String name;

   private ProfileDefinition.Status status;

   private String version;

   private ImmutableMap.Builder<String, ProfileDefinition.Status> definitions
           = ImmutableMap.<String, ProfileDefinition.Status>builder();

   private final StringBuilder currentText = new StringBuilder();

   private boolean inDefinition = false;

   private String definitionVersion = null;
   private ProfileDefinition.Status definitionStatus = null;

   @Override
   public Profile getResult() {
      final Profile result = Profile.create(domain, name, status, version, definitions.build());
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      domain = name = version = null;
      status = null;
      definitions = ImmutableMap.<String, ProfileDefinition.Status>builder();
      inDefinition = false;
   }

   @Override
   public void startElement(
           final String url,
           final String name,
           final String qName,
           final Attributes attributes) {

      if (qName.equals("Definition")) {
         inDefinition = true;
      }
   }

   @Override
   public void endElement(final String ignoredURI, final String ignoredName, final String qName) {
      if (qName.equals("Definition")) {
         inDefinition = false;
         definitions.put(definitionVersion, definitionStatus);
      } else if (inDefinition) {
         if (qName.equals("Status")) {
            final String value = currentText.toString().trim();
            definitionStatus = value.isEmpty()
                    ? null
                    : ProfileDefinition.Status.fromString(value);
         } else if (qName.equals("Version")) {
            definitionVersion = currentOrNull(currentText);
         }
      } else {
         if (qName.equals("DomainName")) {
            domain = currentOrNull(currentText);
         } else if (qName.equals("Name")) {
            name = currentOrNull(currentText);
         } else if (qName.equals("Status")) {
            final String value = currentText.toString().trim();
            status = value.isEmpty()
                    ? null
                    : ProfileDefinition.Status.fromString(value);
         } else if (qName.equals("EnabledVersion")) {
            version = currentOrNull(currentText);
         }
      }
      currentText.setLength(0);

   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      currentText.append(ch, start, length);
   }
}
