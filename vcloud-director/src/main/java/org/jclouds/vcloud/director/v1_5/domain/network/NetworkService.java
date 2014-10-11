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
package org.jclouds.vcloud.director.v1_5.domain.network;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@XmlSeeAlso({
   DhcpService.class,
   IpsecVpnService.class,
   FirewallService.class,
   DhcpService.class,
   StaticRoutingService.class,
   NatService.class
})
public abstract class NetworkService<T extends NetworkService<T>> {
   public abstract Builder<T> toBuilder();

   public abstract static class Builder<T extends NetworkService<T>> {
      protected boolean isEnabled;

      /**
       * @see NetworkService#isEnabled()
       */
      public Builder<T> enabled(boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }

      public abstract NetworkService<T> build();

      public Builder<T> fromNetworkServiceType(NetworkService<T> in) {
         return enabled(in.isEnabled());
      }
   }

   protected NetworkService(boolean enabled) {
      isEnabled = enabled;
   }

   protected NetworkService() {
      // for JAXB
   }

   @XmlElement(name = "IsEnabled")
   private boolean isEnabled;

   /**
    * @return Enable or disable the service using this flag
    */
   public boolean isEnabled() {
      return isEnabled;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NetworkService<?> that = NetworkService.class.cast(o);
      return equal(isEnabled, that.isEnabled);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(isEnabled);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected MoreObjects.ToStringHelper string() {
      return MoreObjects.toStringHelper("").add("isEnabled", isEnabled);
   }
}
