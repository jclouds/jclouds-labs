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
package org.jclouds.cloudsigma2;

import com.google.common.collect.ImmutableList;
import org.jclouds.cloudsigma2.domain.AccountBalance;
import org.jclouds.cloudsigma2.domain.CreateSubscriptionRequest;
import org.jclouds.cloudsigma2.domain.CurrentUsage;
import org.jclouds.cloudsigma2.domain.Discount;
import org.jclouds.cloudsigma2.domain.Drive;
import org.jclouds.cloudsigma2.domain.DriveInfo;
import org.jclouds.cloudsigma2.domain.FirewallAction;
import org.jclouds.cloudsigma2.domain.FirewallDirection;
import org.jclouds.cloudsigma2.domain.FirewallIpProtocol;
import org.jclouds.cloudsigma2.domain.FirewallPolicy;
import org.jclouds.cloudsigma2.domain.FirewallRule;
import org.jclouds.cloudsigma2.domain.IP;
import org.jclouds.cloudsigma2.domain.IPInfo;
import org.jclouds.cloudsigma2.domain.LibraryDrive;
import org.jclouds.cloudsigma2.domain.License;
import org.jclouds.cloudsigma2.domain.Pricing;
import org.jclouds.cloudsigma2.domain.ProfileInfo;
import org.jclouds.cloudsigma2.domain.Server;
import org.jclouds.cloudsigma2.domain.ServerInfo;
import org.jclouds.cloudsigma2.domain.Subscription;
import org.jclouds.cloudsigma2.domain.SubscriptionResource;
import org.jclouds.cloudsigma2.domain.Tag;
import org.jclouds.cloudsigma2.domain.TagResource;
import org.jclouds.cloudsigma2.domain.Transaction;
import org.jclouds.cloudsigma2.domain.VLANInfo;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestApiExpectTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Vladimir Shevchenko
 */
@Test(groups = "unit")
public class CloudSigma2ApiExpectTest extends BaseRestApiExpectTest<CloudSigma2Api> {

    protected String endpoint = "https://zrh.cloudsigma.com/api/2.0/";

    public CloudSigma2ApiExpectTest() {
        provider = "cloudsigma2";
    }

    protected HttpRequest.Builder<?> getBuilder() {
        return HttpRequest.builder()
                .method("GET")
                .addHeader("Accept", MediaType.APPLICATION_JSON)
                .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==");
    }

    protected HttpRequest.Builder<?> postBuilder() {
        return HttpRequest.builder()
                .method("POST")
                .addHeader("Accept", MediaType.APPLICATION_JSON)
                .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==");
    }

    protected HttpRequest.Builder<?> deleteBuilder() {
        return HttpRequest.builder()
                .method("DELETE")
                .addHeader("Accept", MediaType.APPLICATION_JSON)
                .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==");
    }

    protected HttpRequest.Builder<?> putBuilder() {
        return HttpRequest.builder()
                .method("PUT")
                .addHeader("Accept", MediaType.APPLICATION_JSON)
                .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==");
    }

    protected HttpResponse.Builder<?> responseBuilder() {
        return HttpResponse.builder()
                .statusCode(200)
                .message("OK");
    }

    @Test
    public void testListDrives() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "drives/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/drives.json", MediaType.APPLICATION_JSON))
                .build());

        List<Drive> result = api.listDrives();
        assertNotNull(result);
    }

    @Test
    public void testListDrivesInfo() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "drives/detail/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/drives-detail.json", MediaType.APPLICATION_JSON))
                .build());

        List<DriveInfo> result = api.listDrivesInfo();
        assertNotNull(result);
    }

    @Test
    public void testGetDriveInfo() throws Exception {
        String uuid = "e96f3c63-6f50-47eb-9401-a56c5ccf6b32";
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "drives/" + uuid + "/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/drives-detail.json", MediaType.APPLICATION_JSON))
                .build());

        DriveInfo result = api.getDriveInfo(uuid);
        assertNotNull(result);
    }

    @Test
    public void testCreateDrive() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                    .payload(payloadFromResourceWithContentType("/drives-create-request.json", MediaType.APPLICATION_JSON))
                    .endpoint(endpoint + "drives/")
                    .build()
                , responseBuilder()
                    .payload(payloadFromResourceWithContentType("/drives-single.json", MediaType.APPLICATION_JSON))
                    .build());

        DriveInfo result = api.createDrive(new DriveInfo.Builder()
                .media(org.jclouds.cloudsigma2.domain.MediaType.DISK)
                .name("test_drive_0")
                .size(new BigInteger("1024000000"))
                .allowMultimount(false)
                .build());
        assertNotNull(result);
    }

    @Test
    public void testCreateDrives() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .payload(payloadFromResourceWithContentType("/drives-create-multiple-request.json", MediaType.APPLICATION_JSON))
                        .endpoint(endpoint + "drives/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/drives-detail.json", MediaType.APPLICATION_JSON))
                .build());

        List<DriveInfo> result = api.createDrives(ImmutableList.of(
                new DriveInfo.Builder()
                        .media(org.jclouds.cloudsigma2.domain.MediaType.DISK)
                        .name("test_drive_0")
                        .size(new BigInteger("1024000000"))
                        .allowMultimount(false)
                        .build()
                , new DriveInfo.Builder()
                .media(org.jclouds.cloudsigma2.domain.MediaType.DISK)
                .name("test_drive_1")
                .size(new BigInteger("1024000000"))
                .allowMultimount(false)
                .build()
                , new DriveInfo.Builder()
                .media(org.jclouds.cloudsigma2.domain.MediaType.DISK)
                .name("test_drive_2")
                .size(new BigInteger("1024000000"))
                .allowMultimount(false)
                .build()));
        assertNotNull(result);
        assertEquals(result.size(), 3);
    }

    @Test
    public void testDeleteDrive() throws Exception {
        String uuid = "e96f3c63-6f50-47eb-9401-a56c5ccf6b32";
        CloudSigma2Api api = requestSendsResponse(
                deleteBuilder()
                        .endpoint(endpoint + "drives/" + uuid + "/")
                        .build()
                , responseBuilder()
                .build());

        api.deleteDrive(uuid);
    }

    @Test
    public void testDeleteDrives() throws Exception {
        List<String> deleteList = ImmutableList.of(
                "b137e217-42b6-4ecf-8575-d72efc2d3dbd"
                ,"e035a488-8587-4a15-ab25-9b7343236bc9"
                ,"feded33c-106f-49fa-a1c4-be5c718ad1b5");

        CloudSigma2Api api = requestSendsResponse(
                deleteBuilder()
                        .endpoint(endpoint + "drives/")
                        .payload(payloadFromResourceWithContentType("/drives-delete-multiple.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .build());

        api.deleteDrives(deleteList);
    }

    @Test
    public void testEditDrive() throws Exception {
        String uuid = "e96f3c63-6f50-47eb-9401-a56c5ccf6b32";
        CloudSigma2Api api = requestSendsResponse(
                putBuilder()
                    .payload(payloadFromResourceWithContentType("/drives-create-request.json", MediaType.APPLICATION_JSON))
                    .endpoint(endpoint + "drives/" + uuid + "/")
                    .build()
                , responseBuilder()
                    .payload(payloadFromResourceWithContentType("/drives-detail.json", MediaType.APPLICATION_JSON))
                    .build());

        DriveInfo result = api.editDrive(uuid, new DriveInfo.Builder()
                .media(org.jclouds.cloudsigma2.domain.MediaType.DISK)
                .name("test_drive_0")
                .size(new BigInteger("1024000000"))
                .allowMultimount(false)
                .build());
        assertNotNull(result);
    }

    @Test
    public void testCloneDrive() throws Exception {
        String uuid = "e96f3c63-6f50-47eb-9401-a56c5ccf6b32";
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .payload(payloadFromResourceWithContentType("/drives-create-request.json", MediaType.APPLICATION_JSON))
                        .endpoint(endpoint + "drives/" + uuid + "/action/?do=clone")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/drives-detail.json", MediaType.APPLICATION_JSON))
                .build());

        DriveInfo result = api.cloneDrive(uuid, new DriveInfo.Builder()
                .media(org.jclouds.cloudsigma2.domain.MediaType.DISK)
                .name("test_drive_0")
                .size(new BigInteger("1024000000"))
                .allowMultimount(false)
                .build());
        assertNotNull(result);
    }

    @Test
    public void testListLibraryDrives() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "libdrives/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/libdrives.json", MediaType.APPLICATION_JSON))
                .build());

        List<LibraryDrive> result = api.listLibraryDrives();
        assertNotNull(result);
    }

    @Test
    public void testGetLibraryDrive() throws Exception {
        String uuid = "6d53b92c-42dc-472b-a7b6-7021f45f377a";
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "libdrives/" + uuid + "/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/libdrives-single.json", MediaType.APPLICATION_JSON))
                .build());

        LibraryDrive result = api.getLibraryDrive(uuid);
        assertNotNull(result);
    }

    @Test
    public void testCloneLibraryDrive() throws Exception {
        String uuid = "e96f3c63-6f50-47eb-9401-a56c5ccf6b32";
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .payload(payloadFromResourceWithContentType("/libdrives-create-request.json", MediaType.APPLICATION_JSON))
                        .endpoint(endpoint + "libdrives/" + uuid + "/action/?do=clone")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/libdrives-single.json", MediaType.APPLICATION_JSON))
                .build());

        DriveInfo result = api.cloneLibraryDrive(uuid, new LibraryDrive.Builder()
                .media(org.jclouds.cloudsigma2.domain.MediaType.DISK)
                .name("test_drive_0")
                .size(new BigInteger("1024000000"))
                .allowMultimount(false)
                .build());
        assertNotNull(result);
    }

    @Test
    public void testListServers() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "servers/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/servers.json", MediaType.APPLICATION_JSON))
                .build());

        List<Server> result = api.listServers();
        assertNotNull(result);
    }

    @Test
    public void testListServersInfo() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "servers/detail/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/server-detail.json", MediaType.APPLICATION_JSON))
                .build());

        List<ServerInfo> result = api.listServersInfo();
        assertNotNull(result);
    }

    @Test
    public void testCreateServer() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                    .endpoint(endpoint + "servers/")
                    .payload(payloadFromResourceWithContentType("/servers-create-request.json", MediaType.APPLICATION_JSON))
                    .build()
                , responseBuilder()
                    .payload(payloadFromResourceWithContentType("/servers-single.json", MediaType.APPLICATION_JSON))
                    .build());

        ServerInfo result = api.createServer(new ServerInfo.Builder()
                .cpu(100)
                .memory(new BigInteger("536870912"))
                .name("testServerAcc")
                .vncPassword("testserver")
                .build());
        assertNotNull(result);
    }

    @Test
    public void testCreateServers() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "servers/")
                        .payload(payloadFromResourceWithContentType("/servers-create-multiple-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/server-detail.json", MediaType.APPLICATION_JSON))
                .build());
        List<ServerInfo> result = api.createServers(ImmutableList.of(
                new ServerInfo.Builder()
                        .cpu(100)
                        .memory(new BigInteger("536870912"))
                        .name("test_server_0")
                        .vncPassword("testserver")
                        .build()
                , new ServerInfo.Builder()
                .cpu(100)
                .memory(new BigInteger("536870912"))
                .name("test_server_1")
                .vncPassword("testserver")
                .build()
                , new ServerInfo.Builder()
                .cpu(100)
                .memory(new BigInteger("536870912"))
                .name("test_server_2")
                .vncPassword("testserver")
                .build()));

        assertNotNull(result);
    }

    @Test
    public void testEditServer() throws Exception {
        String uuid = "a19a425f-9e92-42f6-89fb-6361203071bb";
        CloudSigma2Api api = requestSendsResponse(
                putBuilder()
                        .endpoint(endpoint + "servers/" + uuid + "/")
                        .payload(payloadFromResourceWithContentType("/servers-create-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/servers-single.json", MediaType.APPLICATION_JSON))
                .build());

        ServerInfo result = api.editServer(uuid, new ServerInfo.Builder()
                .name("testServerAcc")
                .cpu(100)
                .memory(new BigInteger("536870912"))
                .vncPassword("testserver")
                .build());
        assertNotNull(result);
    }

    @Test
    public void testDeleteServer() throws Exception {
        String uuid = "a19a425f-9e92-42f6-89fb-6361203071bb";
        CloudSigma2Api api = requestSendsResponse(
                deleteBuilder()
                        .endpoint(endpoint + "servers/" + uuid + "/")
                        .build()
                , responseBuilder()
                .build());

        api.deleteServer(uuid);
    }

    @Test
    public void testDeleteServers() throws Exception {
        List<String> deleteUuids = ImmutableList.of(
                "33e71c37-0d0a-4a3a-a1ea-dc7265c9a154"
                , "61d61337-884b-4c87-b4de-f7f48f9cfc84"
                , "a19a425f-9e92-42f6-89fb-6361203071bb"
        );
        CloudSigma2Api api = requestSendsResponse(
                deleteBuilder()
                        .endpoint(endpoint + "servers/")
                        .payload(payloadFromResourceWithContentType("/servers-delete-multiple-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .build());

        api.deleteServers(deleteUuids);
    }

    @Test
    public void testCloneServer() throws Exception {

    }

    @Test
    public void testGetServerInfo() throws Exception {
        String uuid = "61d61337-884b-4c87-b4de-f7f48f9cfc84";
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "servers/" + uuid + "/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/servers-single.json", MediaType.APPLICATION_JSON))
                .build());

        api.getServerInfo(uuid);
    }

    @Test
    public void testStartServer() throws Exception {
        String uuid = "61d61337-884b-4c87-b4de-f7f48f9cfc84";
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "servers/" + uuid + "/action/?do=start")
                        .build()
                , responseBuilder()
                .build());

        api.startServer(uuid);
    }

    @Test
    public void testStopServer() throws Exception {
        String uuid = "61d61337-884b-4c87-b4de-f7f48f9cfc84";
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "servers/" + uuid + "/action/?do=stop")
                        .build()
                , responseBuilder()
                .build());

        api.stopServer(uuid);
    }

    @Test
    public void testStartServerInSeparateAvailabilityGroup() throws Exception {
        String uuid = "61d61337-884b-4c87-b4de-f7f48f9cfc84";
        List<String> uuidGroup = ImmutableList.of(
                "313e73a4-592f-48cf-81c4-a6c079d005a5",
                "e035a488-8587-4a15-ab25-9b7343236bc9");
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "servers/" + uuid + "/action/?do=start?avoid=" + uuidGroup.get(0) + "&avoid=" + uuidGroup.get(1))
                        .build()
                , responseBuilder()
                .build());

        api.startServerInSeparateAvailabilityGroup(uuid, uuidGroup);
    }

    @Test
    public void testOpenServerVNCTunnel() throws Exception {
        String uuid = "61d61337-884b-4c87-b4de-f7f48f9cfc84";
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "servers/" + uuid + "/action/?do=open_vnc")
                        .build()
                , responseBuilder()
                .build());

        api.openServerVNCTunnel(uuid);
    }

    @Test
    public void testCloseServerVCNTunnel() throws Exception {
        String uuid = "61d61337-884b-4c87-b4de-f7f48f9cfc84";
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "servers/" + uuid + "/action/?do=close_vnc")
                        .build()
                , responseBuilder()
                .build());

        api.closeServerVCNTunnel(uuid);
    }

    @Test
    public void testListServerAvailabilityGroup() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "servers/availability_groups/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/servers-availability-groups.json", MediaType.APPLICATION_JSON))
                .build());

        List<List<String>> result = api.listServerAvailabilityGroup();
        assertNotNull(result);
    }

    @Test
    public void testGetServerAvailabilityGroup() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "fwpolicies/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/fwpolicies-detail.json", MediaType.APPLICATION_JSON))
                .build());

        List<FirewallPolicy> result = api.listFirewallPolicies();
        assertNotNull(result);
    }

    @Test
    public void testListFirewallPolicies() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "fwpolicies/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/fwpolicies-detail.json", MediaType.APPLICATION_JSON))
                .build());

        List<FirewallPolicy> result = api.listFirewallPolicies();
        assertNotNull(result);
    }

    @Test
    public void testListFirewallPoliciesInfo() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "fwpolicies/detail/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/fwpolicies-detail.json", MediaType.APPLICATION_JSON))
                .build());

        List<FirewallPolicy> result = api.listFirewallPoliciesInfo();
        assertNotNull(result);
    }

    @Test
    public void testCreateFirewallPolicies() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "fwpolicies/")
                        .payload(payloadFromResourceWithContentType("/fwpolicies-create-multiple-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/fwpolicies-detail.json", MediaType.APPLICATION_JSON))
                .build());

        List<FirewallPolicy> result = api.createFirewallPolicies(ImmutableList.of(
                new FirewallPolicy.Builder()
                    .name("New policy")
                    .rules(ImmutableList.of(
                            new FirewallRule.Builder()
                                    .action(FirewallAction.ACCEPT)
                                    .comment("Test comment")
                                    .direction(FirewallDirection.IN)
                                    .destinationIp("192.168.1.132/32")
                                    .destinationPort("1233")
                                    .ipProtocol(FirewallIpProtocol.TCP)
                                    .sourceIp("255.255.255.12/32")
                                    .sourcePort("321")
                                    .build()
                    ))
                    .build()
                , new FirewallPolicy.Builder()
                .name("My awesome policy")
                .rules(ImmutableList.of(
                        new FirewallRule.Builder()
                                .action(FirewallAction.DROP)
                                .comment("Drop traffic from the VM to IP address 23.0.0.0/32")
                                .direction(FirewallDirection.OUT)
                                .destinationIp("23.0.0.0/32")
                                .build()
                        , new FirewallRule.Builder()
                        .action(FirewallAction.ACCEPT)
                        .comment("Allow SSH traffic to the VM from our office in Dubai")
                        .direction(FirewallDirection.IN)
                        .destinationPort("22")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .sourceIp("172.66.32.0/24")
                        .build()
                        , new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop all other SSH traffic to the VM")
                        .direction(FirewallDirection.IN)
                        .destinationPort("22")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .build()
                        , new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop all UDP traffic to the VM, not originating from 172.66.32.55")
                        .direction(FirewallDirection.IN)
                        .ipProtocol(FirewallIpProtocol.UDP)
                        .sourceIp("!172.66.32.55/32")
                        .build()
                        , new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop any traffic, to the VM with destination port not between 1-1024")
                        .direction(FirewallDirection.IN)
                        .destinationPort("!1:1024")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .build()))
                .build()));
        assertNotNull(result);
    }

    @Test
    public void testCreateFirewallPolicy() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "fwpolicies/")
                        .payload(payloadFromResourceWithContentType("/fwpolicies-create-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/fwpolicies-single.json", MediaType.APPLICATION_JSON))
                .build());

        FirewallPolicy result = api.createFirewallPolicy(new FirewallPolicy.Builder()
                .name("My awesome policy")
                .rules(ImmutableList.of(
                        new FirewallRule.Builder()
                                .action(FirewallAction.DROP)
                                .comment("Drop traffic from the VM to IP address 23.0.0.0/32")
                                .direction(FirewallDirection.OUT)
                                .destinationIp("23.0.0.0/32")
                                .build()
                        , new FirewallRule.Builder()
                        .action(FirewallAction.ACCEPT)
                        .comment("Allow SSH traffic to the VM from our office in Dubai")
                        .direction(FirewallDirection.IN)
                        .destinationPort("22")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .sourceIp("172.66.32.0/24")
                        .build()
                        , new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop all other SSH traffic to the VM")
                        .direction(FirewallDirection.IN)
                        .destinationPort("22")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .build()
                        , new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop all UDP traffic to the VM, not originating from 172.66.32.55")
                        .direction(FirewallDirection.IN)
                        .ipProtocol(FirewallIpProtocol.UDP)
                        .sourceIp("!172.66.32.55/32")
                        .build()
                        , new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop any traffic, to the VM with destination port not between 1-1024")
                        .direction(FirewallDirection.IN)
                        .destinationPort("!1:1024")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .build()))
                .build());
        assertNotNull(result);
    }

    @Test
    public void testEditFirewallPolicy() throws Exception {
        String uuid = "cf8479b4-c98b-46c8-ab9c-108bb00c8218";
        CloudSigma2Api api = requestSendsResponse(
                putBuilder()
                        .endpoint(endpoint + "fwpolicies/" + uuid + "/")
                        .payload(payloadFromResourceWithContentType("/fwpolicies-create-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/fwpolicies-single.json", MediaType.APPLICATION_JSON))
                .build());

        FirewallPolicy result = api.editFirewallPolicy(uuid,
                new FirewallPolicy.Builder()
                        .name("My awesome policy")
                        .rules(ImmutableList.of(
                                new FirewallRule.Builder()
                                        .action(FirewallAction.DROP)
                                        .comment("Drop traffic from the VM to IP address 23.0.0.0/32")
                                        .direction(FirewallDirection.OUT)
                                        .destinationIp("23.0.0.0/32")
                                        .build()
                                , new FirewallRule.Builder()
                                .action(FirewallAction.ACCEPT)
                                .comment("Allow SSH traffic to the VM from our office in Dubai")
                                .direction(FirewallDirection.IN)
                                .destinationPort("22")
                                .ipProtocol(FirewallIpProtocol.TCP)
                                .sourceIp("172.66.32.0/24")
                                .build()
                                , new FirewallRule.Builder()
                                .action(FirewallAction.DROP)
                                .comment("Drop all other SSH traffic to the VM")
                                .direction(FirewallDirection.IN)
                                .destinationPort("22")
                                .ipProtocol(FirewallIpProtocol.TCP)
                                .build()
                                , new FirewallRule.Builder()
                                .action(FirewallAction.DROP)
                                .comment("Drop all UDP traffic to the VM, not originating from 172.66.32.55")
                                .direction(FirewallDirection.IN)
                                .ipProtocol(FirewallIpProtocol.UDP)
                                .sourceIp("!172.66.32.55/32")
                                .build()
                                , new FirewallRule.Builder()
                                .action(FirewallAction.DROP)
                                .comment("Drop any traffic, to the VM with destination port not between 1-1024")
                                .direction(FirewallDirection.IN)
                                .destinationPort("!1:1024")
                                .ipProtocol(FirewallIpProtocol.TCP)
                                .build()))
                        .build());
        assertNotNull(result);
    }

    @Test
    public void testGetVLANInfo() throws Exception {
        String uuid = "96537817-f4b6-496b-a861-e74192d3ccb0";
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "vlans/" + uuid + "/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/vlan-single.json", MediaType.APPLICATION_JSON))
                .build());

        VLANInfo result = api.getVLANInfo(uuid);
        assertNotNull(result);
    }

    @Test
    public void testListVLANs() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "vlans/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/vlans.json", MediaType.APPLICATION_JSON))
                .build());

        List<VLANInfo> result = api.listVLANs();
        assertNotNull(result);
    }

    @Test
    public void testListVLANInfo() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "vlans/detail/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/vlans.json", MediaType.APPLICATION_JSON))
                .build());

        List<VLANInfo> result = api.listVLANInfo();
        assertNotNull(result);
    }

    @Test
    public void testEditVLAN() throws Exception {
        String uuid = "96537817-f4b6-496b-a861-e74192d3ccb0";
        Map<String, String> meta = new HashMap<String, String>();
        meta.put("description", "test vlan");
        meta.put("test_key_1", "test_value_1");
        meta.put("test_key_2", "test_value_2");

        CloudSigma2Api api = requestSendsResponse(
                putBuilder()
                    .endpoint(endpoint + "vlans/" + uuid + "/")
                    .payload(payloadFromResourceWithContentType("/vlans-edit-request.json", MediaType.APPLICATION_JSON))
                    .build()
                , responseBuilder()
                    .payload(payloadFromResourceWithContentType("/vlan-single.json", MediaType.APPLICATION_JSON))
                    .build());

        VLANInfo result = api.editVLAN(uuid
                , new VLANInfo.Builder()
                .meta(meta)
                .build());
        assertNotNull(result);
    }

    @Test
    public void testListIPs() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "ips/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/ips.json", MediaType.APPLICATION_JSON))
                .build());

        List<IP> result = api.listIPs();
        assertNotNull(result);
    }

    @Test
    public void testListIPInfo() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "ips/detail/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/ips.json", MediaType.APPLICATION_JSON))
                .build());

        List<IPInfo> result = api.listIPInfo();
        assertNotNull(result);
    }

    @Test
    public void testGetIPInfo() throws Exception {
        String uuid = "185.12.6.183";
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "ips/" + uuid + "/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/ips-single.json", MediaType.APPLICATION_JSON))
                .build());

        IPInfo result = api.getIPInfo(uuid);
        assertNotNull(result);
    }

    @Test
    public void testEditIP() throws Exception {
        String uuid = "96537817-f4b6-496b-a861-e74192d3ccb0";
        Map<String, String> meta = new HashMap<String, String>();
        meta.put("description", "test vlan");
        meta.put("test_key_1", "test_value_1");
        meta.put("test_key_2", "test_value_2");

        CloudSigma2Api api = requestSendsResponse(
                putBuilder()
                        .endpoint(endpoint + "ips/" + uuid + "/")
                        .payload(payloadFromResourceWithContentType("/ips-edit-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/ips-single.json", MediaType.APPLICATION_JSON))
                .build());

        IPInfo result = api.editIP(uuid
                , new IPInfo.Builder()
                .meta(meta)
                .build());
        assertNotNull(result);
    }

    @Test
    public void testListTags() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "tags/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/tags-detail.json", MediaType.APPLICATION_JSON))
                .build());

        List<Tag> result = api.listTags();
        assertNotNull(result);
    }

    @Test
    public void testListTagsInfo() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "tags/detail/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/tags-detail.json", MediaType.APPLICATION_JSON))
                .build());

        List<Tag> result = api.listTagsInfo();
        assertNotNull(result);
    }

    @Test
    public void testGetTagInfo() throws Exception {
        String uuid = "68bb0cfc-0c76-4f37-847d-7bb705c5ae46";
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "tags/" + uuid + "/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/tags-single.json", MediaType.APPLICATION_JSON))
                .build());

        Tag result = api.getTagInfo(uuid);
        assertNotNull(result);
    }

    @Test
    public void testEditTag() throws Exception {
        String uuid = "68bb0cfc-0c76-4f37-847d-7bb705c5ae46";
        CloudSigma2Api api = requestSendsResponse(
                putBuilder()
                        .endpoint(endpoint + "tags/" + uuid + "/")
                        .payload(payloadFromResourceWithContentType("/tags-create-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/tags-single.json", MediaType.APPLICATION_JSON))
                .build());

        Tag result = api.editTag(uuid,
                new Tag.Builder()
                        .name("TagCreatedWithResource")
                        .resources(ImmutableList.of(
                                new TagResource.Builder().uuid("61bcc398-c034-42f1-81c9-f6d7f62c4ea0").build()
                                , new TagResource.Builder().uuid("8ac6ac13-a55e-4b01-bcf4-5eed7b60a3ed").build()
                                , new TagResource.Builder().uuid("3610d935-514a-4552-acf3-a40dd0a5f961").build()
                                , new TagResource.Builder().uuid("185.12.6.183").build()
                                , new TagResource.Builder().uuid("96537817-f4b6-496b-a861-e74192d3ccb0").build()
                        ))
                        .build());
        assertNotNull(result);
    }

    @Test
    public void testCreateTag() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "tags/")
                        .payload(payloadFromResourceWithContentType("/tags-create-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/tags-create-single.json", MediaType.APPLICATION_JSON))
                .build());

        Tag result = api.createTag(new Tag.Builder()
                                        .name("TagCreatedWithResource")
                                        .resources(ImmutableList.of(
                                                new TagResource.Builder().uuid("61bcc398-c034-42f1-81c9-f6d7f62c4ea0").build()
                                                , new TagResource.Builder().uuid("8ac6ac13-a55e-4b01-bcf4-5eed7b60a3ed").build()
                                                , new TagResource.Builder().uuid("3610d935-514a-4552-acf3-a40dd0a5f961").build()
                                                , new TagResource.Builder().uuid("185.12.6.183").build()
                                                , new TagResource.Builder().uuid("96537817-f4b6-496b-a861-e74192d3ccb0").build()
                                        ))
                                        .build());
        assertNotNull(result);
    }

    @Test
    public void testDeleteTag() throws Exception {
        String uuid = "956e2ca0-dee3-4b3f-a1be-a6e86f90946f";

        CloudSigma2Api api = requestSendsResponse(
                deleteBuilder()
                        .endpoint(endpoint + "tags/" + uuid + "/")
                        .build()
                , responseBuilder()
                .build());

        api.deleteTag(uuid);
    }

    @Test
    public void testCreateTags() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "tags/")
                        .payload(payloadFromResourceWithContentType("/tags-create-multiple-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/tags-detail.json", MediaType.APPLICATION_JSON))
                .build());

        List<Tag> result = api.createTags(ImmutableList.of(
                new Tag.Builder()
                    .name("new tag")
                    .resources(ImmutableList.of(new TagResource.Builder().uuid("185.12.6.183").build()))
                    .build()
                , new Tag.Builder()
                    .name("TagCreatedWithResource")
                    .resources(ImmutableList.of(
                            new TagResource.Builder().uuid("61bcc398-c034-42f1-81c9-f6d7f62c4ea0").build()
                            , new TagResource.Builder().uuid("8ac6ac13-a55e-4b01-bcf4-5eed7b60a3ed").build()
                            , new TagResource.Builder().uuid("3610d935-514a-4552-acf3-a40dd0a5f961").build()
                            , new TagResource.Builder().uuid("185.12.6.183").build()
                            , new TagResource.Builder().uuid("96537817-f4b6-496b-a861-e74192d3ccb0").build()
                    ))
                    .build()));
        assertNotNull(result);
    }

    @Test
    public void testGetProfileInfo() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "profile/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/profile.json", MediaType.APPLICATION_JSON))
                .build());

        ProfileInfo result = api.getProfileInfo();
        assertNotNull(result);
    }

    @Test
    public void testEditProfileInfo() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                putBuilder()
                        .endpoint(endpoint + "profile/")
                        .payload(payloadFromResourceWithContentType("/profile-edit-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/profile.json", MediaType.APPLICATION_JSON))
                .build());

        Map<String, String> meta = new HashMap<String, String>();
        meta.put("description", "profile info");

        ProfileInfo result = api.editProfileInfo(new ProfileInfo.Builder()
                .address("test_address")
                .isApiHttpsOnly(false)
                .autotopupAmount("0E-16")
                .autotopupThreshold("0E-16")
                .bankReference("jdoe123")
                .company("Newly Set Company Name")
                .country("GB")
                .currency("USD")
                .email("user@example.com")
                .firstName("John")
                .hasAutotopup(false)
                .invoicing(true)
                .isKeyAuth(false)
                .language("en-au")
                .lastName("Doe")
                .isMailingListEnabled(true)
                .meta(meta)
                .myNotes("test notes")
                .nickname("test nickname")
                .phone("123456789")
                .postcode("12345")
                .reseller("test reseller")
                .signupTime(new SimpleDateFormatDateService().iso8601SecondsDateParse("2013-05-28T11:57:01+00:00"))
                .state("REGULAR")
                .taxRate(3.14)
                .taxName("test tax_name")
                .title("test title")
                .town("test town")
                .uuid("6f670b3c-a2e6-433f-aeab-b976b1cdaf03")
                .vat("test vat")
                .build());

        assertNotNull(result);
    }

    @Test
    public void testGetAccountBalance() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "balance/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/balance.json", MediaType.APPLICATION_JSON))
                .build());

        AccountBalance result = api.getAccountBalance();
        assertNotNull(result);
    }

    @Test
    public void testGetCurrentUsage() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "currentusage/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/currentusage.json", MediaType.APPLICATION_JSON))
                .build());

        CurrentUsage result = api.getCurrentUsage();
        assertNotNull(result);
    }

    @Test
    public void testListSubscriptions() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "subscriptions/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/subscriptions.json", MediaType.APPLICATION_JSON))
                .build());

        List<Subscription> result = api.listSubscriptions();
        assertNotNull(result);
    }

    @Test
    public void testListSubscriptionsCalculator() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "subscriptioncalculator/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/subscriptions.json", MediaType.APPLICATION_JSON))
                .build());

        List<Subscription> result = api.listSubscriptionsCalculator();
        assertNotNull(result);
    }

    @Test
    public void testGetSubscription() throws Exception{
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "subscriptions/5551/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/subscriptions-single-get.json", MediaType.APPLICATION_JSON))
                .build());

        Subscription result = api.getSubscription("5551");
        assertNotNull(result);
    }

    @Test
    public void testCreateSubscription() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "subscriptions/")
                        .payload(payloadFromResourceWithContentType("/subscriptions-create-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/subscriptions-single.json", MediaType.APPLICATION_JSON))
                .build());

        Subscription result = api.createSubscription(new CreateSubscriptionRequest.Builder()
                .resource(SubscriptionResource.DSSD)
                .period("1 month")
                .amount("30000")
                .build());
        assertNotNull(result);
    }

    @Test
    public void testCreateSubscriptions() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "subscriptions/")
                        .payload(payloadFromResourceWithContentType("/subscriptions-create-multiple-request.json", MediaType.APPLICATION_JSON))
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/subscriptions.json", MediaType.APPLICATION_JSON))
                .build());

        List<Subscription> result = api.createSubscriptions(ImmutableList.of(
                new CreateSubscriptionRequest.Builder()
                        .resource(SubscriptionResource.DSSD)
                        .period("1 month")
                        .amount("30000")
                        .build()
                , new CreateSubscriptionRequest.Builder()
                .resource(SubscriptionResource.MEM)
                .period("3 months")
                .amount("30000")
                .build()
                , new CreateSubscriptionRequest.Builder()
                .resource(SubscriptionResource.IP)
                .period("1 year")
                .amount("30000")
                .build()));
        assertNotNull(result);
    }

    @Test
    public void testExtendSubscription() throws Exception {
        String uuid = "509f8e27-1e64-49bb-aa5a-baec074b0210";
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "subscriptions/" + uuid + "/action/?do=extend")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/subscriptions-single.json", MediaType.APPLICATION_JSON))
                .build());

        api.extendSubscription(uuid);
    }

    @Test
    public void testEnableSubscriptionAutorenew() throws Exception {
        String uuid = "509f8e27-1e64-49bb-aa5a-baec074b0210";
        CloudSigma2Api api = requestSendsResponse(
                postBuilder()
                        .endpoint(endpoint + "subscriptions/" +  uuid +"/action/?do=auto_renew")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/pricing.json", MediaType.APPLICATION_JSON))
                .build());

        api.enableSubscriptionAutorenew(uuid);
    }

    @Test
    public void testGetPricing() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "pricing/")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/pricing.json", MediaType.APPLICATION_JSON))
                .build());

        Pricing result = api.getPricing();
        assertNotNull(result);
    }

    @Test
    public void testListDiscounts() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "discount/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/discount.json", MediaType.APPLICATION_JSON))
                .build());

        List<Discount> result = api.listDiscounts();
        assertNotNull(result);
    }

    @Test
    public void testListTransactions() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                        .endpoint(endpoint + "ledger/?limit=0")
                        .build()
                , responseBuilder()
                .payload(payloadFromResourceWithContentType("/ledger.json", MediaType.APPLICATION_JSON))
                .build());

        List<Transaction> result = api.listTransactions();
        assertNotNull(result);
    }

    @Test
    public void testListLicenses() throws Exception {
        CloudSigma2Api api = requestSendsResponse(
                getBuilder()
                    .endpoint(endpoint + "licenses/?limit=0")
                    .build()
                , responseBuilder()
                    .payload(payloadFromResourceWithContentType("/licences.json", MediaType.APPLICATION_JSON))
                    .build());

        List<License> result = api.listLicenses();
        assertNotNull(result);
    }
}
