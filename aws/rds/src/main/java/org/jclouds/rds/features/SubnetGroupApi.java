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
package org.jclouds.rds.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rds.domain.SubnetGroup;
import org.jclouds.rds.functions.SubnetGroupsToPagedIterable;
import org.jclouds.rds.options.ListSubnetGroupsOptions;
import org.jclouds.rds.xml.DescribeDBSubnetGroupsResultHandler;
import org.jclouds.rds.xml.SubnetGroupHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Amazon RDS via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference"
 *      >doc</a>
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface SubnetGroupApi {
 
   /**
    * Retrieves information about the specified {@link SubnetGroup}.
    * 
    * @param name
    *           Name of the subnet group to get information about.
    * @return null if not found
    */
   @Nullable
   @Named("DescribeDBSubnetGroups")
   @POST
   @Path("/")
   @XMLResponseParser(SubnetGroupHandler.class)
   @FormParams(keys = "Action", values = "DescribeDBSubnetGroups")
   @Fallback(NullOnNotFoundOr404.class)
   SubnetGroup get(@FormParam("DBSubnetGroupName") String name);

   /**
    * Returns a list of {@link SubnetGroup}s.
    * 
    * @return the response object
    */
   @Named("DescribeDBSubnetGroups")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeDBSubnetGroupsResultHandler.class)
   @Transform(SubnetGroupsToPagedIterable.class)
   @FormParams(keys = "Action", values = "DescribeDBSubnetGroups")
   PagedIterable<SubnetGroup> list();

   /**
    * Returns a list of {@link SubnetGroup}s.
    * 
    * <br/>
    * You can paginate the results using the {@link ListSubnetGroupsOptions parameter}
    * 
    * @param options
    *           the options describing the subnet groups query
    * 
    * @return the response object
    */
   @Named("DescribeDBSubnetGroups")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeDBSubnetGroupsResultHandler.class)
   @FormParams(keys = "Action", values = "DescribeDBSubnetGroups")
   IterableWithMarker<SubnetGroup> list(ListSubnetGroupsOptions options);

   /**
    * Deletes a DB subnet group.
    * 
    * <h4>Note</h4>
    * 
    * The specified database subnet group must not be associated with any DB instances.
    * 
    * <h4>Note</h4>
    * 
    * By design, if the SubnetGroup does not exist or has already been deleted, DeleteSubnetGroup
    * still succeeds.
    * 
    * 
    * @param name
    *           The name of the database subnet group to delete.
    * 
    *           <h4>Note</h4>
    * 
    *           You cannot delete the default subnet group.
    */
   @Named("DeleteDBSubnetGroup")
   @POST
   @Path("/")
   @Fallback(VoidOnNotFoundOr404.class)
   @FormParams(keys = ACTION, values = "DeleteDBSubnetGroup")
   void delete(@FormParam("DBSubnetGroupName") String name);
}
