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

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.domain.Disk;
import org.jclouds.azurecompute.domain.Disk.Attachment;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ListDisksHandlerTest")
public class ListDisksHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/disks.xml");
      List<Disk> result = factory.create(new ListDisksHandler(new DiskHandler())).parse(is);

      assertEquals(result, expected());
   }

   public static List<Disk> expected() {
      return ImmutableList.of( //
            Disk.create( //
                  "testimage2-testimage2-0-20120817095145", // name
                  "West Europe", // location
                  null, // affinityGroup
                  null, //description
                  OSImage.Type.LINUX, // os
                  URI.create("http://blobs/vhds/testimage2-testimage2-2012-08-17.vhd"), // mediaLink
                  30, // logicalSizeInGB
                  null, // attachedTo
                  "OpenLogic__OpenLogic-CentOS-62-20120531-en-us-30GB.vhd" // sourceImage
            ), Disk.create( //
                  "neotysss-neotysss-0-20120824091357", // name
                  "West Europe", // location
                  null, // affinityGroup
                  null, //description
                  OSImage.Type.WINDOWS, // os
                  URI.create("http://blobs/disks/neotysss/MSFT__Win2K8R2SP1-ABCD-en-us-30GB.vhd"), // mediaLink
                  30, // logicalSizeInGB
                  Attachment.create("neotysss", "neotysss", "neotysss"), // attachedTo
                  "MSFT__Win2K8R2SP1-ABCD-en-us-30GB.vhd" // sourceImage
            ));
   }
}
