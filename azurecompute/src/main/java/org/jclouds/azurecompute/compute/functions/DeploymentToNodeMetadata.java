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
package org.jclouds.azurecompute.compute.functions;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Deployment.RoleInstance;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class DeploymentToNodeMetadata implements Function<Deployment, NodeMetadata> {

	private final Supplier<Set<? extends Location>> locations;
	private final GroupNamingConvention nodeNamingConvention;
	private final OSImageToImage osImageToImage;
	private final RoleSizeToHardware roleSizeToHardware;
	private final Map<String, Credentials> credentialStore;

	public static final Map<Deployment.Status, NodeMetadata.Status> serverStateToNodeStatus = ImmutableMap
			  .<Deployment.Status, NodeMetadata.Status> builder()
			  .put(Deployment.Status.DELETING, NodeMetadata.Status.PENDING)
			  .put(Deployment.Status.SUSPENDED_TRANSITIONING, NodeMetadata.Status.PENDING)
			  .put(Deployment.Status.RUNNING_TRANSITIONING, NodeMetadata.Status.PENDING)
			  .put(Deployment.Status.DEPLOYING, NodeMetadata.Status.PENDING)
			  .put(Deployment.Status.STARTING, NodeMetadata.Status.PENDING)
			  .put(Deployment.Status.SUSPENDED, NodeMetadata.Status.SUSPENDED)
			  .put(Deployment.Status.RUNNING, NodeMetadata.Status.RUNNING)
			  .put(Deployment.Status.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED).build();

	@Inject
	DeploymentToNodeMetadata(@Memoized Supplier<Set<? extends Location>> locations,
										GroupNamingConvention.Factory namingConvention, OSImageToImage osImageToImage,
										RoleSizeToHardware roleSizeToHardware, Map<String, Credentials> credentialStore) {
		this.nodeNamingConvention = namingConvention.createWithoutPrefix();
		this.locations = locations;
		this.osImageToImage = osImageToImage;
		this.roleSizeToHardware = roleSizeToHardware;
		this.credentialStore = credentialStore;
	}

	@Override
	public NodeMetadata apply(Deployment from) {
		NodeMetadataBuilder builder = new NodeMetadataBuilder();
		builder.id(from.name());
      builder.providerId(from.name());
		builder.name(from.name());
		builder.hostname(getHostname(from));
		/* TODO
		if (from.getDatacenter() != null) {
			builder.location(from(locations.get()).firstMatch(
					  LocationPredicates.idEquals(from.getDatacenter().getId() + "")).orNull());
		}
		builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getHostname()));
		builder.hardware(roleSizeToHardware.apply(from.instanceSize()));
		Image image = osImageToImage.apply(from);
		if (image != null) {
			builder.imageId(image.getId());
			builder.operatingSystem(image.getOperatingSystem());
		}
		*/
		if (from.status() != null) {
			builder.status(serverStateToNodeStatus.get(from.status()));
		}
		Set<String> publicIpAddresses = Sets.newLinkedHashSet();
		if (from.virtualIPs() != null) {
			for (Deployment.VirtualIP virtualIP : from.virtualIPs()) {
				publicIpAddresses.add(virtualIP.address());
			}
			builder.publicAddresses(publicIpAddresses);
		}
		Set<String> privateIpAddresses = Sets.newLinkedHashSet();
		if (from.roleInstanceList() != null) {
			for (RoleInstance roleInstance : from.roleInstanceList()) {
				privateIpAddresses.add(roleInstance.ipAddress());
			}
			builder.privateAddresses(privateIpAddresses);
		}
		return builder.build();
	}

	private String getHostname(Deployment from) {
      final Optional<RoleInstance> roleInstanceOptional = tryFindFirstRoleInstanceInDeployment(from);
      if (!roleInstanceOptional.isPresent()) {
         return from.name();
      } else {
         return roleInstanceOptional.get().hostname();
      }
   }

   private Optional<RoleInstance> tryFindFirstRoleInstanceInDeployment(Deployment deployment) {
      if (deployment.roleInstanceList() == null || deployment.roleInstanceList().isEmpty()) return Optional.absent();
      return Optional.of(deployment.roleInstanceList().get(0));
   }

}
