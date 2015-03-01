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
package org.jclouds.azurecompute.xml;

import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import org.jclouds.azurecompute.domain.DataVirtualHardDisk;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.VMImage;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Test(groups = "unit", testName = "ListVMImagesHandlerTest")
public class ListVMImagesHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/vmimages.xml");
      List<VMImage> result = factory.create(new ListVMImagesHandler(new VMImageHandler(
            new DataVirtualHardDiskHandler(),
            new OSConfigHandler()
      ))).parse(is);

      assertEquals(result, expected());
   }

   public static List<VMImage> expected() {
      DateService dateService = new SimpleDateFormatDateService();
      return ImmutableList.of(
            VMImage.create("1acf693f34c74e86a50be61cb631ddfe__ClouderaGolden-202406-699696",
                  "CDH 5.1 Evaluation",
                  "Public",
                  "Single click deployment of CDH 5.1 Evaluation for MR, HDFS and HIVE",
                  OSDiskConfig(),
                  dataDiskConfig(),
                  null,
                  null,
                  null,
                  "East Asia;Southeast Asia;Australia East;Australia Southeast;Brazil South;North Europe",
                  null,
                  dateService.iso8601DateOrSecondsDateParse("2014-07-05T14:55:17Z"),
                  dateService.iso8601DateOrSecondsDateParse("2014-09-06T22:58:11Z"),
                  null,
                  "CDH 5.1 Evaluation",
                  null,
                  false,
                  "http://www.gnu.org/copyleft/gpl.html",
                  null,
                  null,
                  URI.create("http://www.cloudera.com/content/cloudera/en/privacy-policy.html"),
                  dateService.iso8601DateOrSecondsDateParse("2012-07-05T14:55:17Z")
            )
      );
   }

   public static VMImage.OSDiskConfiguration OSDiskConfig() {
      return VMImage.OSDiskConfiguration.create("ClouderaGolden-202406-699696-os-2014-10-06",
            VMImage.OSDiskConfiguration.Caching.READ_WRITE,
            VMImage.OSDiskConfiguration.OSState.SPECIALIZED,
            OSImage.Type.LINUX,
            null,
            30,
            null);
   }

   public static List<DataVirtualHardDisk> dataDiskConfig() {
      return ImmutableList.of(
            DataVirtualHardDisk.create(
                  DataVirtualHardDisk.Caching.READ_ONLY,
                  "testimage1-testimage1-0-20120817095145",
                  10,
                  30,
                  URI.create("http://blobs/disks/neotysss/MSFT__Win2K8R2SP1-ABCD-en-us-30GB.vhd"),
                  "Standard"
            ),
            DataVirtualHardDisk.create(
                  DataVirtualHardDisk.Caching.READ_WRITE,
                  "testimage2-testimage2-0-20120817095145",
                  20,
                  30,
                  URI.create("http://blobs/disks/neotysss/MSFT__Win2K8R2SP1-ABCD-en-us-30GB.vhd"),
                  "Standard"
            )
      );
   }
}
