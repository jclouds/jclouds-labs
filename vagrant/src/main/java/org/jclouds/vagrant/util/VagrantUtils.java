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
package org.jclouds.vagrant.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jclouds.util.Closeables2;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

public class VagrantUtils {
   public static void deleteFolder(File path) {
      if (path.isDirectory()) {
         for (File sub : path.listFiles()) {
            deleteFolder(sub);
         }
      }
      if (!path.delete()) {
         throw new IllegalStateException("Can't delete " + path.getAbsolutePath());
      }
   }

   public static void write(File file, String value) throws IOException {
      write(file, new ByteArrayInputStream(value.getBytes(Charsets.UTF_8)));
   }

   public static void write(File file, InputStream in) throws IOException {
      OutputStream out = new FileOutputStream(file);
      try {
         ByteStreams.copy(in, out);
      } finally {
         Closeables2.closeQuietly(out);
         Closeables2.closeQuietly(in);
      }
   }

   public static void deleteFiles(File path, String filePattern) {
      for (File f : path.listFiles()) {
         if (f.getName().startsWith(filePattern)) {
            if (!f.delete()) {
               throw new IllegalStateException("Failed deleting machine file " + f.getAbsolutePath());
            }
         }
      }
   }

}
