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
package org.jclouds.azurecompute.binders;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.azurecompute.domain.OSImage.Type.LINUX;

import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.jamesmurty.utils.XMLBuilder;

public final class DeploymentParamsToXML implements Binder {

   @Override public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      DeploymentParams params = DeploymentParams.class.cast(input);

      try {
         XMLBuilder builder = XMLBuilder.create("Deployment", "http://schemas.microsoft.com/windowsazure")
            .e("Name").t(params.name()).up()
            .e("DeploymentSlot").t("Production").up()
            .e("Label").t(params.name()).up()
            .e("RoleList")
            .e("Role")
            .e("RoleName").t(params.name()).up()
            .e("RoleType").t("PersistentVMRole").up()
            .e("ConfigurationSets");

         if (params.os() == OSImage.Type.WINDOWS) {
            XMLBuilder configBuilder = builder.e("ConfigurationSet"); // Windows
            configBuilder.e("ConfigurationSetType").t("WindowsProvisioningConfiguration").up()
               .e("ComputerName").t(params.name()).up()
               .e("AdminPassword").t(params.password()).up()
               .e("ResetPasswordOnFirstLogon").t("false").up()
               .e("EnableAutomaticUpdate").t("false").up()
               .e("DomainJoin")
               .e("Credentials")
                  .e("Domain").t(params.name()).up()
                  .e("Username").t(params.username()).up()
                  .e("Password").t(params.password()).up()
               .up() // Credentials
               .e("JoinDomain").t(params.name()).up()
               .up() // Domain Join
               .e("StoredCertificateSettings").up()
               .up(); // Windows ConfigurationSet
         } else if (params.os() == OSImage.Type.LINUX) {
            XMLBuilder configBuilder = builder.e("ConfigurationSet"); // Linux
            configBuilder.e("ConfigurationSetType").t("LinuxProvisioningConfiguration").up()
               .e("HostName").t(params.name()).up()
               .e("UserName").t(params.username()).up()
               .e("UserPassword").t(params.password()).up()
               .e("DisableSshPasswordAuthentication").t("false").up()
               .e("SSH")
                    .e("PublicKeys").up()
                    .e("KeyPairs").up()
               .up(); // Linux ConfigurationSet
         } else {
            throw new IllegalArgumentException("Unrecognized os type " + params);
         }

         XMLBuilder configBuilder = builder.e("ConfigurationSet"); // Network
         configBuilder.e("ConfigurationSetType").t("NetworkConfiguration").up();

         XMLBuilder inputEndpoints = configBuilder.e("InputEndpoints");
         for (DeploymentParams.ExternalEndpoint endpoint : params.externalEndpoints()) {
            XMLBuilder inputBuilder = inputEndpoints.e("InputEndpoint");
            inputBuilder.e("LocalPort").t(Integer.toString(endpoint.localPort())).up()
               .e("Name").t(endpoint.name()).up()
               .e("Port").t(Integer.toString(endpoint.port())).up()
               .e("Protocol").t(endpoint.protocol().toLowerCase()).up()
               .up(); //InputEndpoint
         }

         inputEndpoints.up();
         //configBuilder.e("SubnetNames").up().up();

         XMLBuilder subnetNames = configBuilder.e("SubnetNames");
         for (String subnetName : params.subnetNames()) {
            subnetNames.e("SubnetName").t(subnetName).up()
                    .up(); //subnetName
         }

         builder.up() //ConfigurationSets
            // TODO No Disk should be specified for a Role if using a VMImage
            .e("DataVirtualHardDisks").up()
            .e("OSVirtualHardDisk")
            .e("HostCaching").t("ReadWrite").up()
            .e("MediaLink").t(params.mediaLink().toASCIIString()).up()
            // TODO
            /// If you are using a VM image, it must be specified as VMImageName for the role, not as SourceImageNamefor OSVirtualHardDisk.</Message></Error>]
            .e("SourceImageName").t(params.sourceImageName()).up()
            .e("OS").t(params.os() == LINUX ? "Linux" : "Windows").up()
             .up() //OSVirtualHardDisk
            .e("RoleSize").t(params.size().getText()).up()
            .up() //Role
            .up() //RoleList
                 .e("VirtualNetworkName").t(params.virtualNetworkName()).up();
         // TODO: Undeprecate this method as forcing users to wrap a String in guava's ByteSource is not great.
         return (R) request.toBuilder().payload(builder.asString()).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }

}
