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

import com.google.common.base.Predicate;
import java.util.Random;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.compute.config.AzureComputeServiceContextModule;
import org.testng.annotations.BeforeClass;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

public abstract class AbstractAzureComputeApiLiveTest extends BaseApiLiveTest<AzureComputeApi> {

   protected static final int RAND = new Random().nextInt(999);

   protected Predicate<String> operationSucceeded;

   public AbstractAzureComputeApiLiveTest() {
      provider = "azurecompute";
   }

   @BeforeClass
   @Override
   public void setup() {
      super.setup();

      operationSucceeded = retry(
              new AzureComputeServiceContextModule.OperationSucceededPredicate(api), 600, 5, 5, SECONDS);
   }
}
