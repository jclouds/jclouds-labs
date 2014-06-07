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
package org.jclouds.abiquo.functions.pagination;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getFirst;
import static org.jclouds.http.utils.Queries.encodeQueryLine;
import static org.jclouds.http.utils.Queries.queryParser;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Multimap;

/**
 * Parses a paginated user list.
 */
@Singleton
public class ParseUsers extends BasePaginationParser<UserDto, UsersDto> {
   @Inject
   public ParseUsers(AbiquoApi api, ParseXMLWithJAXB<UsersDto> parser) {
      super(api, parser);
   }

   // Return a custom class to bypass
   // http://jira.abiquo.com/browse/ABICLOUDPREMIUM-5927
   // Remove once the fix has been deployed to production
   @Override
   public PaginatedCollection<UserDto, UsersDto> apply(HttpResponse input) {
      return new UserPaginatedCollection(api, parser.apply(input), parser);
   }

   @Singleton
   public static class ToPagedIterable extends PaginatedCollection.ToPagedIterable<UserDto, UsersDto> {
      @Inject
      public ToPagedIterable(AbiquoApi api, ParseXMLWithJAXB<UsersDto> parser) {
         super(api, parser);
      }

      // Overwrite to return a custom class and bypass
      // http://jira.abiquo.com/browse/ABICLOUDPREMIUM-5927
      // Remove once the fix has been deployed to production
      @Override
      protected Function<Object, IterableWithMarker<UserDto>> nextPage(PaginatedCollection<UserDto, UsersDto> input) {
         return new Function<Object, IterableWithMarker<UserDto>>() {
            @Override
            public IterableWithMarker<UserDto> apply(Object marker) {
               checkArgument(marker instanceof RESTLink, "Marker must be a RESTLink");
               RESTLink next = RESTLink.class.cast(marker);

               // The Abiquo API does not provide the media types in the
               // pagination links, but it will be the same type than the
               // current page, so just set it.
               next.setType(UsersDto.BASE_MEDIA_TYPE);

               UsersDto nextPage = parser.apply(api.get(next));
               return new UserPaginatedCollection(api, nextPage, parser);
            }
         };
      }
   }

   /**
    * This class is used to bypass
    * http://jira.abiquo.com/browse/ABICLOUDPREMIUM-5927
    * <p>
    * The issue has already been fixed but still not been deployed in
    * production. Once that is done, this class should be removed.
    * 
    */
   private static class UserPaginatedCollection extends PaginatedCollection<UserDto, UsersDto> {
      public UserPaginatedCollection(AbiquoApi api, UsersDto delegate, ParseXMLWithJAXB<UsersDto> parser) {
         super(api, delegate, parser);
      }

      @Override
      public Optional<Object> nextMarker() {
         Optional<Object> next = super.nextMarker();
         // Pagination links are not consistent and a "next" link can be
         // returned even if there are no more pages. Overwrite the
         // default behavior to handle this, and to adapt the query
         // parameters returned in the pagination links to what the api
         // expects in the GET requests
         if (next.isPresent()) {
            checkArgument(next.get() instanceof RESTLink, "Marker must be a RESTLink");
            RESTLink link = RESTLink.class.cast(next.get());

            // Get the conflicting query parameters and remove them from the
            // query parameter map
            Multimap<String, String> params = queryParser().apply(URI.create(link.getHref()).getRawQuery());
            String limit = getFirst(params.removeAll("limit"), null);
            String startwith = getFirst(params.removeAll("startwith"), null);

            // Next page links always have both query parameters. Otherwise
            // assume there is no next page
            if (limit != null && startwith != null) {
               int resultsPerPage = Integer.parseInt(limit);
               int totalResults = delegate.getTotalSize();
               int pageNumber = Integer.parseInt(startwith) - resultsPerPage;

               // Check if there is really a next page, and then return the
               // marker with the appropriate values
               if (totalResults / (double) resultsPerPage > pageNumber + 1) {
                  params.put("numResults", limit);
                  params.put("page", String.valueOf(pageNumber + 1));

                  // Build the new URI stripping the existing query
                  // parameters and using the new ones
                  String uri = link.getHref().substring(0, link.getHref().indexOf('?') + 1);
                  uri += encodeQueryLine(params);

                  return Optional.<Object> of(new RESTLink(link.getRel(), uri));
               }
            }
         }

         return Optional.absent();
      }

      @Override
      public PagedIterable<UserDto> toPagedIterable() {
         return new ParseUsers.ToPagedIterable(api, parser).apply(this);
      }
   }

}
