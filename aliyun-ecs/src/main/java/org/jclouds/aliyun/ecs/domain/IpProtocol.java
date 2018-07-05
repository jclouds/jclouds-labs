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
package org.jclouds.aliyun.ecs.domain;

import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * IP protocol. Not case sensitive. Optional values:
 * icmp
 * gre
 * tcp
 * udp
 * all: Support four protocols at the same time
 */
public enum IpProtocol {
   ICMP, GRE, TCP, UDP, ALL;

   public static IpProtocol fromValue(String value) {
      Optional<IpProtocol> ipProtocol = Enums.getIfPresent(IpProtocol.class, value.toUpperCase());
      checkArgument(ipProtocol.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(IpProtocol.values()), value);
      return ipProtocol.get();
   }
}
