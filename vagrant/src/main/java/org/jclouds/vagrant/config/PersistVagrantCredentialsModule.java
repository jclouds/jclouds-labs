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
package org.jclouds.vagrant.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.internal.PersistNodeCredentials;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.functions.CredentialsFromAdminAccess;
import org.jclouds.vagrant.domain.VagrantNode;
import org.jclouds.vagrant.internal.MachineConfig;
import org.jclouds.vagrant.internal.VagrantNodeRegistry;
import org.jclouds.vagrant.internal.MachineConfig.Factory;
import org.jclouds.vagrant.reference.VagrantConstants;
import org.jclouds.vagrant.util.VagrantUtils;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

public class PersistVagrantCredentialsModule extends AbstractModule {

   static class RefreshCredentialsForNodeIfRanAdminAccess implements Function<NodeMetadata, NodeMetadata> {
      protected final Map<String, Credentials> credentialStore;
      protected final VagrantNodeRegistry vagrantNodeRegistry;
      protected final Statement statement;
      protected final Factory machineConfigFactory;

      @Inject
      RefreshCredentialsForNodeIfRanAdminAccess(
            VagrantNodeRegistry vagrantNodeRegistry,
            Map<String, Credentials> credentialStore,
            @Nullable @Assisted Statement statement,
            MachineConfig.Factory machineConfigFactory) {
         this.vagrantNodeRegistry = vagrantNodeRegistry;
         this.credentialStore = credentialStore;
         this.statement = statement;
         this.machineConfigFactory = machineConfigFactory;
      }

      @Override
      public NodeMetadata apply(NodeMetadata input) {
         if (statement == null)
            return input;
         Credentials credentials = CredentialsFromAdminAccess.INSTANCE.apply(statement);
         if (credentials != null) {
            LoginCredentials creds = LoginCredentials.fromCredentials(credentials);
            input = NodeMetadataBuilder.fromNodeMetadata(input).credentials(creds).build();
            credentialStore.put("node#" + input.getId(), input.getCredentials());
            updateMachine(input.getId(), creds);
         }
         return input;
      }

      protected void updateMachine(String id, LoginCredentials credentials) {
         VagrantNode node = vagrantNodeRegistry.get(id);
         if (node == null) {
            throw new IllegalStateException("Updating node credentials failed because node " + id + " not found.");
         }
         String provider = node.image().getUserMetadata().get(VagrantConstants.USER_META_PROVIDER);

         MachineConfig machineConfig = machineConfigFactory.newInstance(node);
         Map<String, Object> config = machineConfig.load();

         config.put(VagrantConstants.CONFIG_USERNAME, credentials.getUser());
         config.remove(VagrantConstants.CONFIG_PASSWORD);
         if (credentials.getOptionalPassword().isPresent()) {
            config.put(VagrantConstants.CONFIG_PASSWORD, credentials.getOptionalPassword().get());
         }
         if (credentials.getOptionalPrivateKey().isPresent()) {
            // Overwrite existing private key and dont't use config.ssh.private_key_path - doesn't work, is ignored.
            File privateKeyFile = new File(node.path(), ".vagrant/machines/" + node.name() + "/" + provider + "/private_key");
            try {
               VagrantUtils.write(privateKeyFile, credentials.getOptionalPrivateKey().get());
            } catch (IOException e) {
               throw new IllegalStateException("Failure updating credentials for " + id +
                     ". Can't save private key to " + privateKeyFile.getAbsolutePath(), e);
            }
         }

         machineConfig.save(config);
      }
   }

   static class RefreshCredentialsForNode extends RefreshCredentialsForNodeIfRanAdminAccess {

      @Inject
      RefreshCredentialsForNode(
            VagrantNodeRegistry vagrantNodeRegistry,
            Map<String, Credentials> credentialStore,
            @Assisted @Nullable Statement statement,
            MachineConfig.Factory machineConfigFactory) {
         super(vagrantNodeRegistry, credentialStore, statement, machineConfigFactory);
      }

      @Override
      public NodeMetadata apply(NodeMetadata input) {
         input = super.apply(input);
         if (input.getCredentials() != null) {
            credentialStore.put("node#" + input.getId(), input.getCredentials());
            updateMachine(input.getId(), input.getCredentials());
         }
         return input;
      }

   }


   @Override
   protected void configure() {
      install(new FactoryModuleBuilder()
            .implement(new TypeLiteral<Function<NodeMetadata, NodeMetadata>>() {},
                  Names.named("ifAdminAccess"),
                  RefreshCredentialsForNodeIfRanAdminAccess.class)
            .implement(new TypeLiteral<Function<NodeMetadata, NodeMetadata>>() {},
                  Names.named("always"),
                  RefreshCredentialsForNode.class)
            .build(PersistNodeCredentials.class));
   }

}
