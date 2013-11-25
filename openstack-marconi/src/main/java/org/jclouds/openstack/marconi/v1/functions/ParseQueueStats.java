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
import com.google.inject.Inject;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.marconi.v1.domain.Aged;
import org.jclouds.openstack.marconi.v1.domain.MessagesStats;
import org.jclouds.openstack.marconi.v1.domain.QueueStats;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.marconi.v1.functions.ParseMessagesToStream.TO_MESSAGE_ID;

/**
 * This parses the stats of a queue.
 * 
 * @author Everett
 */
public class ParseQueueStats implements Function<HttpResponse, QueueStats> {

   private final ParseJson<QueueStats> json;

   @Inject
   ParseQueueStats(ParseJson<QueueStats> json) {
      this.json = checkNotNull(json, "json");
   }

   public QueueStats apply(HttpResponse from) {
      QueueStats rawQueueStats = json.apply(from);

      if (rawQueueStats.getMessagesStats().getTotal() == 0) {
         return rawQueueStats;
      }
      else {
         // change the hrefs to ids
         Aged oldestWithHref = rawQueueStats.getMessagesStats().getOldest().get();
         Aged oldestWithId = oldestWithHref.toBuilder()
               .id(TO_MESSAGE_ID.apply(oldestWithHref.getId()))
               .build();
         Aged newestWithHref = rawQueueStats.getMessagesStats().getNewest().get();
         Aged newestWithId = newestWithHref.toBuilder()
               .id(TO_MESSAGE_ID.apply(newestWithHref.getId()))
               .build();

         MessagesStats messagesStatsWithIds = rawQueueStats.getMessagesStats().toBuilder()
               .oldest(oldestWithId)
               .newest(newestWithId)
               .build();

         QueueStats queueStatsWithIds = rawQueueStats.toBuilder().messageStats(messagesStatsWithIds).build();

         return queueStatsWithIds;
      }
   }
}
