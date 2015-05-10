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
package org.jclouds.jdbc.validators;

import org.jclouds.jdbc.predicates.validators.JdbcBlobKeyValidator;
import org.testng.annotations.Test;


/**
 * Test class for {@link JdbcBlobKeyValidator } class
 */
@Test(groups = "unit", testName = "jdbc.JdbcBlobKeyValidatorTest")
public class JdbcBlobKeyValidatorTest {

   private static final JdbcBlobKeyValidator validator = new JdbcBlobKeyValidator();

   @Test
   public void testNamesValidity() {
      validator.validate("all.img");
      validator.validate("all/is/ok");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testEmptyName() {
      validator.validate("");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidName() {
      validator.validate("/is/not/ok");
   }

}
