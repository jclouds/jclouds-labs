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

import java.io.ByteArrayInputStream;

import org.jclouds.http.internal.HttpWire;

import com.google.common.base.Charsets;
import com.google.inject.Inject;

import vagrant.api.CommandIOListener;

public class VagrantWireLogger implements CommandIOListener {
   private HttpWire wire;

   // Vagrant commands are sequential (non-concurrent)
   private String lastPartialLine = "";

   @Inject
   VagrantWireLogger(HttpWire wire) {
      this.wire = wire;
   }

   @Override
   public void onInput(String input) {
      // Inputs are always single-line
      if (input != null) {
         wire.input(new ByteArrayInputStream(input.getBytes(Charsets.UTF_8)));
      }
   }

   @Override
   public void onOutput(String output) {
      if (output != null) {
         int nlPos = output.indexOf('\n');
         String fullLineOutput;
         if (nlPos != -1) {
            fullLineOutput = lastPartialLine + output.substring(0, nlPos + 1);
            lastPartialLine = output.substring(nlPos + 1);
            wire.output(fullLineOutput);
         } else {
            lastPartialLine += output;
         }
      } else if (!lastPartialLine.isEmpty()) {
         wire.output(lastPartialLine);
         lastPartialLine = "";
      }
   }

}
