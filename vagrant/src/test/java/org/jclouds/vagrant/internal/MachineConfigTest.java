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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
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

   private File machineFolder;
   private MachineConfig machineConfig;
   private File configPath;

   @BeforeMethod
   public void setUp() throws IOException {
      File vagrantHome = new File(System.getProperty("java.io.tmpdir"), "jclouds/vagrant");
      machineFolder = new File(vagrantHome, "jclouds");
      File configFolder = new File(machineFolder, VagrantConstants.MACHINES_CONFIG_SUBFOLDER);
      configFolder.mkdirs();
      configPath = new File(configFolder, "vagrant" + VagrantConstants.MACHINES_CONFIG_EXTENSION);
      machineConfig = new MachineConfig.Factory().newInstance(machineFolder, "vagrant");
   }

   @AfterMethod
   public void tearDown() {
      configPath.delete();
   }

   @Test
   public void testRead() throws IOException {
      Resources.asByteSource(getClass().getResource("/machine-config.yaml")).copyTo(Files.asByteSink(configPath));
      assertEquals(machineConfig.load(), CONFIG);
   }

   @Test
   public void testWrite() throws IOException {
      machineConfig.save(CONFIG);
      ByteArrayOutputStream actualBytes = new ByteArrayOutputStream();
      Files.asByteSource(configPath).copyTo(actualBytes);

      ByteArrayOutputStream expectedBytes = new ByteArrayOutputStream();
      Resources.asByteSource(getClass().getResource("/machine-config.yaml")).copyTo(expectedBytes);

      String actual = new String(actualBytes.toByteArray(), Charsets.UTF_8);
      String expected = new String(expectedBytes.toByteArray(), Charsets.UTF_8);
      assertEquals(actual, expected
            .replace("jcloudsVersion: 0.0.1", "jcloudsVersion: " + JcloudsVersion.get().toString())
            // Strip license headers
            .replaceAll("(?m)^#.*", "")
            .trim());
   }

   @Test
   public void testUpdatesVersion() throws IOException {
      machineConfig.save(CONFIG);
      Map<String, Object> newConfig = machineConfig.load();
      assertEquals(newConfig.get(VagrantConstants.CONFIG_JCLOUDS_VERSION), JcloudsVersion.get().toString());
   }

}
