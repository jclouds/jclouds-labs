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

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.domain.config.options.LicenseOptions;
import org.jclouds.abiquo.domain.config.options.PropertyOptions;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;

import com.abiquo.server.core.appslibrary.CategoriesDto;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.config.LicenseDto;
import com.abiquo.server.core.config.LicensesDto;
import com.abiquo.server.core.config.SystemPropertiesDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;

/**
 * Provides synchronous access to Abiquo Admin API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/config")
public interface ConfigApi extends Closeable {
   /*********************** License ***********************/

   /**
    * List all licenses.
    * 
    * @return The list of licenses.
    */
   @Named("license:list")
   @GET
   @Path("/licenses")
   @Consumes(LicensesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   LicensesDto listLicenses();

   /**
    * List all active/inactive licenses.
    * 
    * @param options
    *           Optional query params.
    * @return The list of licenses.
    */
   @Named("license:list")
   @GET
   @Path("/licenses")
   @Consumes(LicensesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   LicensesDto listLicenses(LicenseOptions options);

   /**
    * Add a new license.
    * 
    * @param license
    *           The license to add.
    * @return The added license.
    */
   @Named("license:add")
   @POST
   @Produces(LicenseDto.BASE_MEDIA_TYPE)
   @Consumes(LicenseDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Path("/licenses")
   LicenseDto addLicense(@BinderParam(BindToXMLPayload.class) LicenseDto license);

   /**
    * Removes an existing license.
    * 
    * @param license
    *           The license to delete.
    */
   @Named("license:remove")
   @DELETE
   void removeLicense(@EndpointLink("edit") @BinderParam(BindToPath.class) LicenseDto license);

   /*********************** Privilege ***********************/

   /**
    * List all privileges in the system.
    * 
    * @return The list of privileges.
    */
   @Named("privilege:list")
   @GET
   @Path("/privileges")
   @Consumes(PrivilegesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PrivilegesDto listPrivileges();

   /**
    * Get the given privilege.
    * 
    * @param privilegeId
    *           The id of the privilege.
    * @return The privilege or <code>null</code> if it does not exist.
    */
   @Named("privilege:get")
   @GET
   @Path("/privileges/{privilege}")
   @Consumes(PrivilegeDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   PrivilegeDto getPrivilege(@PathParam("privilege") Integer privilegeId);

   /*********************** System Properties ***********************/

   /**
    * List all system properties.
    * 
    * @return The list of properties.
    */
   @Named("property:list")
   @GET
   @Path("/properties")
   @Consumes(SystemPropertiesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   SystemPropertiesDto listSystemProperties();

   /**
    * List properties with options.
    * 
    * @param options
    *           Optional query params.
    * @return The list of system properties.
    */
   @Named("property:list")
   @GET
   @Path("/properties")
   @Consumes(SystemPropertiesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   SystemPropertiesDto listSystemProperties(PropertyOptions options);

   /**
    * Updates a system property.
    * 
    * @param property
    *           The new attributes for the system property.
    * @return The updated system property.
    */
   @Named("property:update")
   @PUT
   @Produces(SystemPropertyDto.BASE_MEDIA_TYPE)
   @Consumes(SystemPropertyDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   SystemPropertyDto updateSystemProperty(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) SystemPropertyDto property);

   /*********************** Category ***********************/

   /**
    * List all categories.
    * 
    * @return The list of categories.
    */
   @Named("category:list")
   @GET
   @Path("/categories")
   @Consumes(CategoriesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   CategoriesDto listCategories();

   /**
    * Get the given category.
    * 
    * @param categoryId
    *           The id of the category.
    * @return The category or <code>null</code> if it does not exist.
    */
   @Named("category:get")
   @GET
   @Path("/categories/{category}")
   @Consumes(CategoryDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   CategoryDto getCategory(@PathParam("category") Integer categoryId);

   /**
    * Create a new category.
    * 
    * @param icon
    *           The category to be created.
    * @return The created category.
    */
   @Named("category:create")
   @POST
   @Path("/categories")
   @Produces(CategoryDto.BASE_MEDIA_TYPE)
   @Consumes(CategoryDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   CategoryDto createCategory(@BinderParam(BindToXMLPayload.class) CategoryDto category);

   /**
    * Updates an existing category.
    * 
    * @param category
    *           The new attributes for the category.
    * @return The updated category.
    */
   @Named("category:update")
   @PUT
   @Produces(CategoryDto.BASE_MEDIA_TYPE)
   @Consumes(CategoryDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   CategoryDto updateCategory(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) CategoryDto category);

   /**
    * Deletes an existing category.
    * 
    * @param icon
    *           The category to delete.
    */
   @Named("category:delete")
   @DELETE
   void deleteCategory(@EndpointLink("edit") @BinderParam(BindToPath.class) CategoryDto category);
}
