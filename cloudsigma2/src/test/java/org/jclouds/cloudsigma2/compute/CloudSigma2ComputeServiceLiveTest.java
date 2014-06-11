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
package org.jclouds.cloudsigma2.compute;

import com.google.inject.Module;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "CloudSigma2ComputeServiceLiveTest")
public class CloudSigma2ComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public CloudSigma2ComputeServiceLiveTest() {
      provider = "cloudsigma2";

   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   // CloudSigma templates require manual interaction to change the password on the first login.
   // The only way to automatically authenticate to a server is to use an image that supports Cloud Init
   // and provide the public key
   @Override
   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      Template template = super.buildTemplate(templateBuilder);
      template.getOptions().authorizePublicKey(keyPair.get("public"));
      return template;
   }

   @Override
   protected void checkResponseEqualsHostname(ExecResponse execResponse, NodeMetadata node1) {
      // CloudSigma does not return the hostname
   }

}
