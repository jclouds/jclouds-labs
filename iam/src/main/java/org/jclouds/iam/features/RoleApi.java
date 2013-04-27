/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.iam.features;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.iam.domain.InstanceProfile;
import org.jclouds.iam.domain.Role;
import org.jclouds.iam.functions.InstanceProfilesForRoleToPagedIterable;
import org.jclouds.iam.functions.RolesToPagedIterable;
import org.jclouds.iam.xml.ListInstanceProfilesResultHandler;
import org.jclouds.iam.xml.ListRolesResultHandler;
import org.jclouds.iam.xml.RoleHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Amazon IAM via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference" />
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface RoleApi {
   /**
    * Creates a new role for your AWS account
    * 
    * @param name
    *           Name of the role to create.
    * @param assumeRolePolicy
    *           The policy that grants an entity permission to assume the role.}
    * @return the new role
    */
   @Named("CreateRole")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "CreateRole")
   @XMLResponseParser(RoleHandler.class)
   Role createWithPolicy(@FormParam("RoleName") String name,
         @FormParam("AssumeRolePolicyDocument") String assumeRolePolicy);

   /**
    * like {@link #createWithPolicy(String, String)}, except you can specify a path.
    */
   @Named("CreateRole")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "CreateRole")
   @XMLResponseParser(RoleHandler.class)
   Role createWithPolicyAndPath(@FormParam("RoleName") String name,
         @FormParam("AssumeRolePolicyDocument") String assumeRolePolicy, @FormParam("Path") String path);

   /**
    * returns all roles in order.
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   @Transform(RolesToPagedIterable.class)
   PagedIterable<Role> list();

   /**
    * retrieves up to 100 roles in order.
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   IterableWithMarker<Role> listFirstPage();

   /**
    * retrieves up to 100 roles in order, starting at {@code marker}
    * 
    * @param marker
    *           starting point to resume the list
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   IterableWithMarker<Role> listAt(@FormParam("Marker") String marker);

   /**
    * returns all roles in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   @Transform(RolesToPagedIterable.class)
   PagedIterable<Role> listPathPrefix(@FormParam("PathPrefix") String pathPrefix);

   /**
    * retrieves up to 100 roles in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   IterableWithMarker<Role> listPathPrefixFirstPage(@FormParam("PathPrefix") String pathPrefix);

   /**
    * retrieves up to 100 roles in order at the specified {@code pathPrefix}, starting at {@code marker}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    * @param marker
    *           starting point to resume the list
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   IterableWithMarker<Role> listPathPrefixAt(@FormParam("PathPrefix") String pathPrefix,
         @FormParam("Marker") String marker);

   /**
    * Retrieves information about the specified role, including the role's path, GUID, and ARN.
    * 
    * @param name
    *           Name of the role to get information about.
    * @return null if not found
    */
   @Named("GetRole")
   @POST
   @Path("/")
   @XMLResponseParser(RoleHandler.class)
   @FormParams(keys = "Action", values = "GetRole")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Role get(@FormParam("RoleName") String name);

   /**
    * returns all instance profiles in order for this role.
    * 
    * @param name
    *           Name of the role to get instance profiles for.
    */
   @Named("ListInstanceProfilesForRole")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfilesForRole")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   @Transform(InstanceProfilesForRoleToPagedIterable.class)
   PagedIterable<InstanceProfile> listInstanceProfiles(@FormParam("RoleName") String name);

   /**
    * retrieves up to 100 instance profiles in order for this role.
    * 
    * @param name
    *           Name of the role to get instance profiles for.
    */
   @Named("ListInstanceProfilesForRole")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfilesForRole")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   IterableWithMarker<InstanceProfile> listFirstPageOfInstanceProfiles(
         @FormParam("RoleName") String name);

   /**
    * retrieves up to 100 instance profiles in order for this role, starting at {@code marker}
    * 
    * @param name
    *           Name of the role to get instance profiles for.
    * @param marker
    *           starting point to resume the list
    */
   @Named("ListInstanceProfilesForRole")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfilesForRole")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   IterableWithMarker<InstanceProfile> listInstanceProfilesAt(@FormParam("RoleName") String name,
         @FormParam("Marker") String marker);

   /**
    * Deletes the specified role. The role must not have any policies attached. 
    * 
    * @param name
    *           Name of the role to delete
    */
   @Named("DeleteRole")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "DeleteRole")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@FormParam("RoleName") String name);
}
