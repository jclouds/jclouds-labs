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
package org.apache.jclouds.profitbricks.rest.domain.zonescoped;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class DataCenterAndId {

   public static DataCenterAndId fromSlashEncoded(String id) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(id, "id"));
      checkArgument(Iterables.size(parts) == 2, "id must be in format dataCenterId/id");
      return new DataCenterAndId(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static DataCenterAndId fromDataCenterAndId(String dataCenterId, String id) {
      return new DataCenterAndId(dataCenterId, id);
   }

   private static String slashEncodeDataCenterAndId(String dataCenterId, String id) {
      return checkNotNull(dataCenterId, "dataCenterId") + "/" + checkNotNull(id, "id");
   }

   public String slashEncode() {
      return slashEncodeDataCenterAndId(dataCenterId, id);
   }

   protected final String dataCenterId;
   protected final String id;

   protected DataCenterAndId(String dataCenterId, String id) {
      this.dataCenterId = checkNotNull(dataCenterId, "dataCenterId");
      this.id = checkNotNull(id, "id");
   }

   public String getDataCenter() {
      return dataCenterId;
   }

   public String getId() {
      return id;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(dataCenterId, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      DataCenterAndId other = (DataCenterAndId) obj;
      return Objects.equal(dataCenterId, other.dataCenterId) && Objects.equal(id, other.id);
   }

   protected MoreObjects.ToStringHelper string() {
      return MoreObjects.toStringHelper(this).add("dataCenterId", dataCenterId).add("id", id);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
