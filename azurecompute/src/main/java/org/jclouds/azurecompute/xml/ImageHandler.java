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

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.net.URI;
import org.jclouds.azurecompute.domain.Image;
import org.jclouds.azurecompute.domain.OSType;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.SAXException;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157191" >api</a>
 */
public class ImageHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Image> {

   private StringBuilder currentText = new StringBuilder();
   private Image.Builder builder = Image.builder();

   @Override
   public Image getResult() {
      try {
         return builder.build();
      } finally {
         builder = Image.builder();
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "OS")) {
         builder.os(OSType.fromValue(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "Name")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "LogicalSizeInGB")) {
         String gb = currentOrNull(currentText);
         if (gb != null)
            builder.logicalSizeInGB(Integer.parseInt(gb));
      } else if (equalsOrSuffix(qName, "Description")) {
         builder.description(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Category")) {
         builder.category(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Location")) {
         builder.location(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "AffinityGroup")) {
         builder.affinityGroup(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "MediaLink")) {
         String link = currentOrNull(currentText);
         if (link != null)
            builder.mediaLink(URI.create(link));
      } else if (equalsOrSuffix(qName, "Eula")) {
         String eulaField = currentOrNull(currentText);
         if (eulaField != null) {
            for (String eula : Splitter.on(';').split(eulaField)) {
               if ((eula = Strings.emptyToNull(eula.trim())) != null) { // Dirty data in RightScale eula field.
                  builder.eula(eula);
               }
            }
         }
      } else if (equalsOrSuffix(qName, "Label")) {
         builder.label(currentOrNull(currentText));
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
