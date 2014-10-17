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

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.azurecompute.domain.Error;
import org.jclouds.azurecompute.domain.Error.Code;
import org.jclouds.http.functions.ParseSax;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460801" >api</a>
 */
public final class ErrorHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Error> {
   private Code code;
   private String message;

   private StringBuilder currentText = new StringBuilder();

   @Override public Error getResult() {
      return Error.create(code, message);
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("Code")) {
         String codeText = currentOrNull(currentText);
         code = parseCode(codeText);
      } else if (qName.equals("Message")) {
         message = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   private static Code parseCode(String code) {
      try {
         return Code.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, code));
      } catch (IllegalArgumentException e) {
         return Code.UNRECOGNIZED;
      }
   }
}
