/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rackspace.clouddns.v1.features;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.clouddns.v1.domain.CreateDomain;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.DomainChange;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.Subdomain;
import org.jclouds.rackspace.clouddns.v1.domain.UpdateDomain;
import org.jclouds.rackspace.cloudidentity.v2_0.domain.PaginatedCollection;
import org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.RequestFilters;

/**
 * @see DomainAsyncApi
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface DomainApi {

   /**
    * Provisions one or more new DNS domains based on the configuration defined in CreateDomain. If the domain
    * creation cannot be fulfilled due to insufficient or invalid data, Job with an ERROR status will
    * be returned with information regarding the nature of the failure in the body of the Job. Failures in the
    * validation process are non-recoverable and require the caller to correct the cause of the failure.
    * This is an atomic operation: if there is a failure in creation of even a single record, the entire process
    * will fail.
    * </p>
    * When a domain is created, and no Time To Live (TTL) is specified, the SOA minTTL (3600 seconds) is used as the
    * default. When a record is added without a specified TTL, it will receive the domain TTL by default. When the
    * domain and/or record TTL is supplied by the user, either via a create or update call, the TTL values must be 300
    * seconds or more.
    */
   Job<Set<Domain>> create(Iterable<CreateDomain> createDomains);

   /**
    * The resulting list is flat, and does not break the domains down hierarchically by subdomain. All representative
    * domains are included in the list, even if a domain is conceptually a subdomain of another domain in the list.
    * Records are not included.
    */
   PagedIterable<Domain> list();

   /**
    * Filtering the search to limit the results returned can be performed by using the nameFilter parameter. For
    * example, "hoola.com" matches hoola.com and similar names such as main.hoola.com and sub.hoola.com.
    * </p>
    * Filter criteria may consist of:
    * <ul>
    * <li>Any letter (A-Za-z)</li>
    * <li>Numbers (0-9)</li>
    * <li>Hyphen ("-")</li>
    * <li>1 to 63 characters</li>
    * </ul>
    * Filter criteria should not include any of the following characters: ' + , | ! " £ $ % & / ( ) = ? ^ * ç ° § ; : _
    * > ] [ @ à, é, ò
    */
   PagedIterable<Domain> listWithFilterByNamesMatching(String nameFilter);

   PaginatedCollection<Domain> list(PaginationOptions options);

   /**
    * The resulting list is flat, and does not break the domains down hierarchically by subdomain.
    */
   PagedIterable<Subdomain> listSubdomains(int domainId);

   PaginatedCollection<Subdomain> listSubdomains(int domainId, PaginationOptions options);

   /**
    * Shows all changes to the specified domain since the specified date/time.
    */
   DomainChange listChanges(int id, Date since);

   /**
    * Get all information for a Domain, including records and subdomains.
    */
   Domain get(int id);

   /**
    * This call modifies the domain attributes only. Records cannot be added, modified, or removed.
    * 
    * @see RecordApi
    */
   Job<Void> update(int id, UpdateDomain updateDomain);

   /**
    * This call modifies the domain's TTL only.
    */
   Job<Void> updateTTL(Iterable<Integer> ids, int ttl);

   /**
    * This call modifies the domain's email only.
    */
   Job<Void> updateEmail(Iterable<Integer> ids, String email);

   /**
    * This call removes one or more specified domains from the account; when a domain is deleted, its immediate resource
    * records are also deleted from the account. By default, if a deleted domain had subdomains, each subdomain becomes
    * a root domain and is not deleted; this can be overridden by the optional deleteSubdomains parameter. Utilizing the
    * optional deleteSubdomains parameter on domains without subdomains does not result in a failure. When a domain is
    * deleted, any and all domain data is immediately purged and is not recoverable via the API.
    */
   Job<Void> delete(Iterable<Integer> ids, boolean deleteSubdomains);

   /**
    * This call provides the BIND (Berkeley Internet Name Domain) 9 formatted contents of the requested domain. This
    * call is for a single domain only, and as such, does not traverse up or down the domain hierarchy for details (that
    * is, no subdomain information is provided).
    */
   Job<List<String>> exportFormat(int id, Domain.Format format);

   /**
    * This call provisions a new DNS domain under the account specified by the BIND 9 formatted file configuration
    * contents. If the corresponding request cannot be fulfilled due to insufficient or invalid data, an exception will
    * be thrown with information regarding the nature of the failure in the body of the response. Failures in the
    * validation process are non-recoverable and require the caller to correct the cause of the failure and call again.
    */
   Job<Domain> importFormat(List<String> contents, Domain.Format format);
}
