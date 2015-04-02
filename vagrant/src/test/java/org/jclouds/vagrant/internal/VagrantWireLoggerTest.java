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

import java.io.InputStream;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.jclouds.http.internal.HttpWire;
import org.testng.annotations.Test;

public class VagrantWireLoggerTest {
   private static final String INPUT = VagrantOutputRecorderTest.INPUT;
   private static final String OUT1 = VagrantOutputRecorderTest.OUT1;
   private static final String OUT2 = VagrantOutputRecorderTest.OUT2;
   private static final String OUT3 = VagrantOutputRecorderTest.OUT3;
   private static final String OUT4 = VagrantOutputRecorderTest.OUT4;

   @Test
   public void testWireLogger() {
      HttpWire httpWire = EasyMock.createMock(HttpWire.class);
      Capture<InputStream> wireInCapture = new Capture<InputStream>();
      EasyMock.expect(httpWire.input(EasyMock.capture(wireInCapture))).andReturn(null);
      EasyMock.expect(httpWire.output(OUT1)).andReturn(OUT1);
      EasyMock.expect(httpWire.output(OUT2 + OUT3)).andReturn(OUT2 + OUT3);
      EasyMock.expect(httpWire.output(OUT4)).andReturn(OUT4);

      EasyMock.replay(httpWire);

      VagrantWireLogger wireLogger = new VagrantWireLogger(httpWire);
      wireLogger.onInput(INPUT);
      wireLogger.onInput(null);
      wireLogger.onOutput(OUT1);
      wireLogger.onOutput(OUT2);
      wireLogger.onOutput(OUT3 + OUT4);
      wireLogger.onOutput(null);
      
      EasyMock.verify(httpWire);
   }
}
