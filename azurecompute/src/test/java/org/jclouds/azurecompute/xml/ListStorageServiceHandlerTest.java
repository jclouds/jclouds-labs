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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.assertj.core.util.Lists;
import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ListStorageServiceHandlerTest")
public class ListStorageServiceHandlerTest extends BaseHandlerTest {

   private static final DateService DATE_SERVICE = new SimpleDateFormatDateService();

   public void list() throws MalformedURLException {
      final InputStream input = getClass().getResourceAsStream("/storageservices.xml");
      final List<StorageService> result = factory.create(
              new ListStorageServicesHandler(new StorageServiceHandler(DATE_SERVICE))).
              parse(input);
      assertEquals(result, expected());
   }

   public static List<StorageService> expected() throws MalformedURLException {
      final StorageService.StorageServiceProperties props = StorageService.StorageServiceProperties.create(
              null,
              null,
              "West Europe",
              "serviceName",
              StorageService.Status.Created,
              ImmutableList.of(
                      new URL("https://serviceName.blob.core.windows.net/"),
                      new URL("https://serviceName.queue.core.windows.net/"),
                      new URL("https://serviceName.table.core.windows.net/")),
              "West Europe",
              StorageService.RegionStatus.Available,
              null,
              null,
              null,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2015-03-30T10:15:00Z"),
              Lists.<String>emptyList(),
              Lists.<URL>emptyList(),
              StorageService.AccountType.Standard_LRS);
      final Map<String, String> extProps = ImmutableMap.of(
              "ResourceGroup", "Default-Storage-WestEurope",
              "ResourceLocation", "West Europe");

      return ImmutableList.of(StorageService.create(
              new URL("https://management.core.windows.net/subscriptionid/services/storageservices/serviceName"),
              "serviceName",
              props,
              extProps,
              null));
   }
}
