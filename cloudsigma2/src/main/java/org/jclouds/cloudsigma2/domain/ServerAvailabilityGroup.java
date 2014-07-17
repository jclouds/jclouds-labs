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
package org.jclouds.cloudsigma2.domain;

import java.util.Iterator;
import java.util.List;

public class ServerAvailabilityGroup {

   private final List<String> uuids;

   public ServerAvailabilityGroup(List<String> uuids) {
      this.uuids = uuids;
   }

   public List<String> getUuids() {
      return uuids;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ServerAvailabilityGroup)) return false;

      ServerAvailabilityGroup that = (ServerAvailabilityGroup) o;

      if (uuids != null ? !uuids.equals(that.uuids) : that.uuids != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return uuids != null ? uuids.hashCode() : 0;
   }

   @Override
   public String toString() {
      String returnString = "";

      Iterator<String> iterator = uuids.iterator();

      while (iterator.hasNext()) {
         returnString += iterator.next();

         if (iterator.hasNext()) {
            returnString += ",";
         }
      }

      return returnString;
   }
}
