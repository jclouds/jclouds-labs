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
package org.jclouds.vagrant.strategy;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.vagrant.internal.BoxConfig;
import org.jclouds.vagrant.reference.VagrantConstants;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

public class VagrantDefaultImageCredentialsTest {

   @Test
   public void testCredentials() {
      LoginCredentials creds = LoginCredentials.builder()
            .user("vagrant")
            .password("vagrant")
            .noPrivateKey()
            .build();

      @SuppressWarnings("unchecked")
      Map<String, Credentials> credentialStore = EasyMock.createMock(Map.class);
      BoxConfig.Factory boxConfigFactory = EasyMock.createMock(BoxConfig.Factory.class);

      EasyMock.replay(credentialStore, boxConfigFactory);

      VagrantDefaultImageCredentials defaultImageCredentials =
            new VagrantDefaultImageCredentials(creds, credentialStore, boxConfigFactory);

      OperatingSystem os = new OperatingSystem(OsFamily.LINUX, "Jclouds OS", "10", "x64", "Jclouds Test Image", true);
      Image image = new ImageBuilder()
            .ids("jclouds/box")
            .operatingSystem(os)
            .status(org.jclouds.compute.domain.Image.Status.AVAILABLE)
            .build();

      LoginCredentials actualCreds = defaultImageCredentials.apply(image);

      EasyMock.verify(credentialStore, boxConfigFactory);

      assertEquals(actualCreds, creds);
   }

   @Test
   public void testCredentialStore() {
      LoginCredentials creds = LoginCredentials.builder()
            .user("vagrant")
            .password("vagrant")
            .noPrivateKey()
            .build();

      OperatingSystem os = new OperatingSystem(OsFamily.LINUX, "Jclouds OS", "10", "x64", "Jclouds Test Image", true);
      Image image = new ImageBuilder()
            .ids("jclouds/box")
            .operatingSystem(os)
            .status(org.jclouds.compute.domain.Image.Status.AVAILABLE)
            .build();

      @SuppressWarnings("unchecked")
      Map<String, Credentials> credentialStore = EasyMock.createMock(Map.class);
      EasyMock.expect(credentialStore.containsKey("image#" + image.getId())).andReturn(Boolean.TRUE);
      EasyMock.expect(credentialStore.get("image#" + image.getId())).andReturn(creds);

      BoxConfig.Factory boxConfigFactory = EasyMock.createMock(BoxConfig.Factory.class);

      EasyMock.replay(credentialStore, boxConfigFactory);

      VagrantDefaultImageCredentials defaultImageCredentials =
            new VagrantDefaultImageCredentials(null, credentialStore, boxConfigFactory);

      LoginCredentials actualCreds = defaultImageCredentials.apply(image);

      EasyMock.verify(credentialStore, boxConfigFactory);

      assertEquals(actualCreds, creds);
   }

   @Test
   public void testWinrmCredentials() {
      LoginCredentials creds = LoginCredentials.builder()
            .user("jclouds-user")
            .password("jclouds-pass")
            .noPrivateKey()
            .build();

      OperatingSystem os = new OperatingSystem(OsFamily.WINDOWS, "Jclouds OS", "10", "x64", "Jclouds Test Image", true);
      Image image = new ImageBuilder()
            .ids("jclouds/box")
            .operatingSystem(os)
            .status(org.jclouds.compute.domain.Image.Status.AVAILABLE)
            .build();

      @SuppressWarnings("unchecked")
      Map<String, Credentials> credentialStore = EasyMock.createMock(Map.class);
      EasyMock.expect(credentialStore.containsKey("image#" + image.getId())).andReturn(Boolean.FALSE);

      BoxConfig boxConfig = EasyMock.createMock(BoxConfig.class);
      EasyMock.expect(boxConfig.getStringKey(VagrantConstants.KEY_WINRM_USERNAME)).andReturn(Optional.of(creds.getUser()));
      EasyMock.expect(boxConfig.getStringKey(VagrantConstants.KEY_WINRM_PASSWORD)).andReturn(Optional.of(creds.getOptionalPassword().get()));

      BoxConfig.Factory boxConfigFactory = EasyMock.createMock(BoxConfig.Factory.class);
      EasyMock.expect(boxConfigFactory.newInstance(image)).andReturn(boxConfig);

      EasyMock.replay(credentialStore, boxConfig, boxConfigFactory);

      VagrantDefaultImageCredentials defaultImageCredentials =
            new VagrantDefaultImageCredentials(null, credentialStore, boxConfigFactory);

      LoginCredentials actualCreds = defaultImageCredentials.apply(image);

      EasyMock.verify(credentialStore, boxConfigFactory);

      assertEquals(actualCreds, creds);
   }

   @Test
   public void testSshCredentials() {
      LoginCredentials creds = LoginCredentials.builder()
            .user("jclouds-user")
            .password("jclouds-pass")
            .noPrivateKey()
            .build();

      OperatingSystem os = new OperatingSystem(OsFamily.LINUX, "Jclouds OS", "10", "x64", "Jclouds Test Image", true);
      Image image = new ImageBuilder()
            .ids("jclouds/box")
            .operatingSystem(os)
            .status(org.jclouds.compute.domain.Image.Status.AVAILABLE)
            .build();

      @SuppressWarnings("unchecked")
      Map<String, Credentials> credentialStore = EasyMock.createMock(Map.class);
      EasyMock.expect(credentialStore.containsKey("image#" + image.getId())).andReturn(Boolean.FALSE);

      BoxConfig boxConfig = EasyMock.createMock(BoxConfig.class);
      EasyMock.expect(boxConfig.getStringKey(VagrantConstants.KEY_SSH_USERNAME)).andReturn(Optional.of(creds.getUser()));
      EasyMock.expect(boxConfig.getStringKey(VagrantConstants.KEY_SSH_PASSWORD)).andReturn(Optional.of(creds.getOptionalPassword().get()));
      EasyMock.expect(boxConfig.getStringKey(VagrantConstants.KEY_SSH_PRIVATE_KEY_PATH)).andReturn(Optional.<String>absent());

      BoxConfig.Factory boxConfigFactory = EasyMock.createMock(BoxConfig.Factory.class);
      EasyMock.expect(boxConfigFactory.newInstance(image)).andReturn(boxConfig);

      EasyMock.replay(credentialStore, boxConfig, boxConfigFactory);

      VagrantDefaultImageCredentials defaultImageCredentials =
            new VagrantDefaultImageCredentials(null, credentialStore, boxConfigFactory);

      LoginCredentials actualCreds = defaultImageCredentials.apply(image);

      EasyMock.verify(credentialStore, boxConfigFactory);

      assertEquals(actualCreds, creds);
   }
}
