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
package org.jclouds.cloudsigma2.compute.functions;

import com.google.common.base.Function;
import org.jclouds.cloudsigma2.CloudSigma2Api;
import org.jclouds.cloudsigma2.domain.DriveInfo;
import org.jclouds.cloudsigma2.domain.ServerDrive;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public final class ServerDriveToVolume implements Function<ServerDrive, Volume> {

   private final CloudSigma2Api api;

   @Inject
   public ServerDriveToVolume(CloudSigma2Api api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   public Volume apply(ServerDrive serverDrive) {
      VolumeBuilder builder = new VolumeBuilder();
      DriveInfo driveInfo = api.getDriveInfo(serverDrive.getDriveUuid());
      builder.id(driveInfo.getUuid());
      builder.size(driveInfo.getSize().floatValue());
      builder.durable(true);
      builder.type(Volume.Type.NAS);
      builder.bootDevice(serverDrive.getBootOrder() != null);
      return builder.build();
   }
}
