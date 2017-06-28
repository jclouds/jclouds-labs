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
package org.jclouds.dimensiondata.cloudcontrol.utils;

import org.jclouds.dimensiondata.cloudcontrol.domain.Property;
import org.jclouds.dimensiondata.cloudcontrol.domain.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@Test(groups = "unit", testName = "ResponseParseTest", singleThreaded = true)
public class ResponseParseTest {

   public void testTryFindPropertyValue() {

      List<Property> infoProperties = new ArrayList<Property>();
      infoProperties.add(Property.create("propertyName1", "propertyValue1"));
      infoProperties.add(Property.create("propertyName2", "propertyValue2"));

      Response response = Response.builder().responseCode("responseCode").error(null).message("message")
            .operation("operation").requestId("requestId").info(infoProperties).build();

      Assert.assertEquals(new ParseResponse(null, "propertyName1").tryFindInfoPropertyValue(response),
            "propertyValue1");
      Assert.assertEquals(new ParseResponse(null, "propertyName2").tryFindInfoPropertyValue(response),
            "propertyValue2");
   }

   @Test(expectedExceptions = { IllegalStateException.class })
   public void testTryFindPropertyValue_PropertyNotFound() {

      List<Property> infoProperties = new ArrayList<Property>();
      infoProperties.add(Property.create("propertyName1", "propertyValue1"));

      Response response = Response.builder().responseCode("responseCode").error(null).message("message")
            .operation("operation").requestId("requestId").info(infoProperties).build();

      new ParseResponse(null, "noProperty").tryFindInfoPropertyValue(response);
   }
}
