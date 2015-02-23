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

import org.jclouds.shipyard.domain.images.ImageInfo;
import org.jclouds.shipyard.internal.BaseShipyardParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class ImagesParseTest extends BaseShipyardParseTest<List<ImageInfo>> {

   @Override
   public String resource() {
      return "/images.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public List<ImageInfo> expected() {
      return ImmutableList.of(
              ImageInfo.create(1416370366, 
                    "3f0d936caee4777872d6ad8dfae0077b6857d86f0232a240a95e748fb1c981f1", 
                    "5cd3a141e0cc8523bf5d76b9187124bb9d43b874da2656abf1f417e4d4858643", 
                    ImmutableList.<String>of("nkatsaros/atlassian-stash:3.4"), 
                    6233, 
                    480107370, 
                    null)
      );
   }
}
