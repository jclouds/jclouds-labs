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

import org.jclouds.azurecompute.domain.DataVirtualHardDisk;
import org.jclouds.http.functions.ParseSax;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.net.URI;

import static org.jclouds.util.SaxUtils.currentOrNull;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/jj157193.aspx#DataVirtualHardDisks" >api</a>
 */
final class DataVirtualHardDiskHandler extends ParseSax.HandlerForGeneratedRequestWithResult<DataVirtualHardDisk> {

   private DataVirtualHardDisk.Caching hostCaching;
   private String diskName;
   private Integer lun;
   private Integer logicalDiskSizeInGB;
   private URI mediaLink;
   private String ioType;

   private final StringBuilder currentText = new StringBuilder();

   @Override
   public DataVirtualHardDisk getResult() {
      DataVirtualHardDisk result = DataVirtualHardDisk
            .create(hostCaching, diskName, lun, logicalDiskSizeInGB, mediaLink, ioType);
      return result;
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {

      if (qName.equals("HostCaching")) {
         String hostCachingText = currentOrNull(currentText);
         if (hostCachingText != null) {
            hostCaching = parseHostCache(hostCachingText);
         }
      } else if (qName.equals("DiskName")) {
         diskName = currentOrNull(currentText);
      } else if (qName.equals("Lun")) {
         String lunText = currentOrNull(currentText);
         if (lunText != null) {
            lun = Integer.parseInt(lunText);
         }
      } else if (qName.equals("LogicalDiskSizeInGB")) {
         String gb = currentOrNull(currentText);
         if (gb != null) {
            logicalDiskSizeInGB = Integer.parseInt(gb);
         }
      } else if (qName.equals("MediaLink")) {
         String link = currentOrNull(currentText);
         if (link != null) {
            mediaLink = URI.create(link);
         }
      } else if (qName.equals("IOType")) {
         ioType = currentOrNull(currentText);
      }
      currentText.setLength(0);
   }

   private static DataVirtualHardDisk.Caching parseHostCache(String hostCaching) {
      try {
         return DataVirtualHardDisk.Caching.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, hostCaching));
      } catch (IllegalArgumentException e) {
         return DataVirtualHardDisk.Caching.NONE;
      }
   }

   @Override public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
