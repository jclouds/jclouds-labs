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
package org.jclouds.dimensiondata.cloudcontrol.parse;

import com.google.common.collect.ImmutableList;
import org.jclouds.dimensiondata.cloudcontrol.domain.PublicIpBlock;
import org.jclouds.dimensiondata.cloudcontrol.domain.PublicIpBlocks;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Test(groups = "unit")
public class PublicIpBlocksParseTest extends BaseDimensionDataCloudControlParseTest<PublicIpBlocks> {

   @Override
   public String resource() {
      return "/publicIpBlocks.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public PublicIpBlocks expected() {
      List<PublicIpBlock> publicIpBlocks = ImmutableList
            .of(PublicIpBlock.builder().networkDomainId("690de302-bb80-49c6-b401-8c02bbefb945")
                  .id("9993e5fc-bdce-11e4-8c14-b8ca3a5d9ef8").createTime(parseDate("2016-03-14T11:49:33.000Z"))
                  .state(State.NORMAL).datacenterId("NA9").size(2).baseIp("168.128.6.216").build());
      return new PublicIpBlocks(publicIpBlocks, 1, 5, 5, 250);
   }
}
