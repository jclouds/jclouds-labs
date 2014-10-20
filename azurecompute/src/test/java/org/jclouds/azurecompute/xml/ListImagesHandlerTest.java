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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jclouds.azurecompute.domain.Image;
import org.jclouds.azurecompute.domain.Image.OSType;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ListImagesHandlerTest")
public class ListImagesHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/images.xml");
      List<Image> result = factory.create(new ListImagesHandler()).parse(is);

      assertEquals(result, expected());
   }

   public static List<Image> expected() {
      return ImmutableList.of( //
            Image.create( //
                  "CANONICAL__Canonical-Ubuntu-12-04-amd64-server-20120528.1.3-en-us-30GB.vhd", // name
                  null, // location
                  null, // affinityGroup
                  "Ubuntu Server 12.04 LTS", // label
                  "Ubuntu Server 12.04 LTS amd64 20120528 Cloud Image", //description
                  "Canonical", // category
                  OSType.LINUX, // os
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Arrays.asList("http://www.ubuntu.com/project/about-ubuntu/licensing") // eula
            ),
            Image.create( //
                  "MSFT__Win2K8R2SP1-120612-1520-121206-01-en-us-30GB.vhd", // name
                  null, // location
                  null, // affinityGroup
                  "Windows Server 2008 R2 SP1, June 2012", // label
                  "Windows Server 2008 R2 is a multi-purpose server.", //description
                  "Microsoft", // category
                  OSType.WINDOWS, // os
                  URI.create("http://blobs/disks/mydeployment/MSFT__Win2K8R2SP1-120612-1520-121206-01-en-us-30GB.vhd"), // mediaLink
                  30, // logicalSizeInGB
                  Collections.<String>emptyList() // eula
            ),
            Image.create( //
                  "MSFT__Sql-Server-11EVAL-11.0.2215.0-05152012-en-us-30GB.vhd", // name
                  null, // location
                  null, // affinityGroup
                  "Microsoft SQL Server 2012 Evaluation Edition", // label
                  "SQL Server 2012 Evaluation Edition (64-bit).", //description
                  "Microsoft", // category
                  OSType.WINDOWS, // os
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Arrays.asList("http://go.microsoft.com/fwlink/?LinkID=251820",
                                "http://go.microsoft.com/fwlink/?LinkID=131004") // eula
            ),
            Image.create( //
                  "MSFT__Win2K12RC-Datacenter-201207.02-en.us-30GB.vhd", // name
                  null, // location
                  null, // affinityGroup
                  "Windows Server 2012 Release Candidate, July 2012", // label
                  "Windows Server 2012 incorporates Microsoft's experience building.", //description
                  "Microsoft", // category
                  OSType.WINDOWS, // os
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Collections.<String>emptyList() // eula
            ),
            Image.create( //
                  "MSFT__Win2K8R2SP1-Datacenter-201207.01-en.us-30GB.vhd", // name
                  null, // location
                  null, // affinityGroup
                  "Windows Server 2008 R2 SP1, July 2012", // label
                  "Windows Server 2008 R2 is a multi-purpose server.", //description
                  "Microsoft", // category
                  OSType.WINDOWS, // os
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Collections.<String>emptyList() // eula
            ),
            Image.create( //
                  "OpenLogic__OpenLogic-CentOS-62-20120531-en-us-30GB.vhd", // name
                  null, // location
                  null, // affinityGroup
                  "OpenLogic CentOS 6.2", // label
                  "This distribution of Linux is based on CentOS.", //description
                  "OpenLogic", // category
                  OSType.LINUX, // os
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Arrays.asList("http://www.openlogic.com/azure/service-agreement/") // eula
            ),
            Image.create( //
                  "SUSE__openSUSE-12-1-20120603-en-us-30GB.vhd", // name
                  null, // location
                  null, // affinityGroup
                  "openSUSE 12.1", // label
                  "openSUSE is a free and Linux-based operating system!", //description
                  "SUSE", // category
                  OSType.LINUX, // os
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Arrays.asList("http://opensuse.org/") // eula
            ),
            Image.create( //
                  "SUSE__SUSE-Linux-Enterprise-Server-11SP2-20120601-en-us-30GB.vhd", // name
                  null, // location
                  null, // affinityGroup
                  "SUSE Linux Enterprise Server", // label
                  "SUSE Linux Enterprise Server is a highly reliable value.", //description
                  "SUSE", // category
                  OSType.LINUX, // os
                  null, // mediaLink
                  30, // logicalSizeInGB
                  Arrays.asList("http://www.novell.com/licensing/eula/") // eula
            ),
            Image.create( //
                  "0b11de9248dd4d87b18621318e037d37__RightImage-CentOS-6.4-x64-v13.4", // name
                  null, // location
                  null, // affinityGroup
                  "RightImage-CentOS-6.4-x64-v13.4", // label
                  null, //description
                  "RightScale with Linux", // category
                  OSType.LINUX, // os
                  null, // mediaLink
                  10, // logicalSizeInGB
                  Collections.<String>emptyList() // No EULA, as RightScale stuffed ';' into the field.
            )
      );
   }
}
