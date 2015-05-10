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
package org.jclouds.jdbc;

import com.google.common.collect.ImmutableList;
import org.jclouds.jdbc.service.JdbcService;
import org.jclouds.jdbc.util.JdbcInputStream;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

@Test(groups = "unit", testName = "JdbcInputStreamTest")
public class JdbcInputStreamTest {

   private JdbcService mockJdbcService;

   @BeforeMethod
   public void setUp() {
      mockJdbcService = createNiceMock(JdbcService.class);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidIdList() throws IOException {
      expect(mockJdbcService.findChunkById(0L)).andReturn(null);
      new JdbcInputStream(mockJdbcService, ImmutableList.<Long>builder().add(0L).build());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullList() {
      new JdbcInputStream(mockJdbcService, null);
   }

   @Test
   public void testEmptyIdList() throws IOException {
      JdbcInputStream jdbcInputStream = new JdbcInputStream(mockJdbcService,
            ImmutableList.<Long>builder().build());
      assertThat(jdbcInputStream.read()).isEqualTo(-1);
   }

}
