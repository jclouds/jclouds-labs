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
package org.jclouds.vagrant.functions;

import static org.testng.Assert.assertEquals;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.vagrant.internal.BoxConfig;
import org.jclouds.vagrant.reference.VagrantConstants;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import vagrant.api.domain.Box;

public class BoxToImageTest {
   
   @DataProvider(name = "boxedProvider")
   public Object[][] boxesProvider() {
      return new Object[][] {
         {new Box("centos/7", "20161226", "virtualbox"), null, OsFamily.CENTOS},
         {new Box("ubuntu/xenial64", "20161226", "virtualbox"), null, OsFamily.UBUNTU},
         {new Box("windows-eval", "20161226", "virtualbox"), null, OsFamily.WINDOWS},
         {new Box("some-random-name", "20161226", "virtualbox"), Optional.of(VagrantConstants.VM_GUEST_WINDOWS), OsFamily.WINDOWS},
         {new Box("some-random-name", "20161226", "virtualbox"), Optional.absent(), OsFamily.UNRECOGNIZED},
      };
   }

   @Test(dataProvider = "boxedProvider")
   public void testBoxToImage(Box box, Optional<String> guestType, OsFamily osFamilyExpected) {
      BoxConfig boxConfig = EasyMock.createMock(BoxConfig.class);
      if (guestType != null) {
         EasyMock.expect(boxConfig.getKey(VagrantConstants.KEY_VM_GUEST)).andReturn(guestType);
      }

      BoxConfig.Factory boxConfigFactory = EasyMock.createMock(BoxConfig.Factory.class);
      EasyMock.expect(boxConfigFactory.newInstance(EasyMock.<Box>anyObject())).andReturn(boxConfig);

      EasyMock.replay(boxConfigFactory, boxConfig);

      BoxToImage boxToImage = new BoxToImage(boxConfigFactory);
      Image image = boxToImage.apply(box);

      assertEquals(image.getId(), box.getName());
      assertEquals(image.getProviderId(), box.getName());
      assertEquals(image.getName(), box.getName());
      assertEquals(image.getVersion(), box.getVersion());
      assertEquals(image.getOperatingSystem().getFamily(), osFamilyExpected);
      assertEquals(image.getOperatingSystem().getName(), box.getName());
      assertEquals(image.getOperatingSystem().getVersion(), box.getVersion());
      assertEquals(image.getOperatingSystem().getDescription(), box.getName());
      assertEquals(image.getStatus(), Status.AVAILABLE);
      assertEquals(image.getUserMetadata(), ImmutableMap.of(VagrantConstants.USER_META_PROVIDER, box.getProvider()));
   }
}
