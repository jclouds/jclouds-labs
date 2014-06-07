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

import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.cloudsigma2.domain.CalcSubscription;
import org.jclouds.cloudsigma2.domain.DeviceEmulationType;
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
import org.jclouds.cloudsigma2.domain.Item;
import org.jclouds.cloudsigma2.domain.LibraryDrive;
import org.jclouds.cloudsigma2.domain.License;
import org.jclouds.cloudsigma2.domain.MediaType;
import org.jclouds.cloudsigma2.domain.ProfileInfo;
import org.jclouds.cloudsigma2.domain.Server;
import org.jclouds.cloudsigma2.domain.ServerDrive;
import org.jclouds.cloudsigma2.domain.ServerInfo;
import org.jclouds.cloudsigma2.domain.Subscription;
import org.jclouds.cloudsigma2.domain.SubscriptionCalculator;
import org.jclouds.cloudsigma2.domain.SubscriptionResource;
import org.jclouds.cloudsigma2.domain.Tag;
import org.jclouds.cloudsigma2.domain.TagResource;
import org.jclouds.cloudsigma2.domain.Transaction;
import org.jclouds.cloudsigma2.domain.VLANInfo;
import org.jclouds.cloudsigma2.options.PaginationOptions;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;


@Test(groups = "live")
public class CloudSigma2ApiLiveTest extends BaseApiLiveTest<CloudSigma2Api> {

   private DriveInfo createdDrive;
   private List<DriveInfo> createdDrives;
   private ServerInfo createdServer;
   private List<ServerInfo> createdServers;
   private FirewallPolicy createdFirewallPolicy;
   private Tag createdTag;
   private List<Tag> createdTags;

   public CloudSigma2ApiLiveTest() {
      provider = "cloudsigma2";
   }

   @Test(dependsOnMethods = {"testCreateDrives"})
   public void testListDrives() throws Exception {
      assertNotNull(api.listDrives());
   }

   @Test(dependsOnMethods = {"testCreateDrives"})
   public void testListDrivesInfo() throws Exception {
      assertNotNull(api.listDrivesInfo());
   }

   @Test(dependsOnMethods = {"testCreateDrives"})
   public void testGetDriveInfo() throws Exception {
      for (Drive driveInfo : api.listDrives().concat()) {
         assertNotNull(api.getDriveInfo(driveInfo.getUuid()));
      }
   }

   @Test
   public void testCreateDrive() throws Exception {
      DriveInfo newDrive = new DriveInfo.Builder()
            .name("test drive")
            .size(new BigInteger("2073741824"))
            .media(MediaType.DISK)
            .build();
      createdDrive = api.createDrive(newDrive);
      checkDrive(newDrive, createdDrive);
   }

   @Test
   public void testCreateDrives() throws Exception {
      List<DriveInfo> newDrives = ImmutableList.of(
            new DriveInfo.Builder()
                  .name("New Drive")
                  .size(new BigInteger("2073741824"))
                  .media(MediaType.DISK)
                  .build(),
            new DriveInfo.Builder()
                  .name("Test Drive")
                  .size(new BigInteger("6073741824"))
                  .media(MediaType.DISK)
                  .build());

      createdDrives = api.createDrives(newDrives);
      assertEquals(newDrives.size(), createdDrives.size());

      for (int i = 0; i < newDrives.size(); i++) {
         checkDrive(newDrives.get(i), createdDrives.get(i));
      }
   }

   @Test(dependsOnMethods = {"testCreateDrive"})
   public void testEditDrive() throws Exception {
      DriveInfo editedDrive = new DriveInfo.Builder()
            .name("Edited Drive")
            .size(createdDrive.getSize())
            .media(MediaType.DISK)
            .build();

      checkDrive(editedDrive, api.editDrive(createdDrive.getUuid(), editedDrive));
   }

   @Test(dependsOnMethods = {"testEditDrive", "testCreateTag", "testEditTag"})
   public void testDeleteDrive() throws Exception {
      String uuid = createdDrive.getUuid();
      api.deleteDrive(uuid);
      assertNull(api.getDriveInfo(uuid));
   }

   @Test(dependsOnMethods = {"testCreateDrives"})
   public void testDeleteDrives() throws Exception {
      ImmutableList.Builder<String> stringListBuilder = ImmutableList.builder();
      for (DriveInfo driveInfo : createdDrives) {
         stringListBuilder.add(driveInfo.getUuid());
      }
      ImmutableList<String> uuids = stringListBuilder.build();
      api.deleteDrives(uuids);
      
      // Verify all deleted drives no longer exist
      FluentIterable<Drive> drives = api.listDrives().concat();
      assertFalse(any(transform(drives, extractUuid()), in(uuids)));
   }

   @Test
   public void testListLibraryDrives() throws Exception {
      assertNotNull(api.listLibraryDrives());
   }

   @Test
   public void testGetLibraryDrive() throws Exception {
      for (LibraryDrive libraryDrive : api.listLibraryDrives().concat()) {
         assertNotNull(libraryDrive.getUuid());
      }
   }

   @Test(dependsOnMethods = {"testCreateServers"})
   public void testListServers() throws Exception {
      assertNotNull(api.listServers());
   }

   @Test(dependsOnMethods = {"testCreateServers"})
   public void testListServersInfo() throws Exception {
      assertNotNull(api.listServersInfo());
   }

   @Test
   public void testCreateServer() throws Exception {
      ServerInfo serverInfo = new ServerInfo.Builder()
            .name("New Server")
            .memory(new BigInteger("5368709120"))
            .cpu(3000)
            .vncPassword("new_password")
            .drives(ImmutableList.of(api.listDrives().concat().get(0).toServerDrive(1, "0:1", DeviceEmulationType.IDE)))
            .build();

      createdServer = api.createServer(serverInfo);
      checkServer(serverInfo, createdServer);
   }

   @Test
   public void testCreateServers() throws Exception {
      List<ServerInfo> newServerList = ImmutableList.of(
            new ServerInfo.Builder()
                  .name("New Server")
                  .memory(new BigInteger("5368709120"))
                  .cpu(3000)
                  .vncPassword("new_password")
                  .build(),
            new ServerInfo.Builder()
                  .name("Test Server")
                  .memory(new BigInteger("5368709120"))
                  .cpu(3000)
                  .vncPassword("test_password")
                  .build());

      createdServers = api.createServers(newServerList);
      assertEquals(newServerList.size(), createdServers.size());

      for (int i = 0; i < newServerList.size(); i++) {
         checkServer(newServerList.get(i), createdServers.get(i));
      }
   }

   @Test(dependsOnMethods = {"testCreateServer"})
   public void testEditServer() throws Exception {
      ServerInfo serverInfo = new ServerInfo.Builder()
            .name("Edited Server")
            .memory(new BigInteger("5368709120"))
            .cpu(2000)
            .vncPassword("edited_password")
            .build();

      checkServer(serverInfo, api.editServer(createdServer.getUuid(), serverInfo));
   }

   @Test(dependsOnMethods = {"testCreateServers"})
   public void testGetServerInfo() throws Exception {
      for (Server server : api.listServers().concat()) {
         assertNotNull(server.getUuid());
      }
   }

   @Test(dependsOnMethods = {"testEditServer"})
   public void testDeleteServer() throws Exception {
      String uuid = createdServer.getUuid();
      api.deleteServer(uuid);
      assertNull(api.getServerInfo(uuid));
   }

   @Test(dependsOnMethods = {"testCreateServers"})
   public void testDeleteServers() throws Exception {
      ImmutableList.Builder<String> stringListBuilder = ImmutableList.builder();
      for (ServerInfo serverInfo : createdServers) {
         stringListBuilder.add(serverInfo.getUuid());
      }
      ImmutableList<String> uuids = stringListBuilder.build();
      api.deleteServers(uuids);
      
      // Verify all deleted servers no longer exist
      FluentIterable<Server> servers = api.listServers().concat();
      assertFalse(any(transform(servers, extractUuid()), in(uuids)));
   }

   @Test(dependsOnMethods = {"testCreateFirewallPolicies"})
   public void testListFirewallPolicies() throws Exception {
      assertNotNull(api.listFirewallPolicies());
   }

   @Test(dependsOnMethods = {"testCreateFirewallPolicies"})
   public void testListFirewallPoliciesInfo() throws Exception {
      assertNotNull(api.listFirewallPoliciesInfo());
   }

   @Test
   public void testCreateFirewallPolicies() throws Exception {
      List<FirewallPolicy> newFirewallPolicies = ImmutableList.of(
            new FirewallPolicy.Builder()
                  .name("My awesome policy")
                  .rules(ImmutableList.of(
                        new FirewallRule.Builder()
                              .action(FirewallAction.DROP)
                              .comment("Drop traffic from the VM to IP address 23.0.0.0/32")
                              .direction(FirewallDirection.OUT)
                              .destinationIp("23.0.0.0/32")
                              .build(),
                        new FirewallRule.Builder()
                              .action(FirewallAction.ACCEPT)
                              .comment("Allow SSH traffic to the VM from our office in Dubai")
                              .direction(FirewallDirection.IN)
                              .destinationPort("22")
                              .ipProtocol(FirewallIpProtocol.TCP)
                              .sourceIp("172.66.32.0/24")
                              .build(),
                        new FirewallRule.Builder()
                              .action(FirewallAction.DROP)
                              .comment("Drop all other SSH traffic to the VM")
                              .direction(FirewallDirection.IN)
                              .destinationPort("22")
                              .ipProtocol(FirewallIpProtocol.TCP)
                              .build(),
                        new FirewallRule.Builder()
                              .action(FirewallAction.DROP)
                              .comment("Drop all UDP traffic to the VM, not originating from 172.66.32.55")
                              .direction(FirewallDirection.IN)
                              .ipProtocol(FirewallIpProtocol.UDP)
                              .sourceIp("!172.66.32.55/32")
                              .build(),
                        new FirewallRule.Builder()
                              .action(FirewallAction.DROP)
                              .comment("Drop any traffic, to the VM with destination port not between 1-1024")
                              .direction(FirewallDirection.IN)
                              .destinationPort("!1:1024")
                              .ipProtocol(FirewallIpProtocol.TCP)
                              .build()))
                  .build(),
            new FirewallPolicy.Builder()
                  .name("New policy")
                  .rules(ImmutableList.of(new FirewallRule.Builder()
                        .action(FirewallAction.ACCEPT)
                        .comment("Test comment")
                        .direction(FirewallDirection.IN)
                        .destinationIp("192.168.1.132/32")
                        .destinationPort("1233")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .sourceIp("255.255.255.12/32")
                        .sourcePort("321")
                        .build()))
                  .build());

      List<FirewallPolicy> createdFirewallPolicies = api.createFirewallPolicies(newFirewallPolicies);
      assertEquals(newFirewallPolicies.size(), createdFirewallPolicies.size());

      for (int i = 0; i < newFirewallPolicies.size(); i++) {
         checkFirewallPolicy(newFirewallPolicies.get(i), createdFirewallPolicies.get(i));
      }
   }

   @Test
   public void testCreateFirewallPolicy() throws Exception {
      FirewallPolicy newFirewallPolicy = new FirewallPolicy.Builder()
            .name("My awesome policy")
            .rules(ImmutableList.of(
                  new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop traffic from the VM to IP address 23.0.0.0/32")
                        .direction(FirewallDirection.OUT)
                        .destinationIp("23.0.0.0/32")
                        .build(),
                  new FirewallRule.Builder()
                        .action(FirewallAction.ACCEPT)
                        .comment("Allow SSH traffic to the VM from our office in Dubai")
                        .direction(FirewallDirection.IN)
                        .destinationPort("22")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .sourceIp("172.66.32.0/24")
                        .build(),
                  new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop all other SSH traffic to the VM")
                        .direction(FirewallDirection.IN)
                        .destinationPort("22")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .build(),
                  new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop all UDP traffic to the VM, not originating from 172.66.32.55")
                        .direction(FirewallDirection.IN)
                        .ipProtocol(FirewallIpProtocol.UDP)
                        .sourceIp("!172.66.32.55/32")
                        .build(),
                  new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop any traffic, to the VM with destination port not between 1-1024")
                        .direction(FirewallDirection.IN)
                        .destinationPort("!1:1024")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .build()))
            .build();

      createdFirewallPolicy = api.createFirewallPolicy(newFirewallPolicy);
      checkFirewallPolicy(newFirewallPolicy, createdFirewallPolicy);
   }

   @Test(dependsOnMethods = {"testCreateFirewallPolicy"})
   public void testEditFirewallPolicy() throws Exception {
      FirewallPolicy editedPolicy = new FirewallPolicy.Builder()
            .name("Edited policy")
            .rules(ImmutableList.of(
                  new FirewallRule.Builder()
                        .action(FirewallAction.ACCEPT)
                        .comment("Edited policy rule comment")
                        .direction(FirewallDirection.IN)
                        .destinationIp("192.168.1.132/32")
                        .destinationPort("1233")
                        .ipProtocol(FirewallIpProtocol.TCP)
                        .sourceIp("255.255.255.12/32")
                        .sourcePort("321")
                        .build()))
            .build();

      checkFirewallPolicy(editedPolicy, api.editFirewallPolicy(createdFirewallPolicy.getUuid(), editedPolicy));
   }

   @Test
   public void testListVLANs() throws Exception {
      assertNotNull(api.listVLANs());
   }

   @Test
   public void testListVLANInfo() throws Exception {
      assertNotNull(api.listVLANInfo());
   }

   @Test
   public void testGetVLANInfo() throws Exception {
      for (VLANInfo vlanInfo : api.listVLANs().concat()) {
         assertNotNull(vlanInfo.getUuid());
      }
   }

   @Test
   public void testEditVLAN() throws Exception {
      Map<String, String> meta = Maps.newHashMap();
      meta.put("test", "test data");

      VLANInfo vlanInfo = new VLANInfo.Builder()
            .meta(meta)
            .build();

      if (api.listVLANs().size() > 0) {
         checkVlAN(vlanInfo, api.editVLAN(api.listVLANs().concat().get(0).getUuid(), vlanInfo));
      }
   }

   @Test
   public void testListIPs() throws Exception {
      assertNotNull(api.listIPs());
   }

   @Test
   public void testListIPInfo() throws Exception {
      assertNotNull(api.listIPInfo());
   }

   @Test
   public void testGetIPInfo() throws Exception {
      for (IP ip : api.listIPs().concat()) {
         assertNotNull(api.getIPInfo(ip.getUuid()));
      }
   }

   @Test
   public void testEditIP() throws Exception {
      Map<String, String> meta = Maps.newHashMap();
      meta.put("test", "test data");

      IPInfo ip = new IPInfo.Builder()
            .meta(meta)
            .build();

      if (api.listIPs().size() > 0) {
         checkIP(ip, api.editIP(api.listIPs().concat().get(0).getUuid(), ip));
      }
   }

   @Test(dependsOnMethods = {"testCreateTags"})
   public void testListTags() throws Exception {
      assertNotNull(api.listTags());
   }

   @Test(dependsOnMethods = {"testCreateTags"})
   public void testListTagsInfo() throws Exception {
      assertNotNull(api.listTagsInfo());
   }

   @Test(dependsOnMethods = {"testCreateTags"})
   public void testGetTagInfo() throws Exception {
      for (Tag tag : api.listTags().concat()) {
         assertNotNull(api.getTagInfo(tag.getUuid()));
      }
   }

   @Test(dependsOnMethods = {"testCreateDrive"})
   public void testCreateTag() throws Exception {
      Map<String, String> meta = Maps.newHashMap();
      meta.put("description", "Test tag");

      Tag newTag = new Tag.Builder()
            .meta(meta)
            .name("Cloudsigma2 Test tag")
            .meta(Maps.<String, String>newHashMap())
            .resources(ImmutableList.of(
                  new TagResource.Builder()
                        .uuid(createdDrive.getUuid())
                        .build()))
            .build();

      createdTag = api.createTag(newTag);
      checkTag(newTag, createdTag);
   }

   @Test(dependsOnMethods = {"testCreateDrive"})
   public void testCreateTags() throws Exception {
      List<Tag> newTagsList = ImmutableList.of(
            new Tag.Builder().name("Cloudsigma2 New tag " + System.currentTimeMillis())
                  .meta(Maps.<String, String>newHashMap()).build(),
            new Tag.Builder().name("Cloudsigma2 tag with resource " + System.currentTimeMillis())
                  .meta(Maps.<String, String>newHashMap())
                  .resources(ImmutableList.of(new TagResource.Builder().uuid(createdDrive.getUuid()).build())).build());

      createdTags = api.createTags(newTagsList);
      assertEquals(createdTags.size(), newTagsList.size());

      for (int i = 0; i < newTagsList.size(); i++) {
         checkTag(newTagsList.get(i), createdTags.get(i));
      }
   }

   @Test(dependsOnMethods = {"testCreateTag"})
   public void testEditTag() throws Exception {
      Map<String, String> meta = Maps.newHashMap();
      meta.put("description", "test tag");

      Tag editedTag = new Tag.Builder().meta(meta).name("Edited tag")
            .resources(ImmutableList.of(new TagResource.Builder().uuid(createdDrive.getUuid()).build())).build();

      checkTag(editedTag, api.editTag(createdTag.getUuid(), editedTag));
   }

   @Test(dependsOnMethods = {"testEditTag"})
   public void testDeleteTag() throws Exception {
      String uuid = createdTag.getUuid();
      api.deleteTag(uuid);
      assertNull(api.getTagInfo(uuid));
   }

   @Test(dependsOnMethods = {"testCreateTags"})
   public void testDeleteTags() throws Exception {
      ImmutableList.Builder<String> uuids = ImmutableList.builder();
      for (Tag tag : createdTags) {
         uuids.add(tag.getUuid());
         api.deleteTag(tag.getUuid());
      }

      // Verify all tags no longer exist
      FluentIterable<Tag> tags = api.listTags().concat();
      assertFalse(any(transform(tags, extractUuid()), in(uuids.build())));
   }

   @Test
   public void testGetProfileInfo() throws Exception {
      assertNotNull(api.getProfileInfo());
   }

   @Test
   public void testEditProfileInfo() throws Exception {
      ProfileInfo profileInfo = new ProfileInfo.Builder()
            .address("edited address")
            .bankReference("sigma111")
            .company("Awesome company")
            .country("ES")
            .email("user@example.com")
            .firstName("Tim")
            .lastName("Testersson")
            .town("New York")
            .build();

      checkProfileInfo(profileInfo, api.editProfileInfo(profileInfo));
   }

   @Test
   public void testGetAccountBalance() throws Exception {
      assertNotNull(api.getAccountBalance());
   }

   @Test
   public void testGetCurrentUsage() throws Exception {
      assertNotNull(api.getCurrentUsage());
   }

   @Test
   public void testListSubscriptions() throws Exception {
      for (Subscription subscription : api.listSubscriptions().concat()) {
         assertNotNull(subscription);
      }
   }

   @Test
   public void testGetSubscription() throws Exception {
      for (Subscription subscription : api.listSubscriptions().concat()) {
         assertNotNull(api.getSubscription(subscription.getId()));
      }
   }

   @Test
   public void testCalculateSubscriptions() throws Exception {
      long monthInMilliseconds = (long) 30 * 24 * 3600 * 1000;
      Date startTime = new Date();
      Date endTime = new Date(startTime.getTime() + monthInMilliseconds);

      List<CalcSubscription> subscriptionsToCalculate = ImmutableList.of(
            new CalcSubscription.Builder()
                  .startTime(startTime)
                  .resource(SubscriptionResource.IP)
                  .endTime(endTime)
                  .build(),
            new CalcSubscription.Builder()
                  .startTime(startTime)
                  .endTime(endTime)
                  .resource(SubscriptionResource.VLAN)
                  .discountAmount(10.5d)
                  .build()
      );
      SubscriptionCalculator subscriptionCalculator = api.calculateSubscriptions(subscriptionsToCalculate);
      List<CalcSubscription> calculatedSubscriptions = subscriptionCalculator.getSubscriptions();
      assertEquals(calculatedSubscriptions.size(), subscriptionsToCalculate.size());
      for (int i = 0; i < calculatedSubscriptions.size(); i++) {
         assertEquals(calculatedSubscriptions.get(i).getResource(), subscriptionsToCalculate.get(i).getResource());
      }
   }

   @Test
   public void testGetPricing() throws Exception {
      assertNotNull(api.getPricing());
   }

   @Test
   public void testListDiscounts() throws Exception {
      for (Discount discount : api.listDiscounts().concat()) {
         assertNotNull(discount);
      }
   }

   @Test
   public void testListTransactions() throws Exception {
      for (Transaction transaction : api.listTransactions(new PaginationOptions.Builder().build())) {
         assertNotNull(transaction);
      }
   }

   @Test
   public void testListLicenses() throws Exception {
      for (License license : api.listLicenses().concat()) {
         assertNotNull(license);
      }
   }

   private void checkDrive(DriveInfo newDrive, DriveInfo createdDrive) {
      assertEquals(newDrive.getName(), createdDrive.getName());
      assertEquals(newDrive.getMedia(), createdDrive.getMedia());
   }

   private void checkServer(ServerInfo newServer, ServerInfo createdServer) {
      assertEquals(newServer.getName(), createdServer.getName());
      assertEquals(newServer.getMemory(), createdServer.getMemory());
      assertEquals(newServer.getVncPassword(), createdServer.getVncPassword());
      assertEquals(newServer.getCpu(), createdServer.getCpu());

      assertEquals(newServer.getDrives().size(), createdServer.getDrives().size());

      for (int i = 0; i < newServer.getDrives().size(); i++) {
         checkServerDrive(newServer.getDrives().get(i), createdServer.getDrives().get(i));
      }
   }

   private void checkServerDrive(ServerDrive newServerDrive, ServerDrive createdServerDrive) {
      assertEquals(newServerDrive.getBootOrder(), createdServerDrive.getBootOrder());
      assertEquals(newServerDrive.getDeviceChannel(), createdServerDrive.getDeviceChannel());
      assertEquals(newServerDrive.getDeviceEmulationType(), createdServerDrive.getDeviceEmulationType());
   }

   private void checkFirewallPolicy(FirewallPolicy newFirewallPolicy, FirewallPolicy createdFirewallPolicy) {
      assertEquals(newFirewallPolicy.getName(), createdFirewallPolicy.getName());
      assertEquals(newFirewallPolicy.getRules(), createdFirewallPolicy.getRules());
   }

   private void checkVlAN(VLANInfo newVLAN, VLANInfo createdVLAN) {
      assertEquals(newVLAN.getMeta(), createdVLAN.getMeta());
   }

   private void checkIP(IPInfo newIP, IPInfo createdIP) {
      assertEquals(newIP.getMeta(), createdIP.getMeta());
   }

   private void checkTag(Tag newTag, Tag createdTag) {
      assertEquals(newTag.getName(), createdTag.getName());
      assertEquals(newTag.getMeta(), createdTag.getMeta());

      assertEquals(newTag.getResources().size(), createdTag.getResources().size());

      for (int i = 0; i < newTag.getResources().size(); i++) {
         checkTagRes(newTag.getResources().get(i), createdTag.getResources().get(i));
      }
   }

   private void checkTagRes(TagResource newTagResource, TagResource createdTagResource) {
      assertEquals(newTagResource.getUuid(), createdTagResource.getUuid());
   }

   private void checkProfileInfo(ProfileInfo newProfileInfo, ProfileInfo createdProfileInfo) {
      assertEquals(newProfileInfo.getAddress(), createdProfileInfo.getAddress());
      assertEquals(newProfileInfo.getCompany(), createdProfileInfo.getCompany());
      assertEquals(newProfileInfo.getCountry(), createdProfileInfo.getCountry());
      assertEquals(newProfileInfo.getFirstName(), createdProfileInfo.getFirstName());
      assertEquals(newProfileInfo.getLastName(), createdProfileInfo.getLastName());
      assertEquals(newProfileInfo.getTown(), createdProfileInfo.getTown());
   }
   
   private static Function<Item, String> extractUuid() {
      return new Function<Item, String>() {
         @Override
         public String apply(Item input) {
            return input.getUuid();
         }
      };
   }
}
