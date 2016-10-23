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
import org.jclouds.azurecompute.domain.Profile;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758249.aspx">Response body description</a>
 */
public final class ListProfilesHandler extends
        ParseSax.HandlerForGeneratedRequestWithResult<List<Profile>> {

   private boolean inProfile;

   private final ProfileHandler profileHandler;

   private final Builder<Profile> profiles = ImmutableList.builder();

   @Inject
   ListProfilesHandler(ProfileHandler profileHandler) {
      this.profileHandler = profileHandler;
   }

   @Override
   public List<Profile> getResult() {
      return profiles.build();
   }

   @Override
   public void startElement(final String url, final String name, final String qName, final Attributes attributes) {
      if (qName.equals("Profile")) {
         inProfile = true;
      }

      if (inProfile) {
         profileHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(final String uri, final String name, final String qName) {
      if (qName.equals("Profile")) {
         inProfile = false;
         profiles.add(profileHandler.getResult());
      } else if (inProfile) {
         profileHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(final char[] ch, final int start, final int length) {
      if (inProfile) {
         profileHandler.characters(ch, start, length);
      }
   }
}
