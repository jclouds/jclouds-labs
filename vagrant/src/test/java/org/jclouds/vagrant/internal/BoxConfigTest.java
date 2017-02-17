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
package org.jclouds.vagrant.internal;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jclouds.vagrant.reference.VagrantConstants;
import org.jclouds.vagrant.util.VagrantUtils;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import vagrant.api.domain.Box;

public class BoxConfigTest {

   @Test
   public void testKeys() throws IOException {
      File vagrantHome = new File(Files.createTempDir(), "jclouds/vagrant");
      File boxFolder = new File(vagrantHome, "boxes/jclouds-VAGRANTSLASH-vagrant/0/virtualbox");
      boxFolder.mkdirs();
      File boxPath = new File(boxFolder, VagrantConstants.VAGRANTFILE);
      Resources.asByteSource(getClass().getResource("/Vagrantfile.boxconfig")).copyTo(Files.asByteSink(boxPath));

      BoxConfig boxConfig = new BoxConfig.Factory().newInstance(vagrantHome, new Box("jclouds/vagrant", "0", "virtualbox"));
      assertEquals(boxConfig.getKey(".non.existent"), Optional.absent());
      assertEquals(boxConfig.getStringKey(".non.existent"), Optional.absent());
      assertEquals(boxConfig.getKey(VagrantConstants.KEY_VM_GUEST), Optional.of(VagrantConstants.VM_GUEST_WINDOWS));
      assertEquals(boxConfig.getKey(VagrantConstants.KEY_WINRM_USERNAME), Optional.of("\"jclouds-winrm\""));
      assertEquals(boxConfig.getStringKey(VagrantConstants.KEY_WINRM_USERNAME), Optional.of("jclouds-winrm"));
      assertEquals(boxConfig.getStringKey(VagrantConstants.KEY_WINRM_PASSWORD), Optional.of("password-winrm"));
      assertEquals(boxConfig.getStringKey(VagrantConstants.KEY_WINRM_PORT), Optional.of("8899"));
      assertEquals(boxConfig.getStringKey(VagrantConstants.KEY_SSH_USERNAME), Optional.of("jclouds-ssh"));
      assertEquals(boxConfig.getStringKey(VagrantConstants.KEY_SSH_PASSWORD), Optional.of("password-ssh"));
      assertEquals(boxConfig.getStringKey(VagrantConstants.KEY_SSH_PRIVATE_KEY_PATH), Optional.of("/path/to/private.key"));
      assertEquals(boxConfig.getStringKey(VagrantConstants.KEY_SSH_PORT), Optional.of("2222"));

      VagrantUtils.deleteFolder(vagrantHome);
   }

}
