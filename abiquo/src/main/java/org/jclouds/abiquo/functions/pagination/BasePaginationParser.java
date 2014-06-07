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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;

import com.abiquo.model.transport.WrapperDto;
import com.google.common.base.Function;

/**
 * Base class for all pagination response parsers.
 * <p>
 * Parses the response with the given parser, and wraps the results in a
 * {@link PaginatedCollection} so it can be properly iterated.
 * 
 * 
 * @see PaginatedCollection
 * @see PagedIterable
 */
public abstract class BasePaginationParser<T, W extends WrapperDto<T>> implements
      Function<HttpResponse, PaginatedCollection<T, W>> {
   protected final AbiquoApi api;
   protected final ParseXMLWithJAXB<W> parser;

   public BasePaginationParser(AbiquoApi api, ParseXMLWithJAXB<W> parser) {
      this.api = checkNotNull(api, "api must not be null");
      this.parser = checkNotNull(parser, "parser must not be null");
   }

   @Override
   public PaginatedCollection<T, W> apply(HttpResponse input) {
      return new PaginatedCollection<T, W>(api, parser.apply(input), parser);
   }

}
