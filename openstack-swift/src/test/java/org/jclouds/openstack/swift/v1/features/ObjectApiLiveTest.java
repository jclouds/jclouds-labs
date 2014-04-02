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
package org.jclouds.openstack.swift.v1.features;

import static org.jclouds.http.options.GetOptions.Builder.tail;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.openstack.swift.v1.options.ListContainerOptions.Builder.marker;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.CopyObjectException;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.jclouds.openstack.swift.v1.options.CreateContainerOptions;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Provides live tests for the {@link ObjectApi}.
 * 
 * @author Adrian Cole
 * @author Jeremy Daggett
 */
@Test(groups = "live", testName = "ObjectApiLiveTest", singleThreaded = true)
public class ObjectApiLiveTest extends BaseSwiftApiLiveTest<SwiftApi> {
   
   private String name = getClass().getSimpleName();
   private String containerName = getClass().getSimpleName() + "Container";
   
   public void testCopyObject() throws Exception {
      for (String regionId : regions) {
         // source
         String sourceContainer = "src" + containerName;
         String sourceObjectName = "original.txt";
         String badSource = "badSource";
         
         // destination
         String destinationContainer = "dest" + containerName;
         String destinationObject = "copy.txt";
         String destinationPath = "/" + destinationContainer + "/" + destinationObject;
         
         String stringPayload = "Hello World";
         Payload data = Payloads.newPayload(stringPayload);
         
         ContainerApi containerApi = api.containerApiInRegion(regionId);
         
         // create source and destination dirs
         containerApi.createIfAbsent(sourceContainer, CreateContainerOptions.NONE);
         containerApi.createIfAbsent(destinationContainer, CreateContainerOptions.NONE);
         
         // get the api for this region and container
         ObjectApi srcApi = api.objectApiInRegionForContainer(regionId, sourceContainer);
         ObjectApi destApi = api.objectApiInRegionForContainer(regionId, destinationContainer);
         
         // Create source object 
         assertNotNull(srcApi.replace(sourceObjectName, data, ImmutableMap.<String, String> of()));
         SwiftObject sourceObject = srcApi.get(sourceObjectName, GetOptions.NONE);
         checkObject(sourceObject);

         // Create the destination object
         assertNotNull(destApi.replace(destinationObject, data, ImmutableMap.<String, String> of()));
         SwiftObject object = destApi.get(destinationObject, GetOptions.NONE);
         checkObject(object);

         // check the copy operation 
         assertTrue(destApi.copy(destinationObject, sourceContainer, sourceObjectName));
         assertNotNull(destApi.head(destinationObject));
         
         // now get a real SwiftObject
         SwiftObject destSwiftObject = destApi.get(destinationObject, GetOptions.NONE);
         assertEquals(Strings2.toString(destSwiftObject.getPayload()), stringPayload);
         
         // test exception thrown on bad source name
         try {
            destApi.copy(destinationObject, badSource, sourceObjectName);
            fail("Expected CopyObjectException");
         } catch (CopyObjectException e) {
            assertEquals(e.getSourcePath(), "/" + badSource + "/" + sourceObjectName);
            assertEquals(e.getDestinationPath(), destinationPath);
         }

         deleteAllObjectsInContainer(regionId, sourceContainer);
         containerApi.deleteIfEmpty(sourceContainer);

         deleteAllObjectsInContainer(regionId, destinationContainer);
         containerApi.deleteIfEmpty(destinationContainer);
      }
   }

   public void testList() throws Exception {
      for (String regionId : regions) {
         ObjectApi objectApi = api.objectApiInRegionForContainer(regionId, containerName);
         ObjectList response = objectApi.list();
         assertEquals(response.getContainer(), api.containerApiInRegion(regionId).get(containerName));
         for (SwiftObject object : response) {
            checkObject(object);
         }
      }
   }

   public void testListWithOptions() throws Exception {
      for (String regionId : regions) {
         ObjectApi objectApi = api.objectApiInRegionForContainer(regionId, containerName);
         ObjectList response = objectApi.list(ListContainerOptions.NONE);
         assertEquals(response.getContainer(), api.containerApiInRegion(regionId).get(containerName));
         for (SwiftObject object : response) {
            checkObject(object);
         }
      }
   }

   public void testMetadata() throws Exception {
      for (String regionId : regions) {
         SwiftObject object = api.objectApiInRegionForContainer(regionId, containerName).head(name);
         assertEquals(object.getName(), name);
         checkObject(object);
         assertEquals(toStringAndClose(object.getPayload().getInput()), "");
      }
   }

   public void testUpdateMetadata() throws Exception {
      for (String regionId : regions) {
         ObjectApi objectApi = api.objectApiInRegionForContainer(regionId, containerName);
         
         Map<String, String> meta = ImmutableMap.of("MyAdd1", "foo", "MyAdd2", "bar");
         assertTrue(objectApi.updateMetadata(name, meta));
         
         SwiftObject object = objectApi.head(name);
         for (Entry<String, String> entry : meta.entrySet()) {
            // note keys are returned in lower-case!
            assertEquals(object.getMetadata().get(entry.getKey().toLowerCase()), entry.getValue(),
                  object + " didn't have metadata: " + entry);
         }
      }
   }

   public void testGet() throws Exception {
      for (String regionId : regions) {
         SwiftObject object = api.objectApiInRegionForContainer(regionId, containerName).get(name, GetOptions.NONE);
         assertEquals(object.getName(), name);
         checkObject(object);
         assertEquals(toStringAndClose(object.getPayload().getInput()), "swifty");
      }
   }

   public void testPrivateByDefault() throws Exception {
      for (String regionId : regions) {
         SwiftObject object = api.objectApiInRegionForContainer(regionId, containerName).head(name);
         try {
            object.getUri().toURL().openStream();
            fail("shouldn't be able to access " + object);
         } catch (IOException expected) {
         }
      }
   }

   public void testGetOptions() throws Exception {
      for (String regionId : regions) {
         SwiftObject object = api.objectApiInRegionForContainer(regionId, containerName).get(name, tail(1));
         assertEquals(object.getName(), name);
         checkObject(object);
         assertEquals(toStringAndClose(object.getPayload().getInput()), "y");
      }
   }

   public void testListOptions() throws Exception {
      String lexicographicallyBeforeName = name.substring(0, name.length() - 1);
      for (String regionId : regions) {
         SwiftObject object = api.objectApiInRegionForContainer(regionId, containerName)
               .list(marker(lexicographicallyBeforeName)).get(0);
         assertEquals(object.getName(), name);
         checkObject(object);
      }
   }

   public void testDeleteMetadata() throws Exception {
      for (String regionId : regions) {
         ObjectApi objectApi = api.objectApiInRegionForContainer(regionId, containerName);

         Map<String, String> meta = ImmutableMap.of("MyDelete1", "foo", "MyDelete2", "bar");
          
         assertTrue(objectApi.updateMetadata(name, meta));
         assertFalse(objectApi.head(name).getMetadata().isEmpty());
         
         assertTrue(objectApi.deleteMetadata(name, meta));
         assertTrue(objectApi.head(name).getMetadata().isEmpty());
      }
   }

   @Override
   @BeforeClass(groups = "live")
   public void setup() {
      super.setup();
      for (String regionId : regions) {
         api.containerApiInRegion(regionId).createIfAbsent(containerName, CreateContainerOptions.NONE);
         api.objectApiInRegionForContainer(regionId, containerName).replace(name, newStringPayload("swifty"),
               ImmutableMap.<String, String> of());
      }
   }

   @Override
   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : regions) {
         deleteAllObjectsInContainer(regionId, containerName);
         api.objectApiInRegionForContainer(regionId, containerName).delete(name);
         api.containerApiInRegion(regionId).deleteIfEmpty(containerName);
      }
      
      super.tearDown();
   }
   
   static void checkObject(SwiftObject object) {
      assertNotNull(object.getName());
      assertNotNull(object.getUri());
      assertNotNull(object.getEtag());
      assertTrue(object.getLastModified().getTime() <= System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
      assertNotNull(object.getPayload().getContentMetadata().getContentLength());
      assertNotNull(object.getPayload().getContentMetadata().getContentType());
   }
}
