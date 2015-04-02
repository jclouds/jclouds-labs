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
package org.jclouds.vagrant.functions;

import static org.testng.Assert.assertEquals;

import java.util.Collection;
import java.util.Set;

import org.testng.annotations.Test;
import org.testng.collections.Sets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import vagrant.api.domain.Box;

public class OutdatedBoxesFilterTest {
   Collection<Box> UNFILTERED = ImmutableList.<Box>builder()
         .add(new Box("CiscoCloud/microservices-infrastructure", "0.5", "virtualbox"))
         .add(new Box("boxcutter/eval-win7x86-enterprise", "0", "virtualbox"))
         .add(new Box("centos/7", "1603.01", "virtualbox"))
         .add(new Box("centos/7", "1607.01", "virtualbox"))
         .add(new Box("chef/centos-6.5", "1.0.0", "virtualbox"))
         .add(new Box("chef/centos-7.0", "1.0.0", "virtualbox"))
         .add(new Box("debian/jessie64", "8.4.0", "virtualbox"))
         .add(new Box("debian/jessie64", "8.6.1", "virtualbox"))
         .add(new Box("debian/wheezy64", "7.10.0", "virtualbox"))
         .add(new Box("mitchellh/boot2docker", "1.2.0", "virtualbox"))
         .add(new Box("nrel/CentOS-6.5-x86_64", "1.2.0", "virtualbox"))
         .add(new Box("orlandohohmeier/toolchain", "0.5.1", "virtualbox"))
         .add(new Box("playa_mesos_ubuntu_14.04", "0", "virtualbox"))
         .add(new Box("snappy", "0", "virtualbox"))
         .add(new Box("ubuntu/precise32", "20161208.0.0", "virtualbox"))
         .add(new Box("ubuntu/precise64", "12.04.4", "virtualbox"))
         .add(new Box("ubuntu/trusty64", "14.04", "virtualbox"))
         .add(new Box("ubuntu/trusty64", "20160822.0.2", "virtualbox"))
         .add(new Box("ubuntu/ubuntu-15.04-snappy-core-edge-amd64", "15.04.20150424", "virtualbox"))
         .add(new Box("ubuntu/vivid64", "20150427.0.0", "virtualbox"))
         .add(new Box("ubuntu/vivid64", "20150611.0.1", "virtualbox"))
         .add(new Box("ubuntu/wily64", "20151106.0.0", "virtualbox"))
         .add(new Box("ubuntu/wily64", "20160715.0.0", "virtualbox"))
         .add(new Box("ubuntu/xenial64", "20160922.0.0", "virtualbox"))
         .add(new Box("ubuntu/xenial64", "20161119.0.0", "virtualbox"))
         .add(new Box("ubuntu/xenial64", "20161221.0.0", "virtualbox"))
         .build();

   Set<Box> FILTERED = ImmutableSet.<Box>builder()
         .add(new Box("CiscoCloud/microservices-infrastructure", "0.5", "virtualbox"))
         .add(new Box("boxcutter/eval-win7x86-enterprise", "0", "virtualbox"))
         .add(new Box("centos/7", "1607.01", "virtualbox"))
         .add(new Box("chef/centos-6.5", "1.0.0", "virtualbox"))
         .add(new Box("chef/centos-7.0", "1.0.0", "virtualbox"))
         .add(new Box("debian/jessie64", "8.6.1", "virtualbox"))
         .add(new Box("debian/wheezy64", "7.10.0", "virtualbox"))
         .add(new Box("mitchellh/boot2docker", "1.2.0", "virtualbox"))
         .add(new Box("nrel/CentOS-6.5-x86_64", "1.2.0", "virtualbox"))
         .add(new Box("orlandohohmeier/toolchain", "0.5.1", "virtualbox"))
         .add(new Box("playa_mesos_ubuntu_14.04", "0", "virtualbox"))
         .add(new Box("snappy", "0", "virtualbox"))
         .add(new Box("ubuntu/precise32", "20161208.0.0", "virtualbox"))
         .add(new Box("ubuntu/precise64", "12.04.4", "virtualbox"))
         .add(new Box("ubuntu/trusty64", "20160822.0.2", "virtualbox"))
         .add(new Box("ubuntu/ubuntu-15.04-snappy-core-edge-amd64", "15.04.20150424", "virtualbox"))
         .add(new Box("ubuntu/vivid64", "20150611.0.1", "virtualbox"))
         .add(new Box("ubuntu/wily64", "20160715.0.0", "virtualbox"))
         .add(new Box("ubuntu/xenial64", "20161221.0.0", "virtualbox"))
         .build();

   @Test
   public void testFilter() {
      OutdatedBoxesFilter filter = new OutdatedBoxesFilter();
      Collection<Box> actual = filter.apply(UNFILTERED);
      assertEquals(Sets.newHashSet(actual), FILTERED, "Actual list: " + actual);
   }
}
