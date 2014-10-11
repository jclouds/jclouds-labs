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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IpsecVpnLocalPeer")
public class IpsecVpnLocalPeer
    extends IpsecVpnManagedPeer<IpsecVpnLocalPeer>

{
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpsecVpnLocalPeer(this);
   }

   public static class Builder extends IpsecVpnManagedPeer.Builder<IpsecVpnLocalPeer> {
      public IpsecVpnLocalPeer build() {
         return new IpsecVpnLocalPeer(id, name);
      }

      @Override
      public Builder fromIpsecVpnManagedPeerType(IpsecVpnManagedPeer<IpsecVpnLocalPeer> in) {
          return Builder.class.cast(super.fromIpsecVpnManagedPeerType(in));
      }

      public Builder fromIpsecVpnLocalPeer(IpsecVpnLocalPeer in) {
         return fromIpsecVpnManagedPeerType(in);
      }

      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

   }

   private IpsecVpnLocalPeer(String id, String name) {
      super(id, name);
   }

   private IpsecVpnLocalPeer() {
      // For JAXB
   }
}
