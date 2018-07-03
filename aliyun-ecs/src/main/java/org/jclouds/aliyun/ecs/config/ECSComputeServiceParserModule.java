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
package org.jclouds.aliyun.ecs.config;

import com.google.common.base.CharMatcher;
import com.google.gson.stream.JsonReader;
import com.google.inject.AbstractModule;
import org.jclouds.date.DateService;
import org.jclouds.json.config.GsonModule;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;

public class ECSComputeServiceParserModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(GsonModule.DateAdapter.class).to(AliyunDateAdapter.class);
   }

   /**
    * Data adapter for the date formats used by Aliyun.
    * <p>
    * Essentially this is a workaround for the Aliyun getUsage() API call returning a corrupted form of ISO-8601
    * dates, which doesn't have seconds 2018-06-20T13:39Z
    */
   public static class AliyunDateAdapter extends GsonModule.Iso8601DateAdapter {

      @Inject
      AliyunDateAdapter(DateService dateService) {
         super(dateService);
      }

      public Date read(JsonReader reader) throws IOException {
         String date = reader.nextString();
         int count = CharMatcher.is(':').countIn(date);
         if (count < 2) {
            date = date.replaceAll("Z", ":00Z");
         }
         return parseDate(date);
      }

   }

}
