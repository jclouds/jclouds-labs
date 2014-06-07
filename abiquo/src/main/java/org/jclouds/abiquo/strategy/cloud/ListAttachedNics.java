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
package org.jclouds.abiquo.strategy.cloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.network.ExternalIp;
import org.jclouds.abiquo.domain.network.Ip;
import org.jclouds.abiquo.domain.network.PrivateIp;
import org.jclouds.abiquo.domain.network.PublicIp;
import org.jclouds.abiquo.domain.network.UnmanagedIp;
import org.jclouds.abiquo.domain.util.LinkUtils;
import org.jclouds.abiquo.strategy.ListEntities;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.ApiContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.network.ExternalIpDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpDto;
import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 * List all NICs attached to a given virtual machine.
 */
@Singleton
public class ListAttachedNics implements ListEntities<Ip<?, ?>, VirtualMachine> {
   protected final ApiContext<AbiquoApi> context;

   @Inject
   public ListAttachedNics(final ApiContext<AbiquoApi> context) {
      this.context = checkNotNull(context, "context");
   }

   @Override
   public Iterable<Ip<?, ?>> execute(final VirtualMachine parent) {
      parent.refresh();
      Iterable<RESTLink> nicLinks = LinkUtils.filterNicLinks(parent.unwrap().getLinks());
      return listIps(nicLinks);
   }

   private Iterable<Ip<?, ?>> listIps(final Iterable<RESTLink> nicLinks) {
      return transform(nicLinks, new Function<RESTLink, Ip<?, ?>>() {
         @Override
         public Ip<?, ?> apply(final RESTLink input) {
            HttpResponse response = context.getApi().get(input);

            if (input.getType().equals(PrivateIpDto.BASE_MEDIA_TYPE)) {
               ParseXMLWithJAXB<PrivateIpDto> parser = new ParseXMLWithJAXB<PrivateIpDto>(context.utils().xml(),
                     TypeLiteral.get(PrivateIpDto.class));

               return wrap(context, PrivateIp.class, parser.apply(response));
            } else if (input.getType().equals(PublicIpDto.BASE_MEDIA_TYPE)) {
               ParseXMLWithJAXB<PublicIpDto> parser = new ParseXMLWithJAXB<PublicIpDto>(context.utils().xml(),
                     TypeLiteral.get(PublicIpDto.class));

               return wrap(context, PublicIp.class, parser.apply(response));
            } else if (input.getType().equals(ExternalIpDto.BASE_MEDIA_TYPE)) {
               ParseXMLWithJAXB<ExternalIpDto> parser = new ParseXMLWithJAXB<ExternalIpDto>(context.utils().xml(),
                     TypeLiteral.get(ExternalIpDto.class));

               return wrap(context, ExternalIp.class, parser.apply(response));
            } else if (input.getType().equals(UnmanagedIpDto.BASE_MEDIA_TYPE)) {
               ParseXMLWithJAXB<UnmanagedIpDto> parser = new ParseXMLWithJAXB<UnmanagedIpDto>(context.utils().xml(),
                     TypeLiteral.get(UnmanagedIpDto.class));

               return wrap(context, UnmanagedIp.class, parser.apply(response));
            } else {
               throw new IllegalArgumentException("Unsupported media type: " + input.getType());
            }
         }
      });
   }
}
