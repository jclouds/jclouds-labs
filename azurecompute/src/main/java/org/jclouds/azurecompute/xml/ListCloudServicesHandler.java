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

import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460781">Response body description</a>
 */
public final class ListCloudServicesHandler extends ParseSax.HandlerForGeneratedRequestWithResult<List<CloudService>> {
   private boolean inHostedService;
   private final CloudServiceHandler cloudServiceHandler;
   private final Builder<CloudService> hostedServices = ImmutableList.builder();

   @Inject ListCloudServicesHandler(CloudServiceHandler cloudServiceHandler) {
      this.cloudServiceHandler = cloudServiceHandler;
   }

   @Override public List<CloudService> getResult() {
      return hostedServices.build();
   }

   @Override public void startElement(String url, String name, String qName, Attributes attributes) {
      if (qName.equals("HostedService")) {
         inHostedService = true;
      }
      if (inHostedService) {
         cloudServiceHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override public void endElement(String uri, String name, String qName) {
      if (qName.equals("HostedService")) {
         inHostedService = false;
         hostedServices.add(cloudServiceHandler.getResult());
      } else if (inHostedService) {
         cloudServiceHandler.endElement(uri, name, qName);
      }
   }

   @Override public void characters(char ch[], int start, int length) {
      if (inHostedService) {
         cloudServiceHandler.characters(ch, start, length);
      }
   }
}
