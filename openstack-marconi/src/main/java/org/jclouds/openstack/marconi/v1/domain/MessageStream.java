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
package org.jclouds.openstack.marconi.v1.domain;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.jclouds.openstack.marconi.v1.options.StreamOptions;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;

import static org.jclouds.http.utils.Queries.queryParser;

public class MessageStream extends PaginatedCollection<Message> {
   protected MessageStream(Iterable<Message> resources, Iterable<Link> links) {
      super(resources, links);
   }

   /**
    * Only call this method if {@code nextMarker().isPresent()} returns true.
    *
    * @return The options necessary to get the next page of messages.
    */
   public StreamOptions nextStreamOptions() {
      return StreamOptions.class.cast(nextMarker().get());
   }

   @Override
   public Optional<Object> nextMarker() {
      Optional<Link> nextMarkerLink = Iterables.tryFind(getLinks(), IS_NEXT_LINK);
      return nextMarkerLink.transform(TO_LIST_OPTIONS);
   }

   private static final Predicate<Link> IS_NEXT_LINK = new Predicate<Link>() {
      @Override
      public boolean apply(Link link) {
         return Link.Relation.NEXT == link.getRelation();
      }
   };

   private static final Function<Link, Object> TO_LIST_OPTIONS = new Function<Link, Object>() {
      @Override
      public Object apply(Link link) {
         Multimap<String, String> queryParams = queryParser().apply(link.getHref().getRawQuery());
         StreamOptions paginationOptions = StreamOptions.Builder.queryParameters(queryParams);

         return paginationOptions;
      }
   };
}
