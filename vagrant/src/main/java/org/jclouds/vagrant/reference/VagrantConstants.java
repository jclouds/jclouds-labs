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
package org.jclouds.vagrant.reference;

import java.io.File;

public final class VagrantConstants {
   private VagrantConstants() {}

   public static final String JCLOUDS_VAGRANT_HOME = "vagrant.home";
   public static final String JCLOUDS_VAGRANT_HOME_DEFAULT = new File(System.getProperty("user.home"), ".jclouds/vagrant").getAbsolutePath();
   public static final String VAGRANTFILE = "Vagrantfile";
   public static final String DEFAULT_USERNAME = "vagrant";
   public static final String DEFAULT_PASSWORD = "vagrant";
   public static final String USER_META_PROVIDER = "provider";

   public static final String ENV_VAGRANT_HOME = "VAGRANT_HOME";
   public static final String ENV_VAGRANT_HOME_DEFAULT = new File(System.getProperty("user.home"), ".vagrant.d").getAbsolutePath();
   public static final String VAGRANT_BOXES_SUBFOLDER = "boxes";

   public static final String ESCAPE_SLASH = "-VAGRANTSLASH-";

   public static final String DELIMITER_NETWORKS_START = "================= Networks start =================";
   public static final String DELIMITER_NETWORKS_END = "================= Networks end ===================";
   public static final String DELIMITER_HOSTNAME_START = "================= Hostname start ==========================";
   public static final String DELIMITER_HOSTNAME_END = "================= Hostname end ============================";

   // Vagrantfile config
   public static final String KEY_VM_GUEST = ".vm.guest";
   public static final String VM_GUEST_WINDOWS = ":windows";
   public static final String KEY_WINRM_USERNAME = ".winrm.username";
   public static final String KEY_WINRM_PASSWORD = ".winrm.password";
   public static final String KEY_WINRM_PORT = ".winrm.port";
   public static final String KEY_SSH_USERNAME = ".ssh.username";
   public static final String KEY_SSH_PASSWORD = ".ssh.password";
   public static final String KEY_SSH_PRIVATE_KEY_PATH = ".ssh.private_key_path";
   public static final String KEY_SSH_PORT = ".ssh.port";

   public static final String MACHINES_CONFIG_SUBFOLDER = "machines";
   public static final String MACHINES_CONFIG_EXTENSION = ".yaml";
   public static final String MACHINES_AUTO_HARDWARE = "automatic";

   // Config file keys
   public static final String CONFIG_JCLOUDS_VERSION = "jcloudsVersion";
   public static final String CONFIG_BOX = "box";
   public static final String CONFIG_HARDWARE_ID = "hardwareId";
   public static final String CONFIG_OS_FAMILY = "osFamily";
   public static final String CONFIG_MEMORY = "memory";
   public static final String CONFIG_CPUS = "cpus";
   public static final String CONFIG_USERNAME = "username";
   public static final String CONFIG_PASSWORD = "password";
}
