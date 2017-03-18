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

import static com.google.common.base.Objects.firstNonNull;
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
import org.jclouds.azurecompute.compute.functions.OSImageToImage;
import org.jclouds.azurecompute.compute.options.AzureComputeTemplateOptions;
import org.jclouds.azurecompute.config.AzureComputeProperties;
import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Deployment.RoleInstance;
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.DeploymentParams.ExternalEndpoint;
import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.util.ConflictManagementPredicate;
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
 * Defines the connection between the {@link AzureComputeApi} implementation and the jclouds
 * {@link org.jclouds.compute.ComputeService}.
 */
@Singleton
public class AzureComputeServiceAdapter implements ComputeServiceAdapter<Deployment, RoleSize, OSImage, Location> {

   private static final String DEFAULT_LOGIN_USER = "jclouds";

   private static final String DEFAULT_LOGIN_PASSWORD = "Azur3Compute!";
   public static final String POST_SHUTDOWN_ACTION = "StoppedDeallocated";
   private static final String POST_SHUTDOWN_ACTION_NO_DEALLOCATE = "Stopped";

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

      final String loginUser = firstNonNull(templateOptions.getLoginUser(), DEFAULT_LOGIN_USER);
      final String loginPassword = firstNonNull(templateOptions.getLoginPassword(), DEFAULT_LOGIN_PASSWORD);
      final String location = template.getLocation().getId();
      final int[] inboundPorts = template.getOptions().getInboundPorts();

      final String storageAccountName = templateOptions.getStorageAccountName();

      String message = String.format("Creating a cloud service with name '%s', label '%s' in location '%s'", name, name, location);
      logger.debug(message);
      final String createCloudServiceRequestId = api.getCloudServiceApi().createWithLabelInLocation(name, name, location);
      if (!operationSucceededPredicate.apply(createCloudServiceRequestId)) {
         final String exceptionMessage = generateIllegalStateExceptionMessage(message, createCloudServiceRequestId, azureComputeConstants.operationTimeout());
         logger.warn(exceptionMessage);
         throw new IllegalStateException(exceptionMessage);
      }
      logger.info("Cloud Service (%s) created with operation id: %s", name, createCloudServiceRequestId);

      final OSImage.Type os = template.getImage().getOperatingSystem().getFamily() == OsFamily.WINDOWS ?
              OSImage.Type.WINDOWS : OSImage.Type.LINUX;
      final Set<ExternalEndpoint> externalEndpoints = Sets.newHashSet();
      for (int inboundPort : inboundPorts) {
         externalEndpoints.add(ExternalEndpoint.inboundTcpToLocalPort(inboundPort, inboundPort));
      }

      final DeploymentParams.Builder paramsBuilder = DeploymentParams.builder()
              .name(name)
              .os(os)
              .username(loginUser)
              .password(loginPassword)
              .sourceImageName(OSImageToImage.fromGeoName(template.getImage().getId())[0])
              .mediaLink(createMediaLink(storageAccountName, name))
              .size(RoleSize.Type.fromString(template.getHardware().getName()))
              .externalEndpoints(externalEndpoints)
              .virtualNetworkName(templateOptions.getVirtualNetworkName())
              .subnetNames(templateOptions.getSubnetNames())
              .provisionGuestAgent(templateOptions.getProvisionGuestAgent());
      if (os.equals(OSImage.Type.WINDOWS)) {
         paramsBuilder.winrmUseHttps(templateOptions.getWinrmUseHttps());
      }
      final DeploymentParams params = paramsBuilder.build();

      message = String.format("Creating a deployment with params '%s' ...", params);
      logger.debug(message);

      if (!new ConflictManagementPredicate(api) {
         @Override
         protected String operation() {
            return api.getDeploymentApiForService(name).create(params);
         }
      }.apply(name)) {
         final String illegalStateExceptionMessage = generateIllegalStateExceptionMessage(message, createCloudServiceRequestId, azureComputeConstants.operationTimeout());
         logger.warn(illegalStateExceptionMessage);
         logger.debug("Deleting cloud service (%s) ...", name);
         deleteCloudService(name);
         logger.debug("Cloud service (%s) deleted.", name);
         throw new IllegalStateException(illegalStateExceptionMessage);
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
         final String illegalStateExceptionMessage = format("Deployment %s was not created within %sms so it will be destroyed.",
                 name, azureComputeConstants.operationTimeout());
         logger.warn(illegalStateExceptionMessage);

         api.getDeploymentApiForService(name).delete(name);
         api.getCloudServiceApi().delete(name);

         throw new IllegalStateException(illegalStateExceptionMessage);
      }

      final Deployment deployment = deployments.iterator().next();

      // check if the role inside the deployment is ready
      checkRoleStatusInDeployment(name, deployment);

      return new NodeAndInitialCredentials<Deployment>(deployment, name,
              LoginCredentials.builder().user(loginUser).password(loginPassword).authenticateSudo(true).build());
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

   /** Returns the {@code deployment} argument itself if already settled, otherwise {@code null}. */
   private Deployment isSettled(Deployment deployment) {
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

   @Override
   public Deployment getNode(final String id) {
      // all nodes created by this provider will always have a cloud service name equal to deployment name
      final Deployment deployment = api.getDeploymentApiForService(id).get(id);
      if (deployment != null) {
         return isSettled(deployment);
      }

      return FluentIterable.from(api.getCloudServiceApi().list()).
              transform(new Function<CloudService, Deployment>() {
                 @Override
                 public Deployment apply(final CloudService input) {
                    final Deployment deployment = api.getDeploymentApiForService(input.name()).get(id);
                    return isSettled(deployment);
                 }
              }).
              firstMatch(notNull()).
              orNull();
   }

   private void trackRequest(final String requestId) {
      if (!operationSucceededPredicate.apply(requestId)) {
         final String message = generateIllegalStateExceptionMessage(
                 "tracking request", requestId, azureComputeConstants.operationTimeout());
         logger.warn(message);
         throw new IllegalStateException(message);
      }
   }

   public Deployment internalDestroyNode(final String nodeId) {

      Deployment deployment = getDeploymentFromNodeId(nodeId);

      if (deployment == null) return null;

      final String deploymentName = deployment.name();
      String message = String.format("Deleting deployment(%s) of cloud service (%s)", nodeId, deploymentName);
      logger.debug(message);

         if (deployment != null) {
            for (Role role : deployment.roleList()) {
               trackRequest(api.getVirtualMachineApiForDeploymentInService(deploymentName, role.roleName()).shutdown(nodeId, POST_SHUTDOWN_ACTION));
            }

            deleteDeployment(deploymentName, nodeId);

            logger.debug("Deleting cloud service (%s) ...", deploymentName);
            trackRequest(api.getCloudServiceApi().delete(deploymentName));
            logger.debug("Cloud service (%s) deleted.", deploymentName);

            for (Role role : deployment.roleList()) {
               final Role.OSVirtualHardDisk disk = role.osVirtualHardDisk();
               if (disk != null) {
                  if (!new ConflictManagementPredicate(api, operationSucceededPredicate) {

                     @Override
                     protected String operation() {
                        return api.getDiskApi().delete(disk.diskName());
                     }
                  }.apply(nodeId)) {
                     final String illegalStateExceptionMessage = generateIllegalStateExceptionMessage("Delete disk " + disk.diskName(),
                             "Delete disk", azureComputeConstants.operationTimeout());
                     logger.warn(illegalStateExceptionMessage);
                  }
               }
            }
         }
      return deployment;
   }

   public Deployment getDeploymentFromNodeId(final String nodeId) {
      final List<Deployment> nodes = Lists.newArrayList();
      retry(new Predicate<String>() {
         @Override
         public boolean apply(final String input) {
            final Deployment deployment = getNode(nodeId);
            if (deployment != null) {
               nodes.add(deployment);
            }
            return !nodes.isEmpty();
         }
      }, 30 * 60, 1, SECONDS).apply(nodeId);

      return Iterables.getFirst(nodes, null);
   }

   @Override
   public void destroyNode(final String id) {
      logger.debug("Destroying %s ...", id);
      if (internalDestroyNode(id) != null) {
         logger.debug("Destroyed %s!", id);
      } else {
         logger.warn("Can't destroy %s!", id);
      }
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

         // it happens sometimes that even though the trackRequest call above returns successfully,
         // the node is still in the process of starting and this.getNode(id) returns null
         //
         // this is a temporary workaround for JCLOUDS-1092 and should be removed once the issue is resolved properly
         if (!retry(new Predicate<String>() {
            @Override
            public boolean apply(final String id) {
               return getNode(id) != null;
            }
         }, azureComputeConstants.operationTimeout(), 1, SECONDS).apply(id)) {
            final String message = generateIllegalStateExceptionMessage(
                    "waiting for node to resume", "", azureComputeConstants.operationTimeout());
            logger.warn(message);
            throw new IllegalStateException(message);
         }

         logger.debug("Resumed %s", id);
      }
   }

   @Override
   public void suspendNode(final String id) {
      final CloudService cloudService = api.getCloudServiceApi().get(id);
      if (cloudService != null) {
         logger.debug("Suspending %s ...", id);
         String postShutdownAction = azureComputeConstants.deallocateWhenSuspending()
                 ? POST_SHUTDOWN_ACTION : POST_SHUTDOWN_ACTION_NO_DEALLOCATE;
         trackRequest(api.getVirtualMachineApiForDeploymentInService(id, cloudService.name()).shutdown(id, postShutdownAction));
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
         final String deleteMessage = generateIllegalStateExceptionMessage("Delete cloud service " + name,
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
         final String deleteMessage = generateIllegalStateExceptionMessage("Delete deployment " + cloudServiceName,
                 "Deployment delete", azureComputeConstants.operationTimeout());
         logger.warn(deleteMessage);
         throw new IllegalStateException(deleteMessage);
      }
   }


   private void checkRoleStatusInDeployment(final String name, Deployment deployment) {
      if (!retry(new Predicate<Deployment>() {

         @Override
         public boolean apply(Deployment deployment) {
            deployment = api.getDeploymentApiForService(deployment.name()).get(name);
            if (deployment.roleInstanceList() == null || deployment.roleInstanceList().isEmpty()) return false;
            return Iterables.all(deployment.roleInstanceList(), new Predicate<RoleInstance>() {
               @Override
               public boolean apply(RoleInstance input) {
                  if (input.instanceStatus() == Deployment.InstanceStatus.PROVISIONING_FAILED) {
                     final String message = format("Deployment %s is in provisioning failed status, so it will be destroyed.", name);
                     logger.warn(message);

                     api.getDeploymentApiForService(name).delete(name);
                     api.getCloudServiceApi().delete(name);

                     throw new IllegalStateException(message);
                  }
                  return input.instanceStatus() == Deployment.InstanceStatus.READY_ROLE;
               }
            });
         }
      }, azureComputeConstants.operationTimeout(), 1, SECONDS).apply(deployment)) {
         final String message = format("Role %s has not reached the READY_ROLE within %sms so it will be destroyed.",
                 deployment.name(), azureComputeConstants.operationTimeout());
         logger.warn(message);

         api.getDeploymentApiForService(name).delete(name);
         api.getCloudServiceApi().delete(name);

         throw new IllegalStateException(message);
      }
   }

   public static String generateIllegalStateExceptionMessage(String prefix, final String operationId, final long timeout) {
      final String warnMessage = format("%s - %s has not been completed within %sms.", prefix, operationId, timeout);
      return format("%s. Please, try by increasing `%s` and try again",
              warnMessage, AzureComputeProperties.OPERATION_TIMEOUT);
   }

}
