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

import org.jclouds.azurecompute.domain.Error;
import org.jclouds.azurecompute.domain.Error.Code;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ErrorHandlerTest")
public class ErrorHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/error.xml");
      Error result = factory.create(new ErrorHandler()).parse(is);

      assertEquals(result, expected());
   }

   public static Error expected() {
      return Error.create(Code.MISSING_OR_INVALID_REQUIRED_QUERY_PARAMETER,
              "A required query parameter was not specified for this request or was specified incorrectly.");
   }
}
