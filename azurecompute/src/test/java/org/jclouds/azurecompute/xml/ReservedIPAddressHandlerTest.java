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
import org.jclouds.azurecompute.domain.ReservedIPAddress;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ReservedIPAddressHandlerTest")
public class ReservedIPAddressHandlerTest extends BaseHandlerTest {

   public void test() {
      final InputStream is = getClass().getResourceAsStream("/reservedipaddress.xml");
      final ReservedIPAddress result = factory.create(new ReservedIPAddressHandler()).parse(is);
      assertEquals(result, expected());
   }

   public static ReservedIPAddress expected() {
      return ReservedIPAddress.create(
              "jclouds_ip1",
              "23.101.78.155",
              "582c04d0-b169-4791-a706-f2b2bc36a742",
              null,
              ReservedIPAddress.State.CREATED,
              true,
              "jclouds_s",
              "jclouds_d",
              "West Europe");
   }
}
