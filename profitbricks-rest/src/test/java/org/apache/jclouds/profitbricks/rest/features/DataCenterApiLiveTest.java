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
package org.apache.jclouds.profitbricks.rest.features;

import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "DataCenterApiLiveTest")
public class DataCenterApiLiveTest extends BaseProfitBricksLiveTest {
   
   @Test
   public void testGetList() {
      List<DataCenter> list = dataCenterApi().list(new DepthOptions().depth(1));
      assertNotNull(list);
   }
   
   @Test
   public void testCreate() {
      DataCenter dataCenter = createDataCenter();
      
      assertNotNull(dataCenter);
      assertNotNull(dataCenter.id());
      
      deleteDataCenter(dataCenter.id());
   }
   
   @Test
   public void testGetDataCenter() {
      DataCenter dataCenter = createDataCenter();
      
      dataCenter = dataCenterApi().getDataCenter(dataCenter.id());
      
      assertNotNull(dataCenter);
      assertEquals(dataCenter.properties().name(), "test-data-center");
      
      deleteDataCenter(dataCenter.id());
   }
      
   @Test
   public void testUpdate() {      
      DataCenter dataCenter = createDataCenter();
      
      dataCenter = dataCenterApi().update(dataCenter.id(), "test-data-center2");
      assertRequestCompleted(dataCenter);
      
      assertNotNull(dataCenter);
      assertEquals(dataCenter.properties().name(), "test-data-center2");
      
      deleteDataCenter(dataCenter.id());
   }
   
   @Test
   public void testDelete() {      
      DataCenter dataCenter = createDataCenter();      
      dataCenterApi().delete(dataCenter.id());      
      dataCenter = getDataCenter(dataCenter.id());      
   }
   
   @Test
   public void testDepth() {
      
      DataCenter dataCenter = createDataCenter();
      
      for (int i = 1; i <= 5; ++i) {
         dataCenter = getDataCenter(dataCenter.id(), new DepthOptions().depth(i));
         assertNotNull(dataCenter);
         
         switch(i) {
            case 1:
               assertNotNull(dataCenter.entities());
               assertNotNull(dataCenter.entities().servers().items());
               assertNotNull(dataCenter.entities().volumes().items());
               assertNotNull(dataCenter.entities().loadbalancers().items());
               assertNotNull(dataCenter.entities().lans().items());
               break;
         }
      }
      
      deleteDataCenter(dataCenter.id());
   }
      
   private DataCenter getDataCenter(String id) {
      return dataCenterApi().getDataCenter(id);
   }
   
   private DataCenter getDataCenter(String id, DepthOptions options) {
      return dataCenterApi().getDataCenter(id, options);
   }
      
   private DataCenterApi dataCenterApi() {
      return api.dataCenterApi();
   }
   
}
