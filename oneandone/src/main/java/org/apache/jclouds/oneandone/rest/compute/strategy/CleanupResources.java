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
package org.apache.jclouds.oneandone.rest.compute.strategy;

import java.util.List;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.jclouds.oneandone.rest.OneAndOneApi;
import org.apache.jclouds.oneandone.rest.domain.FirewallPolicy;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

@Singleton
public class CleanupResources {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final OneAndOneApi api;

   @Inject
   CleanupResources(OneAndOneApi oneandoneapi) {
      this.api = oneandoneapi;
   }

   public boolean cleanupNode(final String id) {
      Server server = api.serverApi().get(id);
      if (server == null) {
         return true;
      }

      logger.debug(">> destroying %s ...", server.id());
      deleteServer(server);
      deleteFirewallPolicy(server);

      return true;
   }

   private void deleteFirewallPolicy(Server server) {
      try {
         GenericQueryOptions options = new GenericQueryOptions().options(0, 0, null, server.name() + " firewall policy", null);
         List<FirewallPolicy> firewallRules = api.firewallPolicyApi().list(options);
         for (FirewallPolicy firewallRule : firewallRules) {
            api.firewallPolicyApi().delete(firewallRule.id());
         }
      } catch (Exception ex) {
         logger.debug("no firewall policies found for %s ...", server.id());
      }
   }

   private void deleteServer(Server server) {
      api.serverApi().delete(server.id());
   }

}
