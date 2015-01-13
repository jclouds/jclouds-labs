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
package org.jclouds.azurecompute.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.all.JustProvider;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "OSImageToImageTest")
public class OSImageToImageTest {
   public void testImageTransform() {
      OSImageToImage imageToImage = new OSImageToImage(new JustProvider("azurecompute", Suppliers
            .ofInstance(URI.create("foo")), ImmutableSet.<String>of()));
      // OSImage OSImage = createOSImage();
      for (OSImage OSImage : createOSImage()) {
         org.jclouds.compute.domain.Image transformed = imageToImage.apply(OSImage);
         OperatingSystem os = OSImageToImage.osFamily().apply(OSImage).build();
         assertNotNull(OSImage.label());
         assertNotNull(transformed.getId());
         assertEquals(transformed.getId(), OSImage.name());
         assertEquals(transformed.getName(), OSImage.label());
         assertEquals(transformed.getOperatingSystem().getFamily(), os.getFamily());
         assertEquals(transformed.getOperatingSystem().getVersion(), os.getVersion());
         assertEquals(transformed.getProviderId(), OSImage.name());
         Location location = transformed.getLocation();
         if (location != null) {
            assertEquals(location.getId(), OSImage.location());
         }
      }
   }

   public void testOperatingSystem() {
      ImmutableList<String> version = ImmutableList.of(
            "13.1",
            "12.04 LTS",
            "Windows Server 2008 R2 SP1, June 2012",
            "Microsoft SQL Server 2012 Evaluation Edition",
            "Windows Server 2012 Release Candidate, July 2012",
            "Windows Server 2008 R2 SP1, July 2012",
            "OpenLogic CentOS 6.2",
            "12.1",
            "Linux Enterprise Server",
            "RightImage-CentOS-6.4-x64-v13.4"
      );
      ImmutableList<OsFamily> osFamily = ImmutableList.of(
            OsFamily.SUSE,
            OsFamily.UBUNTU,
            OsFamily.WINDOWS,
            OsFamily.WINDOWS,
            OsFamily.WINDOWS,
            OsFamily.WINDOWS,
            OsFamily.CENTOS,
            OsFamily.SUSE,
            OsFamily.SUSE,
            OsFamily.CENTOS
      );

      List<OSImage> images = createOSImage();
      for (int i = 0; i < images.size(); i++) {
         OSImage OSImage = images.get(i);
         OperatingSystem os = OSImageToImage.osFamily().apply(OSImage).build();
         assertEquals(os.getFamily(), osFamily.get(i));
         assertEquals(os.getVersion(), version.get(i));
      }
   }

   private static ImmutableList<OSImage> createOSImage() {
      return ImmutableList.of(
            OSImage.create(
                  "SUSE__openSUSE-12-1-20120603-en-us-30GB.vhd", // name
                  "Central US", // location
                  null, // affinityGroup
                  "openSUSE 13.1", // label
                  "openSUSE 13.1 brings updated desktop environments and software, lot of polishing, a brand new KDE theme, "
                        + "complete systemd integration and many other features.", // description
                  "MSDN", // category
                  OSImage.Type.WINDOWS, // os
                  "SUSE", // publisherName
                  URI.create("http://example.blob.core.windows.net/disks/myimage.vhd"), // mediaLink
                  30, // logicalSizeInGB
                  Arrays.asList("http://www.ubuntu.com/project/about-ubuntu/licensing")// eula
            ),
            OSImage.create(
                  "CANONICAL__Canonical-Ubuntu-12-04-amd64-server-20120528.1.3-en-us-30GB.vhd", // name
                  null, // locations
                  null, // affinityGroup
                  "Ubuntu Server 12.04 LTS", // label
                  "Ubuntu Server 12.04 LTS amd64 20120528 Cloud Image", //description
                  "Canonical", // category
                  OSImage.Type.LINUX, // os
                  "Canonical", // publisherName
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
