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
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpsDto;
import com.abiquo.server.core.infrastructure.network.NetworkServiceTypeDto;
import com.abiquo.server.core.infrastructure.network.NetworkServiceTypesDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

/**
 * Expect tests for the {@link InfrastructureApi} class.
 */
@Test(groups = "unit", testName = "InfrastructureApiExpectTest")
public class InfrastructureApiExpectTest extends BaseAbiquoApiExpectTest<InfrastructureApi> {

   public void testListNetworkServiceTypesReturns2xx() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(NetworkServiceTypesDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-list.xml",
                              normalize(NetworkServiceTypesDto.MEDIA_TYPE))) //
                  .build());

      DatacenterDto datacenter = new DatacenterDto();
      datacenter.addLink(new RESTLink("networkservicetypes",
            "http://localhost/api/admin/datacenters/1/networkservicetypes"));

      NetworkServiceTypesDto nsts = api.listNetworkServiceTypes(datacenter);
      assertEquals(nsts.getCollection().size(), 2);
      assertEquals(nsts.getCollection().get(0).getName(), "Service Network");
      assertEquals(nsts.getCollection().get(1).getName(), "Storage Network");
   }

   public void testGetNetworkServiceTypeReturns2xx() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes/1")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(NetworkServiceTypeDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-edit.xml",
                              normalize(NetworkServiceTypeDto.MEDIA_TYPE))) //
                  .build());

      DatacenterDto datacenter = new DatacenterDto();
      datacenter.addLink(new RESTLink("networkservicetypes",
            "http://localhost/api/admin/datacenters/1/networkservicetypes"));

      NetworkServiceTypeDto created = api.getNetworkServiceType(datacenter, 1);
      assertNotNull(created.getId());
      assertEquals(created.getName(), "Service Network");
      assertEquals(created.isDefaultNST(), true);
   }

   public void testGetNetworkServiceTypeReturns4xx() {
      InfrastructureApi api = requestSendsResponse(HttpRequest.builder() //
            .method("GET") //
            .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes/1")) //
            .addHeader("Cookie", tokenAuth) //
            .addHeader("Accept", normalize(NetworkServiceTypeDto.MEDIA_TYPE)) //
            .build(), //
            HttpResponse.builder().statusCode(404).build());

      DatacenterDto datacenter = new DatacenterDto();
      datacenter.addLink(new RESTLink("networkservicetypes",
            "http://localhost/api/admin/datacenters/1/networkservicetypes"));

      assertNull(api.getNetworkServiceType(datacenter, 1));
   }

   public void testCreateNetworkServiceTypeReturns2xx() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("POST") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(NetworkServiceTypeDto.MEDIA_TYPE))
                  //
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-create.xml",
                              normalize(NetworkServiceTypeDto.MEDIA_TYPE))) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(201)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-edit.xml",
                              normalize(NetworkServiceTypeDto.MEDIA_TYPE))) //
                  .build());

      DatacenterDto datacenter = new DatacenterDto();
      datacenter.addLink(new RESTLink("networkservicetypes",
            "http://localhost/api/admin/datacenters/1/networkservicetypes"));

      NetworkServiceTypeDto nst = new NetworkServiceTypeDto();
      nst.setName("Service Network");
      nst.setDefaultNST(true);

      NetworkServiceTypeDto created = api.createNetworkServiceType(datacenter, nst);
      assertNotNull(created.getId());
      assertEquals(created.getName(), "Service Network");
      assertEquals(created.isDefaultNST(), true);
   }

   public void testUpdateNetworkServiceTypeReturns2xx() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("PUT") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes/1")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(NetworkServiceTypeDto.MEDIA_TYPE))
                  //
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-edit.xml",
                              normalize(NetworkServiceTypeDto.MEDIA_TYPE))) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-edit.xml",
                              normalize(NetworkServiceTypeDto.MEDIA_TYPE))) //
                  .build());

      NetworkServiceTypeDto nst = new NetworkServiceTypeDto();
      RESTLink editLink = new RESTLink("edit", "http://localhost/api/admin/datacenters/1/networkservicetypes/1");
      editLink.setType(NetworkServiceTypeDto.BASE_MEDIA_TYPE);
      nst.addLink(editLink);
      nst.setId(1);
      nst.setDefaultNST(true);
      nst.setName("Service Network");

      NetworkServiceTypeDto created = api.updateNetworkServiceType(nst);
      assertNotNull(created.getId());
      assertEquals(created.getName(), "Service Network");
   }

   public void testDeleteNetworkServiceTypeReturns2xx() {
      InfrastructureApi api = requestSendsResponse(HttpRequest.builder() //
            .method("DELETE") //
            .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes/1")) //
            .addHeader("Cookie", tokenAuth) //
            .build(), //
            HttpResponse.builder().statusCode(204).build());

      NetworkServiceTypeDto nst = new NetworkServiceTypeDto();
      RESTLink editLink = new RESTLink("edit", "http://localhost/api/admin/datacenters/1/networkservicetypes/1");
      editLink.setType(NetworkServiceTypeDto.BASE_MEDIA_TYPE);
      nst.addLink(editLink);

      api.deleteNetworkServiceType(nst);
   }

   public void testListPublicIps() {
      InfrastructureApi api = requestsSendResponses(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/network/1/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PublicIpsDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/publicips-page.xml",
                              normalize(PublicIpsDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/network/1/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PublicIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "3") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/publicips-lastpage.xml",
                              normalize(PublicIpsDto.MEDIA_TYPE))) //
                  .build());

      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.addLink(new RESTLink("ips", "http://localhost/api/admin/datacenters/1/network/1/ips"));

      PagedIterable<PublicIpDto> publicIps = api.listPublicIps(vlan);
      List<PublicIpDto> ips = publicIps.concat().toList();

      assertEquals(ips.size(), 4);
      assertEquals(ips.get(0).getId().intValue(), 1);
      assertEquals(ips.get(1).getId().intValue(), 2);
      assertEquals(ips.get(2).getId().intValue(), 3);
      assertEquals(ips.get(3).getId().intValue(), 4);
   }

   public void testListPublicIpsWithPagination() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/network/1/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(PublicIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "3") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/publicips-lastpage.xml",
                              normalize(PublicIpsDto.MEDIA_TYPE))) //
                  .build());

      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.addLink(new RESTLink("ips", "http://localhost/api/admin/datacenters/1/network/1/ips"));

      IpOptions options = IpOptions.builder().startWith(3).build();
      PaginatedCollection<PublicIpDto, PublicIpsDto> ips = api.listPublicIps(vlan, options);

      assertEquals(ips.size(), 2);
      assertEquals(ips.getTotalSize().intValue(), 4);
      assertEquals(ips.get(0).getId().intValue(), 3);
      assertEquals(ips.get(1).getId().intValue(), 4);
   }

   public void testListExternalIps() {
      InfrastructureApi api = requestsSendResponses(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/enterprises/2/limits/2/externalnetworks/2/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(ExternalIpsDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/externalips-page.xml",
                              normalize(ExternalIpsDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/enterprises/2/limits/2/externalnetworks/2/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(ExternalIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "3") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/externalips-lastpage.xml",
                              normalize(ExternalIpsDto.MEDIA_TYPE))) //
                  .build());

      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.addLink(new RESTLink("ips", "http://localhost/api/admin/enterprises/2/limits/2/externalnetworks/2/ips"));

      PagedIterable<ExternalIpDto> publicIps = api.listExternalIps(vlan);
      List<ExternalIpDto> ips = publicIps.concat().toList();

      assertEquals(ips.size(), 4);
      assertEquals(ips.get(0).getId().intValue(), 1);
      assertEquals(ips.get(1).getId().intValue(), 2);
      assertEquals(ips.get(2).getId().intValue(), 3);
      assertEquals(ips.get(3).getId().intValue(), 4);
   }

   public void testListExternalIpsWithPagination() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/enterprises/2/limits/2/externalnetworks/2/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(ExternalIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "3") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/externalips-lastpage.xml",
                              normalize(ExternalIpsDto.MEDIA_TYPE))) //
                  .build());

      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.addLink(new RESTLink("ips", "http://localhost/api/admin/enterprises/2/limits/2/externalnetworks/2/ips"));

      IpOptions options = IpOptions.builder().startWith(3).build();
      PaginatedCollection<ExternalIpDto, ExternalIpsDto> ips = api.listExternalIps(vlan, options);

      assertEquals(ips.size(), 2);
      assertEquals(ips.getTotalSize().intValue(), 4);
      assertEquals(ips.get(0).getId().intValue(), 3);
      assertEquals(ips.get(1).getId().intValue(), 4);
   }

   public void testListUnmanagedIps() {
      InfrastructureApi api = requestsSendResponses(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/enterprises/2/limits/2/externalnetworks/2/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(UnmanagedIpsDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/unmanagedips-page.xml",
                              normalize(UnmanagedIpsDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/enterprises/2/limits/2/externalnetworks/2/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(UnmanagedIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "3") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/unmanagedips-lastpage.xml",
                              normalize(UnmanagedIpsDto.MEDIA_TYPE))) //
                  .build());

      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.addLink(new RESTLink("ips", "http://localhost/api/admin/enterprises/2/limits/2/externalnetworks/2/ips"));

      PagedIterable<UnmanagedIpDto> unmanagedIps = api.listUnmanagedIps(vlan);
      List<UnmanagedIpDto> ips = unmanagedIps.concat().toList();

      assertEquals(ips.size(), 4);
      assertEquals(ips.get(0).getId().intValue(), 1);
      assertEquals(ips.get(1).getId().intValue(), 2);
      assertEquals(ips.get(2).getId().intValue(), 3);
      assertEquals(ips.get(3).getId().intValue(), 4);
   }

   public void testListUnmanagedIpsWithPagination() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/enterprises/2/limits/2/externalnetworks/2/ips")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(UnmanagedIpsDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "3") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/unmanagedips-lastpage.xml",
                              normalize(UnmanagedIpsDto.MEDIA_TYPE))) //
                  .build());

      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.addLink(new RESTLink("ips", "http://localhost/api/admin/enterprises/2/limits/2/externalnetworks/2/ips"));

      IpOptions options = IpOptions.builder().startWith(3).build();
      PaginatedCollection<UnmanagedIpDto, UnmanagedIpsDto> ips = api.listUnmanagedIps(vlan, options);

      assertEquals(ips.size(), 2);
      assertEquals(ips.getTotalSize().intValue(), 4);
      assertEquals(ips.get(0).getId().intValue(), 3);
      assertEquals(ips.get(1).getId().intValue(), 4);
   }

   @Override
   protected InfrastructureApi clientFrom(AbiquoApi api) {
      return api.getInfrastructureApi();
   }

}
