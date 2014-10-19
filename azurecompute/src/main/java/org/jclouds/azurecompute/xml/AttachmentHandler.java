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

import org.jclouds.azurecompute.domain.Disk.Attachment;
import org.jclouds.http.functions.ParseSax;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157176" >api</a>
 */
final class AttachmentHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Attachment> {
   private String hostedService;
   private String deployment;
   private String virtualMachine;

   private final StringBuilder currentText = new StringBuilder();

   @Override public Attachment getResult() {
      Attachment result = Attachment.create(hostedService, deployment, virtualMachine);
      hostedService = deployment = virtualMachine = null; // handler could be called in a loop.
      return result;
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("HostedServiceName")) {
         hostedService = currentOrNull(currentText);
      } else if (qName.equals("DeploymentName")) {
         deployment = currentOrNull(currentText);
      } else if (qName.equals("RoleName")) {
         virtualMachine = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
