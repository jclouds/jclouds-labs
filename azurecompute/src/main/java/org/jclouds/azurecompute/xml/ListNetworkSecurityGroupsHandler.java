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

import org.jclouds.azurecompute.domain.NetworkSecurityGroup;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

public class ListNetworkSecurityGroupsHandler extends ParseSax.HandlerForGeneratedRequestWithResult<List<NetworkSecurityGroup>> {

   private boolean inNetworkSecurityGroup;
   private final NetworkSecurityGroupHandler networkSecurityGroupHandler;
   private final ImmutableList.Builder<NetworkSecurityGroup> networkSecurityGroups = ImmutableList.builder();

   @Inject ListNetworkSecurityGroupsHandler(NetworkSecurityGroupHandler networkSecurityGroupHandler) {
      this.networkSecurityGroupHandler = networkSecurityGroupHandler;
   }

   @Override public List<NetworkSecurityGroup> getResult() {
      return networkSecurityGroups.build();
   }

   @Override public void startElement(String url, String name, String qName, Attributes attributes) {
      if (qName.equals("NetworkSecurityGroup")) {
         inNetworkSecurityGroup = true;
      }
   }

   @Override public void endElement(String uri, String name, String qName) {
      if (qName.equals("NetworkSecurityGroup")) {
         inNetworkSecurityGroup = false;
         networkSecurityGroups.add(networkSecurityGroupHandler.getResult());
      } else if (inNetworkSecurityGroup) {
         networkSecurityGroupHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inNetworkSecurityGroup) {
         networkSecurityGroupHandler.characters(ch, start, length);
      }
   }

}
