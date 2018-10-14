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
import org.jclouds.azurecompute.domain.VMImage;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.List;

public final class ListVMImagesHandler extends ParseSax.HandlerForGeneratedRequestWithResult<List<VMImage>> {
   private boolean inVMImage;
   private final VMImageHandler vmImageHandler;
   private final Builder<VMImage> images = ImmutableList.builder();

   @Inject ListVMImagesHandler(VMImageHandler vmImageHandler) {
      this.vmImageHandler = vmImageHandler;
   }

   @Override
   public List<VMImage> getResult() {
      return images.build();
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("VMImage")) {
         inVMImage = true;
      }
      if (inVMImage) {
         vmImageHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("VMImage")) {
         inVMImage = false;
         images.add(vmImageHandler.getResult());
      }
      if (inVMImage) {
         vmImageHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (inVMImage) {
         vmImageHandler.characters(ch, start, length);
      }
   }
}
