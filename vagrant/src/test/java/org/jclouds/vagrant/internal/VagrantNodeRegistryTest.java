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
package org.jclouds.vagrant.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.vagrant.domain.VagrantNode;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

public class VagrantNodeRegistryTest {
   private static class TestTimeSupplier implements Supplier<Long> {
      long time = System.currentTimeMillis();

      @Override
      public Long get() {
         return time;
      }

      public void advanceTime(long add) {
         time += add;
      }

   }

   @Test
   public void testNodeRegistry() {
      TestTimeSupplier timeSupplier = new TestTimeSupplier();
      VagrantNodeRegistry registry = new VagrantNodeRegistry(timeSupplier);
      OperatingSystem os = new OperatingSystem(OsFamily.UNRECOGNIZED, "Jclouds OS", "10", "x64", "Jclouds Test Image", true);
      Image image = new ImageBuilder()
            .ids("jclouds/box")
            .operatingSystem(os)
            .status(Image.Status.AVAILABLE)
            .build();

      ImmutableList<String> networks = ImmutableList.of("172.28.128.3");
      VagrantNode node = VagrantNode.builder()
            .setPath(new File("/path/to/machine"))
            .setId("vagrant/node")
            .setGroup("vagrant")
            .setName("node")
            .setImage(image)
            .setNetworks(networks)
            .setHostname("vagrant-node")
            .build();

      assertNull(registry.get(node.id()));
      registry.add(node);
      assertEquals(registry.get(node.id()), node);
      registry.onTerminated(node);
      assertEquals(registry.get(node.id()), node);
      timeSupplier.advanceTime(TimeUnit.MINUTES.toMillis(10));
      assertNull(registry.get(node.id()));
   }
}
