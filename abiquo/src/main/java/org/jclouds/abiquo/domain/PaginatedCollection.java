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
package org.jclouds.abiquo.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.collect.PagedIterables.advance;
import static org.jclouds.collect.PagedIterables.onlyPage;

import java.util.Iterator;
import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.functions.ParseXMLWithJAXB;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.WrapperDto;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * This class represents a collection that is paginated.
 * <p>
 * Contains a single page of the collection, and all the information needed to
 * fetch the next page on demand.
 * 
 * 
 * @see PagedIterable
 */
public class PaginatedCollection<T, W extends WrapperDto<T>> extends IterableWithMarker<T> {
   protected final AbiquoApi api;
   protected final W delegate;
   protected final ParseXMLWithJAXB<W> parser;

   public PaginatedCollection(AbiquoApi api, W delegate, ParseXMLWithJAXB<W> parser) {
      this.api = checkNotNull(api, "api must not be null");
      this.delegate = checkNotNull(delegate, "delegate must not be null");
      this.parser = checkNotNull(parser, "parser must not be null");
   }

   @Override
   public Iterator<T> iterator() {
      return delegate.getCollection().iterator();
   }

   @Override
   public Optional<Object> nextMarker() {
      return Optional.<Object> fromNullable(delegate.searchLink("next"));
   }

   /**
    * Transforms this {@link PaginatedCollection} into a {@link PagedIterable}
    * so next the pages can be easily fetched.
    * 
    * @return A PagedIterable that is capable of fetching more pages.
    */
   public PagedIterable<T> toPagedIterable() {
      return new ToPagedIterable<T, W>(api, parser).apply(this);
   }

   /**
    * Returns a function that transforms the PaginatedCollection into a
    * {@link PagedIterable}.
    * <p>
    * The PagedIterable will fetch the next pages based on the <code>next</code>
    * link of the current object.
    * <p>
    * Subclasses may overwrite this one, to provide a concrete type for the
    * parser parameter, so this function can be injected in the different api
    * methods and be used as a transformer for the returned collection.
    * 
    */
   public static class ToPagedIterable<T, W extends WrapperDto<T>> implements
         Function<PaginatedCollection<T, W>, PagedIterable<T>> {
      protected final AbiquoApi api;
      protected final ParseXMLWithJAXB<W> parser;

      public ToPagedIterable(AbiquoApi api, ParseXMLWithJAXB<W> parser) {
         this.api = checkNotNull(api, "api must not be null");
         this.parser = checkNotNull(parser, "parser must not be null");
      }

      @Override
      public PagedIterable<T> apply(final PaginatedCollection<T, W> input) {
         return input.nextMarker().isPresent() ? advance(input, nextPage(input)) : onlyPage(input);
      }

      protected Function<Object, IterableWithMarker<T>> nextPage(final PaginatedCollection<T, W> input) {
         return new Function<Object, IterableWithMarker<T>>() {
            @Override
            public IterableWithMarker<T> apply(Object marker) {
               checkArgument(marker instanceof RESTLink, "Marker must be a RESTLink");
               RESTLink next = RESTLink.class.cast(marker);

               // The Abiquo API does not provide the media types in the
               // pagination links, but it will be the same type than the
               // current page, so just set it.
               next.setType(input.delegate.getMediaType());

               W nextPage = parser.apply(api.get(next));
               return new PaginatedCollection<T, W>(api, nextPage, parser);
            }
         };
      }

   }

   // Delegate methods

   public Integer getTotalSize() {
      return delegate.getTotalSize();
   }

   public List<RESTLink> getLinks() {
      return delegate.getLinks();
   }

   public RESTLink searchLink(String rel) {
      return delegate.searchLink(rel);
   }

   public List<RESTLink> searchLinks(String rel) {
      return delegate.searchLinks(rel);
   }

   public RESTLink searchLink(String rel, String title) {
      return delegate.searchLink(rel, title);
   }

   public RESTLink searchLinkByHref(String href) {
      return delegate.searchLinkByHref(href);
   }

   public Integer getIdFromLink(String rel) {
      return delegate.getIdFromLink(rel);
   }

   public String getMediaType() {
      return delegate.getMediaType();
   }

}
