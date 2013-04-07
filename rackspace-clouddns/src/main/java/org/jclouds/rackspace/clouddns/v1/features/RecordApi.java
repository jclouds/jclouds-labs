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
package org.jclouds.rackspace.clouddns.v1.features;

import java.util.Map;
import java.util.Set;

import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.cloudidentity.v2_0.domain.PaginatedCollection;
import org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.RequestFilters;

/**
 * @see RecordAsyncApi
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface RecordApi {

   /**
    * Create Records for a Domain or Subdomain.
    * </p>
    * See <a href="http://docs.rackspace.com/cdns/api/v1.0/cdns-devguide/content/supported_record_types.html">
    * Supported Record Types</a>
    */
   Job<Set<RecordDetail>> create(Iterable<Record> createRecords);

   /**
    * This call lists all records configured for the specified domain.
    */
   PagedIterable<RecordDetail> list();

   /**
    * RecordDetails filtered by type.
    */
   PagedIterable<RecordDetail> listByType(String typeFilter);

   /**
    * RecordDetails filtered by type and data.
    */
   PagedIterable<RecordDetail> listByTypeAndData(String typeFilter, String dataFilter);
   
   /**
    * RecordDetails filtered by name and type.
    */
   PagedIterable<RecordDetail> listByNameAndType(String nameFilter, String typeFilter);

   /**
    * Use PaginationOptions to manually control the list of RecordDetail pages returned.
    */
   PaginatedCollection<RecordDetail> list(PaginationOptions options);
   
   /**
    * RecordDetails filtered by name and type and data.
    */
   RecordDetail getByNameAndTypeAndData(String nameFilter, String typeFilter, String dataFilter);

   /**
    * Get the details for the specified record in the specified domain.
    */
   RecordDetail get(String recordId);

   /**
    * Update the configuration of the specified record in the specified domain.
    */
   Job<Void> update(String recordId, Record record);

   /**
    * Update the configuration of the specified records in the specified domain.
    */
   Job<Void> update(Map<String, Record> idsToRecords);

   /**
    * Delete the specified record in the specified domain.
    */
   Job<Void> delete(String recordId);

   /**
    * Delete the specified records in the specified domain.
    */
   Job<Void> delete(Iterable<String> recordIds);
}
