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
import org.jclouds.azurecompute.domain.ReservedIPAddress;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn722418.aspx">Response body description</a>
 */
public final class ListReservedIPAddressHandler extends
        ParseSax.HandlerForGeneratedRequestWithResult<List<ReservedIPAddress>> {

   private boolean inReservedIP;

   private final ReservedIPAddressHandler reservedIPAddressHandler;

   private final Builder<ReservedIPAddress> ips = ImmutableList.builder();

   @Inject
   ListReservedIPAddressHandler(ReservedIPAddressHandler reservedIPAddressHandler) {
      this.reservedIPAddressHandler = reservedIPAddressHandler;
   }

   @Override
   public List<ReservedIPAddress> getResult() {
      return ips.build();
   }

   @Override
   public void startElement(final String url, final String name, final String qName, final Attributes attributes) {
      if (qName.equals("ReservedIP")) {
         inReservedIP = true;
      }
      if (inReservedIP) {
         reservedIPAddressHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(final String uri, final String name, final String qName) {
      if (qName.equals("ReservedIP")) {
         inReservedIP = false;
         ips.add(reservedIPAddressHandler.getResult());
      } else if (inReservedIP) {
         reservedIPAddressHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      if (inReservedIP) {
         reservedIPAddressHandler.characters(ch, start, length);
      }
   }
}
