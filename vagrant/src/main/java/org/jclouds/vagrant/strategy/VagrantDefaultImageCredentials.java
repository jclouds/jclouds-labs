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
package org.jclouds.vagrant.strategy;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.LoginCredentials.Builder;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.vagrant.internal.BoxConfig;
import org.jclouds.vagrant.reference.VagrantConstants;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.inject.Singleton;

@Singleton
public class VagrantDefaultImageCredentials implements PopulateDefaultLoginCredentialsForImageStrategy {

   @Resource
   protected Logger logger = Logger.NULL;

   protected final LoginCredentials creds;
   protected final Map<String, Credentials> credentialStore;
   protected final BoxConfig.Factory boxConfigFactory;

   @Inject
   VagrantDefaultImageCredentials(
         @Nullable @Named("image") LoginCredentials creds,
         Map<String, Credentials> credentialStore,
         BoxConfig.Factory boxConfigFactory) {
      this.creds = creds;
      this.credentialStore = credentialStore;
      this.boxConfigFactory = boxConfigFactory;
   }

   @Override
   public LoginCredentials apply(Object resourceToAuthenticate) {
      checkState(resourceToAuthenticate instanceof Image, "this is only valid for images, not %s",
            resourceToAuthenticate.getClass().getSimpleName());
      if (creds != null)
         return creds;
      Image image = Image.class.cast(resourceToAuthenticate);
      if (credentialStore.containsKey("image#" + image.getId())) {
         return LoginCredentials.fromCredentials(credentialStore.get("image#" + image.getId()));
      // Skipping osFamilyToCredentials - not applicable to vagrant world
      } else if (image.getOperatingSystem().getFamily() == OsFamily.WINDOWS) {
         return parseWinRmBoxCredentials(image);
      } else {
         return parseSshBoxCredentials(image);
      }
   }

   private LoginCredentials parseWinRmBoxCredentials(Image image) {
      BoxConfig parser = boxConfigFactory.newInstance(image);
      String username = parser.getStringKey(VagrantConstants.KEY_WINRM_USERNAME).or(VagrantConstants.DEFAULT_USERNAME);
      String password = parser.getStringKey(VagrantConstants.KEY_WINRM_PASSWORD).or(VagrantConstants.DEFAULT_PASSWORD);
      return LoginCredentials.builder()
            .user(username)
            .password(password)
            .noPrivateKey()
            .build();
   }

   private LoginCredentials parseSshBoxCredentials(Image image) {
      BoxConfig parser = boxConfigFactory.newInstance(image);
      String username = parser.getStringKey(VagrantConstants.KEY_SSH_USERNAME).or(VagrantConstants.DEFAULT_USERNAME);
      Builder credBuilder = LoginCredentials.builder().user(username);
      Optional<String> password = parser.getStringKey(VagrantConstants.KEY_SSH_PASSWORD);
      if (password.isPresent()) {
         credBuilder.password(password.get());
      }
      Optional<String> privateKeyPath = parser.getStringKey(VagrantConstants.KEY_SSH_PRIVATE_KEY_PATH);
      if (privateKeyPath.isPresent()) {
         File privateKey = new File(parser.getFolder(), privateKeyPath.get());
         if (privateKey.exists()) {
            try {
               credBuilder.privateKey(Files.toString(privateKey, Charsets.UTF_8));
            } catch (IOException e) {
               throw new IllegalStateException("Failure reading private key file " +
                     privateKey.getAbsolutePath() + " for box " + parser.getFolder().getAbsolutePath());
            }
         } else {
            logger.warn("Private key " + privateKeyPath.get() + " for box " +
                  parser.getFolder().getAbsolutePath() + " not found at " + privateKey.getAbsolutePath() + ". Ignoring.");
         }
      }
      return credBuilder.build();
   }

}
