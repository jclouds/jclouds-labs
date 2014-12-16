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
package org.jclouds.azurecompute.compute.functions.internal;

import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.compute.domain.OsFamily;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class OperatingSystems {

   protected static final String CENTOS = "CentOS";
   protected static final String SUSE = "SUSE";
   protected static final String OPENSUSE = "openSUSE";
   protected static final String UBUNTU = "Ubuntu";
   protected static final String WINDOWS = "Windows";
   private static final String MICROSOFT = "Microsoft";
   public static final String WINDOWS_SERVER = "Windows Server";
   public static final String MICROSOFT_SQL_SERVER = "Microsoft SQL Server";

   public static Function<String, OsFamily> osFamily() {
      return new Function<String, OsFamily>() {
         @Override
         public OsFamily apply(final String label) {
            if (label != null) {
               if (label.contains(CENTOS)) return OsFamily.CENTOS;
               else if (label.contains(SUSE)) return OsFamily.SUSE;
               else if (label.contains(UBUNTU)) return OsFamily.UBUNTU;
               else if (label.contains(WINDOWS)) return OsFamily.WINDOWS;
            }
            return OsFamily.UNRECOGNIZED;
         }
      };
   }

   public static Function<OSImage, String> version() {
      return new Function<OSImage, String>() {
         @Override
         public String apply(OSImage osImage) {
            if (osImage.category().matches("Canonical|OpenLogic")) {
               return Iterables.get(Splitter.on(" ").split(osImage.label()), 2);
            } else if (osImage.category().matches(SUSE)) {
               if (osImage.label().startsWith(OPENSUSE)) {
                  return osImage.label().substring(OPENSUSE.length() + 1);
               }
               if (osImage.label().startsWith(SUSE)) {
                  return Iterables.get(Splitter.on("-").split(osImage.name()), 4);
               }
            } else if (osImage.category().matches(MICROSOFT)) {
               if (osImage.label().startsWith(WINDOWS_SERVER)) {
                  return osImage.label().substring(WINDOWS_SERVER.length() + 1);
               }
               if (osImage.label().startsWith(MICROSOFT_SQL_SERVER)) {
                  return osImage.label().substring(MICROSOFT_SQL_SERVER.length() + 1);
               }
            } else if (osImage.category().matches("RightScale with Linux|Public ")) {
               Iterable<String> splittedLabel = Splitter.on("-").split(osImage.label());
               if (Iterables.size(splittedLabel) > 2) {
                  return Iterables.get(splittedLabel, 2);
               }
            }
            return null;
         }
      };
   }

}
