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
package org.jclouds.shipyard.parse;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.shipyard.domain.engines.EngineInfo;
import org.jclouds.shipyard.domain.engines.EngineSettingsInfo;
import org.jclouds.shipyard.internal.BaseShipyardParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class EnginesParseTest extends BaseShipyardParseTest<List<EngineInfo>> {

   @Override
   public String resource() {
      return "/engines.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public List<EngineInfo> expected() {
      return ImmutableList.of(
              EngineInfo.create("e2059d20-e9df-44f3-8a9b-1bf2321b4eae", 
                    EngineSettingsInfo.create("sdrelnx150", 
                          "http://sdrelnx150:2375", 
                          8, 
                          8096, 
                          ImmutableList.<String>of("sdrelnx150")))
      );
   }
}
