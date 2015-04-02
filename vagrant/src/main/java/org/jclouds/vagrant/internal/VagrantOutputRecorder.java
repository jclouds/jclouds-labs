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

import vagrant.api.CommandIOListener;

public class VagrantOutputRecorder implements CommandIOListener {

   private CommandIOListener next;
   private StringBuilder output = new StringBuilder();
   private boolean isRecording;

   public VagrantOutputRecorder(CommandIOListener next) {
      this.next = next;
   }

   @Override
   public void onInput(String input) {
      if (isRecording) {
         next.onInput(input);
      }
   }

   @Override
   public void onOutput(String output) {
      if (isRecording) {
         next.onOutput(output);
         if (output != null) {
            this.output.append(output);
         }
      }
   }

   public void record() {
      isRecording = true;
   }

   public String stopRecording() {
      isRecording = false;
      String out = output.toString();
      output.setLength(0);
      return out;
   }

}
