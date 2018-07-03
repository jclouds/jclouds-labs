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
package org.jclouds.aliyun.ecs.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.beans.ConstructorProperties;

public class VPCRequest extends Request {

   private final String routeTableId;
   private final String vRouterId;
   private final String vpcId;

   @ConstructorProperties({ "RequestId", "RouteTableId", "VRouterId", "VpcId" })
   public VPCRequest(String requestId, String routeTableId, String vRouterId, String vpcId) {
      super(requestId);
      this.routeTableId = routeTableId;
      this.vRouterId = vRouterId;
      this.vpcId = vpcId;
   }

   public String getRouteTableId() {
      return routeTableId;
   }

   public String getvRouterId() {
      return vRouterId;
   }

   public String getVpcId() {
      return vpcId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      VPCRequest that = (VPCRequest) o;
      return Objects.equal(routeTableId, that.routeTableId) &&
              Objects.equal(vRouterId, that.vRouterId) &&
              Objects.equal(vpcId, that.vpcId);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), routeTableId, vRouterId, vpcId);
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
              .add("routeTableId", routeTableId)
              .add("vRouterId", vRouterId)
              .add("vpcId", vpcId)
              .toString();
   }
}
