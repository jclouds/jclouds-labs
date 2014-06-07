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
package org.jclouds.cloudsigma2.functions;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.inject.Guice;
import org.jclouds.cloudsigma2.domain.ProfileInfo;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

@Test(groups = "unit")
public class ProfileInfoToJsonTest {

   private static final ProfileInfoToJson PROFILE_INFO_TO_JSON = Guice.createInjector().getInstance(ProfileInfoToJson.class);

   private ProfileInfo input;
   private JsonObject expected;

   @BeforeMethod
   public void setUp() throws Exception {
      Map<String, String> meta = Maps.newHashMap();
      meta.put("description", "profile info");

      input = new ProfileInfo.Builder()
            .address("test_address")
            .isApiHttpsOnly(false)
            .autotopupAmount("0E-16")
            .autotopupThreshold("0E-16")
            .bankReference("jdoe123")
            .company("Newly Set Company Name")
            .country("GB")
            .currency("USD")
            .email("user@example.com")
            .firstName("John")
            .hasAutotopup(false)
            .invoicing(true)
            .isKeyAuth(false)
            .language("en-au")
            .lastName("Doe")
            .isMailingListEnabled(true)
            .meta(meta)
            .myNotes("test notes")
            .nickname("test nickname")
            .phone("123456789")
            .postcode("12345")
            .reseller("test reseller")
            .signupTime(new SimpleDateFormatDateService().iso8601SecondsDateParse("2013-05-28T11:57:01+00:00"))
            .state("REGULAR")
            .taxRate(3.14)
            .taxName("test tax_name")
            .title("test title")
            .town("test town")
            .uuid("6f670b3c-a2e6-433f-aeab-b976b1cdaf03")
            .vat("test vat")
            .build();

      expected = new JsonObject();
      expected.addProperty("address", "test_address");
      expected.addProperty("bank_reference", "jdoe123");
      expected.addProperty("company", "Newly Set Company Name");
      expected.addProperty("country", "GB");
      expected.addProperty("email", "user@example.com");
      expected.addProperty("first_name", "John");
      expected.addProperty("last_name", "Doe");

      JsonObject metaObject = new JsonObject();
      metaObject.addProperty("description", "profile info");

      expected.add("meta", metaObject);
      expected.addProperty("my_notes", "test notes");
      expected.addProperty("nickname", "test nickname");
      expected.addProperty("phone", "123456789");
      expected.addProperty("postcode", "12345");
      expected.addProperty("title", "test title");
      expected.addProperty("town", "test town");
   }

   public void test() {
      Assert.assertEquals(PROFILE_INFO_TO_JSON.apply(input), expected);
   }
}
