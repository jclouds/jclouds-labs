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

import java.net.URI;
import org.jclouds.azurecompute.domain.ServiceCertificate;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >Response body description</a>
 */
public final class ServiceCertificateHandler extends ParseSax.HandlerForGeneratedRequestWithResult<ServiceCertificate> {

   private URI url;

   private String thumbprint;

   private String thumbprintAlgorithm;

   private String data;

   private final StringBuilder currentText = new StringBuilder();

   @Override
   public ServiceCertificate getResult() {
      final ServiceCertificate result = ServiceCertificate.create(url, thumbprint, thumbprintAlgorithm, data);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      url = null;
      thumbprint = thumbprintAlgorithm = data = null;
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
      if (qName.equals("CertificateUrl")) {
         url = URI.create(currentOrNull(currentText));
      } else if (qName.equals("Thumbprint")) {
         thumbprint = currentOrNull(currentText);
      } else if (qName.equals("ThumbprintAlgorithm")) {
         thumbprintAlgorithm = currentOrNull(currentText);
      } else if (qName.equals("Data")) {
         data = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      currentText.append(ch, start, length);
   }
}
