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
package org.jclouds.openstack.marconi.v1.internal;

import com.google.common.collect.Sets;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.marconi.v1.MarconiApi;
import org.testng.annotations.BeforeClass;

import java.util.Properties;
import java.util.Set;

public class BaseMarconiApiLiveTest extends BaseApiLiveTest<MarconiApi> {

   protected Set<String> regions = Sets.newHashSet();

   public BaseMarconiApiLiveTest() {
      provider = "openstack-marconi";
   }

   @BeforeClass
   public void setupRegions() {
      String key = "test." + provider + ".region";

      if (System.getProperties().containsKey(key)) {
         regions.add(System.getProperty(key));
      }
      else {
         regions = api.getConfiguredRegions();
      }
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, KeystoneProperties.CREDENTIAL_TYPE);
      return props;
   }
}
