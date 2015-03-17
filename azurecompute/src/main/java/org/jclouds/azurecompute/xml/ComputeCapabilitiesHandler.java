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

import com.google.common.collect.Lists;
import java.util.List;
import org.jclouds.azurecompute.domain.ComputeCapabilities;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ComputeCapabilitiesHandler
        extends ParseSax.HandlerForGeneratedRequestWithResult<ComputeCapabilities> {

   private final StringBuilder currentText = new StringBuilder();

   private final List<RoleSize.Type> virtualMachineRoleSizes = Lists.newArrayList();

   private final List<RoleSize.Type> webWorkerRoleSizes = Lists.newArrayList();

   private boolean inVirtualMachineRoleSizes = false;

   private boolean inWebWorkerRoleSizes = false;

   @Override
   public ComputeCapabilities getResult() {
      final ComputeCapabilities result = ComputeCapabilities.create(virtualMachineRoleSizes, webWorkerRoleSizes);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      virtualMachineRoleSizes.clear();
      webWorkerRoleSizes.clear();
   }

   @Override
   public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
           throws SAXException {

      if ("WebWorkerRoleSizes".equals(qName)) {
         inWebWorkerRoleSizes = true;
      } else if ("VirtualMachinesRoleSizes".equals(qName)) {
         inVirtualMachineRoleSizes = true;
      }
   }

   @Override
   public void endElement(final String ignoredUri, final String ignoredName, final String qName) {
      if ("RoleSize".equals(qName)) {
         final RoleSize.Type roleSizeType = RoleSize.Type.fromString(currentOrNull(currentText));
         if (inVirtualMachineRoleSizes) {
            virtualMachineRoleSizes.add(roleSizeType);
         } else if (inWebWorkerRoleSizes) {
            webWorkerRoleSizes.add(roleSizeType);
         }
      } else if ("WebWorkerRoleSizes".equals(qName)) {
         inWebWorkerRoleSizes = false;
      } else if ("VirtualMachinesRoleSizes".equals(qName)) {
         inVirtualMachineRoleSizes = false;
      }

      currentText.setLength(0);
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      currentText.append(ch, start, length);
   }
}
