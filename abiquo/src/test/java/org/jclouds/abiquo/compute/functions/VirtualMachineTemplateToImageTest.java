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
package org.jclouds.abiquo.compute.functions;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.easymock.EasyMock;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;
import org.jclouds.rest.ApiContext;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * Unit tests for the {@link VirtualMachineTemplateToImage} class.
 */
@Test(groups = "unit", testName = "VirtualMachineTemplateToImageTest")
public class VirtualMachineTemplateToImageTest {
   @SuppressWarnings("unchecked")
   public void testVirtualMachineTemplateToImage() {
      ApiContext<AbiquoApi> context = EasyMock.createMock(ApiContext.class);
      Function<Datacenter, Location> dcToLocation = mockDatacenterToLocation();
      Supplier<Map<Integer, Datacenter>> regionMap = mockRegionMap();
      VirtualMachineTemplateToImage function = new VirtualMachineTemplateToImage(dcToLocation, regionMap);

      VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
      dto.setId(5);
      dto.setName("Template");
      dto.setDescription("Template description");
      dto.addLink(new RESTLink("diskfile", "http://foo/bar"));
      dto.addLink(new RESTLink("datacenter", "http://foo/bar/4"));

      Image image = function.apply(wrap(context, VirtualMachineTemplate.class, dto));

      verify(regionMap);
      verify(dcToLocation);

      assertEquals(image.getId(), dto.getId().toString());
      assertEquals(image.getName(), dto.getName());
      assertEquals(image.getDescription(), dto.getDescription());
      assertEquals(image.getUri(), URI.create("http://foo/bar"));
      assertEquals(image.getOperatingSystem(), OperatingSystem.builder().description(dto.getName()).build());
   }

   @SuppressWarnings("unchecked")
   public void testConvertWithoutDownloadLink() {
      ApiContext<AbiquoApi> context = EasyMock.createMock(ApiContext.class);
      Function<Datacenter, Location> dcToLocation = mockDatacenterToLocation();
      Supplier<Map<Integer, Datacenter>> regionMap = mockRegionMap();
      VirtualMachineTemplateToImage function = new VirtualMachineTemplateToImage(dcToLocation, regionMap);

      VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
      dto.setId(5);
      dto.setName("Template");
      dto.setDescription("Template description");
      dto.addLink(new RESTLink("datacenter", "http://foo/bar/4"));

      Image image = function.apply(wrap(context, VirtualMachineTemplate.class, dto));

      verify(regionMap);
      verify(dcToLocation);

      assertNull(image.getUri());
   }

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = NullPointerException.class)
   public void testConvertWithoutId() {
      ApiContext<AbiquoApi> context = EasyMock.createMock(ApiContext.class);
      Function<Datacenter, Location> dcToLocation = mockDatacenterToLocation();
      Supplier<Map<Integer, Datacenter>> regionMap = mockRegionMap();
      VirtualMachineTemplateToImage function = new VirtualMachineTemplateToImage(dcToLocation, regionMap);

      VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
      function.apply(wrap(context, VirtualMachineTemplate.class, dto));
   }

   @SuppressWarnings("unchecked")
   private static Function<Datacenter, Location> mockDatacenterToLocation() {
      Function<Datacenter, Location> mock = EasyMock.createMock(Function.class);
      expect(mock.apply(anyObject(Datacenter.class))).andReturn(null);
      replay(mock);
      return mock;
   }

   @SuppressWarnings("unchecked")
   private static Supplier<Map<Integer, Datacenter>> mockRegionMap() {
      Supplier<Map<Integer, Datacenter>> mock = EasyMock.createMock(Supplier.class);
      expect(mock.get()).andReturn(Collections.EMPTY_MAP);
      replay(mock);
      return mock;
   }
}
