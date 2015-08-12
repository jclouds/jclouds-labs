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
package org.jclouds.azurecompute.internal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.azurecompute.config.AzureComputeProperties.OPERATION_POLL_INITIAL_PERIOD;
import static org.jclouds.azurecompute.config.AzureComputeProperties.OPERATION_POLL_MAX_PERIOD;
import static org.jclouds.azurecompute.config.AzureComputeProperties.OPERATION_TIMEOUT;
import static org.jclouds.azurecompute.config.AzureComputeProperties.TCP_RULE_FORMAT;
import static org.jclouds.azurecompute.config.AzureComputeProperties.TCP_RULE_REGEXP;
import java.util.Properties;
import java.util.Random;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.util.ConflictManagementPredicate;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Predicate;

public abstract class AbstractAzureComputeApiLiveTest extends BaseApiLiveTest<AzureComputeApi> {

   protected static final int RAND = new Random().nextInt(999);

   protected Predicate<String> operationSucceeded;

   public AbstractAzureComputeApiLiveTest() {
      provider = "azurecompute";
   }

   @Override protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      properties.put(ComputeServiceProperties.POLL_INITIAL_PERIOD, 1000);
      properties.put(ComputeServiceProperties.POLL_MAX_PERIOD, 10000);
      properties.setProperty(OPERATION_TIMEOUT, "60000");
      properties.setProperty(OPERATION_POLL_INITIAL_PERIOD, "5");
      properties.setProperty(OPERATION_POLL_MAX_PERIOD, "15");
      properties.setProperty(TCP_RULE_FORMAT, "tcp_%s-%s");
      properties.setProperty(TCP_RULE_REGEXP, "tcp_\\d{1,5}-\\d{1,5}");
      return properties;
   }

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      operationSucceeded = new ConflictManagementPredicate(api, 600, 5, 5, SECONDS);
   }
}
