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
package org.jclouds.digitalocean.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.digitalocean.compute.util.LocationNamingUtils.extractRegionId;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.compute.options.DigitalOceanTemplateOptions;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.DropletCreation;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.domain.Size;
import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.digitalocean.domain.options.CreateDropletOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshKeyPairGenerator;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

/**
 * Implementation of the Compute Service for the DigitalOcean API.
 */
public class DigitalOceanComputeServiceAdapter implements ComputeServiceAdapter<Droplet, Size, Image, Region> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final DigitalOceanApi api;
   private final SshKeyPairGenerator keyGenerator;
   private final Predicate<Integer> nodeRunningPredicate;
   private final Predicate<Integer> nodeStoppedPredicate;
   private final Predicate<Integer> nodeTerminatedPredicate;

   @Inject
   DigitalOceanComputeServiceAdapter(DigitalOceanApi api, SshKeyPairGenerator keyGenerator,
         @Named(TIMEOUT_NODE_RUNNING) Predicate<Integer> nodeRunningPredicate,
         @Named(TIMEOUT_NODE_SUSPENDED) Predicate<Integer> nodeStoppedPredicate,
         @Named(TIMEOUT_NODE_TERMINATED) Predicate<Integer> nodeTerminatedPredicate) {
      this.api = checkNotNull(api, "api cannot be null");
      this.keyGenerator = checkNotNull(keyGenerator, "keyGenerator cannot be null");
      this.nodeRunningPredicate = checkNotNull(nodeRunningPredicate, "nodeRunningPredicate cannot be null");
      this.nodeStoppedPredicate = checkNotNull(nodeStoppedPredicate, "nodeStoppedPredicate cannot be null");
      this.nodeTerminatedPredicate = checkNotNull(nodeTerminatedPredicate, "nodeTerminatedPredicate cannot be null");
   }

   @Override
   public NodeAndInitialCredentials<Droplet> createNodeWithGroupEncodedIntoName(String group, final String name,
         Template template) {
      DigitalOceanTemplateOptions templateOptions = template.getOptions().as(DigitalOceanTemplateOptions.class);
      CreateDropletOptions.Builder options = CreateDropletOptions.builder();

      // Create a default keypair for the node so it has a known private key
      Map<String, String> defaultKeys = keyGenerator.get();
      logger.debug(">> creating default keypair for node...");
      SshKey defaultKey = api.getKeyPairApi().create(name, defaultKeys.get("public"));
      logger.debug(">> keypair created! %s", defaultKey);
      options.addSshKeyId(defaultKey.getId());

      // Check if there is a key to authorize in the portable options
      if (!Strings.isNullOrEmpty(template.getOptions().getPublicKey())) {
         logger.debug(">> creating user keypair for node...");
         // The DigitalOcean API accepts multiple key pairs with the same name. It will be useful to identify all
         // keypairs associated with the node when it comes to destroy it
         SshKey key = api.getKeyPairApi().create(name, template.getOptions().getPublicKey());
         logger.debug(">> keypair created! %s", key);
         options.addSshKeyId(key.getId());
      }

      // DigitalOcean specific options
      if (!templateOptions.getSshKeyIds().isEmpty()) {
         options.addSshKeyIds(templateOptions.getSshKeyIds());
      }
      if (templateOptions.getPrivateNetworking() != null) {
         options.privateNetworking(templateOptions.getPrivateNetworking());
      }
      if (templateOptions.getBackupsEnabled() != null) {
         options.backupsEnabled(templateOptions.getBackupsEnabled());
      }

      // Find the location where the Droplet has to be created
      int regionId = extractRegionId(template.getLocation());

      DropletCreation dropletCreation = api.getDropletApi().create(name,
            Integer.parseInt(template.getImage().getProviderId()),
            Integer.parseInt(template.getHardware().getProviderId()), regionId, options.build());

      // We have to actively wait until the droplet has been provisioned until
      // we can build the entire Droplet object we want to return
      nodeRunningPredicate.apply(dropletCreation.getEventId());
      Droplet droplet = api.getDropletApi().get(dropletCreation.getId());

      LoginCredentials defaultCredentials = LoginCredentials.builder().user("root")
            .privateKey(defaultKeys.get("private")).build();

      return new NodeAndInitialCredentials<Droplet>(droplet, String.valueOf(droplet.getId()), defaultCredentials);
   }

   @Override
   public Iterable<Image> listImages() {
      return api.getImageApi().list();
   }

   @Override
   public Iterable<Size> listHardwareProfiles() {
      return api.getSizesApi().list();
   }

   @Override
   public Iterable<Region> listLocations() {
      return api.getRegionApi().list();
   }

   @Override
   public Iterable<Droplet> listNodes() {
      return api.getDropletApi().list();
   }

   @Override
   public Iterable<Droplet> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<Droplet>() {
         @Override
         public boolean apply(Droplet droplet) {
            return contains(ids, String.valueOf(droplet.getId()));
         }
      });
   }

   @Override
   public Image getImage(String id) {
      // The id of the image can be an id or a slug. Use the corresponding method of the API depending on what is
      // provided. If it can be parsed as a number, use the method to get by ID. Otherwise, get by slug.
      Integer imageId = Ints.tryParse(id);
      return imageId != null ? api.getImageApi().get(imageId) : api.getImageApi().get(id);
   }

   @Override
   public Droplet getNode(String id) {
      return api.getDropletApi().get(Integer.valueOf(id));
   }

   @Override
   public void destroyNode(String id) {
      Droplet droplet = api.getDropletApi().get(Integer.valueOf(id));
      final String nodeName = droplet.getName();

      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      int event = api.getDropletApi().destroy(Integer.valueOf(id), true);
      nodeTerminatedPredicate.apply(event);

      // Destroy the keypairs created for the node
      Iterable<SshKey> keys = filter(api.getKeyPairApi().list(), new Predicate<SshKey>() {
         @Override
         public boolean apply(SshKey input) {
            return input.getName().equals(nodeName);
         }
      });

      for (SshKey key : keys) {
         try {
            logger.info(">> deleting keypair %s...", key);
            api.getKeyPairApi().delete(key.getId());
         } catch (RuntimeException ex) {
            logger.warn(ex, ">> could not delete keypair %s. You can safely delete this key pair manually", key);
         }
      }
   }

   @Override
   public void rebootNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      int event = api.getDropletApi().reboot(Integer.valueOf(id));
      nodeRunningPredicate.apply(event);
   }

   @Override
   public void resumeNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      int event = api.getDropletApi().powerOn(Integer.valueOf(id));
      nodeRunningPredicate.apply(event);
   }

   @Override
   public void suspendNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      int event = api.getDropletApi().powerOff(Integer.valueOf(id));
      nodeStoppedPredicate.apply(event);
   }

}
