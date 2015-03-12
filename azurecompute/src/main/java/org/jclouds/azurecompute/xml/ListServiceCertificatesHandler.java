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

import java.util.List;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import org.jclouds.azurecompute.domain.ServiceCertificate;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/ee460788.aspx">Response body description</a>
 */
public final class ListServiceCertificatesHandler extends
        ParseSax.HandlerForGeneratedRequestWithResult<List<ServiceCertificate>> {

   private boolean inCertificate;

   private final ServiceCertificateHandler serviceCertificateHandler;

   private final Builder<ServiceCertificate> certificates = ImmutableList.builder();

   @Inject
   ListServiceCertificatesHandler(ServiceCertificateHandler serviceCertificateHandler) {
      this.serviceCertificateHandler = serviceCertificateHandler;
   }

   @Override
   public List<ServiceCertificate> getResult() {
      return certificates.build();
   }

   @Override
   public void startElement(final String url, final String name, final String qName, final Attributes attributes) {
      if (qName.equals("Certificate")) {
         inCertificate = true;
      }
      if (inCertificate) {
         serviceCertificateHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(final String uri, final String name, final String qName) {
      if (qName.equals("Certificate")) {
         inCertificate = false;
         certificates.add(serviceCertificateHandler.getResult());
      } else if (inCertificate) {
         serviceCertificateHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      if (inCertificate) {
         serviceCertificateHandler.characters(ch, start, length);
      }
   }
}
