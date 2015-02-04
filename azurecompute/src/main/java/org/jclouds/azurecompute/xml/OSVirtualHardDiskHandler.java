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

import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.http.functions.ParseSax;

public class OSVirtualHardDiskHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Role.OSVirtualHardDisk> {
   private String hostCaching;
   private String diskName;
   private URI mediaLink;
   private String sourceImageName;
   private OSImage.Type os;
   private Integer lun;
   private Integer logicalDiskSizeInGB;

   private final StringBuilder currentText = new StringBuilder();

   @Override public Role.OSVirtualHardDisk getResult() {
      return Role.OSVirtualHardDisk.create(hostCaching, diskName, lun, logicalDiskSizeInGB, mediaLink, sourceImageName, os);
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("HostCaching")) {
         hostCaching = currentOrNull(currentText);
      } else if (qName.equals("DiskName")) {
         diskName = currentOrNull(currentText);
      } else if (qName.equals("MediaLink")) {
         String link = currentOrNull(currentText);
         if (link != null) {
            mediaLink = URI.create(link);
         }
      } else if (qName.equals("SourceImageName")) {
         sourceImageName = currentOrNull(currentText);
      } else if (qName.equals("OS")) {
         String osText = currentOrNull(currentText);
         if (osText != null) {
            os = OSImage.Type.valueOf(currentOrNull(currentText).toUpperCase());
         }
      }
      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
         currentText.append(ch, start, length);
   }

}
