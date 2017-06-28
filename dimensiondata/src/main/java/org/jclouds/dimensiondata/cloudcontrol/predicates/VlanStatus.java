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
package org.jclouds.dimensiondata.cloudcontrol.predicates;

import com.google.common.base.Predicate;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.domain.Vlan;
import org.jclouds.dimensiondata.cloudcontrol.features.NetworkApi;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

public class VlanStatus implements Predicate<String> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final State state;
   private final NetworkApi networkApi;

   public VlanStatus(NetworkApi networkApi, State state) {
      this.networkApi = networkApi;
      this.state = state;
   }

   @Override
   public boolean apply(String vlanId) {
      checkNotNull(vlanId, "vlanId");
      logger.trace("looking for state on vlan %s", vlanId);
      final Vlan vlan = networkApi.getVlan(vlanId);
      final boolean isDeleted = (vlan == null) && (state == State.DELETED);
      return isDeleted || ((vlan != null) && vlan.state() == state);

   }
}

