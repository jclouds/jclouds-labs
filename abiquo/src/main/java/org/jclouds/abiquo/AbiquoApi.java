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
package org.jclouds.abiquo;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.GET;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.BindLinkToPathAndAcceptHeader;
import org.jclouds.abiquo.features.AdminApi;
import org.jclouds.abiquo.features.CloudApi;
import org.jclouds.abiquo.features.ConfigApi;
import org.jclouds.abiquo.features.EnterpriseApi;
import org.jclouds.abiquo.features.EventApi;
import org.jclouds.abiquo.features.InfrastructureApi;
import org.jclouds.abiquo.features.TaskApi;
import org.jclouds.abiquo.features.VirtualMachineTemplateApi;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * Provides synchronous access to Abiquo.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
public interface AbiquoApi extends Closeable {
   /**
    * The version of the supported Abiquo API.
    */
   public static final String API_VERSION = SingleResourceTransportDto.API_VERSION;

   /**
    * The supported build version of the Abiquo Api.
    */
   public static final String BUILD_VERSION = "7bbfe95-158721b";

   /**
    * Provides synchronous access to Admin features.
    */
   @Delegate
   AdminApi getAdminApi();

   /**
    * Provides synchronous access to Infrastructure features.
    */
   @Delegate
   InfrastructureApi getInfrastructureApi();

   /**
    * Provides synchronous access to Cloud features.
    */
   @Delegate
   CloudApi getCloudApi();

   /**
    * Provides synchronous access to Apps library features.
    */
   @Delegate
   VirtualMachineTemplateApi getVirtualMachineTemplateApi();

   /**
    * Provides synchronous access to Enterprise features.
    */
   @Delegate
   EnterpriseApi getEnterpriseApi();

   /**
    * Provides synchronous access to configuration features.
    */
   @Delegate
   ConfigApi getConfigApi();

   /**
    * Provides synchronous access to task asynchronous features.
    */
   @Delegate
   TaskApi getTaskApi();

   /**
    * Provides synchronous access to Event features.
    */
   @Delegate
   EventApi getEventApi();

   /**
    * Perform a GET request to the given link.
    * 
    * @param link
    *           The link to get.
    * @return The response.
    */
   @Named("link:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   HttpResponse get(@BinderParam(BindLinkToPathAndAcceptHeader.class) final RESTLink link);

}
