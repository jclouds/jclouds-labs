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

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.util.Date;

public class ServerRuntime {

   public static class Builder {
      private Date activeSince;
      private Iterable<NICStats> nicStats;


      public Builder activeSince(Date activeSince) {
         this.activeSince = activeSince;
         return this;
      }

      public Builder nicStats(Iterable<NICStats> nicStats) {
         this.nicStats = nicStats;
         return this;
      }

      public ServerRuntime build() {
         return new ServerRuntime(activeSince, nicStats);
      }
   }

   @Named("active_since")
   private final Date activeSince;
   @Named("nics")
   private final Iterable<NICStats> nicStats;

   @ConstructorProperties({
         "active_since", "nics"
   })
   public ServerRuntime(Date activeSince, Iterable<NICStats> nicStats) {
      this.activeSince = activeSince;
      this.nicStats = nicStats;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((nicStats == null) ? 0 : nicStats.hashCode());
      result = prime * result + ((activeSince == null) ? 0 : activeSince.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      ServerRuntime other = (ServerRuntime) obj;
      if (nicStats == null) {
         if (other.nicStats != null)
            return false;
      } else if (!nicStats.equals(other.nicStats))
         return false;
      if (activeSince == null) {
         if (other.activeSince != null)
            return false;
      } else if (!activeSince.equals(other.activeSince))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[activeSince=" + activeSince + ", nicStats=" + nicStats + "]";
   }
}
