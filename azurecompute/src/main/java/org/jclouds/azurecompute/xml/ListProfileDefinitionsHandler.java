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

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import org.jclouds.azurecompute.domain.ProfileDefinition;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758252.aspx">Response body description</a>
 */
public final class ListProfileDefinitionsHandler extends
        ParseSax.HandlerForGeneratedRequestWithResult<List<ProfileDefinition>> {

   private boolean inDefinition;

   private final ProfileDefinitionHandler profileDefinitionHandler;

   private final Builder<ProfileDefinition> definitions = ImmutableList.builder();

   @Inject
   ListProfileDefinitionsHandler(ProfileDefinitionHandler profileDefinitionHandler) {
      this.profileDefinitionHandler = profileDefinitionHandler;
   }

   @Override
   public List<ProfileDefinition> getResult() {
      return definitions.build();
   }

   @Override
   public void startElement(final String url, final String name, final String qName, final Attributes attributes) {
      if (qName.equals("Definition")) {
         inDefinition = true;
      }

      if (inDefinition) {
         profileDefinitionHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(final String uri, final String name, final String qName) {
      if (qName.equals("Definition")) {
         inDefinition = false;
         definitions.add(profileDefinitionHandler.getResult());
      } else if (inDefinition) {
         profileDefinitionHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(final char[] ch, final int start, final int length) {
      if (inDefinition) {
         profileDefinitionHandler.characters(ch, start, length);
      }
   }
}
