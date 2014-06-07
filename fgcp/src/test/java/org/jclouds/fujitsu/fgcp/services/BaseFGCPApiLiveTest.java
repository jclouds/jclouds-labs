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
package org.jclouds.fujitsu.fgcp.services;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.fujitsu.fgcp.FGCPApi;


public class BaseFGCPApiLiveTest extends BaseApiLiveTest<FGCPApi> {

   public BaseFGCPApiLiveTest() {
      provider = "fgcp";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();

      String proxy = System.getenv("http_proxy");
      if (proxy != null) {

         String[] parts = proxy.split("http://|:|@");

         overrides.setProperty(Constants.PROPERTY_PROXY_HOST,
               parts[parts.length - 2]);
         overrides.setProperty(Constants.PROPERTY_PROXY_PORT,
               parts[parts.length - 1]);

         if (parts.length >= 4) {
            overrides.setProperty(Constants.PROPERTY_PROXY_USER,
                  parts[parts.length - 4]);
            overrides.setProperty(Constants.PROPERTY_PROXY_PASSWORD,
                  parts[parts.length - 3]);
         }
         overrides.setProperty(Constants.PROPERTY_PROXY_FOR_SOCKETS, "false");
      }

      // enables peer verification using the CAs bundled with the JRE (or
      // value of javax.net.ssl.trustStore if set)
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "false");

      return overrides;
   }
}
