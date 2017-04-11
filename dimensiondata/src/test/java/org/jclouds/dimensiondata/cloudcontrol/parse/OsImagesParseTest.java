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
import com.google.common.collect.Lists;
import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.Cluster;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.Guest;
import org.jclouds.dimensiondata.cloudcontrol.domain.ImageNic;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.OsImages;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit")
public class OsImagesParseTest extends BaseDimensionDataCloudControlParseTest<OsImages> {

   @Override
   public String resource() {
      return "/osImages.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public OsImages expected() {
      final OsImage osImage = OsImage.builder().id("12ea8472-6e4e-4068-b2cb-f04ecacd3962").name("CentOS 5 64-bit")
            .description("DRaaS CentOS Release 5.9 64-bit").guest(Guest.builder().osCustomization(false)
                  .operatingSystem(
                        OperatingSystem.builder().id("CENTOS564").displayName("CENTOS5/64").family("UNIX").build())
                  .build()).cpu(CPU.builder().count(2).speed("STANDARD").coresPerSocket(1).build()).memoryGb(4)
            .nics(ImmutableList.of(ImageNic.builder().networkAdapter("E1000").key(4040).build())).disks(ImmutableList
                  .of(Disk.builder().id("98299851-37a3-4ebe-9cf1-090da9ae42a0").scsiId(0).sizeGb(20).speed("STANDARD")
                        .build())).softwareLabels(Lists.<String>newArrayList()).osImageKey("T-CENT-5-64-2-4-10")
            .createTime(parseDate("2016-06-09T17:36:31.000Z")).datacenterId("NA1")
            .cluster(Cluster.builder().id("NA12-01").name("my cluster name").build()).build();
      assertEquals(osImage.type, OsImage.TYPE, "OsImage type is not OS_IMAGE");
      List<OsImage> osImages = ImmutableList.of(osImage);
      return new OsImages(osImages, 1, 2, 2, 250);
   }
}
