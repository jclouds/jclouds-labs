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

import org.testng.annotations.Test;

import vagrant.api.CommandIOListener;

public class VagrantOutputRecorderTest {
   protected static final String INPUT = "vagrant up";
   protected static final String OUT1 = "1482768916,f99,metadata,provider,virtualbox\n";
   protected static final String OUT2 = "1482768916,,ui,info,Bringing machine ";
   protected static final String OUT3 = "'f99' up with 'virtualbox' provider...\n";
   protected static final String OUT4 = "1482768916,f99,action,up,sta";

   private static CommandIOListener nopIOListener = new CommandIOListener() {
      @Override
      public void onInput(String input) {
      }

      @Override
      public void onOutput(String output) {
      }
   };

   @Test
   public void testOutputRecorder() {
      VagrantOutputRecorder outputRecorder = new VagrantOutputRecorder(nopIOListener);
      outputRecorder.record();
      assertEquals(outputRecorder.stopRecording(), "");
      outputRecorder.record();
      outputRecorder.onInput("vagrant up");
      assertEquals(outputRecorder.stopRecording(), "");
      outputRecorder.record();
      outputRecorder.onOutput(OUT1);
      outputRecorder.onOutput(OUT2);
      outputRecorder.onOutput(OUT3 + OUT4);
      assertEquals(outputRecorder.stopRecording(), OUT1 + OUT2 + OUT3 + OUT4);
      outputRecorder.onOutput(OUT1);
      assertEquals(outputRecorder.stopRecording(), "");
   }
}
