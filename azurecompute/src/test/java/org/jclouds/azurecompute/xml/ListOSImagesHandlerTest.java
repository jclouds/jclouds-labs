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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ListImagesHandlerTest")
public class ListOSImagesHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/images.xml");
      List<OSImage> result = factory.create(new ListOSImagesHandler(new OSImageHandler())).parse(is);

      assertEquals(result, expected());
   }

   public static List<OSImage> expected() {
      return ImmutableList.of( //
            OSImage.create( //
                  "CANONICAL__Canonical-Ubuntu-12-04-amd64-server-20120528.1.3-en-us-30GB.vhd", // name
                  null, // locations
                  null, // affinityGroup
                  "Ubuntu Server 12.04 LTS", // label
                  "Ubuntu Server 12.04 LTS amd64 20120528 Cloud Image", //description
                  "Canonical", // category
                  OSImage.Type.LINUX, // os
                  "Canonical", //publisherName
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Arrays.asList("http://www.ubuntu.com/project/about-ubuntu/licensing") // eula
            ),
            OSImage.create( //
                  "MSFT__Win2K8R2SP1-120612-1520-121206-01-en-us-30GB.vhd", // name
                  "North Europe", // locations
                   null, // affinityGroup
                  "Windows Server 2008 R2 SP1, June 2012", // label
                  "Windows Server 2008 R2 is a multi-purpose server.", //description
                  "Microsoft", // category
                  OSImage.Type.WINDOWS, // os
                  "Microsoft", //publisherName
                  URI.create("http://blobs/disks/mydeployment/MSFT__Win2K8R2SP1-120612-1520-121206-01-en-us-30GB.vhd"),
                  // mediaLink
                  30, // logicalSizeInGB
                  Collections.<String>emptyList() // eula
            ),
            OSImage.create( //
                  "MSFT__Sql-Server-11EVAL-11.0.2215.0-05152012-en-us-30GB.vhd", // name
                  null, // locations
                  null, // affinityGroup
                  "Microsoft SQL Server 2012 Evaluation Edition", // label
                  "SQL Server 2012 Evaluation Edition (64-bit).", //description
                  "Microsoft", // category
                  OSImage.Type.WINDOWS, // os
                  "Microsoft", //publisherName
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Arrays.asList("http://go.microsoft.com/fwlink/?LinkID=251820",
                        "http://go.microsoft.com/fwlink/?LinkID=131004") // eula
            ),
            OSImage.create( //
                  "MSFT__Win2K12RC-Datacenter-201207.02-en.us-30GB.vhd", // name
                  null, // locations
                  null, // affinityGroup
                  "Windows Server 2012 Release Candidate, July 2012", // label
                  "Windows Server 2012 incorporates Microsoft's experience building.", //description
                  "Microsoft", // category
                  OSImage.Type.WINDOWS, // os
                  "Microsoft", //publisherName
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Collections.<String>emptyList() // eula
            ),
            OSImage.create( //
                  "MSFT__Win2K8R2SP1-Datacenter-201207.01-en.us-30GB.vhd", // name
                  null, // locations
                  null, // affinityGroup
                  "Windows Server 2008 R2 SP1, July 2012", // label
                  "Windows Server 2008 R2 is a multi-purpose server.", //description
                  "Microsoft", // category
                  OSImage.Type.WINDOWS, // os
                  "Microsoft", //publisherName
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Collections.<String>emptyList() // eula
            ),
            OSImage.create( //
                  "OpenLogic__OpenLogic-CentOS-62-20120531-en-us-30GB.vhd", // name
                  null, // locations
                  null, // affinityGroup
                  "OpenLogic CentOS 6.2", // label
                  "This distribution of Linux is based on CentOS.", //description
                  "OpenLogic", // category
                  OSImage.Type.LINUX, // os
                  "openLogic", //publisherName
                  URI.create("http://blobs/disks/mydeployment/OpenLogic__OpenLogic-CentOS-62-20120531-en-us-30GB.vhd"),
                  // mediaLink
                  30, //logicalSizeInGB
                  Arrays.asList("http://www.openlogic.com/azure/service-agreement/") // eula
            ),
            OSImage.create( //
                  "SUSE__openSUSE-12-1-20120603-en-us-30GB.vhd", // name
                  null, // locations
                  null, // affinityGroup
                  "openSUSE 12.1", // label
                  "openSUSE is a free and Linux-based operating system!", //description
                  "SUSE", // category
                  OSImage.Type.LINUX, // os
                  "SUSE", //publisherName
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Arrays.asList("http://opensuse.org/") // eula
            ),
            OSImage.create( //
                  "SUSE__SUSE-Linux-Enterprise-Server-11SP2-20120601-en-us-30GB.vhd", // name
                  null, // locations
                  null, // affinityGroup
                  "SUSE Linux Enterprise Server", // label
                  "SUSE Linux Enterprise Server is a highly reliable value.", //description
                  "SUSE", // category
                  OSImage.Type.LINUX, // os
                  "SUSE", //publisherName
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Arrays.asList("http://www.novell.com/licensing/eula/") // eula
            ),
            OSImage.create( //
                  "0b11de9248dd4d87b18621318e037d37__RightImage-CentOS-6.4-x64-v13.4", // name
                  null, // locations
                  null, // affinityGroup
                  "RightImage-CentOS-6.4-x64-v13.4", // label
                  null, //description
                  "RightScale with Linux", // category
                  OSImage.Type.LINUX, // os
                  "RightScale with Linux",
                  null, // mediaLink
                  10, // logicalSizeInGB
                  Collections.<String>emptyList() // No EULA, as RightScale stuffed ';' into the field.
            )
      );
   }
}
