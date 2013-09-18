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


import com.google.inject.name.Named;
import org.jclouds.Fallbacks;
import org.jclouds.cloudsigma2.binders.BindCreateSubscriptionRequest;
import org.jclouds.cloudsigma2.binders.BindCreateSubscriptionRequestList;
import org.jclouds.cloudsigma2.binders.BindDriveToJson;
import org.jclouds.cloudsigma2.binders.BindDrivesToJson;
import org.jclouds.cloudsigma2.binders.BindFirewallPoliciesListToJsonRequest;
import org.jclouds.cloudsigma2.binders.BindFirewallPolicyToJsonRequest;
import org.jclouds.cloudsigma2.binders.BindIPInfoToJsonRequest;
import org.jclouds.cloudsigma2.binders.BindLibraryDriveToJson;
import org.jclouds.cloudsigma2.binders.BindProfileInfoToJsonRequest;
import org.jclouds.cloudsigma2.binders.BindServerInfoListToJsonRequest;
import org.jclouds.cloudsigma2.binders.BindServerInfoToJsonRequest;
import org.jclouds.cloudsigma2.binders.BindTagListToJsonRequest;
import org.jclouds.cloudsigma2.binders.BindTagToJsonRequest;
import org.jclouds.cloudsigma2.binders.BindUuidStringsToJsonArray;
import org.jclouds.cloudsigma2.binders.BindVLANToJsonRequest;
import org.jclouds.cloudsigma2.domain.AccountBalance;
import org.jclouds.cloudsigma2.domain.CreateSubscriptionRequest;
import org.jclouds.cloudsigma2.domain.CurrentUsage;
import org.jclouds.cloudsigma2.domain.Discount;
import org.jclouds.cloudsigma2.domain.Drive;
import org.jclouds.cloudsigma2.domain.DriveInfo;
import org.jclouds.cloudsigma2.domain.DrivesListRequestFieldsGroup;
import org.jclouds.cloudsigma2.domain.FirewallPolicy;
import org.jclouds.cloudsigma2.domain.IP;
import org.jclouds.cloudsigma2.domain.IPInfo;
import org.jclouds.cloudsigma2.domain.LibraryDrive;
import org.jclouds.cloudsigma2.domain.License;
import org.jclouds.cloudsigma2.domain.Pricing;
import org.jclouds.cloudsigma2.domain.ProfileInfo;
import org.jclouds.cloudsigma2.domain.Server;
import org.jclouds.cloudsigma2.domain.ServerAvailabilityGroup;
import org.jclouds.cloudsigma2.domain.ServerInfo;
import org.jclouds.cloudsigma2.domain.Subscription;
import org.jclouds.cloudsigma2.domain.Tag;
import org.jclouds.cloudsigma2.domain.Transaction;
import org.jclouds.cloudsigma2.domain.VLANInfo;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.Closeable;
import java.util.List;

/**
 * Provides synchronous access to CloudSigma v2 API.
 *
 * @author Vladimir Shevchenko
 * @see <a href="https://zrh.cloudsigma.com/docs/" />
 */
@RequestFilters(BasicAuthentication.class)
@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@SkipEncoding({'?', '/', '='})
public interface CloudSigma2Api extends Closeable {

    /**
     * Gets the list of drives to which the authenticated user has access.
     *
     * @return list of drives or empty list if no drives are found
     */
    @Named("drive:listDrives")
    @GET
    @Path("/drives/?limit=0")
    @SelectJson("objects")
    List<Drive> listDrives();

    /**
     * Gets the list of drives to which the authenticated user has access.
     *
     * @param fields A set of field names specifying the returned fields
     * @param limit number of drives to show
     * @return or empty set if no drives are found
     */
    @Named("drive:listDrives")
    @GET
    @Path("/drives/?limit=0")
    @SelectJson("objects")
    List<DriveInfo> listDrives(@QueryParam("fields") DrivesListRequestFieldsGroup fields
            , @DefaultValue("0") @QueryParam("limit") int limit);

    /**
     * Gets the detailed list of drives with additional information to which the authenticated user has access.
     *
     * @return list of drives or empty list if no drives are found
     */
    @Named("drive:listDrivesInfo")
    @GET
    @Path("/drives/detail/?limit=0")
    @SelectJson("objects")
    List<DriveInfo> listDrivesInfo();

    /**
     * Gets detailed information for drive identified by drive uuid
     *
     * @param uuid drive uuid to get
     * @return null, if not found
     */
    @Named("drive:getDriveInfo/{uuid}")
    @GET
    @Path("/drives/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    DriveInfo getDriveInfo(@PathParam("uuid") String uuid);

    /**
     * Creates a new drive
     *
     * @param createDrive required parameters: name, size, media
     * @return newly created drive
     */
    @Named("drive:createDrive")
    @POST
    @Path("/drives/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @SelectJson("objects")
    @OnlyElement
    DriveInfo createDrive(@BinderParam(BindDriveToJson.class) DriveInfo createDrive);

    /**
     * Creates multiple new drives
     *
     * @param createDrives required parameters: name, size, media
     * @return newly created drives
     */
    @Named("drive:listDrives")
    @POST
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @SelectJson("objects")
    @Path("/drives/")
    List<DriveInfo> createDrives(@BinderParam(BindDrivesToJson.class) List<DriveInfo> createDrives);

    /**
     * Deletes a single mounted or unmounted drive.
     *
     * @param uuid what to delete
     */
    @Named("drive:deleteDrive/{uuid}")
    @DELETE
    @Path("/drives/{uuid}/")
    @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
    void deleteDrive(@PathParam("uuid") String uuid);

    /**
     * Deletes multiple mounted or unmounted drives specified by their UUID’s.
     *
     * @param uuids what drives to delete
     */
    @Named("drive:deleteDrives")
    @DELETE
    @Path("/drives/")
    @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
    void deleteDrives(@BinderParam(BindUuidStringsToJsonArray.class) Iterable<String> uuids);

    /**
     * Edits a mounted or unmounted drive. If mounted, the server mounted on should be stopped.
     *
     * @param sourceUuid source drive to edit
     * @param driveInfo drive parameters to change
     * @return changed drive
     */
    @Named("drive:editDrive/{uuid}")
    @PUT
    @Path("/drives/{uuid}/")
    @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
    DriveInfo editDrive(@PathParam("uuid") String sourceUuid
            , @BinderParam(BindDriveToJson.class) DriveInfo driveInfo);


    /**
     * Clones a drive. Request body is optional and any or all of the key/value pairs can be omitted.
     *
     * @param sourceUuid source drive to clone
     * @param driveInfo drive parameters to change
     * @return new drive
     */
    @Named("drive:cloneDrive/{uuid}")
    @POST
    @Path("/drives/{uuid}/action/?do=clone")
    @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
    DriveInfo cloneDrive(@PathParam("uuid") String sourceUuid
            , @Nullable @BinderParam(BindDriveToJson.class) DriveInfo driveInfo);

    /**
     * Gets the list of library drives to which the authenticated user has access.
     *
     * @return list of library drives to which the authenticated user has access
     */
    @Named("libdrive:listLibraryDrives")
    @GET
    @Path("/libdrives/?limit=0")
    @SelectJson("objects")
    List<LibraryDrive> listLibraryDrives();

    /**
     * Gets detailed information for library drive identified by uuid.
     *
     * @param uuid uuid of library drive to be listed
     * @return drive information or null if not found
     */
    @Named("libdrive:getLibraryDrive/{uuid}")
    @GET
    @Path("/libdrives/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    LibraryDrive getLibraryDrive(@PathParam("uuid") String uuid);

    /**
     * If a library drive is not a CDROM, you have to clone it in your account in order to use it.
     *
     * @param uuid uuid of library drive to clone
     * @param libraryDrive cloned drive
     * @return cloned drive information or null if not found
     */
    @Named("libdrive:cloneLibraryDrive/{uuid}")
    @POST
    @Path("/libdrives/{uuid}/action/?do=clone")
    @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
    LibraryDrive cloneLibraryDrive(@PathParam("uuid") String uuid
            , @Nullable @BinderParam(BindLibraryDriveToJson.class) LibraryDrive libraryDrive);

    /**
     * Gets the list of servers to which the authenticated user has access.
     *
     * @return list of servers or empty list if no servers are found
     */
    @Named("server:listServers")
    @GET
    @Path("/servers/?limit=0")
    @SelectJson("objects")
    List<Server> listServers();

    /**
     * Gets the detailed list of servers to which the authenticated user has access.
     *
     * @return list of servers or empty list if no servers are found
     */
    @Named("server:listServersInfo")
    @GET
    @Path("/servers/detail/?limit=0")
    @SelectJson("objects")
    List<ServerInfo> listServersInfo();

    /**
     * Creates a new virtual server or multiple servers.
     * The minimial amount of information you need to set is:
     *      cpu
     *      memory
     *      name
     *      vncPassword
     *
     * @param createServer required parameters: cpu, memory, name, vncPassword
     * @return newly created server
     */
    @Named("server:createServer")
    @POST
    @Path("/servers/")
    @SelectJson("objects")
    @OnlyElement
    ServerInfo createServer(@BinderParam(BindServerInfoToJsonRequest.class) ServerInfo createServer);

    /**
     * create a new servers
     *
     * @param servers servers to create. Required parameters: cpu, memory, name, vncPassword
     * @return newly created servers
     */
    @Named("server:createServers")
    @POST
    @Path("/servers/")
    @SelectJson("objects")
    List<ServerInfo> createServers(@BinderParam(BindServerInfoListToJsonRequest.class) Iterable<ServerInfo> servers);

    /**
     * Edits a server. Used also for attaching NIC’s and drives to servers.
     *
     * @param uuid server uuid
     * @param server data to change
     * @return modified server
     */
    @Named("server:editServer/{uuid}")
    @PUT
    @Path("/servers/{uuid}/")
    @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
    ServerInfo editServer(@PathParam("uuid") String uuid
            , @BinderParam(BindServerInfoToJsonRequest.class) ServerInfo server);

    /**
     * Deletes a single server.
     *
     * @param uuid uuid of server to delete
     */
    @Named("server:deleteServer/{uuid}")
    @DELETE
    @Path("/servers/{uuid}/")
    @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
    void deleteServer(@PathParam("uuid") String uuid);

    /**
     * Deletes multiple servers specified by their UUID’s.
     *
     * @param uuids server uuids to delete
     */
    @Named("server:deleteServers")
    @DELETE
    @Path("/servers/")
    @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
    void deleteServers(@BinderParam(BindUuidStringsToJsonArray.class) Iterable<String> uuids);

    /**
     * Clones a server. Empty body.
     * Does cascading clone of server drives.
     * IPs of the cloned server are set to DHCP.
     * All other properties of the clone are equal to the original.
     *
     * @param uuid server what to clone
     *
     * @return cloned server
     */
    @Named("server:cloneServer/{uuid}")
    @POST
    @Path("/servers/{uuid}/action/?do=clone")
    @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
    ServerInfo cloneServer(@PathParam("uuid") String uuid);

    /**
     * Gets detailed information for server identified by server uuid.
     *
     * @param uuid server uuid
     * @return server info or null, if not found
     */
    @Named("server:getServerInfo/{uuid}")
    @GET
    @Path("/servers/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    ServerInfo getServerInfo(@PathParam("uuid") String uuid);

    /**
     * Starts a server with specific UUID.
     *
     * @param uuid uuid of server to start
     */
    @Named("server:startServer/{uuid}")
    @POST
    @Path("/servers/{uuid}/action/?do=start")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    void startServer(@PathParam("uuid") String uuid);

    /**
     * Stops a server with specific UUID.
     *
     * @param uuid uuid of server to stop
     */
    @Named("server:stopServer/{uuid}")
    @POST
    @Path("/servers/{uuid}/action/?do=stop")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    void stopServer(@PathParam("uuid") String uuid);

    /**
     * Starts a server with specific UUID assuring that it is started on a different physical infrastructure host
     * from the other servers specified in the avoid argument which is
     * a single server UUID or a comma-separated list of server UUIDs.
     * This way the server specified by uuid is run in a distinct availability group from the other listed servers.
     * Note that it might not always be possible to run a server in a different availability group,
     * therefore the order of the avoid list also signifies the priority of avoiding other servers.
     *
     * @param uuid      uuid of server to start
     * @param uuidGroup availability group to avoid
     */
    @Named("server:startServerInSeparateAvailabilityGroup/{uuid}")
    @POST
    @Path("/servers/{uuid}/action/?do=start")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    void startServerInSeparateAvailabilityGroup(@PathParam("uuid") String uuid
            , @QueryParam("avoid") List<String> uuidGroup);

    /**
     * Opens a VNC tunnel to a server with specific UUID.
     *
     * @param uuid uuid of server to open VNC tunnel
     */
    @Named("server:openServerVNCTunnel/{uuid}")
    @POST
    @Path("/servers/{uuid}/action/?do=open_vnc")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    void openServerVNCTunnel(@PathParam("uuid") String uuid);

    /**
     * Closes a VNC tunnel to a server with specific UUID.
     *
     * @param uuid uuid of server to close VNC tunnel
     */
    @Named("server:closeServerVCNTunnel/{uuid}")
    @POST
    @Path("/servers/{uuid}/action/?do=close_vnc")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    void closeServerVCNTunnel(@PathParam("uuid") String uuid);

    /**
     * @return which servers share same physical infrastructure host.
     */
    @Named("server:listServerAvailabilityGroup")
    @GET
    @Path("/servers/availability_groups/?limit=0")
    List<List<String>> listServerAvailabilityGroup();

    /**
     * Queries which other servers share same physical host with the given one.
     *
     * @param uuid Uuid of server to find availability group
     *
     * @return an array holding server UUIDs. The response includes also the UUID of the queried server.
     */
    @Named("server:getServerAvailabilityGroup/{uuid}")
    @GET
    @PathParam("/servers/availability_groups/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    ServerAvailabilityGroup getServerAvailabilityGroup(@PathParam("uuid") String uuid);

    /**
     * Gets the list of firewall policies to which the authenticated user has access.
     *
     * @return list of firewall policies to which the authenticated user has access.
     */
    @Named("fwpolicy:listFirewallPolicies")
    @GET
    @Path("/fwpolicies/?limit=0")
    @SelectJson("objects")
    List<FirewallPolicy> listFirewallPolicies();

    /**
     * Gets a detailed list of firewall policies to which the authenticated user has access.
     *
     * @return list of firewall policies to which the authenticated user has access.
     */
    @Named("fwpolicy:listFirewallPoliciesInfo")
    @GET
    @Path("/fwpolicies/detail/?limit=0")
    @SelectJson("objects")
    List<FirewallPolicy> listFirewallPoliciesInfo();

    /**
     * Creates firewall policies.
     *
     * @param firewallPolicies firewall policies to create
     * @return list of created firewall policies
     */
    @Named("fwpolicy:createFirewallPolicies")
    @POST
    @Path("/fwpolicies/")
    @SelectJson("objects")
    List<FirewallPolicy> createFirewallPolicies(
            @BinderParam(BindFirewallPoliciesListToJsonRequest.class) List<FirewallPolicy> firewallPolicies);

    /**
     * Creates a firewall policy.
     *
     * @param firewallPolicy firewall policy to create
     * @return created firewall policy
     */
    @Named("fwpolicy:createFirewallPolicy")
    @POST
    @Path("/fwpolicies/")
    @SelectJson("objects")
    @OnlyElement
    FirewallPolicy createFirewallPolicy(
            @BinderParam(BindFirewallPolicyToJsonRequest.class) FirewallPolicy firewallPolicy);

    /**
     * Update an existing firewall policy
     *
     * @param uuid uuid of policy to update
     * @param firewallPolicy firewall policy data to update
     * @return updated firewall policy
     */
    @Named("fwpolicy:editFirewallPolicy/{uuid}")
    @PUT
    @Path("/fwpolicies/{uuid}/")
    FirewallPolicy editFirewallPolicy(@PathParam("uuid") String uuid
            , @BinderParam(BindFirewallPolicyToJsonRequest.class) FirewallPolicy firewallPolicy);

    /**
     * Gets detailed information for VLAN identified by VLAN uuid.
     *
     * @param uuid uuid of VLAN to get
     * @return null, if not found
     */
    @Named("vlan:getVLANInfo/{uuid}")
    @GET
    @Path("/vlans/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    VLANInfo getVLANInfo(@PathParam("uuid") String uuid);

    /**
     * Gets the list of VLANs to which the authenticated user has access.
     *
     * @return list of VLANs or empty list if no vlans are found
     */
    @Named("vlan:listVLANs")
    @GET
    @Path("/vlans/?limit=0")
    @SelectJson("objects")
    List<VLANInfo> listVLANs();

    /**
     * Gets the list of VLANs to which the authenticated user has access.
     *
     * @return list of VLANs or empty list if no vlans are found
     */
    @Named("vlan:listVLANInfo")
    @GET
    @Path("/vlans/detail/?limit=0")
    @SelectJson("objects")
    List<VLANInfo> listVLANInfo();

    /**
     * Currently only VLAN meta field can be edited.
     *
     * @param uuid uuid of VLAN to edit
     * @param vlanInfo data to change
     * @return changed VLAN
     */
    @Named("vlan:listVLANInfo/{uuid}")
    @PUT
    @Path("/vlans/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    VLANInfo editVLAN(@PathParam("uuid") String uuid
            , @BinderParam(BindVLANToJsonRequest.class) VLANInfo vlanInfo);

    /**
     * Gets the list of IPs to which the authenticated user has access.
     *
     * @return list of IPs or empty list if no ips are found
     */
    @Named("ip:listIPs")
    @GET
    @Path("/ips/?limit=0")
    @SelectJson("objects")
    List<IP> listIPs();

    /**
     * Gets the detailed list of IPs with additional information to which the authenticated user has access.
     *
     * @return list of IPs or empty list if no ips are found
     */
    @Named("ip:listIPInfo")
    @GET
    @Path("/ips/detail/?limit=0")
    @SelectJson("objects")
    List<IPInfo> listIPInfo();

    /**
     * Gets detailed information for IP identified by IP uuid.
     *
     * @param uuid uuid of IP to get
     * @return null, if not found
     */
    @Named("ip:getIPInfo/{uuid}")
    @GET
    @Path("/ips/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    IPInfo getIPInfo(@PathParam("uuid") String uuid);

    /**
     * Currently only IP meta field can be edited.
     *
     * @param uuid uuid of IP to edit
     * @param ipInfo data to change
     * @return changed IP
     */
    @Named("ip:editIP/{uuid}")
    @PUT
    @Path("/ips/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    IPInfo editIP(@PathParam("uuid") String uuid
            , @BinderParam(BindIPInfoToJsonRequest.class) IPInfo ipInfo);

    /**
     * Gets the list of tags to which the authenticated user has access.
     *
     * @return list of tags to which the authenticated user has access
     */
    @Named("tag:listTags")
    @GET
    @Path("/tags/?limit=0")
    @SelectJson("objects")
    List<Tag> listTags();

    /**
     * Gets the detailed list of tags with additional information to which the authenticated user has access,
     * like the tagged resource
     *
     * @return detailed listings of your tags
     */
    @Named("tag:listTagsInfo")
    @GET
    @Path("/tags/detail/?limit=0")
    @SelectJson("objects")
    List<Tag> listTagsInfo();

    /**
     * Gets detailed information for tag identified by tag uuid.
     *
     * @param uuid tag uuid
     * @return detailed info of tag
     */
    @Named("tag:getTagInfo/{uuid}")
    @GET
    @Path("/tags/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    Tag getTagInfo(@PathParam("uuid") String uuid);

    /**
     * Edits a tag.
     * It is possible to add or remove resources to a tag by replacing the resources list with a new one
     *
     * @param uuid tag uuid
     * @param tag info to change
     * @return detailed info of tag
     */
    @Named("tag:editTag/{uuid}")
    @PUT
    @Path("/tags/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    Tag editTag(@PathParam("uuid") String uuid, @BinderParam(BindTagToJsonRequest.class) Tag tag);

    /**
     * Creates a new tag
     *
     * @param tag tag to create
     * @return created tag
     */
    @Named("tag:createTag")
    @POST
    @Path("/tags/")
    @SelectJson("objects")
    @OnlyElement
    Tag createTag(@BinderParam(BindTagToJsonRequest.class) Tag tag);

    /**
     * Deletes a single tag.
     *
     * @param uuid uuid of tag to delete
     */
    @Named("tag:deleteTag/{uuid}")
    @DELETE
    @Path("/tags/{uuid}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    void deleteTag(@PathParam("uuid")String uuid);

    /**
     * Creates a multiple new tags
     *
     * @param tags tags to create
     * @return created tags
     */
    @Named("tag:createTags")
    @POST
    @Path("/tags/")
    @SelectJson("objects")
    List<Tag> createTags(@BinderParam(BindTagListToJsonRequest.class) List<Tag> tags);

    /**
     * Gets the user profile.
     *
     * @return profile information
     */
    @Named("profile:getProfileInfo")
    @GET
    @Path("/profile/")
    ProfileInfo getProfileInfo();

    /**
     * Edits a user profile.
     *
     * @param profile data to change
     * @return info or null, if not found
     */
    @Named("profile:editProfileInfo")
    @PUT
    @Path("/profile/")
    ProfileInfo editProfileInfo(@BinderParam(BindProfileInfoToJsonRequest.class) ProfileInfo profile);

    /**
     * Get the balance and currency of the current account.
     *
     * @return current account balance and currency
     */
    @Named("balance:getAccountBalance")
    @GET
    @Path("/balance/")
    AccountBalance getAccountBalance();

    /**
     * Get the current usage of the user.
     *
     * @return current usage of the user
     */
    @Named("currentusage:getCurrentUsage")
    @GET
    @Path("/currentusage/")
    CurrentUsage getCurrentUsage();

    /**
     * Gets the list of subscriptions of the user.
     *
     * @return list of subscriptions of the user.
     */
    @Named("subscription:listSubscriptions")
    @GET
    @Path("/subscriptions/?limit=0")
    @SelectJson("objects")
    List<Subscription> listSubscriptions();

    /**
     * Returns requested subscription
     *
     * @return requested subscription.
     */
    @Named("subscription:getSubscription")
    @GET
    @Path("/subscriptions/{id}/")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    Subscription getSubscription(@PathParam("id") String id);

    /**
     * This is identical to the listSubscriptions(), except that subscriptions are not actually bought.
     *
     * @return list of subscriptions that are not actually bought.
     */
    @Named("subscription:listSubscriptionsCalculator")
    @GET
    @Path("/subscriptioncalculator/?limit=0")
    @SelectJson("objects")
    List<Subscription> listSubscriptionsCalculator();

    /**
     * Creates a new subscription.
     * Subscription times are rounded to noon UTC, using the following rules:
     *      - End time is always rounded to the next noon.
     *      - Start time is rounded to the maximum between the current time an the previous noon.
     *      This means that subscriptions bought for now do start now, but subscriptions for the future start at the previous noon.
     *
     * @param subscriptionRequest subscription request object
     * @return created subscription
     */
    @Named("subscription:listSubscriptionsCalculator")
    @POST
    @Path("/subscriptions/")
    @SelectJson("objects")
    @OnlyElement
    Subscription createSubscription(
            @BinderParam(BindCreateSubscriptionRequest.class) CreateSubscriptionRequest subscriptionRequest);

    /**
     * Creates a new subscription. There is a limit of 500 subscriptions that can be purchased in one request.
     * Subscription times are rounded to noon UTC, using the following rules:
     *      - End time is always rounded to the next noon.
     *      - Start time is rounded to the maximum between the current time an the previous noon.
     *      This means that subscriptions bought for now do start now, but subscriptions for the future start at the previous noon.
     *
     * @param subscriptionRequest parameters for new subscriptions
     * @return new subscriptions
     */
    @Named("subscription:createSubscriptions")
    @POST
    @Path("/subscriptions/")
    @SelectJson("objects")
    List<Subscription> createSubscriptions(
            @BinderParam(BindCreateSubscriptionRequestList.class) List<CreateSubscriptionRequest> subscriptionRequest);

    /**
     * Extends the subscription. An extended subscription is actually just another subscription that is linked to the original
     * If a period or and end_time are specified in the request,they are used. If neither are specified,
     * the creation length of the subscription is used.
     *
     * A caveat to this is that a subscription created initially with an end_time, the exact interval is used.
     * Subscriptions that are created with a period have the period parsed again in the context of the new start_time.
     * An example would be a subscription created on the 1st of February with a period of ‘1 month’ will be extended for 31 days,
     * but one that was created with an end date of 1st of March will be extended for 28 days.
     *
     * If the specified subscription has actually been extended, it traverses and extends the last subscription in the chain.
     *
     * @param id id of subscription to extend
     */
    @Named("subscription:extendSubscription/{id}")
    @POST
    @Path("/subscriptions/{id}/action/?do=extend")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    void extendSubscription(@PathParam("id") String id);

    /**
     * Toggles the autorenew flag of the subscription.
     *
     * @param id id of subscription to enable autorenew
     */
    @Named("subscription:enableSubscriptionAutorenew/{id}")
    @POST
    @Path("/subscriptions/{id}/action/?do=auto_renew")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    void enableSubscriptionAutorenew(@PathParam("id") String id);

    /**
     * Gets the pricing information that are applicable to the cloud. Subscription prices use a burst level of 0.
     *
     * @return pricing information that are applicable to the cloud.
     */
    @Named("pricing:getPricing")
    @GET
    @Path("/pricing/")
    Pricing getPricing();

    /**
     * Get discount information.
     *
     * @return discount information.
     */
    @Named("discount:listDiscounts")
    @GET
    @Path("/discount/?limit=0")
    @SelectJson("objects")
    List<Discount> listDiscounts();

    /**
     * Get the transactions for the account.
     *
     * @return transactions for the account.
     */
    @Named("ledger:listTransactions")
    @GET
    @Path("/ledger/?limit=0")
    @SelectJson("objects")
    List<Transaction> listTransactions();

    /**
     * Get the licenses available on the cloud. The type of the license can be one of:
     *      install - These licenses are billed per installation, regardless of whether it is attached to a running guests or not.
     *      instance - These licenses are billed per running instance of a guest. A license attached to a guest that’s stopped is not billed.
     *      stub - These licenses are billed per a metric specified by the customer (i.e. per number of users license)
     *
     * The user metric field specifies what attribute on the instance of the guest is used for determining the number of licenses.
     * For example, “smp” will count one license for each CPU/core in the virtual machine.
     *
     * @return licenses available on the cloud
     */
    @Named("license:listLicenses")
    @GET
    @Path("/licenses/?limit=0")
    @SelectJson("objects")
    List<License> listLicenses();
}
