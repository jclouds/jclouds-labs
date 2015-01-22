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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Inject;
import org.jclouds.azurecompute.domain.DataVirtualHardDisk;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import java.util.List;

public final class ListDataVirtualHardDisksHandler
      extends ParseSax.HandlerForGeneratedRequestWithResult<List<DataVirtualHardDisk>> {
   private boolean inDataVHD;
   private final DataVirtualHardDiskHandler dataVirtualHardDiskHandler;
   private final Builder<DataVirtualHardDisk> VHDs = ImmutableList.builder();

   @Inject ListDataVirtualHardDisksHandler(DataVirtualHardDiskHandler dataVirtualHardDiskHandler) {
      this.dataVirtualHardDiskHandler = dataVirtualHardDiskHandler;
   }

   @Override
   public List<DataVirtualHardDisk> getResult() {
      return VHDs.build();
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (qName.equals("DataVirtualHardDisk")) {
         inDataVHD = true;
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("DataVirtualHardDisk")) {
         inDataVHD = false;
         VHDs.add(dataVirtualHardDiskHandler.getResult());
      } else if (inDataVHD) {
         dataVirtualHardDiskHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inDataVHD) {
         dataVirtualHardDiskHandler.characters(ch, start, length);
      }
   }
}
