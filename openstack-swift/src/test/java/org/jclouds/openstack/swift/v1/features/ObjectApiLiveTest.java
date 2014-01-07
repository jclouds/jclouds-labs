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
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ObjectApiLiveTest")
public class ObjectApiLiveTest extends BaseSwiftApiLiveTest {
   private String name = getClass().getSimpleName();
   private String containerName = getClass().getSimpleName() + "Container";
   
   public void copyObject() throws Exception {
      for (String regionId : regions) {               
         // source
         String sourceContainer = "src" + containerName;
         String sourceObject = "original.txt";
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
         assertNotNull(srcApi.replace(sourceObject, data, ImmutableMap.<String, String> of()));
         SwiftObject object = srcApi.get(sourceObject, GetOptions.NONE);
         checkObject(object);

         // Create the destination object
         assertNotNull(destApi.replace(destinationObject, data, ImmutableMap.<String, String> of()));
         object = destApi.get(destinationObject, GetOptions.NONE);
         checkObject(destApi.get(destinationObject, GetOptions.NONE));

         // check the copy operation 
         assertTrue(destApi.copy(destinationObject, sourceContainer, sourceObject));
         assertNotNull(destApi.head(destinationObject));
         
         // now get a real SwiftObject
         SwiftObject destSwiftObject = destApi.get(destinationObject, GetOptions.NONE);
         assertEquals(Strings2.toString(destSwiftObject.payload()), stringPayload);
         
         // test exception thrown on bad source name
         try {
            destApi.copy(destinationObject, badSource, sourceObject);
            fail("Expected CopyObjectException");
         } catch (CopyObjectException e) {             
            assertEquals(e.getSourcePath(), "/" + badSource + "/" + sourceObject);
            assertEquals(e.getDestinationPath(), destinationPath);
         }

         deleteAllObjectsInContainer(regionId, sourceContainer);
         containerApi.deleteIfEmpty(sourceContainer);

         deleteAllObjectsInContainer(regionId, destinationContainer);
         containerApi.deleteIfEmpty(destinationContainer);
      }
   }

   public void list() throws Exception {
      for (String regionId : regions) {
         ObjectApi objectApi = api.objectApiInRegionForContainer(regionId, containerName);
         ObjectList response = objectApi.list(new ListContainerOptions());
         assertEquals(response.container(), api.containerApiInRegion(regionId).get(containerName));
         assertNotNull(response);
         for (SwiftObject object : response) {
            checkObject(object);
         }
      }
   }

   static void checkObject(SwiftObject object) {
      assertNotNull(object.name());
      assertNotNull(object.uri());
      assertNotNull(object.etag());
      assertTrue(object.lastModified().getTime() <= System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
      assertNotNull(object.payload().getContentMetadata().getContentLength());
      assertNotNull(object.payload().getContentMetadata().getContentType());
   }

   public void metadata() throws Exception {
      for (String regionId : regions) {
         SwiftObject object = api.objectApiInRegionForContainer(regionId, containerName).head(name);
         assertEquals(object.name(), name);
         checkObject(object);
         assertEquals(toStringAndClose(object.payload().getInput()), "");
      }
   }

   public void get() throws Exception {
      for (String regionId : regions) {
         SwiftObject object = api.objectApiInRegionForContainer(regionId, containerName).get(name, GetOptions.NONE);
         assertEquals(object.name(), name);
         checkObject(object);
         assertEquals(toStringAndClose(object.payload().getInput()), "swifty");
      }
   }

   public void privateByDefault() throws Exception {
      for (String regionId : regions) {
         SwiftObject object = api.objectApiInRegionForContainer(regionId, containerName).head(name);
         try {
            object.uri().toURL().openStream();
            fail("shouldn't be able to access " + object);
         } catch (IOException expected) {
         }
      }
   }

   public void getOptions() throws Exception {
      for (String regionId : regions) {
         SwiftObject object = api.objectApiInRegionForContainer(regionId, containerName).get(name, tail(1));
         assertEquals(object.name(), name);
         checkObject(object);
         assertEquals(toStringAndClose(object.payload().getInput()), "y");
      }
   }

   public void listOptions() throws Exception {
      String lexicographicallyBeforeName = name.substring(0, name.length() - 1);
      for (String regionId : regions) {
         SwiftObject object = api.objectApiInRegionForContainer(regionId, containerName)
               .list(marker(lexicographicallyBeforeName)).get(0);
         assertEquals(object.name(), name);
         checkObject(object);
      }
   }

   public void updateMetadata() throws Exception {
      for (String regionId : regions) {
         ObjectApi objectApi = api.objectApiInRegionForContainer(regionId, containerName);

         Map<String, String> meta = ImmutableMap.of("MyAdd1", "foo", "MyAdd2", "bar");
         assertTrue(objectApi.updateMetadata(name, meta));
         containerHasMetadata(objectApi, name, meta);
      }
   }

   public void deleteMetadata() throws Exception {
      for (String regionId : regions) {
         ObjectApi objectApi = api.objectApiInRegionForContainer(regionId, containerName);

         Map<String, String> meta = ImmutableMap.of("MyDelete1", "foo", "MyDelete2", "bar");

         assertTrue(objectApi.updateMetadata(name, meta));
         containerHasMetadata(objectApi, name, meta);

         assertTrue(objectApi.deleteMetadata(name, meta));
         SwiftObject object = objectApi.head(name);
         for (Entry<String, String> entry : meta.entrySet()) {
            // note keys are returned in lower-case!
            assertFalse(object.metadata().containsKey(entry.getKey().toLowerCase()));
         }
      }
   }

   static void containerHasMetadata(ObjectApi objectApi, String name, Map<String, String> meta) {
      SwiftObject object = objectApi.head(name);
      for (Entry<String, String> entry : meta.entrySet()) {
         // note keys are returned in lower-case!
         assertEquals(object.metadata().get(entry.getKey().toLowerCase()), entry.getValue(), //
               object + " didn't have metadata: " + entry);
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
}
