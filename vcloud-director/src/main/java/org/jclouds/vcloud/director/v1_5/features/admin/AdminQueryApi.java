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
package org.jclouds.vcloud.director.v1_5.features.admin;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.director.v1_5.domain.RoleReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.features.QueryApi;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface AdminQueryApi extends QueryApi {

   /**
    * Retrieves a list of {@link Group}s for organization the org admin belongs to by using REST API general
    * QueryHandler
    *
    * <pre>
    * GET /admin/groups/query
    * </pre>
    */
   @GET
   @Path("/admin/groups/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords groupsQueryAll();

   /**
    * Retrieves a list of {@link Org}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/orgs/query
    * </pre>
    */
   @GET
   @Path("/admin/orgs/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords orgsQueryAll();

   /**
    * Retrieves a list of {@link Right}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/rights/query
    * </pre>
    */
   @GET
   @Path("/admin/rights/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords rightsQueryAll();

   /**
    * Retrieves a list of {@link Role}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/roles/query
    * </pre>
    */
   @GET
   @Path("/admin/roles/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords rolesQueryAll();

   /**
    * Retrieves a list of {@link RoleReference}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/roles/query?format=references
    * </pre>
    */
   @GET
   @Path("/admin/roles/query")
   @Consumes
   @QueryParams(keys = { "format" }, values = { "references" })
   @JAXBResponseParser
   RoleReferences roleReferencesQueryAll();

   /**
    * Retrieves a list of {@link User}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/strandedUsers/query
    * </pre>
    */
   @GET
   @Path("/admin/strandedUsers/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords strandedUsersQueryAll();

   /**
    * Retrieves a list of {@link User}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/users/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   @GET
   @Path("/admin/users/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords usersQueryAll();

   /**
    * Retrieves a list of {@link Vdc}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/vdcs/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   @GET
   @Path("/admin/vdcs/query")
   @Consumes
   @JAXBResponseParser
   QueryResultRecords vdcsQueryAll();
}
