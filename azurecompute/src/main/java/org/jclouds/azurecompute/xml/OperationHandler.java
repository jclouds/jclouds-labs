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

import javax.inject.Inject;
import org.jclouds.azurecompute.domain.Operation;
import org.jclouds.azurecompute.domain.Operation.Builder;
import org.jclouds.azurecompute.domain.Operation.Status;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460783" >api</a>
 */
public class OperationHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Operation> {

   private final ErrorHandler errorHandler;

   @Inject
   private OperationHandler(ErrorHandler errorHandler) {
      this.errorHandler = errorHandler;
   }

   private StringBuilder currentText = new StringBuilder();
   private Operation.Builder builder = builder();

   private Builder builder() {
      return Operation.builder();
   }

   private boolean inError;

   @Override
   public Operation getResult() {
      try {
         return builder.build();
      } finally {
         builder = builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "Error")) {
         inError = true;
      }
      if (inError) {
         errorHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "Error")) {
         builder.error(errorHandler.getResult());
         inError = false;
      } else if (inError) {
         errorHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "ID")) {
         builder.id(currentOrNull(currentText));
      } else if (qName.equals("Status")) {
         String rawStatus = currentOrNull(currentText);
         builder.rawStatus(rawStatus);
         builder.status(Status.fromValue(rawStatus));
      } else if (equalsOrSuffix(qName, "HttpStatusCode")) {
         builder.httpStatusCode(Integer.parseInt(currentOrNull(currentText)));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inError) {
         errorHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
