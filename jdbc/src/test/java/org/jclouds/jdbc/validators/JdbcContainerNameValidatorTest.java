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

import org.jclouds.jdbc.predicates.validators.JdbcContainerNameValidator;
import org.testng.annotations.Test;


/**
 * Test class for {@link JdbcContainerNameValidator } class
 */
@Test(groups = "unit", testName = "jdbc.JdbcContainerNameValidatorTest")
public class JdbcContainerNameValidatorTest {

    private static final JdbcContainerNameValidator validator = new JdbcContainerNameValidator();

    @Test
    public void testNamesValidity() {
        validator.validate("all.img");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyName() {
        validator.validate("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullName() {
        validator.validate(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidStartingCharacterInName() {
        validator.validate("/is/not/ok");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidEndingCharacterInName() {
        validator.validate("is/not/ok/");
    }

}
