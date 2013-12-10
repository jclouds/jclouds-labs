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
package org.jclouds.management.internal;


import org.testng.annotations.Test;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;


@Test(groups = "unit", testName = "ManagementUtilsTest")
public class ManagementUtilsTest {

   @Test
   public void testRegister() throws Exception {
      MBeanServer mBeanServer = createMock(MBeanServer.class);

      ObjectName firstObjectName = ManagementUtils.objectNameFor("test", "first");
      ObjectName secondObjectName = ManagementUtils.objectNameFor("test", "second");
      ObjectInstance firstInstance = new ObjectInstance(firstObjectName, "FirstClass");
      ObjectInstance secondInstance = new ObjectInstance(firstObjectName, "FirstClass");

      expect(mBeanServer.isRegistered(firstObjectName)).andReturn(false).once();
      expect(mBeanServer.isRegistered(firstObjectName)).andReturn(true).atLeastOnce();
      expect(mBeanServer.isRegistered(secondObjectName)).andReturn(false).once();
      expect(mBeanServer.isRegistered(secondObjectName)).andReturn(true).atLeastOnce();

      expect(mBeanServer.registerMBean(anyObject(), eq(firstObjectName))).andReturn(firstInstance).once();
      expect(mBeanServer.registerMBean(anyObject(), eq(secondObjectName))).andReturn(secondInstance).once();
      replay(mBeanServer);

      for (int i = 0; i < 5; i++) {
         ManagementUtils.register(mBeanServer, new Object(), "test", "first");
      }

      for (int i = 0; i < 5; i++) {
         ManagementUtils.register(mBeanServer, new Object(), "test", "second");
      }
      verify(mBeanServer);
   }

   @Test
   public void testUnregister() throws Exception {
      MBeanServer mBeanServer = createMock(MBeanServer.class);

      ObjectName firstObjectName = ManagementUtils.objectNameFor("test", "first");
      ObjectName secondObjectName = ManagementUtils.objectNameFor("test", "second");

      expect(mBeanServer.isRegistered(firstObjectName)).andReturn(true).once();
      expect(mBeanServer.isRegistered(firstObjectName)).andReturn(false).atLeastOnce();
      expect(mBeanServer.isRegistered(secondObjectName)).andReturn(true).once();
      expect(mBeanServer.isRegistered(secondObjectName)).andReturn(false).atLeastOnce();


      mBeanServer.unregisterMBean(firstObjectName);
      expectLastCall().once();
      mBeanServer.unregisterMBean(secondObjectName);
      expectLastCall().once();
      replay(mBeanServer);

      for (int i = 0; i < 5; i++) {
         ManagementUtils.unregister(mBeanServer, "test", "first");
      }

      for (int i = 0; i < 5; i++) {
         ManagementUtils.unregister(mBeanServer, "test", "second");
      }
      verify(mBeanServer);
   }
}
