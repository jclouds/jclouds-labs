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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.http.functions.ParseXMLWithJAXB;

import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpsDto;

/**
 * Parses a paginated private ip list.
 */
@Singleton
public class ParsePrivateIps extends BasePaginationParser<PrivateIpDto, PrivateIpsDto> {
   @Inject
   public ParsePrivateIps(AbiquoApi api, ParseXMLWithJAXB<PrivateIpsDto> parser) {
      super(api, parser);
   }

   @Singleton
   public static class ToPagedIterable extends PaginatedCollection.ToPagedIterable<PrivateIpDto, PrivateIpsDto> {
      @Inject
      public ToPagedIterable(AbiquoApi api, ParseXMLWithJAXB<PrivateIpsDto> parser) {
         super(api, parser);
      }
   }

}
