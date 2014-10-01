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
package org.jclouds.azurecompute.parse;

import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import java.util.List;
import org.jclouds.azurecompute.domain.Image;
import org.jclouds.azurecompute.domain.OSType;
import org.jclouds.azurecompute.xml.ListImagesHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "ListOSImagesTest")
public class ListImagesTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/images.xml");

      ListImagesHandler handler = injector.getInstance(ListImagesHandler.class);
      List<Image> result = factory.create(handler).parse(is);

      assertThat(result).usingFieldByFieldElementComparator().containsExactlyElementsOf(expected());
   }

   public static List<Image> expected() {
      return ImmutableList.<Image>builder()
         .add(Image.builder()
                   .category("Canonical")
                   .label("Ubuntu Server 12.04 LTS")
                   .logicalSizeInGB(30)
                   .name("CANONICAL__Canonical-Ubuntu-12-04-amd64-server-20120528.1.3-en-us-30GB.vhd")
                   .os(OSType.LINUX)
                   .eula("http://www.ubuntu.com/project/about-ubuntu/licensing")
                   .description("Ubuntu Server 12.04 LTS amd64 20120528 Cloud Image").build())
         .add(Image.builder()
                   .category("Microsoft")
                   .label("Windows Server 2008 R2 SP1, June 2012")
                   .logicalSizeInGB(30)
                   .name("MSFT__Win2K8R2SP1-120612-1520-121206-01-en-us-30GB.vhd")
                   .os(OSType.WINDOWS)
                   .description("Windows Server 2008 R2 is a multi-purpose server.").build())
         .add(Image.builder()
                   .category("Microsoft")
                   .label("Microsoft SQL Server 2012 Evaluation Edition")
                   .logicalSizeInGB(30)
                   .name("MSFT__Sql-Server-11EVAL-11.0.2215.0-05152012-en-us-30GB.vhd")
                   .os(OSType.WINDOWS)
                   .eula("http://go.microsoft.com/fwlink/?LinkID=251820") // NOTE multiple eula.
                   .eula("http://go.microsoft.com/fwlink/?LinkID=131004")
                   .description("SQL Server 2012 Evaluation Edition (64-bit).").build())
         .add(Image.builder()
                   .category("Microsoft")
                   .label("Windows Server 2012 Release Candidate, July 2012")
                   .logicalSizeInGB(30)
                   .name("MSFT__Win2K12RC-Datacenter-201207.02-en.us-30GB.vhd")
                   .os(OSType.WINDOWS)
                   .description("Windows Server 2012 incorporates Microsoft's experience building.").build())
         .add(Image.builder()
                   .category("Microsoft")
                   .label("Windows Server 2008 R2 SP1, July 2012")
                   .logicalSizeInGB(30)
                   .name("MSFT__Win2K8R2SP1-Datacenter-201207.01-en.us-30GB.vhd")
                   .os(OSType.WINDOWS)
                   .description("Windows Server 2008 R2 is a multi-purpose server.")
                   .build())
         .add(Image.builder()
                   .category("OpenLogic")
                   .label("OpenLogic CentOS 6.2")
                   .logicalSizeInGB(30)
                   .name("OpenLogic__OpenLogic-CentOS-62-20120531-en-us-30GB.vhd")
                   .os(OSType.LINUX)
                   .eula("http://www.openlogic.com/azure/service-agreement/")
                   .description("This distribution of Linux is based on CentOS.").build())
         .add(Image.builder()
                   .category("SUSE")
                   .label("openSUSE 12.1")
                   .logicalSizeInGB(30)
                   .name("SUSE__openSUSE-12-1-20120603-en-us-30GB.vhd")
                   .os(OSType.LINUX)
                   .eula("http://opensuse.org/")
                   .description("openSUSE is a free and Linux-based operating system!")
                   .build())
         .add(Image.builder()
                   .category("SUSE")
                   .label("SUSE Linux Enterprise Server")
                   .logicalSizeInGB(30)
                   .name("SUSE__SUSE-Linux-Enterprise-Server-11SP2-20120601-en-us-30GB.vhd")
                   .os(OSType.LINUX)
                   .eula("http://www.novell.com/licensing/eula/")
                   .description("SUSE Linux Enterprise Server is a highly reliable value.").build())
         .add(Image.builder()
                   .category("RightScale with Linux")
                   .label("RightImage-CentOS-6.4-x64-v13.4")
                   .logicalSizeInGB(10)
                   .name("0b11de9248dd4d87b18621318e037d37__RightImage-CentOS-6.4-x64-v13.4")
                   .os(OSType.LINUX) // No EULA, as RightScale stuffed ';' into the field.
                   .build()).build();
   }

}
