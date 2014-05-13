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

package org.jclouds.abiquo.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineTemplateOptions;
import org.jclouds.abiquo.domain.cloud.options.VolumeOptions;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineInstanceDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpsDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;
import com.abiquo.server.core.infrastructure.storage.VolumesManagementDto;

/**
 * Expect tests for the {@link CloudApi} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "CloudApiExpectTest")
public class CloudApiExpectTest extends BaseAbiquoApiExpectTest<CloudApi> {

   public void testListAllVirtualMachinesWhenResponseIs2xx() {
      CloudApi api = requestsSendResponses(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualmachines")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/all-vms.xml",
                              normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualmachines")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "2").build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/all-vms-lastpage.xml",
                              normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE))) //
                  .build());

      PagedIterable<VirtualMachineWithNodeExtendedDto> result = api.listAllVirtualMachines();
      List<VirtualMachineWithNodeExtendedDto> vms = result.concat().toList();

      assertEquals(vms.size(), 2);
      assertEquals(vms.get(0).getId(), Integer.valueOf(1));
      assertEquals(vms.get(1).getId(), Integer.valueOf(2));
      assertEquals(vms.get(0).getName(), "VM");
      assertNotNull(vms.get(0).getEditLink());
   }

   public void testListAllVirtualMachinesWithPagination() {
      CloudApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualmachines")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "2").build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/all-vms-lastpage.xml",
                              normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE))) //
                  .build());

      VirtualMachineOptions options = VirtualMachineOptions.builder().startWith(2).build();
      PaginatedCollection<VirtualMachineWithNodeExtendedDto, VirtualMachinesWithNodeExtendedDto> vms = api
            .listAllVirtualMachines(options);

      assertEquals(vms.size(), 1);
      assertEquals(vms.getTotalSize().intValue(), 2);
      assertEquals(vms.get(0).getId().intValue(), 2);
   }

   public void testListVirtualMachinesWhenResponseIs2xx() {
      CloudApi api = requestsSendResponses(
            HttpRequest.builder() //
                  .method("GET")
                  //
                  .endpoint(
                        URI.create("http://localhost/api/cloud/virtualdatacenters/1/virtualappliances/1/virtualmachines")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/vms-page.xml",
                              normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder() //
                  .method("GET")
                  //
                  .endpoint(
                        URI.create("http://localhost/api/cloud/virtualdatacenters/1/virtualappliances/1/virtualmachines")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "2").build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/vms-lastpage.xml",
                              normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE))) //
                  .build());

      VirtualApplianceDto vapp = new VirtualApplianceDto();
      vapp.addLink(new RESTLink("virtualmachines",
            "http://localhost/api/cloud/virtualdatacenters/1/virtualappliances/1/virtualmachines"));

      PagedIterable<VirtualMachineWithNodeExtendedDto> result = api.listVirtualMachines(vapp);
      List<VirtualMachineWithNodeExtendedDto> vms = result.concat().toList();

      assertEquals(vms.size(), 2);
      assertEquals(vms.get(0).getId(), Integer.valueOf(1));
      assertEquals(vms.get(1).getId(), Integer.valueOf(2));
      assertEquals(vms.get(0).getName(), "VM");
      assertNotNull(vms.get(0).getEditLink());
   }

   public void testLisVirtualMachinesWithPagination() {
      CloudApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET")
                  //
                  .endpoint(
                        URI.create("http://localhost/api/cloud/virtualdatacenters/1/virtualappliances/1/virtualmachines")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "2").build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/vms-lastpage.xml",
                              normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE))) //
                  .build());

      VirtualApplianceDto vapp = new VirtualApplianceDto();
      vapp.addLink(new RESTLink("virtualmachines",
            "http://localhost/api/cloud/virtualdatacenters/1/virtualappliances/1/virtualmachines"));

      VirtualMachineOptions options = VirtualMachineOptions.builder().startWith(2).build();
      PaginatedCollection<VirtualMachineWithNodeExtendedDto, VirtualMachinesWithNodeExtendedDto> vms = api
            .listVirtualMachines(vapp, options);

      assertEquals(vms.size(), 1);
      assertEquals(vms.getTotalSize().intValue(), 2);
      assertEquals(vms.get(0).getId().intValue(), 2);
   }

   public void testSnapshotVirtualMachineReturns2xx() {
      CloudApi api = requestSendsResponse(
            HttpRequest
                  .builder()
                  .method("POST")
                  .endpoint(
                        URI.create("http://localhost/api/admin/virtualdatacenters/1/virtualappliances/1/virtualmachines/1/action/instance")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(AcceptedRequestDto.MEDIA_TYPE)) //
                  .payload(
                        payloadFromResourceWithContentType("/payloads/vm-snapshot.xml",
                              normalize(VirtualMachineInstanceDto.MEDIA_TYPE))) //
                  .build(), //
            HttpResponse
                  .builder()
                  .statusCode(202)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/vm-accepted-request.xml",
                              normalize(VirtualMachineInstanceDto.MEDIA_TYPE))).build());

      VirtualMachineDto vm = new VirtualMachineDto();
      vm.addLink(new RESTLink("instance",
            "http://localhost/api/admin/virtualdatacenters/1/virtualappliances/1/virtualmachines/1/action/instance"));
      VirtualMachineInstanceDto snapshotConfig = new VirtualMachineInstanceDto();
      snapshotConfig.setInstanceName("foo");

      AcceptedRequestDto<String> taskRef = api.snapshotVirtualMachine(vm, snapshotConfig);
      assertNotNull(taskRef);
   }

   public void testListAvailablePublicIps() {
      CloudApi api = requestsSendResponses(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualdatacenters/1/publicips/topurchase")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PublicIpsDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/publicips-available-page.xml",
                              normalize(PublicIpsDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualdatacenters/1/publicips/topurchase")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PublicIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "3") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/publicips-available-lastpage.xml",
                              normalize(PublicIpsDto.MEDIA_TYPE))) //
                  .build());

      VirtualDatacenterDto vdc = new VirtualDatacenterDto();
      vdc.addLink(new RESTLink("topurchase", "http://localhost/api/cloud/virtualdatacenters/1/publicips/topurchase"));

      PagedIterable<PublicIpDto> publicIps = api.listAvailablePublicIps(vdc);
      List<PublicIpDto> ips = publicIps.concat().toList();

      assertEquals(ips.size(), 4);
      assertEquals(ips.get(0).getId().intValue(), 1);
      assertEquals(ips.get(1).getId().intValue(), 2);
      assertEquals(ips.get(2).getId().intValue(), 3);
      assertEquals(ips.get(3).getId().intValue(), 4);
   }

   public void testListAvailablePublicIpsWithPagination() {
      CloudApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualdatacenters/1/publicips/topurchase")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PublicIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "3") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/publicips-available-lastpage.xml",
                              normalize(PublicIpsDto.MEDIA_TYPE))) //
                  .build());

      VirtualDatacenterDto vdc = new VirtualDatacenterDto();
      vdc.addLink(new RESTLink("topurchase", "http://localhost/api/cloud/virtualdatacenters/1/publicips/topurchase"));

      IpOptions options = IpOptions.builder().startWith(3).build();
      PaginatedCollection<PublicIpDto, PublicIpsDto> ips = api.listAvailablePublicIps(vdc, options);

      assertEquals(ips.size(), 2);
      assertEquals(ips.getTotalSize().intValue(), 4);
      assertEquals(ips.get(0).getId().intValue(), 3);
      assertEquals(ips.get(1).getId().intValue(), 4);
   }

   public void testListPurchasedPublicIps() {
      CloudApi api = requestsSendResponses(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualdatacenters/1/publicips/purchased")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PublicIpsDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/publicips-purchased-page.xml",
                              normalize(PublicIpsDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualdatacenters/1/publicips/purchased")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PublicIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "3") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/publicips-purchased-lastpage.xml",
                              normalize(PublicIpsDto.MEDIA_TYPE))) //
                  .build());

      VirtualDatacenterDto vdc = new VirtualDatacenterDto();
      vdc.addLink(new RESTLink("purchased", "http://localhost/api/cloud/virtualdatacenters/1/publicips/purchased"));

      PagedIterable<PublicIpDto> publicIps = api.listPurchasedPublicIps(vdc);
      List<PublicIpDto> ips = publicIps.concat().toList();

      assertEquals(ips.size(), 4);
      assertEquals(ips.get(0).getId().intValue(), 1);
      assertEquals(ips.get(1).getId().intValue(), 2);
      assertEquals(ips.get(2).getId().intValue(), 3);
      assertEquals(ips.get(3).getId().intValue(), 4);
   }

   public void testListPurchasedPublicIpsWithPagination() {
      CloudApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualdatacenters/1/publicips/purchased")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PublicIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "3") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/publicips-purchased-lastpage.xml",
                              normalize(PublicIpsDto.MEDIA_TYPE))) //
                  .build());

      VirtualDatacenterDto vdc = new VirtualDatacenterDto();
      vdc.addLink(new RESTLink("purchased", "http://localhost/api/cloud/virtualdatacenters/1/publicips/purchased"));

      IpOptions options = IpOptions.builder().startWith(3).build();
      PaginatedCollection<PublicIpDto, PublicIpsDto> ips = api.listPurchasedPublicIps(vdc, options);

      assertEquals(ips.size(), 2);
      assertEquals(ips.getTotalSize().intValue(), 4);
      assertEquals(ips.get(0).getId().intValue(), 3);
      assertEquals(ips.get(1).getId().intValue(), 4);
   }

   public void testListPrivteIps() {
      CloudApi api = requestsSendResponses(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualdatacenters/1/privatenetworks/1/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PrivateIpsDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/privateips-page.xml",
                              normalize(PublicIpsDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualdatacenters/1/privatenetworks/1/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PrivateIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "1") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/privateips-lastpage.xml",
                              normalize(PrivateIpsDto.MEDIA_TYPE))) //
                  .build());

      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.addLink(new RESTLink("ips", "http://localhost/api/cloud/virtualdatacenters/1/privatenetworks/1/ips"));

      PagedIterable<PrivateIpDto> privateIps = api.listPrivateNetworkIps(vlan);
      List<PrivateIpDto> ips = privateIps.concat().toList();

      assertEquals(ips.size(), 2);
      assertEquals(ips.get(0).getId().intValue(), 1);
      assertEquals(ips.get(1).getId().intValue(), 2);
   }

   public void testListPrivateIpsWithPagination() {
      CloudApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualdatacenters/1/privatenetworks/1/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PrivateIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "1") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/privateips-lastpage.xml",
                              normalize(PrivateIpsDto.MEDIA_TYPE))) //
                  .build());

      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.addLink(new RESTLink("ips", "http://localhost/api/cloud/virtualdatacenters/1/privatenetworks/1/ips"));

      IpOptions options = IpOptions.builder().startWith(1).build();
      PaginatedCollection<PrivateIpDto, PrivateIpsDto> ips = api.listPrivateNetworkIps(vlan, options);

      assertEquals(ips.size(), 1);
      assertEquals(ips.getTotalSize().intValue(), 2);
      assertEquals(ips.get(0).getId().intValue(), 2);
   }

   public void testListAvailableTemplates() {
      CloudApi api = requestsSendResponses(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://example.com/api/cloud/virtualdatacenters/1/action/templates")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VirtualMachineTemplatesDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/available-templates-page.xml",
                              normalize(VirtualMachineTemplatesDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://example.com/api/cloud/virtualdatacenters/1/action/templates")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VirtualMachineTemplatesDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "1") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/available-templates-lastpage.xml",
                              normalize(VirtualMachineTemplatesDto.MEDIA_TYPE))) //
                  .build());

      VirtualDatacenterDto vdc = new VirtualDatacenterDto();
      vdc.addLink(new RESTLink("templates", "http://example.com/api/cloud/virtualdatacenters/1/action/templates"));

      PagedIterable<VirtualMachineTemplateDto> templates = api.listAvailableTemplates(vdc);
      List<VirtualMachineTemplateDto> all = templates.concat().toList();

      assertEquals(all.size(), 2);
      assertEquals(all.get(0).getId().intValue(), 15);
      assertEquals(all.get(1).getId().intValue(), 16);
   }

   public void testListAvailableTemplatesWithPagination() {
      CloudApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://example.com/api/cloud/virtualdatacenters/1/action/templates")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VirtualMachineTemplatesDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "1") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/available-templates-lastpage.xml",
                              normalize(VirtualMachineTemplatesDto.MEDIA_TYPE))) //
                  .build());

      VirtualDatacenterDto vdc = new VirtualDatacenterDto();
      vdc.addLink(new RESTLink("templates", "http://example.com/api/cloud/virtualdatacenters/1/action/templates"));

      VirtualMachineTemplateOptions options = VirtualMachineTemplateOptions.builder().startWith(1).build();
      PaginatedCollection<VirtualMachineTemplateDto, VirtualMachineTemplatesDto> templates = api
            .listAvailableTemplates(vdc, options);

      assertEquals(templates.size(), 1);
      assertEquals(templates.getTotalSize().intValue(), 2);
      assertEquals(templates.get(0).getId().intValue(), 16);
   }

   public void testListVolumes() {
      CloudApi api = requestsSendResponses(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://example.com/api/cloud/virtualdatacenters/1/volumes")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VolumesManagementDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/volumes-page.xml",
                              normalize(VolumesManagementDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://example.com/api/cloud/virtualdatacenters/1/volumes")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VolumesManagementDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "1") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/volumes-lastpage.xml",
                              normalize(VolumesManagementDto.MEDIA_TYPE))) //
                  .build());

      VirtualDatacenterDto vdc = new VirtualDatacenterDto();
      vdc.addLink(new RESTLink("volumes", "http://example.com/api/cloud/virtualdatacenters/1/volumes"));

      PagedIterable<VolumeManagementDto> volumes = api.listVolumes(vdc);
      List<VolumeManagementDto> all = volumes.concat().toList();

      assertEquals(all.size(), 2);
      assertEquals(all.get(0).getId().intValue(), 1530);
      assertEquals(all.get(1).getId().intValue(), 1531);
   }

   public void testListVolumesWithPagination() {
      CloudApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://example.com/api/cloud/virtualdatacenters/1/volumes")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(VolumesManagementDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "1") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/volumes-lastpage.xml",
                              normalize(VolumesManagementDto.MEDIA_TYPE))) //
                  .build());

      VirtualDatacenterDto vdc = new VirtualDatacenterDto();
      vdc.addLink(new RESTLink("volumes", "http://example.com/api/cloud/virtualdatacenters/1/volumes"));

      VolumeOptions options = VolumeOptions.builder().startWith(1).build();
      PaginatedCollection<VolumeManagementDto, VolumesManagementDto> templates = api.listVolumes(vdc, options);

      assertEquals(templates.size(), 1);
      assertEquals(templates.getTotalSize().intValue(), 2);
      assertEquals(templates.get(0).getId().intValue(), 1531);
   }

   @Override
   protected CloudApi clientFrom(AbiquoApi api) {
      return api.getCloudApi();
   }

}
