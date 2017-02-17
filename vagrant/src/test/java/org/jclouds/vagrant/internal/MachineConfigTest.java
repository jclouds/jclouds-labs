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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jclouds.JcloudsVersion;
import org.jclouds.vagrant.reference.VagrantConstants;
import org.jclouds.vagrant.util.VagrantUtils;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class MachineConfigTest {
   private static final Map<String, Object> CONFIG = ImmutableMap.<String, Object>builder()
         .put("jcloudsVersion", "0.0.1")
         .put("box", "jclouds/vagrant")
         .put("osFamily", "ubuntu")
         .put("hardwareId", "micro")
         .put("memory", "512")
         .put("cpus", "1")
         .build();

   @Test
   public void testRead() throws IOException {
      File machineFolder = Files.createTempDir();
      File configFile = getConifgFile(machineFolder);
      MachineConfig machineConfig = getMachineConfig(configFile);

      Resources.asByteSource(getClass().getResource("/machine-config.yaml")).copyTo(Files.asByteSink(configFile));
      assertEquals(machineConfig.load(), CONFIG);
      VagrantUtils.deleteFolder(machineFolder);
   }

   @Test
   public void testWrite() throws IOException {
      File machineFolder = Files.createTempDir();
      File configFile = getConifgFile(machineFolder);
      MachineConfig machineConfig = getMachineConfig(configFile);

      machineConfig.save(CONFIG);
      ByteArrayOutputStream actualBytes = new ByteArrayOutputStream();
      Files.asByteSource(configFile).copyTo(actualBytes);

      ByteArrayOutputStream expectedBytes = new ByteArrayOutputStream();
      Resources.asByteSource(getClass().getResource("/machine-config.yaml")).copyTo(expectedBytes);

      String actual = new String(actualBytes.toByteArray(), Charsets.UTF_8);
      String expected = new String(expectedBytes.toByteArray(), Charsets.UTF_8);
      assertEquals(actual, expected
            .replace("jcloudsVersion: 0.0.1", "jcloudsVersion: " + JcloudsVersion.get().toString())
            // Strip license headers
            .replaceAll("(?m)^#.*", "")
            .trim());
      VagrantUtils.deleteFolder(machineFolder);
   }

   @Test
   public void testUpdatesVersion() throws IOException {
      File machineFolder = Files.createTempDir();
      File configFile = getConifgFile(machineFolder);
      MachineConfig machineConfig = getMachineConfig(configFile);

      machineConfig.save(CONFIG);
      Map<String, Object> newConfig = machineConfig.load();
      assertEquals(newConfig.get(VagrantConstants.CONFIG_JCLOUDS_VERSION), JcloudsVersion.get().toString());
      VagrantUtils.deleteFolder(machineFolder);
   }

   private MachineConfig getMachineConfig(File configFile) {
      String machineName = configFile.getName().replaceAll(VagrantConstants.MACHINES_CONFIG_EXTENSION, "");
      return new MachineConfig.Factory().newInstance(configFile.getParentFile().getParentFile(), machineName);
   }

   private File getConifgFile(File machineFolder) {
      File configFolder = new File(machineFolder, VagrantConstants.MACHINES_CONFIG_SUBFOLDER);
      configFolder.mkdirs();
      File machineFile = new File(configFolder, "vagrant" + VagrantConstants.MACHINES_CONFIG_EXTENSION);
      return machineFile;
   }

}
