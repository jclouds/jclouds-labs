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
import java.util.List;

import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import org.jclouds.azurecompute.domain.ReservedIPAddress;

@Test(groups = "unit", testName = "ListReservedIPAddressHandlerTest")
public class ListReservedIPAddressHandlerTest extends BaseHandlerTest {

   public void test() {
      final InputStream is = getClass().getResourceAsStream("/listreservedipaddress.xml");
      final ListReservedIPAddressHandler handler
              = new ListReservedIPAddressHandler(new ReservedIPAddressHandler());
      final List<ReservedIPAddress> result = factory.create(handler).parse(is);

      assertEquals(result, expected());
   }

   public static List<ReservedIPAddress> expected() {
      return ImmutableList.of(
              ReservedIPAddress.create(
                      "jclouds_ip1",
                      "23.101.78.155",
                      "582c04d0-b169-4791-a706-f2b2bc36a742",
                      null,
                      ReservedIPAddress.State.CREATED,
                      true,
                      "jclouds_s",
                      "jclouds_d",
                      "West Europe"),
              ReservedIPAddress.create(
                      "jclouds_ip2",
                      "23.101.69.44",
                      "4e00454a-351a-4dae-aabd-c933b60e967c",
                      "jclouds ip2 label",
                      ReservedIPAddress.State.CREATED,
                      null,
                      null,
                      null,
                      "West Europe"));
   }
}
