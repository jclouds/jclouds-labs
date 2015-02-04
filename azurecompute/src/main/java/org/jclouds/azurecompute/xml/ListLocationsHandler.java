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

import java.util.List;

import org.jclouds.azurecompute.domain.Location;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Inject;

public final class ListLocationsHandler extends ParseSax.HandlerForGeneratedRequestWithResult<List<Location>> {
   private boolean inLocation;
   private final LocationHandler locationHandler;
   private final Builder<Location> locations = ImmutableList.builder();

   @Inject ListLocationsHandler(LocationHandler locationHandler) {
      this.locationHandler = locationHandler;
   }

   @Override public List<Location> getResult() {
      return locations.build();
   }

   @Override public void startElement(String url, String name, String qName, Attributes attributes) {
      if (qName.equals("Location")) {
         inLocation = true;
      }
   }

   @Override public void endElement(String uri, String name, String qName) {
      if (qName.equals("Location")) {
         inLocation = false;
         locations.add(locationHandler.getResult());
      } else if (inLocation) {
         locationHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inLocation) {
         locationHandler.characters(ch, start, length);
      }
   }
}
