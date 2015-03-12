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

import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Inject;
import org.jclouds.azurecompute.domain.AffinityGroup;
import org.xml.sax.SAXException;

public final class ListAffinityGroupsHandler
        extends ParseSax.HandlerForGeneratedRequestWithResult<List<AffinityGroup>> {

   private final Builder<AffinityGroup> affinityGroups = ImmutableList.builder();

   private final AffinityGroupHandler affinityGroupHandler;

   private boolean inAffinityGroup;

   @Inject
   ListAffinityGroupsHandler(AffinityGroupHandler affinityGroupHandler) {
      this.affinityGroupHandler = affinityGroupHandler;
   }

   @Override
   public List<AffinityGroup> getResult() {
      return affinityGroups.build();
   }

   @Override
   public void startElement(final String url, final String name, final String qName, final Attributes attributes)
           throws SAXException {

      if ("AffinityGroup".equals(qName)) {
         inAffinityGroup = true;
      } else if (inAffinityGroup) {
         affinityGroupHandler.startElement(url, qName, qName, attributes);
      }
   }

   @Override
   public void endElement(final String uri, final String name, final String qName) {
      if ("AffinityGroup".equals(qName)) {
         inAffinityGroup = false;
         affinityGroups.add(affinityGroupHandler.getResult());
      } else if (inAffinityGroup) {
         affinityGroupHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      if (inAffinityGroup) {
         affinityGroupHandler.characters(ch, start, length);
      }
   }
}
