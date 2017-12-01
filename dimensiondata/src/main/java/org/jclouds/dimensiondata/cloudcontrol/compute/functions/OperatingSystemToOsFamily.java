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
package org.jclouds.dimensiondata.cloudcontrol.compute.functions;

import com.google.common.base.Function;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;

import javax.inject.Singleton;
import java.util.regex.Pattern;

@Singleton
public class OperatingSystemToOsFamily implements Function<OperatingSystem, OsFamily> {

   private static final String CENTOS = "CENTOS";
   private static final String REDHAT = "REDHAT";
   private static final String UBUNTU = "UBUNTU";
   private static final String SUSE = "SUSE";
   private static final String SLES = "SLES";
   private static final String SOLARIS = "SOLARIS";
   private static final String WINDOWS_FAMILY = "WINDOWS";
   private static final Pattern OTHER_LINUX_PATTERN = Pattern.compile("OTHER\\w+LINUX\\w+");

   @Override
   public OsFamily apply(final OperatingSystem os) {
      if (os.family().equals(WINDOWS_FAMILY)) {
         return OsFamily.WINDOWS;
      } else if (os.id().startsWith(CENTOS)) {
         return OsFamily.CENTOS;
      } else if (os.id().startsWith(UBUNTU)) {
         return OsFamily.UBUNTU;
      } else if (os.id().startsWith(REDHAT)) {
         return OsFamily.RHEL;
      } else if (os.id().startsWith(SOLARIS)) {
         return OsFamily.SOLARIS;
      } else if (os.id().startsWith(SUSE) || os.id().startsWith(SLES)) {
         return OsFamily.SUSE;
      } else if (OTHER_LINUX_PATTERN.matcher(os.id()).matches()) {
         return OsFamily.LINUX;
      } else {
         return OsFamily.UNRECOGNIZED;
      }
   }

}
