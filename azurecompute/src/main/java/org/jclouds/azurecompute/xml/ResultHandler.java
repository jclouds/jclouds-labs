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

import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

/**
 * @see for example <a href="https://msdn.microsoft.com/en-us/library/azure/dn510368.aspx">Check DNS Prefix
 * Availability</a>
 */
public final class ResultHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Boolean> {

   private boolean result = false;
   private final StringBuilder currentText = new StringBuilder();

   @Override
   public Boolean getResult() {
      final boolean res = result;
      resetState(); // handler is called in a loop.
      return res;
   }

   private void resetState() {
      result = false;
   }

   @Override
   public void startElement(
           final String url,
           final String name,
           final String qName,
           final Attributes attributes) {
   }

   @Override
   public void endElement(final String ignoredURI, final String ignoredName, final String qName) {
      if (qName.equals("Result")) {
         final String value = currentText.toString().trim();
         result = value.isEmpty() ? false : Boolean.parseBoolean(value);
      }
      currentText.setLength(0);

   }

   @Override
   public void characters(final char[] ch, final int start, final int length) {
      currentText.append(ch, start, length);
   }
}
