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

import org.jclouds.azurecompute.domain.Role.ResourceExtensionReference;
import org.jclouds.azurecompute.domain.Role.ResourceExtensionReference.ResourceExtensionParameterValue;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ResourceExtensionReferenceHandler extends ParseSax.HandlerForGeneratedRequestWithResult<ResourceExtensionReference> {

   private String referenceName;
   private String publisher;
   private String name;
   private String version;
   private List<ResourceExtensionParameterValue> resourceExtensionParameterValues = Lists.newArrayList();
   private String state;

   private final ResourceExtensionParameterValueHandler resourceExtensionParameterValueHandler;
   private boolean inResourceExtensionParameterValue = false;
   private final StringBuilder currentText = new StringBuilder();

   @Inject
   ResourceExtensionReferenceHandler(ResourceExtensionParameterValueHandler resourceExtensionParameterValueHandler) {
      this.resourceExtensionParameterValueHandler = resourceExtensionParameterValueHandler;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (qName.equals("ResourceExtensionParameterValue")) {
         inResourceExtensionParameterValue = true;
      }
      if (inResourceExtensionParameterValue) {
         resourceExtensionParameterValueHandler.startElement(uri, localName, qName, attributes);
      }
   }

   @Override
   public ResourceExtensionReference getResult() {
      ResourceExtensionReference result = ResourceExtensionReference.create(referenceName, publisher, name, version,
              resourceExtensionParameterValues, state);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      referenceName = publisher = version = name = state = null;
      resourceExtensionParameterValues = Lists.newArrayList();
   }

   @Override
   public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("ResourceExtensionParameterValue")) {
         inResourceExtensionParameterValue = false;
         resourceExtensionParameterValues.add(resourceExtensionParameterValueHandler.getResult());
      } else if (inResourceExtensionParameterValue) {
         resourceExtensionParameterValueHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("ReferenceName")) {
         referenceName = currentOrNull(currentText);
      } else if (qName.equals("Publisher")) {
         publisher = currentOrNull(currentText);
      } else if (qName.equals("Name")) {
         name = currentOrNull(currentText);
      } else if (qName.equals("Version")) {
         version = currentOrNull(currentText);
      } else if (qName.equals("State")) {
         state = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inResourceExtensionParameterValue) {
         resourceExtensionParameterValueHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
