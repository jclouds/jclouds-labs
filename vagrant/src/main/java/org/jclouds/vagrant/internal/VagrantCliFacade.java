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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

import javax.inject.Inject;

import org.jclouds.domain.LoginCredentials;
import org.jclouds.vagrant.api.VagrantApiFacade;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.inject.assistedinject.Assisted;

import vagrant.Vagrant;
import vagrant.api.CommandIOListener;
import vagrant.api.VagrantApi;
import vagrant.api.domain.Box;
import vagrant.api.domain.SshConfig;

public class VagrantCliFacade implements VagrantApiFacade<Box> {
   private final VagrantApi vagrant;
   private final VagrantOutputRecorder outputRecorder;

   @Inject
   VagrantCliFacade(CommandIOListener wireLogger, @Assisted File path) {
      this.outputRecorder = new VagrantOutputRecorder(checkNotNull(wireLogger, "wireLogger"));
      this.vagrant = Vagrant.forPath(path, outputRecorder);
   }

   @Override
   public String up(String machineName) {
      outputRecorder.record();
      vagrant.up(machineName);
      return outputRecorder.stopRecording();
   }

   @Override
   public void halt(String machineName) {
      vagrant.halt(machineName);
   }

   @Override
   public void destroy(String machineName) {
      vagrant.destroy(machineName);
   }

   @Override
   public LoginCredentials sshConfig(String machineName) {
      SshConfig sshConfig = vagrant.sshConfig(machineName);
      LoginCredentials.Builder loginCredentialsBuilder = LoginCredentials.builder()
            .user(sshConfig.getUser());
      try {
         String privateKey = Files.toString(new File(sshConfig.getIdentityFile()), Charset.defaultCharset());
         loginCredentialsBuilder.privateKey(privateKey);
      } catch (IOException e) {
         throw new IllegalStateException("Invalid private key " + sshConfig.getIdentityFile(), e);
      }

      return loginCredentialsBuilder.build();
   }

   @Override
   public Collection<Box> listBoxes() {
      return vagrant.box().list();
   }

   @Override
   public Box getBox(final String boxName) {
      return Iterables.find(listBoxes(), new Predicate<Box>() {
         @Override
         public boolean apply(Box input) {
            return boxName.equals(input.getName());
         }
      }, null);
   }

   @Override
   public void haltForced(String name) {
      vagrant.haltForced(name);
   }

   @Override
   public boolean exists() {
      return vagrant.exists();
   }

}
