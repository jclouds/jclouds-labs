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
import java.util.List;
import org.jclouds.azurecompute.domain.Image;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ListImagesHandler extends ParseSax.HandlerForGeneratedRequestWithResult<List<Image>> {

   private final ImageHandler locationHandler;

   private Builder<Image> locations = ImmutableList.<Image> builder();

   private boolean inOSImage;

   @Inject
   public ListImagesHandler(ImageHandler locationHandler) {
      this.locationHandler = locationHandler;
   }

   @Override
   public List<Image> getResult() {
      return locations.build();
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (SaxUtils.equalsOrSuffix(qName, "OSImage")) {
         inOSImage = true;
      }
      if (inOSImage) {
         locationHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (qName.equals("OSImage")) {
         inOSImage = false;
         locations.add(locationHandler.getResult());
      } else if (inOSImage) {
         locationHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inOSImage) {
         locationHandler.characters(ch, start, length);
      }
   }
}
