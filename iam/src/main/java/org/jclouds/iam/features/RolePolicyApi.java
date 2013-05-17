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
import org.jclouds.iam.domain.Policy;
import org.jclouds.iam.functions.PoliciesToPagedIterable.RolePoliciesToPagedIterable;
import org.jclouds.iam.xml.ListPoliciesResultHandler;
import org.jclouds.iam.xml.PolicyHandler;
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
 * @see <a href="http://docs.aws.amazon.com/IAM/latest/APIReference/API_ListRolePolicies.html" />
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface RolePolicyApi extends PolicyApi {
   /**
    * {@inheritDoc}
    */
   @Named("PutRolePolicy")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "PutRolePolicy")
   void create(@FormParam("PolicyName") String name, @FormParam("PolicyDocument") String document);

   /**
    * {@inheritDoc}
    */
   @Named("ListRolePolicies")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRolePolicies")
   @XMLResponseParser(ListPoliciesResultHandler.class)
   @Transform(RolePoliciesToPagedIterable.class)
   PagedIterable<String> list();

   /**
    * {@inheritDoc}
    */
   @Named("ListRolePolicies")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRolePolicies")
   @XMLResponseParser(ListPoliciesResultHandler.class)
   IterableWithMarker<String> listFirstPage();

   /**
    * {@inheritDoc}
    */
   @Named("ListRolePolicies")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRolePolicies")
   @XMLResponseParser(ListPoliciesResultHandler.class)
   IterableWithMarker<String> listAt(@FormParam("Marker") String marker);

   /**
    * {@inheritDoc}
    */
   @Named("GetRolePolicy")
   @POST
   @Path("/")
   @XMLResponseParser(PolicyHandler.class)
   @FormParams(keys = "Action", values = "GetRolePolicy")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Policy get(@FormParam("PolicyName") String name);

   /**
    * {@inheritDoc}
    */
   @Named("DeleteRolePolicy")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "DeleteRolePolicy")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@FormParam("PolicyName") String name);
}
