/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.abiquo.features;

import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineInstanceDto;

/**
 * Expect tests for the {@link CloudApi} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "CloudApiExpectTest")
public class CloudApiExpectTest extends BaseAbiquoApiExpectTest<CloudApi> {

   public void testSnapshotVirtualMachineReturns2xx() {
      CloudApi api = requestSendsResponse(
            HttpRequest
                  .builder()
                  .method("POST")
                  .endpoint(
                        URI.create("http://localhost/api/admin/virtualdatacenters/1/virtualappliances/1/virtualmachines/1/action/instance")) //
                  .addHeader("Authorization", basicAuth) //
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

   @Override
   protected CloudApi clientFrom(AbiquoApi api) {
      return api.getCloudApi();
   }

}
