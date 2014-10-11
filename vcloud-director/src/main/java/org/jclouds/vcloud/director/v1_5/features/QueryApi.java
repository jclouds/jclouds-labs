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
package org.jclouds.vcloud.director.v1_5.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface QueryApi {

   // TODO Add a typed object for filter syntax, or at least a fluent builder

   /**
    * Retrieves a list of entities.
    *
    * If filter is provided it will be applied to the corresponding result set.
    * Format determines the elements representation - references or records.
    * Default format is references.
    *
    * <pre>
    * GET /query
    * </pre>
    */
   @GET
   @Path("/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords queryAll(@QueryParam("type") String type);

   @GET
   @Path("/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords query(@QueryParam("type") String type, @QueryParam("filter") String filter);

   @GET
   @Path("/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords query(@QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize,
         @QueryParam("format") String format, @QueryParam("type") String type, @QueryParam("filter") String filter);

   /**
    * Retrieves a list of {@link Catalog}s.
    *
    * @see #queryAll(String)
    */
   @GET
   @Path("/catalogs/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords catalogsQueryAll();

   /**
    * Retrieves a list of {@link CatalogReference}s.
    *
    * @see #queryAll(String)
    */
   @GET
   @Path("/catalogs/query")
   @Consumes
   @QueryParams(keys = { "format" }, values = { "references" })
   @JAXBResponseParser
   CatalogReferences catalogReferencesQueryAll();

   /**
    * Retrieves a list of {@link VAppTemplate}s.
    *
    * @see #queryAll(String)
    */
   @GET
   @Path("/vAppTemplates/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords vAppTemplatesQueryAll();

   @GET
   @Path("/vAppTemplates/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords vAppTemplatesQuery(@QueryParam("filter") String filter);

   /**
    * Retrieves a list of {@link VApp}s.
    *
    * @see #queryAll(String)
    */
   @GET
   @Path("/vApps/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords vAppsQueryAll();

   @GET
   @Path("/vApps/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords vAppsQuery(@QueryParam("filter") String filter);

   /**
    * Retrieves a list of {@link Vm}s.
    *
    * @see #queryAll(String)
    */
   @GET
   @Path("/vms/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords vmsQueryAll();

   @GET
   @Path("/vms/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords vmsQuery(@QueryParam("filter") String filter);

   /**
    * Retrieves a list of {@link Media}s.
    *
    * @see #queryAll(String)
    */
   @GET
   @Path("/mediaList/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords mediaListQueryAll();

   @GET
   @Path("/mediaList/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords mediaListQuery(@QueryParam("filter") String filter);
}
