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
package org.jclouds.cloudsigma2.compute.config;

import org.easymock.EasyMock;
import org.jclouds.cloudsigma2.CloudSigma2Api;
import org.jclouds.cloudsigma2.compute.config.CloudSigma2ComputeServiceContextModule.DriveClonedPredicate;
import org.jclouds.cloudsigma2.domain.DriveInfo;
import org.jclouds.cloudsigma2.domain.DriveStatus;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for the drive cloned predicate
 */
@Test(groups = "unit", testName = "DriveClonedPredicateTest")
public class DriveClonedPredicateTest {

   public void testDriveCloned() {
      CloudSigma2Api api = EasyMock.createMock(CloudSigma2Api.class);

      for (DriveStatus status : DriveStatus.values()) {
         expect(api.getDriveInfo(status.name())).andReturn(mockDrive(status));
      }

      replay(api);

      DriveClonedPredicate predicate = new DriveClonedPredicate(api);

      assertFalse(predicate.apply(mockDrive(DriveStatus.COPYING)));
      assertFalse(predicate.apply(mockDrive(DriveStatus.UNAVAILABLE)));
      assertTrue(predicate.apply(mockDrive(DriveStatus.MOUNTED)));
      assertTrue(predicate.apply(mockDrive(DriveStatus.UNMOUNTED)));

      verify(api);
   }

   private static DriveInfo mockDrive(DriveStatus status) {
      return new DriveInfo.Builder().uuid(status.name()).status(status).build();
   }
}
