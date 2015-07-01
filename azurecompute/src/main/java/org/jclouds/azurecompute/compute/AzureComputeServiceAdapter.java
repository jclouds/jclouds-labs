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
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.DeploymentParams.ExternalEndpoint;
import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.domain.Deployment.RoleInstance;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.compute.functions.OSImageToImage;
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
import org.jclouds.azurecompute.util.ConflictManagementPredicate;

/**
 * Defines the connection between the {@link AzureComputeApi} implementation and the jclouds
 * {@link org.jclouds.compute.ComputeService}.
 */
@Singleton
public class AzureComputeServiceAdapter implements ComputeServiceAdapter<Deployment, RoleSize, OSImage, Location> {

   private static final String DEFAULT_LOGIN_USER = "jclouds";

   private static final String DEFAULT_LOGIN_PASSWORD = "Azur3Compute!";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;

   private final AzureComputeApi api;

   private final Predicate<String> operationSucceededPredicate;

   private final AzureComputeConstants azureComputeConstants;

   @Inject
   AzureComputeServiceAdapter(final AzureComputeApi api,
           final Predicate<String> operationSucceededPredicate, final AzureComputeConstants azureComputeConstants) {

      this.api = api;
      this.operationSucceededPredicate = operationSucceededPredicate;
      this.azureComputeConstants = azureComputeConstants;
   }

   @Override
   public NodeAndInitialCredentials<Deployment> createNodeWithGroupEncodedIntoName(
           final String group, final String name, final Template template) {

      // azure-specific options
      final AzureComputeTemplateOptions templateOptions = template.getOptions().as(AzureComputeTemplateOptions.class);

      final String loginUser = templateOptions.getLoginUser() == null
              ? DEFAULT_LOGIN_USER : templateOptions.getLoginUser();
      final String loginPassword = templateOptions.getLoginPassword() == null
              ? DEFAULT_LOGIN_PASSWORD : templateOptions.getLoginPassword();
      final String location = template.getLocation().getId();
      final int[] inboundPorts = template.getOptions().getInboundPorts();

      final String storageAccountName = templateOptions.getStorageAccountName().get();
      final String virtualNetworkName = templateOptions.getVirtualNetworkName().get();
      final String reservedIPAddress = templateOptions.getReservedIPName().orNull();
      final String subnetName = templateOptions.getSubnetName().get();

      logger.debug("Creating a cloud service with name '%s', label '%s' in location '%s'", name, name, location);
      final String createCloudServiceRequestId
              = api.getCloudServiceApi().createWithLabelInLocation(name, name, location);
      if (!operationSucceededPredicate.apply(createCloudServiceRequestId)) {
         final String message = generateIllegalStateExceptionMessage(
                 createCloudServiceRequestId, azureComputeConstants.operationTimeout());
         logger.warn(message);
         throw new IllegalStateException(message);
      }
      logger.info("Cloud Service (%s) created with operation id: %s", name, createCloudServiceRequestId);

      final OSImage.Type os = template.getImage().getOperatingSystem().getFamily() == OsFamily.WINDOWS
              ? OSImage.Type.WINDOWS : OSImage.Type.LINUX;
      final Set<ExternalEndpoint> externalEndpoints = Sets.newHashSet();
      for (int inboundPort : inboundPorts) {
         externalEndpoints.add(ExternalEndpoint.inboundTcpToLocalPort(inboundPort, inboundPort));
      }
      final DeploymentParams params = DeploymentParams.builder()
              .name(name)
              .os(os)
              .username(loginUser)
              .password(loginPassword)
              .sourceImageName(OSImageToImage.fromGeoName(template.getImage().getId())[0])
              .mediaLink(createMediaLink(storageAccountName, name))
              .size(RoleSize.Type.fromString(template.getHardware().getName()))
              .externalEndpoints(externalEndpoints)
              .subnetName(subnetName)
              .virtualNetworkName(virtualNetworkName)
              .reservedIPName(reservedIPAddress)
              .build();

      logger.debug("Creating a deployment with params '%s' ...", params);

      if (!new ConflictManagementPredicate(api) {
         @Override
         protected String operation() {
            return api.getDeploymentApiForService(name).create(params);
         }
      }.apply(name)) {
         final String message = generateIllegalStateExceptionMessage(
                 createCloudServiceRequestId, azureComputeConstants.operationTimeout());
         logger.warn(message);
         logger.debug("Deleting cloud service (%s) ...", name);
         deleteCloudService(name);
         logger.debug("Cloud service (%s) deleted.", name);
         throw new IllegalStateException(message);
      }

      logger.info("Deployment created with name: %s", name);

      final Set<Deployment> deployments = Sets.newHashSet();
      if (!retry(new Predicate<String>() {
         @Override
         public boolean apply(final String name) {
            final Deployment deployment = api.getDeploymentApiForService(name).get(name);
            if (deployment != null) {
               deployments.add(deployment);
            }
            return !deployments.isEmpty();
         }
      }, azureComputeConstants.operationTimeout(), 1, SECONDS).apply(name)) {
         final String message = format("Deployment %s was not created within %sms so it will be destroyed.",
                 name, azureComputeConstants.operationTimeout());
         logger.warn(message);

         api.getDeploymentApiForService(name).delete(name);
         api.getCloudServiceApi().delete(name);

         throw new IllegalStateException(message);
      }

      final Deployment deployment = deployments.iterator().next();
      return new NodeAndInitialCredentials<Deployment>(deployment, name,
              LoginCredentials.builder().user(loginUser).password(loginPassword).build());
   }

   public static String generateIllegalStateExceptionMessage(final String operationId, final long timeout) {
      final String warnMessage = format("%s has not been completed within %sms.", operationId, timeout);
      return format("%s. Please, try by increasing `%s` and try again",
              warnMessage, AzureComputeProperties.OPERATION_TIMEOUT);
   }

   @Override
   public Iterable<RoleSize> listHardwareProfiles() {
      return api.getSubscriptionApi().listRoleSizes();
   }

   @Override
   public Iterable<OSImage> listImages() {
      final List<OSImage> osImages = Lists.newArrayList();
      for (OSImage osImage : api.getOSImageApi().list()) {
         if (osImage.location() == null) {
            osImages.add(OSImage.create(
                    osImage.name(),
                    null,
                    osImage.affinityGroup(),
                    osImage.label(),
                    osImage.description(),
                    osImage.imageFamily(),
                    osImage.category(),
                    osImage.os(),
                    osImage.publisherName(),
                    osImage.mediaLink(),
                    osImage.logicalSizeInGB(),
                    osImage.eula()
            ));
         } else {
            for (String actualLocation : Splitter.on(';').split(osImage.location())) {
               osImages.add(OSImage.create(
                       OSImageToImage.toGeoName(osImage.name(), actualLocation),
                       actualLocation,
                       osImage.affinityGroup(),
                       osImage.label(),
                       osImage.description(),
                       osImage.imageFamily(),
                       osImage.category(),
                       osImage.os(),
                       osImage.publisherName(),
                       osImage.mediaLink(),
                       osImage.logicalSizeInGB(),
                       osImage.eula()
               ));
            }
         }
      }
      return osImages;
   }

   @Override
   public OSImage getImage(final String id) {
      final String[] idParts = OSImageToImage.fromGeoName(id);
      final OSImage image = Iterables.find(api.getOSImageApi().list(), new Predicate<OSImage>() {
         @Override
         public boolean apply(final OSImage input) {
            return idParts[0].equals(input.name());
         }
      });

      return image == null
              ? null
              : idParts[1] == null
                      ? image
                      : OSImage.create(
                              id,
                              idParts[1],
                              image.affinityGroup(),
                              image.label(),
                              image.description(),
                              image.imageFamily(),
                              image.category(),
                              image.os(),
                              image.publisherName(),
                              image.mediaLink(),
                              image.logicalSizeInGB(),
                              image.eula());
   }

   @Override
   public Iterable<Location> listLocations() {
      return api.getLocationApi().list();
   }

   @Override
   public Deployment getNode(final String id) {
      return FluentIterable.from(api.getCloudServiceApi().list()).
              transform(new Function<CloudService, Deployment>() {
                 @Override
                 public Deployment apply(final CloudService input) {
                    final Deployment deployment = api.getDeploymentApiForService(input.name()).get(id);
                    return deployment == null || deployment.roleInstanceList().isEmpty()
                            ? null
                            : FluentIterable.from(deployment.roleInstanceList()).allMatch(
                                    new Predicate<RoleInstance>() {
                                       @Override
                                       public boolean apply(final RoleInstance input) {
                                          return input != null && !input.instanceStatus().isTransient();
                                       }
                                    })
                                    ? deployment
                                    : null;
                 }
              }).
              firstMatch(notNull()).
              orNull();
   }

   private void trackRequest(final String requestId) {
      if (!operationSucceededPredicate.apply(requestId)) {
         final String message = generateIllegalStateExceptionMessage(
                 requestId, azureComputeConstants.operationTimeout());
         logger.warn(message);
         throw new IllegalStateException(message);
      }
   }

   private List<CloudService> getCloudServicesForDeployment(final String id) {
      return FluentIterable.from(api.getCloudServiceApi().list()).filter(new Predicate<CloudService>() {

         @Override
         public boolean apply(final CloudService input) {
            final Deployment deployment
                    = input.status() == CloudService.Status.DELETING || input.status() == CloudService.Status.DELETED
                            ? null
                            : api.getDeploymentApiForService(input.name()).get(id);
            return deployment != null && deployment.status() != Deployment.Status.DELETING;
         }
      }).toList();
   }

   public Deployment internalDestroyNode(final String id) {
      Deployment deployment = null;

      for (CloudService cloudService : getCloudServicesForDeployment(id)) {
         final List<Deployment> nodes = Lists.newArrayList();
         retry(new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
               final Deployment deployment = getNode(id);
               if (deployment != null) {
                  nodes.add(deployment);
               }
               return !nodes.isEmpty();
            }
         }, 30 * 60, 1, SECONDS).apply(id);

         if (!nodes.isEmpty()) {
            deployment = nodes.iterator().next();
         }

         final String cloudServiceName = cloudService.name();
         logger.debug("Deleting deployment(%s) of cloud service (%s)", id, cloudServiceName);

         if (!new ConflictManagementPredicate(api, operationSucceededPredicate) {

            @Override
            protected String operation() {
               return api.getDeploymentApiForService(cloudServiceName).delete(id);
            }
         }.apply(id)) {
            final String message = generateIllegalStateExceptionMessage(
                    "Delete deployment", azureComputeConstants.operationTimeout());
            logger.warn(message);
            throw new IllegalStateException(message);
         }

         logger.debug("Deleting cloud service (%s) ...", cloudServiceName);
         trackRequest(api.getCloudServiceApi().delete(cloudServiceName));
         logger.debug("Cloud service (%s) deleted.", cloudServiceName);

         if (deployment != null) {
            for (Role role : deployment.roleList()) {
               final Role.OSVirtualHardDisk disk = role.osVirtualHardDisk();
               if (disk != null) {
                  if (!new ConflictManagementPredicate(api, operationSucceededPredicate) {

                     @Override
                     protected String operation() {
                        return api.getDiskApi().delete(disk.diskName());
                     }
                  }.apply(id)) {
                     final String message = generateIllegalStateExceptionMessage(
                             "Delete disk", azureComputeConstants.operationTimeout());
                     logger.warn(message);
                  }
               }
            }
         }
      }

      return deployment;
   }

   @Override
   public void destroyNode(final String id) {
      internalDestroyNode(id);
   }

   @Override
   public void rebootNode(final String id) {
      final CloudService cloudService = api.getCloudServiceApi().get(id);
      if (cloudService != null) {
         logger.debug("Restarting %s ...", id);
         trackRequest(api.getVirtualMachineApiForDeploymentInService(id, cloudService.name()).restart(id));
         logger.debug("Restarted %s", id);
      }
   }

   @Override
   public void resumeNode(final String id) {
      final CloudService cloudService = api.getCloudServiceApi().get(id);
      if (cloudService != null) {
         logger.debug("Resuming %s ...", id);
         trackRequest(api.getVirtualMachineApiForDeploymentInService(id, cloudService.name()).start(id));
         logger.debug("Resumed %s", id);
      }
   }

   @Override
   public void suspendNode(final String id) {
      final CloudService cloudService = api.getCloudServiceApi().get(id);
      if (cloudService != null) {
         logger.debug("Suspending %s ...", id);
         trackRequest(api.getVirtualMachineApiForDeploymentInService(id, cloudService.name()).shutdown(id));
         logger.debug("Suspended %s", id);
      }
   }

   @Override
   public Iterable<Deployment> listNodes() {
      return FluentIterable.from(api.getCloudServiceApi().list()).
              transform(new Function<CloudService, Deployment>() {
                 @Override
                 public Deployment apply(final CloudService cloudService) {
                    return api.getDeploymentApiForService(cloudService.name()).get(cloudService.name());
                 }
              }).
              filter(notNull()).
              toSet();
   }

   @Override
   public Iterable<Deployment> listNodesByIds(final Iterable<String> ids) {
      return Iterables.filter(listNodes(), new Predicate<Deployment>() {
         @Override
         public boolean apply(final Deployment input) {
            return Iterables.contains(ids, input.name());
         }
      });
   }

   @VisibleForTesting
   public static URI createMediaLink(final String storageServiceName, final String diskName) {
      return URI.create(
              String.format("https://%s.blob.core.windows.net/vhds/disk-%s.vhd", storageServiceName, diskName));
   }

   private void deleteCloudService(final String name) {
      if (!new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api.getCloudServiceApi().delete(name);
         }

      }.apply(name)) {
         final String deleteMessage = generateIllegalStateExceptionMessage(
                 "CloudService delete", azureComputeConstants.operationTimeout());
         logger.warn(deleteMessage);
         throw new IllegalStateException(deleteMessage);
      }
   }

   private void deleteDeployment(final String id, final String cloudServiceName) {
      if (!new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api.getDeploymentApiForService(cloudServiceName).delete(id);
         }

      }.apply(id)) {
         final String deleteMessage = generateIllegalStateExceptionMessage(
                 "Deployment delete", azureComputeConstants.operationTimeout());
         logger.warn(deleteMessage);
         throw new IllegalStateException(deleteMessage);
      }
   }
}
