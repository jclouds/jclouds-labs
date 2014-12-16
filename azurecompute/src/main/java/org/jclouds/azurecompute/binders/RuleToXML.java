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
package org.jclouds.azurecompute.binders;

import static com.google.common.base.Throwables.propagate;

import org.jclouds.azurecompute.domain.Rule;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.jamesmurty.utils.XMLBuilder;

public final class RuleToXML implements Binder {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      Rule rule = Rule.class.cast(input);
      try {
         String xml = XMLBuilder.create("Rule", "http://schemas.microsoft.com/windowsazure")
                         .e("Type").t(rule.type()).up()
                         .e("Priority").t(rule.priority()).up()
                         .e("Action").t(rule.action()).up()
                         .e("SourceAddressPrefix").t(rule.sourceAddressPrefix()).up()
                         .e("SourcePortRange").t(rule.sourcePortRange()).up()
                         .e("DestinationAddressPrefix").t(rule.destinationAddressPrefix()).up()
                         .e("DestinationPortRange").t(rule.destinationPortRange()).up()
                         .e("Protocol").t(rule.protocol()).up().asString();
         return (R) request.toBuilder().payload(xml).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }
}
