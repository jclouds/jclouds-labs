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
package org.jclouds.azurecompute.parse;

import java.io.InputStream;
import java.net.URI;
import org.jclouds.azurecompute.domain.HostedService;
import org.jclouds.azurecompute.domain.HostedServiceProperties;
import org.jclouds.azurecompute.xml.HostedServiceHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "GetHostedServiceTest")
public class GetHostedServiceTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/hostedservice.xml");

      HostedService expected = expected();

      HostedServiceHandler handler = injector.getInstance(HostedServiceHandler.class);
      HostedService result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public static HostedService expected() {
      return HostedService.builder()
                          .url(URI.create("https://management.core.windows.net/eb0347c3-68d4-4550-9b39-5e7e0f92f7db/services/hostedservices/neotys"))
                          .name("neotys")
                          .properties(HostedServiceProperties.builder()
                                                             .description("Implicitly created hosted service2012-08-06 14:55")
                                                             .location("West Europe")
                                                             .label("neotys")
                                                             .build())
                          .build();
   }
}
