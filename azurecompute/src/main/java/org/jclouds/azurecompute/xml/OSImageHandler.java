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

import static com.google.common.base.Strings.emptyToNull;
import static org.jclouds.util.SaxUtils.currentOrNull;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.ParseSax;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157191" >api</a>
 */
final class OSImageHandler extends ParseSax.HandlerForGeneratedRequestWithResult<OSImage> {

   private String name;
   private String location;
   private String affinityGroup;
   private String label;
   private String category;
   private String description;
   private OSImage.Type os;
   private URI mediaLink;
   private Integer logicalSizeInGB;
   private final List<String> eulas = Lists.newArrayList();
   private String imageFamily;
   private Date publishedDate;
   private String iconUri;
   private String smallIconUri;
   private URI privacyUri;
   private URI pricingDetailLink;
   private String recommendedVMSize;
   private Boolean isPremium;
   private Boolean showInGui;
   private String publisherName;

   private final StringBuilder currentText = new StringBuilder();

   @Override
   public OSImage getResult() {
      OSImage result = OSImage
            .create(name, location, affinityGroup, label, description, category, os, publisherName, mediaLink,
                  logicalSizeInGB, ImmutableList.copyOf(eulas));
      resetState(); // handler is called in a loop.
      return result;
   }

   private void resetState() {
      name = affinityGroup = label = description = category = imageFamily = iconUri = smallIconUri = recommendedVMSize
              = publisherName = null;
      os = null;
      publisherName = null;
      mediaLink = null;
      logicalSizeInGB = null;
      publishedDate = null;
      privacyUri = pricingDetailLink = null;
      isPremium = null;
      showInGui = null;
      eulas.clear();
      location = null;
   }

   @Override public void endElement(String ignoredUri, String ignoredName, String qName) {
      if (qName.equals("OS")) {
         String osText = currentOrNull(currentText);
         if (osText != null) {
            os = OSImage.Type.valueOf(currentOrNull(currentText).toUpperCase());
         }
      } else if (qName.equals("Name")) {
         name = currentOrNull(currentText);
      } else if (qName.equals("LogicalSizeInGB")) {
         String gb = currentOrNull(currentText);
         if (gb != null) {
            logicalSizeInGB = Integer.parseInt(gb);
         }
      } else if (qName.equals("Description")) {
         description = currentOrNull(currentText);
      } else if (qName.equals("Category")) {
         category = currentOrNull(currentText);
      } else if (qName.equals("Location")) {
         location = currentOrNull(currentText);
      } else if (qName.equals("AffinityGroup")) {
         affinityGroup = currentOrNull(currentText);
      } else if (qName.equals("PublisherName")) {
         publisherName = currentOrNull(currentText);
      } else if (qName.equals("MediaLink")) {
         String link = currentOrNull(currentText);
         if (link != null) {
            mediaLink = URI.create(link);
         }
      } else if (qName.equals("Eula")) {
         String eulaField = currentOrNull(currentText);
         if (eulaField != null) {
            for (String eula : Splitter.on(';').split(eulaField)) {
               if ((eula = emptyToNull(eula.trim())) != null) { // Dirty data in RightScale eulas field.
                  eulas.add(eula);
               }
            }
         }
      } else if (qName.equals("Label")) {
         label = currentOrNull(currentText);
      } else if (qName.equals("ImageFamily")) {
         imageFamily = currentOrNull(currentText);
      } else if (qName.equals("PublishedDate")) {
         String date = currentOrNull(currentText);
         if (date != null) {
            publishedDate = new SimpleDateFormatDateService().iso8601DateOrSecondsDateParse(date);
         }
      } else if (qName.equals("IconUri")) {
         iconUri = currentOrNull(currentText);
      } else if (qName.equals("SmallIconUri")) {
         smallIconUri = currentOrNull(currentText);
      } else if (qName.equals("PrivacyUri")) {
         String uri = currentOrNull(currentText);
         if (uri != null) {
            privacyUri = URI.create(uri);
         }
      } else if (qName.equals("RecommendedVMSize")) {
         recommendedVMSize = currentOrNull(currentText);
      } else if (qName.equals("IsPremium")) {
         String premium = currentOrNull(currentText);
         if (premium != null) {
            isPremium = Boolean.valueOf(premium);
         }
      } else if (qName.equals("ShowInGui")) {
         String show = currentOrNull(currentText);
         if (show != null) {
            showInGui = Boolean.valueOf(show);
         }
      } else if (qName.equals("PublisherName")) {
         publisherName = currentOrNull(currentText);
      } else if (qName.equals("PricingDetailLink")) {
         String uri = currentOrNull(currentText);
         if (uri != null) {
            pricingDetailLink = URI.create(uri);
         }
      }

      currentText.setLength(0);
   }

   @Override public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
