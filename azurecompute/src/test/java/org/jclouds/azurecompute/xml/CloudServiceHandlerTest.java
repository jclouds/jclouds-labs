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

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Collections;

import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.CloudService.Status;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "HostedServiceHandlerTest")
public class CloudServiceHandlerTest extends BaseHandlerTest {
   private static final DateService DATE_SERVICE = new SimpleDateFormatDateService();

   public void test() {
      InputStream is = getClass().getResourceAsStream("/hostedservice.xml");
      CloudService result = factory.create(new CloudServiceHandler(DATE_SERVICE)).parse(is);

      assertEquals(result, expected());
   }

   public static CloudService expected() {
      return CloudService.create( //
            "neotys", // name
            "West Europe", // location
            null, // affinityGroup
            "neotys", // label
            "Implicitly created cloud service2012-08-06 14:55", // description
            Status.CREATED, // status
            DATE_SERVICE.iso8601SecondsDateParse("2012-08-06T14:55:17Z"), // created
            DATE_SERVICE.iso8601SecondsDateParse("2012-08-06T15:50:34Z"), // lastModified
            Collections.<String, String>emptyMap() // extendedProperties
      );
   }
}
