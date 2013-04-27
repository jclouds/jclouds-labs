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
import org.jclouds.iam.functions.InstanceProfilesToPagedIterable;
import org.jclouds.iam.xml.InstanceProfileHandler;
import org.jclouds.iam.xml.ListInstanceProfilesResultHandler;
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
public interface InstanceProfileApi {

   /**
    * Creates a new instance profile for your AWS account
    * 
    * @param name
    *           Name of the instance profile to create.
    * @return the new instance profile
    */
   @Named("CreateInstanceProfile")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "CreateInstanceProfile")
   @XMLResponseParser(InstanceProfileHandler.class)
   InstanceProfile create(@FormParam("InstanceProfileName") String name);

   /**
    * like {@link #create(String)}, except you can specify a path.
    */
   @Named("CreateInstanceProfile")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "CreateInstanceProfile")
   @XMLResponseParser(InstanceProfileHandler.class)
   InstanceProfile createWithPath(@FormParam("InstanceProfileName") String name,
         @FormParam("Path") String path);

   /**
    * returns all instance profiles in order.
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   @Transform(InstanceProfilesToPagedIterable.class)
   PagedIterable<InstanceProfile> list();

   /**
    * retrieves up to 100 instance profiles in order.
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   IterableWithMarker<InstanceProfile> listFirstPage();

   /**
    * retrieves up to 100 instance profiles in order, starting at {@code marker}
    * 
    * @param marker
    *           starting point to resume the list
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   IterableWithMarker<InstanceProfile> listAt(@FormParam("Marker") String marker);

   /**
    * returns all instance profiles in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   @Transform(InstanceProfilesToPagedIterable.class)
   PagedIterable<InstanceProfile> listPathPrefix(@FormParam("PathPrefix") String pathPrefix);

   /**
    * retrieves up to 100 instance profiles in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   IterableWithMarker<InstanceProfile> listPathPrefixFirstPage(
         @FormParam("PathPrefix") String pathPrefix);

   /**
    * retrieves up to 100 instance profiles in order at the specified {@code pathPrefix}, starting at {@code marker}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    * @param marker
    *           starting point to resume the list
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   IterableWithMarker<InstanceProfile> listPathPrefixAt(@FormParam("PathPrefix") String pathPrefix,
         @FormParam("Marker") String marker);

   /**
    * Retrieves information about the specified instance profile, including the instance profile's path, GUID, and ARN.
    * 
    * @param name
    *           Name of the instance profile to get information about.
    * @return null if not found
    */
   @Named("GetInstanceProfile")
   @POST
   @Path("/")
   @XMLResponseParser(InstanceProfileHandler.class)
   @FormParams(keys = "Action", values = "GetInstanceProfile")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   InstanceProfile get(@FormParam("InstanceProfileName") String name);

   /**
    * Deletes the specified instanceProfile. The instance profile must not have any policies attached.
    * 
    * @param name
    *           Name of the instance profile to delete
    */
   @Named("DeleteInstanceProfile")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "DeleteInstanceProfile")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@FormParam("InstanceProfileName") String name);

   /**
    * Adds the specified role to the specified instance profile.
    * 
    * @param name
    *           Name of the instance profile to update.
    * @param roleName
    *           Name of the role to add
    */
   @Named("AddRoleToInstanceProfile")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "AddRoleToInstanceProfile")
   @Fallback(VoidOnNotFoundOr404.class)
   void addRole(@FormParam("InstanceProfileName") String name, @FormParam("RoleName") String roleName);

   /**
    * Removes the specified role from the specified instance profile.
    * 
    * @param name
    *           Name of the instance profile to update.
    * @param roleName
    *           Name of the role to remove
    */
   @Named("RemoveRoleFromInstanceProfile")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "RemoveRoleFromInstanceProfile")
   @Fallback(VoidOnNotFoundOr404.class)
   void removeRole(@FormParam("InstanceProfileName") String name,
         @FormParam("RoleName") String roleName);
}
