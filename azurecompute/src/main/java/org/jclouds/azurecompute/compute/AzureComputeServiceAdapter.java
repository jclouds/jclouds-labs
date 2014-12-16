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
package org.jclouds.azurecompute.compute;

import static com.google.common.base.Predicates.notNull;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.azurecompute.domain.Deployment.InstanceStatus.READY_ROLE;
import static org.jclouds.util.Predicates2.retry;
import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;
import org.jclouds.azurecompute.config.AzureComputeProperties;
import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Deployment.RoleInstance;
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.DeploymentParams.ExternalEndpoint;
import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.options.AzureComputeTemplateOptions;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * defines the connection between the {@link AzureComputeApi} implementation and the
 * jclouds {@link org.jclouds.compute.ComputeService}
 */
@Singleton
public class AzureComputeServiceAdapter implements ComputeServiceAdapter<Deployment, RoleSize, OSImage, Location> {

   private static final String DEFAULT_LOGIN_USER = "jclouds";
   private static final String DEFAULT_LOGIN_PASSWORD = "Azur3Compute!";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final AzureComputeApi api;
   private final Predicate<String> operationSucceededPredicate;
   private final AzureComputeConstants azureComputeConstants;

   @Inject
   AzureComputeServiceAdapter(final AzureComputeApi api, Predicate<String> operationSucceededPredicate, AzureComputeConstants azureComputeConstants) {
      this.api = api;
      this.operationSucceededPredicate = operationSucceededPredicate;
      this.azureComputeConstants = azureComputeConstants;
   }

   @Override
   public NodeAndInitialCredentials<Deployment> createNodeWithGroupEncodedIntoName(
           final String group, final String name, Template template) {

      // azure-specific options
      AzureComputeTemplateOptions templateOptions = template.getOptions().as(AzureComputeTemplateOptions.class);

      final String loginUser = templateOptions.getLoginUser() != null ? templateOptions.getLoginUser() : DEFAULT_LOGIN_USER;
      final String loginPassword = templateOptions.getLoginPassword() != null ? templateOptions.getLoginPassword() : DEFAULT_LOGIN_PASSWORD;
      final String location = template.getLocation().getId();
      final int[] inboundPorts = template.getOptions().getInboundPorts();

      final String storageAccountName = templateOptions.getStorageAccountName().get();
      final String virtualNetworkName = templateOptions.getVirtualNetworkName().get();
      final String subnetName = templateOptions.getSubnetName().get();

      logger.debug("Creating a cloud service with name '%s', label '%s' in location '%s'", name, name, location);
      String createCloudServiceRequestId = api.getCloudServiceApi().createWithLabelInLocation(name, name, location);
      if (!operationSucceededPredicate.apply(createCloudServiceRequestId)) {
         final String message = generateIllegalStateExceptionMessage(createCloudServiceRequestId, azureComputeConstants.operationTimeout());
         logger.warn(message);
         throw new IllegalStateException(message);
      }
      logger.info("Cloud Service (%s) created with operation id: %s", name, createCloudServiceRequestId);

      final OSImage.Type os = template.getImage().getOperatingSystem().getFamily().equals(OsFamily.WINDOWS) ? OSImage.Type.WINDOWS : OSImage.Type.LINUX;
      Set<ExternalEndpoint> externalEndpoints = Sets.newHashSet();
      for (int inboundPort : inboundPorts) {
         externalEndpoints.add(ExternalEndpoint.inboundTcpToLocalPort(inboundPort, inboundPort));
      }
      final DeploymentParams params = DeploymentParams.builder()
              .name(name)
              .os(os)
              .username(loginUser)
              .password(loginPassword)
              .sourceImageName(template.getImage().getId())
              .mediaLink(createMediaLink(storageAccountName, name))
              .size(RoleSize.Type.fromString(template.getHardware().getName()))
              .externalEndpoints(externalEndpoints)
              .subnetName(subnetName)
              .virtualNetworkName(virtualNetworkName)
              .build();

      logger.debug("Creating a deployment with params '%s' ...", params);
      String createDeploymentRequestId = api.getDeploymentApiForService(name).create(params);
      if (!operationSucceededPredicate.apply(createDeploymentRequestId)) {
         final String message = generateIllegalStateExceptionMessage(createCloudServiceRequestId, azureComputeConstants.operationTimeout());
         logger.warn(message);
         logger.debug("Deleting cloud service (%s) ...", name);
         deleteCloudService(name);
         logger.debug("Cloud service (%s) deleted.", name);
      }
      logger.info("Deployment created with operation id: %s", createDeploymentRequestId);

      if (!retry(new Predicate<String>() {
         public boolean apply(String name) {
            return FluentIterable.from(api.getDeploymentApiForService(name).get(name).roleInstanceList())
                    .allMatch(new Predicate<RoleInstance>() {
                       @Override
                       public boolean apply(RoleInstance input) {
                          return input != null && input.instanceStatus() == READY_ROLE;
                       }
                    });
         }
      }, 30 * 60, 1, SECONDS).apply(name)) {
         logger.warn("Instances %s of %s has not reached the status %s within %sms so it will be destroyed.",
                 Iterables.toString(api.getDeploymentApiForService(name).get(name).roleInstanceList()), name,
                 READY_ROLE, azureComputeConstants.operationTimeout());
         api.getDeploymentApiForService(group).delete(name);
         api.getCloudServiceApi().delete(name);
         throw new IllegalStateException(format("Deployment %s is being destroyed as its instanceStatus didn't reach " +
                 "status %s after %ss. Please, try by increasing `jclouds.azure.operation-timeout` and " +
                 " try again", name, READY_ROLE, 30 * 60));
      }

      Deployment deployment = api.getDeploymentApiForService(name).get(name);

      return new NodeAndInitialCredentials(deployment, deployment.name(),
              LoginCredentials.builder().user(loginUser).password(loginPassword).build());
   }

   public static String generateIllegalStateExceptionMessage(String operationId, long timeout) {
      final String warnMessage = format("%s has not been completed within %sms.", operationId, timeout);
      return format("%s. Please, try by increasing `%s` and try again", warnMessage, AzureComputeProperties.OPERATION_TIMEOUT);
   }

   @Override
   public Iterable<RoleSize> listHardwareProfiles() {
      return api.getSubscriptionApi().listRoleSizes();
   }

   @Override
   public Iterable<OSImage> listImages() {
      List<OSImage> osImages = Lists.newArrayList();
      for (OSImage osImage : api.getOSImageApi().list()) {
         Iterable<String> locations = Splitter.on(";").split(osImage.location());
         for (String location : locations) {
            osImages.add(OSImage.create(
                    osImage.name(),
                    location,
                    osImage.affinityGroup(),
                    osImage.label(),
                    osImage.description(),
                    osImage.category(),
                    osImage.os(),
                    osImage.publisherName(),
                    osImage.mediaLink(),
                    osImage.logicalSizeInGB(),
                    osImage.eula()
            ));
         }
      }
      return osImages;
   }

   @Override
   public OSImage getImage(final String id) {
      return Iterables.find(api.getOSImageApi().list(), new Predicate<OSImage>() {
         @Override
         public boolean apply(OSImage input) {
            return input.name().equals(id);
         }
      });
   }

   @Override
   public Iterable<Location> listLocations() {
      return api.getLocationApi().list();
   }

   @Override
   public Deployment getNode(final String id) {
      return FluentIterable.from(api.getCloudServiceApi().list())
              .transform(new Function<CloudService, Deployment>() {
                 @Override
                 public Deployment apply(CloudService input) {
                    return api.getDeploymentApiForService(input.name()).get(id);
                 }
              })
              .firstMatch(notNull())
              .orNull();
   }

   @Override
   public void destroyNode(final String id) {
      CloudService cloudService = api.getCloudServiceApi().get(id);
      if (cloudService != null) {
         // TODO detach disk before deleting node

         final String cloudServiceName = cloudService.name();
         logger.debug("Deleting deployment(%s) of cloud service (%s)", id, cloudServiceName);
         deleteDeployment(id, cloudServiceName);
         logger.debug("Deployment (%s) deleted in cloud service (%s).", id, cloudServiceName);

         logger.debug("Deleting cloud service (%s) ...", cloudServiceName);
         deleteCloudService(cloudServiceName);
         logger.debug("Cloud service (%s) deleted.", cloudServiceName);
      }
   }

   @Override
   public void rebootNode(final String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void resumeNode(String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void suspendNode(String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterable<Deployment> listNodes() {
      Set<Deployment> deployments = FluentIterable.from(api.getCloudServiceApi().list())
              .transform(new Function<CloudService, Deployment>() {
                 @Override
                 public Deployment apply(CloudService cloudService) {
                    return api.getDeploymentApiForService(cloudService.name()).get(cloudService.name());
                 }
              })
              .filter(notNull())
              .toSet();
      return deployments;
   }

   @Override
   public Iterable<Deployment> listNodesByIds(final Iterable<String> ids) {
      return Iterables.filter(listNodes(), new Predicate<Deployment>() {
         @Override
         public boolean apply(Deployment input) {
            return Iterables.contains(ids, input.name());
         }
      });
   }

   @VisibleForTesting
   public static URI createMediaLink(String storageServiceName, String diskName) {
      return URI.create(String.format("https://%s.blob.core.windows.net/vhds/disk-%s.vhd", storageServiceName, diskName));
   }

   private void deleteCloudService(String name) {
      String deleteCloudServiceId = api.getCloudServiceApi().delete(name);
      if (!operationSucceededPredicate.apply(deleteCloudServiceId)) {
         final String deleteMessage = generateIllegalStateExceptionMessage(deleteCloudServiceId, azureComputeConstants.operationTimeout());
         logger.warn(deleteMessage);
         throw new IllegalStateException(deleteMessage);
      }
   }

   private void deleteDeployment(String id, String cloudServiceName) {
      String deleteDeploymentId = api.getDeploymentApiForService(cloudServiceName).delete(id);
      if (!operationSucceededPredicate.apply(deleteDeploymentId)) {
         final String deleteMessage = generateIllegalStateExceptionMessage(deleteDeploymentId, azureComputeConstants.operationTimeout());
         logger.warn(deleteMessage);
         throw new IllegalStateException(deleteMessage);
      }
   }

}
