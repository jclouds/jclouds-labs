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
package org.jclouds.fujitsu.fgcp.compute;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.compute.options.RunScriptOptions.Builder.nameTask;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.compute.options.TemplateOptions.Builder.inboundPorts;
import static org.jclouds.compute.options.TemplateOptions.Builder.runAsRoot;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.compute.JettyStatements;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Module;

public abstract class FGCPBaseComputeServiceLiveTest extends
      BaseComputeServiceLiveTest {

   public FGCPBaseComputeServiceLiveTest() {
      // create operation must complete before start request is sent, taking a
      // few minutes
      nonBlockDurationSeconds = 300; // 5 min.
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
   
      String proxy = System.getenv("http_proxy");
      if (proxy != null) {
   
         String[] parts = proxy.split("http://|:|@");
   
         overrides.setProperty(Constants.PROPERTY_PROXY_HOST, parts[parts.length - 2]);
         overrides.setProperty(Constants.PROPERTY_PROXY_PORT, parts[parts.length - 1]);
   
         if (parts.length >= 4) {
            overrides.setProperty(Constants.PROPERTY_PROXY_USER, parts[parts.length - 4]);
            overrides.setProperty(Constants.PROPERTY_PROXY_PASSWORD, parts[parts.length - 3]);
         }

         overrides.setProperty(Constants.PROPERTY_PROXY_FOR_SOCKETS, "false");
      }

      // enables peer verification using the CAs bundled with the JRE (or
      // value of javax.net.ssl.trustStore if set)
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "false");

      return overrides;
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   // fatal handshake is not returned as AuthorizationException; should it?
   @Test(enabled = false, expectedExceptions = AuthorizationException.class)
   public void testCorrectAuthException() throws Exception {
   }

   // fgcp does not support metadata
   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assert node.getUserMetadata().isEmpty() : String.format(
            "node userMetadata not empty: %s %s", node,
            node.getUserMetadata());
    }

   // node name can't be retrieved through the API and is therefore null
   @Override
   protected void checkResponseEqualsHostname(ExecResponse execResponse,
         NodeMetadata node) {
      assert node.getHostname() == null : node + " with hostname: "
            + node.getHostname();
   }

   // tags are not (yet) supported for fgcp
   @Override
   protected void checkTagsInNodeEquals(NodeMetadata node,
         ImmutableSet<String> tags) {
      assert node.getTags().isEmpty() : String.format(
            "node tags found %s (%s) in node %s", node.getTags(), tags, node);
   }

   // using user/pwd based auth to ssh to nodes instead
   @Override
   protected void setupKeyPairForTest() {
   }

   // these tests require network access to the VMs they create:
   // before running it, start an SSL/VPN connection to the last updated vsys'
   // DMZ.
   // May also need to configure SNAT and FW rules to allow the VM to
   // communicate out (53/tcp-udp for DNS, 80/tcp for yum).
/*   @Override
   @Test(enabled = true, dependsOnMethods = { "testCompareSizes" })
   public void testAScriptExecutionAfterBootWithBasicTemplate()
         throws Exception {
      super.testAScriptExecutionAfterBootWithBasicTemplate();
   }
   @Override
   @Test(enabled = true)
   public void testCreateAndRunAService() throws Exception {
      super.testCreateAndRunAService();
   }
   @Override
   @Test(enabled = true, dependsOnMethods = "testCompareSizes")
   public void testConcurrentUseOfComputeServiceToCreateNodes() throws Exception {
      super.testConcurrentUseOfComputeServiceToCreateNodes();
   }
*/

}
