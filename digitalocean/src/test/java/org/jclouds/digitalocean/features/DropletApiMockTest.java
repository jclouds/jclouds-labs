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
package org.jclouds.digitalocean.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.Droplet.Status;
import org.jclouds.digitalocean.domain.DropletCreation;
import org.jclouds.digitalocean.domain.options.CreateDropletOptions;
import org.jclouds.digitalocean.internal.BaseDigitalOceanMockTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link DropletApi} class.
 */
@Test(groups = "unit", testName = "DropletApiMockTest")
public class DropletApiMockTest extends BaseDigitalOceanMockTest {

   public void testListDroplets() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/droplets.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         List<Droplet> sizes = dropletApi.list();

         assertRequestHasCommonFields(server.takeRequest(), "/droplets");
         assertEquals(sizes.size(), 1);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/droplet.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         Droplet droplet = dropletApi.get(100823);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/100823");
         assertNotNull(droplet);
         assertNotNull(droplet.getBackups());
         assertNotNull(droplet.getSnapshots());
         assertEquals(droplet.getName(), "test222");
         assertEquals(droplet.getStatus(), Status.ACTIVE);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         Droplet droplet = dropletApi.get(100823);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/100823");
         assertNull(droplet);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testCreateDropletUsingSlugs() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/droplet-creation.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         DropletCreation droplet = dropletApi.create("test", "img-1", "size-1", "region-1");

         assertRequestHasParameters(server.takeRequest(), "/droplets/new", ImmutableMultimap.of("name", "test",
               "image_slug", "img-1", "size_slug", "size-1", "region_slug", "region-1"));

         assertNotNull(droplet);
         assertEquals(droplet.getName(), "test");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testCreateDropletUsingSlugsWithOptions() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/droplet-creation.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         CreateDropletOptions options = CreateDropletOptions.builder().addSshKeyId(5).addSshKeyId(4)
               .privateNetworking(true).backupsEnabled(false).build();
         DropletCreation droplet = dropletApi.create("test", "img-1", "size-1", "region-1", options);

         ImmutableMultimap.Builder<String, String> params = ImmutableMultimap.builder();
         params.put("name", "test");
         params.put("image_slug", "img-1");
         params.put("size_slug", "size-1");
         params.put("region_slug", "region-1");
         params.put("ssh_key_ids", "5,4");
         params.put("private_networking", "true");
         params.put("backups_enabled", "false");

         assertRequestHasParameters(server.takeRequest(), "/droplets/new", params.build());

         assertNotNull(droplet);
         assertEquals(droplet.getName(), "test");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testCreateDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/droplet-creation.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         DropletCreation droplet = dropletApi.create("test", 419, 32, 1);

         assertRequestHasParameters(server.takeRequest(), "/droplets/new",
               ImmutableMultimap.of("name", "test", "image_id", "419", "size_id", "32", "region_id", "1"));

         assertNotNull(droplet);
         assertEquals(droplet.getName(), "test");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testCreateDropletWithOptions() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/droplet-creation.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         CreateDropletOptions options = CreateDropletOptions.builder().addSshKeyId(5).addSshKeyId(4)
               .privateNetworking(true).backupsEnabled(false).build();
         DropletCreation droplet = dropletApi.create("test", 419, 32, 1, options);

         ImmutableMultimap.Builder<String, String> params = ImmutableMultimap.builder();
         params.put("name", "test");
         params.put("image_id", "419");
         params.put("size_id", "32");
         params.put("region_id", "1");
         params.put("ssh_key_ids", "5,4");
         params.put("private_networking", "true");
         params.put("backups_enabled", "false");

         assertRequestHasParameters(server.takeRequest(), "/droplets/new", params.build());

         assertNotNull(droplet);
         assertEquals(droplet.getName(), "test");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testRebootDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.reboot(1);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/reboot");
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testRebootNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.reboot(1);
            fail("Reboot droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/reboot");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testPowerCycleDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.powerCycle(1);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/power_cycle");
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testPowerCycleNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.powerCycle(1);
            fail("Power cycle droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/power_cycle");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testShutdownDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.shutdown(1);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/shutdown");
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testShutdownNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.shutdown(1);
            fail("Shutdown droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/shutdown");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testPowerOffDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.powerOff(1);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/power_off");
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testPowerOffNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.powerOff(1);
            fail("Power off droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/power_off");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testPowerOnDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.powerOn(1);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/power_on");
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testPowerOnNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.powerOn(1);
            fail("Power on droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/power_on");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testResetPasswordForDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.resetPassword(1);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/password_reset");
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testResetPasswordForNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.resetPassword(1);
            fail("Reset password for droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/password_reset");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testResizeDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.resize(1, 3);

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/resize", ImmutableMultimap.of("size_id", "3"));
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testResizeNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.resize(1, 3);
            fail("Resize droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/resize", ImmutableMultimap.of("size_id", "3"));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testSnapshotDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.snapshot(1);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/snapshot");
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testSnapshotNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.snapshot(1);
            fail("Snapshot droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/snapshot");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testSnapshotWithNameDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.snapshot(1, "foo");

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/snapshot", ImmutableMultimap.of("name", "foo"));
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testSnapshotWithNameNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.snapshot(1, "foo");
            fail("Snapshot droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/snapshot", ImmutableMultimap.of("name", "foo"));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testRestoreDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.restore(1, 3);

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/restore", ImmutableMultimap.of("image_id", "3"));
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testRestoreNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.restore(1, 3);
            fail("Restore droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/restore", ImmutableMultimap.of("image_id", "3"));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testRebuildDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.rebuild(1, 3);

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/rebuild", ImmutableMultimap.of("image_id", "3"));
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testRebuildNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.rebuild(1, 3);
            fail("Rebuild droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/rebuild", ImmutableMultimap.of("image_id", "3"));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testRenameDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.rename(1, "foo");

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/rename", ImmutableMultimap.of("name", "foo"));
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testRenameNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.rename(1, "foo");
            fail("Rename droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/rename", ImmutableMultimap.of("name", "foo"));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testDestroyDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.destroy(1);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/destroy");
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testDestroyNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.destroy(1);
            fail("Destroy droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/1/destroy");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testDestroyWithOptionsDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         int event = dropletApi.destroy(1, true);

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/destroy",
               ImmutableMultimap.of("scrub_data", "true"));
         assertTrue(event > 0);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testDestroyWithOptionsNonexistentDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         try {
            dropletApi.destroy(1, true);
            fail("Destroy droplet should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasParameters(server.takeRequest(), "/droplets/1/destroy",
               ImmutableMultimap.of("scrub_data", "true"));
      } finally {
         api.close();
         server.shutdown();
      }
   }

}
