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
package org.jclouds.openstack.marconi.v1.functions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.marconi.v1.domain.Claim;
import org.jclouds.openstack.marconi.v1.domain.Message;

import javax.inject.Inject;
import java.beans.ConstructorProperties;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.openstack.marconi.v1.functions.ParseMessagesToStream.MessageWithHref;
import static org.jclouds.openstack.marconi.v1.functions.ParseMessagesToStream.TO_ID_FROM_HREF;
import static org.jclouds.openstack.marconi.v1.functions.ParseMessagesToStream.TO_MESSAGE;

public class ParseClaim implements Function<HttpResponse, Claim> {

   protected static final Function<ClaimWithHref, Claim> TO_CLAIM = new Function<ClaimWithHref, Claim>() {
      @Override
      public Claim apply(ClaimWithHref claimWithHref) {
         List<Message> messages = ImmutableList.copyOf(transform(claimWithHref.messagesWithHref, TO_MESSAGE));
         String claimId = TO_ID_FROM_HREF.apply(claimWithHref.getId());

         return claimWithHref.toBuilder()
               .id(claimId)
               .messages(messages)
               .build();
      }
   };
   private final ParseJson<ClaimWithHref> json;

   @Inject
   ParseClaim(ParseJson<ClaimWithHref> json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public Claim apply(HttpResponse response) {
      ClaimWithHref claimWithHref = json.apply(response);
      Claim claim = TO_CLAIM.apply(claimWithHref);

      return claim;
   }

   private static class ClaimWithHref extends Claim {
      private final List<MessageWithHref> messagesWithHref;

      @ConstructorProperties({"href", "ttl", "age", "messages"})
      protected ClaimWithHref(String href, int ttl, int age, List<MessageWithHref> messagesWithHref) {
         super(href, ttl, age, null);
         this.messagesWithHref = messagesWithHref;
      }
   }
}
