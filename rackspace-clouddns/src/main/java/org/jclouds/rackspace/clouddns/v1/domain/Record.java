/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.rackspace.clouddns.v1.domain;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * @author Everett Toews
 */
public class Record {
   private final String id;
   private final String name;
   private final String type;
   private final int ttl;
   private final String data;
   private final Optional<Integer> priority;
   private final Optional<String> comment;
   private final Date created;
   private final Date updated;

   @ConstructorProperties({ "id", "name", "type", "ttl", "data", "priority", "comment", "created", "updated" })
   protected Record(String id, String name, String type, int ttl, String data, @Nullable Integer priority,
         @Nullable String comment, Date created, Date updated) {
      this.id = id;
      this.name = name;
      this.type = type;
      this.ttl = ttl;
      this.data = data;
      this.priority = Optional.fromNullable(priority);
      this.comment = Optional.fromNullable(comment);
      this.created = created;
      this.updated = updated;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }

   public int getTTL() {
      return ttl;
   }

   public String getData() {
      return data;
   }

   public Optional<Integer> getPriority() {
      return priority;
   }

   public Optional<String> getComment() {
      return comment;
   }

   public Date getCreated() {
      return created;
   }

   public Date getUpdated() {
      return updated;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Record that = Record.class.cast(obj);

      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("name", name).add("type", type)
            .add("ttl", ttl).add("data", data).add("priority", priority.orNull()).add("comment", comment.orNull())
            .add("created", created).add("updated", updated);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
