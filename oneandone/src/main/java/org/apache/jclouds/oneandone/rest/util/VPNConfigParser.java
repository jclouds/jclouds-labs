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
package org.apache.jclouds.oneandone.rest.util;

import com.google.common.base.Function;
import static com.google.common.io.BaseEncoding.base64;
import com.google.inject.TypeLiteral;
import java.io.ByteArrayInputStream;
import java.util.zip.ZipInputStream;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.jclouds.oneandone.rest.domain.VPNConfig;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

@Singleton
public class VPNConfigParser extends ParseJson<VPNConfig> {

   @Inject
   VPNConfigParser(Json json) {
      super(json, TypeLiteral.get(VPNConfig.class));
   }

   public static class ToZipStream implements Function<VPNConfig, ZipInputStream> {

      @Override
      public ZipInputStream apply(VPNConfig input) {
         byte[] decoded = base64().decode(input.content());
         ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(decoded));
         return zipStream;
      }
   };
}
