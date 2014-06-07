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
package org.jclouds.fujitsu.fgcp.services;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;

import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.FGCPProviderMetadata;
import org.jclouds.fujitsu.fgcp.config.FGCPHttpApiModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.Resources;
import com.google.inject.Module;

public class BaseFGCPRestApiExpectTest extends BaseRestApiExpectTest<FGCPApi> {

   /**
    * self-signed dummy cert: <br>
    * keytool -genkey -alias test-fgcp -keyalg RSA -keysize 1024 -validity 5475
    * -dname "CN=localhost" -keystore jclouds-test-fgcp.p12 -storepass
    * jcloudsjclouds -storetype pkcs12 <br>
    * openssl pkcs12 -nodes -in jclouds-test-fgcp.p12 -out jclouds-test-fgcp.pem
    */
   public BaseFGCPRestApiExpectTest() {
      provider = "fgcp";

      String cert = "/certs/jclouds-test-fgcp.pem";
      URL url = this.getClass().getResource(cert);
      assertNotNull(url, cert + " not found");

      try {
         credential = Resources.toString(url, Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   @ConfiguresHttpApi
   protected static final class TestFGCPHttpApiModule extends FGCPHttpApiModule {

      @Override
      protected Calendar provideCalendar() {
         // pick country/TZ with no DST just in case to maintain constant
         // time wherever the tests are run.
         Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-9"), Locale.JAPAN);
         cal.setTimeInMillis(1234567890);
         return cal;
      }
   }

   @Override
   protected Module createModule() {
      return new TestFGCPHttpApiModule();
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      return new FGCPProviderMetadata();
   }

   @Override
   public Payload payloadFromResource(String resource) {
      return super.payloadFromResource("/responses" + resource);
   }

   protected static HttpRequest buildGETWithQuery(String query) {
      URI uri = URI
            .create("https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint"
                  + "?Version=2012-02-18"
                  + "&"
                  + query
                  + "&Locale=en"
                  + "&AccessKeyId=R01ULTA5OjAwJjEyMzQ1Njc4OTAmMS4wJlNIQTF3aXRoUlNB"
                  + "&Signature=G2rGfLAkbq0IURQfXIWYxj3BnMGbjRk4KPnZLAze3Lt4SMMRt8lkjqKvR5Cm%2B%0AnFpDN7J6IprVCCsIrRq5BqPeXT6xtWyb6qMNds2BAr1h/JePGs0UosOh2tgPU%0AMSFlZwLVjgNyrSa2zeHA3AEHjF6H1jqcWXXqfCAD4SOHaNavk%3D");
      return HttpRequest.builder().method("GET").endpoint(uri).addHeader("Accept", "text/xml")
            .addHeader("User-Agent", "OViSS-API-CLIENT").build();
   }

   protected HttpRequest preparePOSTForAction(String action) {
      return HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint"))
            .payload(
                  payloadFromResourceWithContentType("/" + action.toLowerCase() + "-request.xml", MediaType.TEXT_XML))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "text/xml")
                        .put("User-Agent", "OViSS-API-CLIENT").build()).build();
   }
}
