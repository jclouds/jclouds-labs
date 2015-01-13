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

import java.net.URI;

import org.jclouds.azurecompute.domain.Disk;
import org.jclouds.azurecompute.domain.Disk.Attachment;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157176" >api</a>
 */
final class DiskHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Disk> {
   private String name;
   private String location;
   private String affinityGroup;
   private String description;
   private OSImage.Type os;
   private URI mediaLink;
   private Integer logicalSizeInGB;
   private Attachment attachedTo;
   private String sourceImage;

   private boolean inAttachment;
   private final AttachmentHandler attachmentHandler = new AttachmentHandler();
   private final StringBuilder currentText = new StringBuilder();

   @Override public Disk getResult() {
      Disk result = Disk.create(name, location, affinityGroup, description, os, mediaLink, logicalSizeInGB, attachedTo,
            sourceImage);
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      name = location = affinityGroup = description = sourceImage = null;
      os = null;
      mediaLink = null;
      logicalSizeInGB = null;
      attachedTo = null;
   }

   @Override public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (qName.equals("AttachedTo")) {
         inAttachment = true;
      }
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("AttachedTo")) {
         attachedTo = attachmentHandler.getResult();
         inAttachment = false;
      } else if (inAttachment) {
         attachmentHandler.endElement(ignoredUri, ignoredName, qName);
      } else if (qName.equals("OS")) {
         String osText = currentOrNull(currentText);
         if (osText != null) {
            os = OSImage.Type.valueOf(osText.toUpperCase());
         }
      } else if (qName.equals("Name")) {
         name = currentOrNull(currentText);
      } else if (qName.equals("LogicalDiskSizeInGB")) {
         String gb = currentOrNull(currentText);
         if (gb != null) {
            logicalSizeInGB = Integer.parseInt(gb);
         }
      } else if (qName.equals("Description")) {
         description = currentOrNull(currentText);
      } else if (qName.equals("Location")) {
         location = currentOrNull(currentText);
      } else if (qName.equals("AffinityGroup")) {
         affinityGroup = currentOrNull(currentText);
      } else if (qName.equals("MediaLink")) {
         String link = currentOrNull(currentText);
         if (link != null) {
            mediaLink = URI.create(link);
         }
      } else if (qName.equals("SourceImageName")) {
         sourceImage = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      if (inAttachment) {
         attachmentHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
