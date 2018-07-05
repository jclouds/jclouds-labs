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
package org.jclouds.aliyun.ecs.functions;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(groups = "unit")
public class ArrayToCommaSeparatedStringTest {

   public void testArrayOfString() {
      String[] input = {"Cheese", "Pepperoni", "Black Olives"};
      String actual = new ArrayToCommaSeparatedString().apply(input);
      assertNotNull(actual);
      assertEquals(actual, "[\"Cheese\",\"Pepperoni\",\"Black Olives\"]");
   }

   public void testSingleArrayOfString() {
      String[] input = {"Sun"};
      String actual = new ArrayToCommaSeparatedString().apply(input);
      assertNotNull(actual);
      assertEquals(actual, "[\"Sun\"]");
   }

   public void testEmptyArrayOfString() {
      String[] input = {};
      String actual = new ArrayToCommaSeparatedString().apply(input);
      assertNotNull(actual);
      assertEquals(actual, "[]");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullInput() {
      new ArrayToCommaSeparatedString().apply(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "This function is only valid for array of Strings!")
   public void testWrongInputType() {
      new ArrayToCommaSeparatedString().apply("wrong");
   }
}
